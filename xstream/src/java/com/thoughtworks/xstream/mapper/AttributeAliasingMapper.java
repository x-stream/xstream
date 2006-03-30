package com.thoughtworks.xstream.mapper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Mapper that allows aliasing of attribute names.
 *
 * @author J&ouml;rg Schaible
 * @since 1.2
 */
public class AttributeAliasingMapper extends MapperWrapper {

    private final Map aliasToName = new HashMap();
    private transient Map nameToAlias = new HashMap();

    public AttributeAliasingMapper(Mapper wrapped) {
        super(wrapped);
    }

    public void addAliasFor(final String attributeName, final String alias) {
        aliasToName.put(alias, attributeName);
        nameToAlias.put(attributeName, alias);
    }

    public String attributeForClassDefiningField() {
        return getAliasForName(super.attributeForClassDefiningField());
    }

    public String attributeForEnumType() {
        return getAliasForName(super.attributeForEnumType());
    }

    public String attributeForImplementationClass() {
        return getAliasForName(super.attributeForImplementationClass());
    }

    public String attributeForReadResolveField() {
        return getAliasForName(super.attributeForReadResolveField());
    }

    public String attributeForReference() {
        return getAliasForName(super.attributeForReference());
    }

    public String aliasForField(String fieldName) {
        String alias = (String)nameToAlias.get(fieldName);
        return alias == null ? super.aliasForField(fieldName) : alias;
    }

    public String fieldForAlias(String alias) {
        String name = (String)aliasToName.get(alias);
        return name == null ? super.fieldForAlias(alias) : name;
    }

    private String getAliasForName(String name) {
        String alias = (String)nameToAlias.get(name);
        return alias == null ? name : alias;
    }
    
    private Object readResolve() {
        nameToAlias = new HashMap();
        for (final Iterator iter = aliasToName.keySet().iterator(); iter.hasNext();) {
            final Object alias = iter.next();
            nameToAlias.put(aliasToName.get(alias), alias);
        }
        return this;
    }
}
