package com.thoughtworks.xstream.converters.extended;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.alias.ClassMapper;

import java.lang.reflect.Proxy;
import java.lang.reflect.InvocationHandler;
import java.util.ArrayList;
import java.util.List;

public class DynamicProxyConverter implements Converter {

    private ClassLoader classLoader;
    private ClassMapper classMapper;

    public DynamicProxyConverter(ClassMapper classMapper) {
        this(classMapper, DynamicProxyConverter.class.getClassLoader());
    }

    public DynamicProxyConverter(ClassMapper classMapper, ClassLoader classLoader) {
        this.classLoader = classLoader;
        this.classMapper = classMapper;
    }

    public boolean canConvert(Class type) {
        return type.equals(ClassMapper.DynamicProxy.class) || Proxy.isProxyClass(type);
    }

    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        InvocationHandler invocationHandler = Proxy.getInvocationHandler(source);
        addInterfacesToXml(source, writer);
        writer.startNode("handler");
        writer.addAttribute("class", classMapper.lookupName(invocationHandler.getClass()));
        context.convertAnother(invocationHandler);
        writer.endNode();
    }

    private void addInterfacesToXml(Object source, HierarchicalStreamWriter writer) {
        Class[] interfaces = source.getClass().getInterfaces();
        for (int i = 0; i < interfaces.length; i++) {
            Class currentInterface = interfaces[i];
            writer.startNode("interface");
            writer.setValue(classMapper.lookupName(currentInterface));
            writer.endNode();
        }
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        List interfaces = new ArrayList();
        InvocationHandler handler = null;
        while(reader.hasMoreChildren()) {
            reader.moveDown();
            String elementName = reader.getNodeName();
            if (elementName.equals("interface")) {
                interfaces.add(classMapper.lookupType(reader.getValue()));
            } else if (elementName.equals("handler")) {
                Class handlerType = classMapper.lookupType(reader.getAttribute("class"));
                handler = (InvocationHandler) context.convertAnother(null, handlerType);
            }
            reader.moveUp();
        }
        if (handler == null) {
            throw new ConversionException("No InvocationHandler specified for dynamic proxy");
        }
        Class[] interfacesAsArray = new Class[interfaces.size()];
        interfaces.toArray(interfacesAsArray);
        return Proxy.newProxyInstance(classLoader, interfacesAsArray, handler);
    }
}
