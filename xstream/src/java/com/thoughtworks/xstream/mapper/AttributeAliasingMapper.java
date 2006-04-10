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

    public String aliasForAttribute(String attribute) {
        String alias = (String)nameToAlias.get(attribute);
        return alias == null ? super.aliasForAttribute(attribute) : alias;
    }

    public String attributeForAlias(String alias) {
        String name = (String)aliasToName.get(alias);
        return name == null ? super.attributeForAlias(alias) : name;
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
