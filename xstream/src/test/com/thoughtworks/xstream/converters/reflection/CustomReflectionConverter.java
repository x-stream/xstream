package com.thoughtworks.xstream.converters.reflection;

import com.thoughtworks.xstream.mapper.Mapper;

public class CustomReflectionConverter extends ReflectionConverter {

    public CustomReflectionConverter(Mapper mapper, ReflectionProvider reflectionProvider) {
        super(mapper, reflectionProvider);
    }

}
