package com.thoughtworks.acceptance.objects;

import org.apache.commons.lang.enum.Enum;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class ColorEnum extends Enum {
    public static final ColorEnum RED = new ColorEnum("Red");
    public static final ColorEnum GREEN = new ColorEnum("Green");
    public static final ColorEnum BLUE = new ColorEnum("Blue");

    private ColorEnum(String color) {
        super(color);
    }

    public static ColorEnum getEnum(String color) {
        return (ColorEnum) getEnum(ColorEnum.class, color);
    }

    public static Map getEnumMap() {
        return getEnumMap(ColorEnum.class);
    }

    public static List getEnumList() {
        return getEnumList(ColorEnum.class);
    }

    public static Iterator iterator() {
        return iterator(ColorEnum.class);
    }
}