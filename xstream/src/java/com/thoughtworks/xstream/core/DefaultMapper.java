package com.thoughtworks.xstream.core;

import com.thoughtworks.xstream.mapper.AliasingMapper;
import com.thoughtworks.xstream.mapper.ArrayMapper;
import com.thoughtworks.xstream.mapper.CachingMapper;
import com.thoughtworks.xstream.mapper.MapperWrapper;
import com.thoughtworks.xstream.mapper.DefaultImplementationsMapper;
import com.thoughtworks.xstream.mapper.DefaultMapper;
import com.thoughtworks.xstream.mapper.DynamicProxyMapper;
import com.thoughtworks.xstream.mapper.ImmutableTypesMapper;
import com.thoughtworks.xstream.mapper.ImmutableTypesMapper;
import com.thoughtworks.xstream.mapper.XmlFriendlyMapper;
import com.thoughtworks.xstream.core.util.CompositeClassLoader;

/**
 * @deprecated As of 1.1.1.
 */
public class DefaultMapper extends MapperWrapper {

    public DefaultMapper() {
        super(new CachingMapper(new ImmutableTypesMapper(new DefaultImplementationsMapper(new ArrayMapper(new DynamicProxyMapper(new AliasingMapper(new XmlFriendlyMapper(new DefaultMapper(new CompositeClassLoader())))))))));
    }

}
