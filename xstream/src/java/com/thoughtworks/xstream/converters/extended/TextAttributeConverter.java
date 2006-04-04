package com.thoughtworks.xstream.converters.extended;

import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;
import com.thoughtworks.xstream.converters.reflection.ObjectAccessException;

import java.awt.font.TextAttribute;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.AttributedCharacterIterator;
import java.util.Map;

/**
 * A converter for {@link TextAttribute} constants.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.2
 */
public class TextAttributeConverter  extends AbstractSingleValueConverter {
    
    private static final Method getName;
    private static final Field instanceMap;
    static {
        try {
            getName = AttributedCharacterIterator.Attribute.class.getDeclaredMethod("getName", (Class[])null);
            instanceMap = TextAttribute.class.getDeclaredField("instanceMap");
        } catch (NoSuchMethodException e) {
            throw new InternalError("Missing TextAttribute.getName()");
        } catch (NoSuchFieldException e) {
            throw new InternalError("Missing TextAttribute.instanceMap");
        }
    }

    public boolean canConvert(Class type) {
        return type == TextAttribute.class;
    }

    public String toString(Object source) {
        TextAttribute attribute = (TextAttribute)source;
        try {
            getName.setAccessible(true);
            return (String)getName.invoke(attribute, (Object[])null);
        } catch (IllegalAccessException e) {
            throw new ObjectAccessException("Cannot get name of TextAttribute", e);
        } catch (InvocationTargetException e) {
            throw new ObjectAccessException("Cannot get name of TextAttribute", e.getTargetException());
        }
    }

    public Object fromString(String str) {
        instanceMap.setAccessible(true);
        Map map;
        try {
            map = (Map)instanceMap.get(null);
        } catch (IllegalAccessException e) {
            throw new ObjectAccessException("Cannot get named instance of TextAttribute " + str);
        }
        return map.get(str);
    }

}
