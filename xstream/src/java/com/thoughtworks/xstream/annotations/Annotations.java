package com.thoughtworks.xstream.annotations;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.collections.AbstractCollectionConverter;
import com.thoughtworks.xstream.mapper.Mapper;

/**
 * Contains utility methods that enable to configure an XStream instance
 * with class and field aliases, based on a class decorated
 * with annotations defined in this package.
 *
 * @author Emil Kirschner
 * @author Chung-Onn Cheong
 */
public class Annotations {
    private static final Set<Class<?>> configuredTypes = new HashSet<Class<?>>();

    /**
     * This class is not instantiable
     */
    private Annotations() {
    }

    /**
     * Configures aliases on the specified XStream object based on annotations that decorate the specified class.
     *
     * @param topLevelClasses the class for which the XStream object is configured.
     * This class is expected to be decorated with annotations defined in this package.
     * @param xstream the XStream object that will be configured
     */
    public static synchronized void configureAliases(XStream xstream, Class<?>... topLevelClasses) {
        configuredTypes.clear();
        for(Class<?> topLevelClass : topLevelClasses){
            configureClass(xstream, topLevelClass);
        }
    }

    private static synchronized void configureClass(XStream xstream, Class<?> configurableClass) {
        if (configurableClass == null
              || configuredTypes.contains(configurableClass)) {
            return;
        }

        if(Converter.class.isAssignableFrom(configurableClass)){
            Class<Converter> converterType = (Class<Converter>)configurableClass;
            registerConverter(xstream, converterType);
            return;
        }

        //Do Class Level Converters
        AnnotatedElement element = configurableClass;
        if(configurableClass.isAnnotationPresent(XStreamConverters.class)){
            XStreamConverters convertersAnnotation = element.getAnnotation(XStreamConverters.class);
            for(XStreamConverter converterAnnotation : convertersAnnotation.value()){
                registerConverter(xstream, converterAnnotation.value());
            }
        }

        //Do Class Leve - Converter
        if(configurableClass.isAnnotationPresent(XStreamConverter.class)){
            XStreamConverter converterAnnotation = element.getAnnotation(XStreamConverter.class);
            registerConverter(xstream, converterAnnotation.value());
        }

        //Do Class Level Alias
        if(configurableClass.isAnnotationPresent(XStreamAlias.class)){
            XStreamAlias aliasAnnotation = element.getAnnotation(XStreamAlias.class);
            if(aliasAnnotation.impl() != Void.class){
                //Alias for Interface/Class with an impl
                xstream.alias(aliasAnnotation.value(), configurableClass, aliasAnnotation.impl());
                configuredTypes.add(configurableClass);
                if(configurableClass.isInterface()){
                    configureClass(xstream,aliasAnnotation.impl()); //alias Interface's impl
                    return;
                }
            }else{
                xstream.alias(aliasAnnotation.value(), configurableClass);
                configuredTypes.add(configurableClass);
            }
        }

        //Do Class Level ImplicitCollection
        if(configurableClass.isAnnotationPresent(XStreamImplicitCollection.class)){
            XStreamImplicitCollection implicitColAnnotation = element.getAnnotation(XStreamImplicitCollection.class);
            String fieldName = implicitColAnnotation.value();
            String itemFieldName = implicitColAnnotation.item();
            Field field;
            try {
                field = configurableClass.getDeclaredField(fieldName);
                Class itemType = getFieldParameterizedType(field, xstream);
                if (itemType == null) {
                    xstream.addImplicitCollection(configurableClass, fieldName);
                } else {
                    if (itemFieldName.equals("")) {
                        xstream.addImplicitCollection(configurableClass, fieldName,
                                itemType);
                    } else {
                        xstream.addImplicitCollection(configurableClass, fieldName,
                                itemFieldName, itemType);
                    }
                }
                configuredTypes.add(configurableClass);
            } catch (Exception e) {
                System.err.println("Fail to derive ImplicitCollection member type");
            }
        }

        //Do Member Level Alias and  XStreamContainedType
        Field[] fields = configurableClass.getDeclaredFields();
        for (Field field : fields) {
            if(field.isSynthetic()) continue;

            //Alias the member's Type
            Class fieldType = field.getType();
            if (Collection.class.isAssignableFrom(fieldType)) {
                if(field.isAnnotationPresent(XStreamContainedType.class)){
                    Class containedClass = getFieldParameterizedType(field, xstream);
                    configureClass(xstream, containedClass);
                }
            }
            if(field.isAnnotationPresent(XStreamAlias.class)){
                XStreamAlias fieldXStreamAliasAnnotation =  field.getAnnotation(XStreamAlias.class);
                xstream.aliasField(fieldXStreamAliasAnnotation.value(), configurableClass, field.getName());
                configureClass(xstream, field.getType());
            }
        }

        //Do Member Classes Alias
        for(Class<?>memberClass : configurableClass.getDeclaredClasses()){
            configureClass(xstream, memberClass);
        }

        //Do Superclass and Superinterface Alias
        Class superClass = configurableClass.getSuperclass();
        if (superClass != null && !Object.class.equals(superClass))
            configureClass(xstream, superClass);
        Class[] interfaces = configurableClass.getInterfaces();
        for(Class intf : interfaces){
            configureClass(xstream, intf);
        }
    }


    private static void registerConverter(XStream xstream, Class<? extends Converter> converterType) {
        Converter converter;
        if(configuredTypes.contains(converterType))
            return;
        if (AbstractCollectionConverter.class.isAssignableFrom(converterType)) {
            try {
                Constructor<? extends Converter> converterConstructor = converterType.getConstructor(Mapper.class);
                converter = converterConstructor.newInstance(xstream.getMapper());
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

        } else {
            try {
                converter = converterType.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
        xstream.registerConverter(converter);
        configuredTypes.add(converterType);

    }

    /*
     * Return a concrete class
     */
    private static Class getFieldParameterizedType(Field field, XStream xstream){
        if(field.getGenericType() instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) field.getGenericType();
            Class type =  (Class) pType.getActualTypeArguments()[0];
            //Get the interface Impl
            if(type.isInterface()){
                AnnotatedElement element = type;
                XStreamAlias alias =  element.getAnnotation(XStreamAlias.class);
                configureClass(xstream, type);
                type = alias.impl();
                assert !type.isInterface()  : type;
            }
            return type;
        }
        assert false : "Field is raw type :" + field;
        return null;
    }
}
