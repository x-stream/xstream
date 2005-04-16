package com.thoughtworks.xstream.converters.javabean;

import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * Can convert any bean with a public default constructor. BeanInfo are not
 * taken into consideration, this class looks for bean patterns for simple
 * properties
 */
public class JavaBeanConverter implements Converter {

    /**
     * TODO:
     *  - use bean introspection instead of reflection.
     *  - support indexed properties
     *  - ignore default values
     *  - use BeanInfo
     */
    private ClassMapper classMapper;

    private String classAttributeIdentifier;

    private BeanProvider beanProvider;

    public JavaBeanConverter(ClassMapper classMapper, String classAttributeIdentifier) {
        this.classMapper = classMapper;
        this.classAttributeIdentifier = classAttributeIdentifier;
        this.beanProvider = new BeanProvider();
    }

    /**
     * Only checks for the availability of a public default constructor.
     * If you need stricter checks, subclass JavaBeanConverter
     */
    public boolean canConvert(Class type) {
        return beanProvider.canInstantiate(type);
    }

    public void marshal(final Object source, final HierarchicalStreamWriter writer, final MarshallingContext context) {

        beanProvider.visitSerializableProperties(source, new BeanProvider.Visitor() {
            public void visit(String propertyName, Class fieldType, Object newObj) {
                if (newObj != null) {
                    writeField(propertyName, fieldType, newObj);
                }
            }

            private void writeField(String propertyName, Class fieldType, Object newObj) {
                writer.startNode(classMapper.serializedMember(source.getClass(), propertyName));

                Class actualType = newObj.getClass();

                Class defaultType = classMapper.defaultImplementationOf(fieldType);
                if (!actualType.equals(defaultType)) {
                    writer.addAttribute(classAttributeIdentifier, classMapper.serializedClass(actualType));
                }
                context.convertAnother(newObj);

                writer.endNode();
            }

        });
    }

    public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext context) {
        final Object result = instantiateNewInstance(context);

        while (reader.hasMoreChildren()) {
            reader.moveDown();

            String propertyName = classMapper.realMember(result.getClass(), reader.getNodeName());

            boolean propertyExistsInClass = beanProvider.propertyDefinedInClass(propertyName, result.getClass());

            Class type = determineType(reader, result, propertyName);
            Object value = context.convertAnother(result, type);

            if (propertyExistsInClass) {
                beanProvider.writeProperty(result, propertyName, value);
            }

            reader.moveUp();
        }

        return result;
    }

    private Object instantiateNewInstance(UnmarshallingContext context) {
        Object result = context.currentObject();
        if (result == null) {
            result = beanProvider.newInstance(context.getRequiredType());
        }
        return result;
    }

    private Class determineType(HierarchicalStreamReader reader, Object result, String fieldName) {
        String classAttribute = reader.getAttribute(classAttributeIdentifier);
        if (classAttribute != null) {
            return classMapper.realClass(classAttribute);
        } else {
            return classMapper.defaultImplementationOf(beanProvider.getPropertyType(result, fieldName));
        }
    }

    public static class DuplicateFieldException extends ConversionException {
        public DuplicateFieldException(String msg) {
            super(msg);
        }
    }
}