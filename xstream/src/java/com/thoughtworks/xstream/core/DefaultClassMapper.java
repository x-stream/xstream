package com.thoughtworks.xstream.core;

import com.thoughtworks.xstream.alias.AliasingMapper;
import com.thoughtworks.xstream.alias.ArrayMapper;
import com.thoughtworks.xstream.alias.CachingMapper;
import com.thoughtworks.xstream.alias.ClassMapperWrapper;
import com.thoughtworks.xstream.alias.DefaultImplementationsMapper;
import com.thoughtworks.xstream.alias.DefaultMapper;
import com.thoughtworks.xstream.alias.DynamicProxyMapper;
import com.thoughtworks.xstream.alias.ImmutableTypesMapper;
import com.thoughtworks.xstream.alias.XmlFriendlyClassMapper;

/**
 * @deprecated As of 1.1.1. 
 */
public class DefaultClassMapper extends ClassMapperWrapper {

    public DefaultClassMapper() {
        super(new CachingMapper(new ImmutableTypesMapper(new DefaultImplementationsMapper(new ArrayMapper(new DynamicProxyMapper(new AliasingMapper(new XmlFriendlyClassMapper(new DefaultMapper()))))))));
    }

}
