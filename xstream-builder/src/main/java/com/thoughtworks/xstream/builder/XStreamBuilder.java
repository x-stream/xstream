package com.thoughtworks.xstream.builder;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.ReadOnlyXStream;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.builder.processor.AnnotatedTypeProcessor;
import com.thoughtworks.xstream.builder.processor.ConfigProcessor;
import com.thoughtworks.xstream.builder.processor.ConverterProcessor;
import com.thoughtworks.xstream.builder.processor.AliasFieldProcessor;
import com.thoughtworks.xstream.builder.processor.FieldConfigProcessor;
import com.thoughtworks.xstream.builder.processor.IgnoreFieldProcessor;
import com.thoughtworks.xstream.builder.processor.ImplementedByProcessor;
import com.thoughtworks.xstream.builder.processor.AliasTypeProcessor;
import com.thoughtworks.xstream.builder.processor.TypeConfigProcessor;
import com.thoughtworks.xstream.converters.Converter;

/**
 * The base xstream builder. This is xstream's new entrypoint. Instantiate a builder, configure it
 * and invoke buildXStream at the end.
 *
 * @author Guilherme Silveira
 * @since 1.3
 */
public class XStreamBuilder {

    private final List childrenNodes = new ArrayList();

    protected TypeConfig handle(Class type) {
        TypeConfig classConfig = new TypeConfig(type);
        childrenNodes.add(classConfig);
        return classConfig;
    }

    public ReadOnlyXStream buildXStream() {
        XStream instance = createBasicInstance();
        for(int i=0;i<childrenNodes.size();i++) {
            ConfigProcessor node = (ConfigProcessor) childrenNodes.get(i);
            node.process(instance);
        }
        return new ReadOnlyXStream(instance);
    }

    /**
     * Extension point to allow lower-level programmers to create their own xstream instance.
     * @return the xstream instance to configure and wrap
     */
    protected XStream createBasicInstance() {
        return new XStream();
    }

    protected TypeConfigProcessor alias(String alias) {
    	return new AliasTypeProcessor(alias);
    }

    protected FieldConfigProcessor as(String alias) {
    	return new AliasFieldProcessor(alias);
    }

    protected TypeConfigProcessor ignores(String fieldName) {
    	return new IgnoreFieldProcessor(fieldName);
    }

    protected TypeConfigProcessor implementedBy(Class defaultImplementation) {
    	return new ImplementedByProcessor(defaultImplementation);
    }

    protected FieldConfig field(String fieldName) {
    	return new FieldConfig(fieldName);
    }

    protected XStreamBuilder register(ConfigProcessor ... nodes) {
    	for(ConfigProcessor node : nodes) {
            childrenNodes.add(node);
    	}
        return this;
    }

    protected ConfigProcessor converter(Converter converter) {
    	return new ConverterProcessor(converter);
    }

    protected TypeConfigProcessor annotated() {
    	return new AnnotatedTypeProcessor();
    }

}
