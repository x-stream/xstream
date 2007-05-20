package com.thoughtworks.xstream.mapper;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Mapper that allows aliasing of attribute names.
 *
 * @author J&ouml;rg Schaible
 * @author Guilherme Silveira
 * @since 1.2
 */
public class AttributeAliasingMapper extends MapperWrapper {

    private final Map aliasToName = new HashMap();
    private transient Map nameToAlias = new HashMap();
    private final Map fieldToAlias = new HashMap();
    private final Map aliasToField = new HashMap();

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
    
    public String aliasForAttribute(Class definedIn, String fieldName) {
        Field field = getField(definedIn, fieldName);
        if (fieldToAlias.containsKey(field)) {
            return (String)fieldToAlias.get(field);
        }
        return aliasForAttribute(fieldName);
    }

    public String attributeForAlias(Class definedIn, String alias) {
        if (aliasToField.containsKey(makeKey(definedIn, alias))) {
            return (String)aliasToField.get(makeKey(definedIn, alias));
        }
        return attributeForAlias(alias);
    }

    private AliasInfo makeKey(Class definedIn, String alias) {
        return new AliasInfo(definedIn, alias);
    }

    private class AliasInfo {
        private final Class definedIn;
        private final String alias;

        public AliasInfo(final Class definedIn, final String alias) {
            this.definedIn = definedIn;
            this.alias = alias;
        }

        public int hashCode() {
            return alias.hashCode() * definedIn.hashCode();
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof AliasInfo)) {
                return false;
            }
            AliasInfo info = (AliasInfo)obj;
            return info.alias.equals(this.alias) && definedIn.equals(info.definedIn);
        }
    }

    /**
     * Adds an alias for a specific field.
     * 
     * @param definedIn the type where the field was defined
     * @param fieldName the field name
     * @param alias the alias to be used
     * since 1.2.2
     */
    public void addAliasFor(Class definedIn, String fieldName, String alias) {
        try {
            Field field = definedIn.getDeclaredField(fieldName);
            this.fieldToAlias.put(field, alias);
            this.aliasToField.put(makeKey(definedIn, alias), fieldName);
        } catch (SecurityException e) {
            throw new IllegalArgumentException(fieldName
                + " is not a proper field of "
                + definedIn.getName());
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException(fieldName
                + " is not a proper field of "
                + definedIn.getName());
        }
    }

    private Field getField(Class definedIn, String fieldName) {
        try {
            return definedIn.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException(fieldName
                + " is not a proper field of "
                + definedIn.getName());
        }
    }

}
