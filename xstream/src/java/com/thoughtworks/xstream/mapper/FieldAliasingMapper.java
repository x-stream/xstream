package com.thoughtworks.xstream.mapper;

import com.thoughtworks.xstream.alias.ClassMapper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Mapper that allows a field of a specific class to be replaced with a shorter alias, or omitted
 * entirely.
 *
 * @author Joe Walnes
 */
public class FieldAliasingMapper extends MapperWrapper {

    protected final Map fieldToAliasMap = new HashMap();
    protected final Map aliasToFieldMap = new HashMap();
    protected final Set fieldsToOmit = new HashSet();

    public FieldAliasingMapper(Mapper wrapped) {
        super(wrapped);
    }

    /**
     * @deprecated As of 1.2, use {@link #FieldAliasingMapper(Mapper)}
     */
    public FieldAliasingMapper(ClassMapper wrapped) {
        this((Mapper)wrapped);
    }

    public void addFieldAlias(String alias, Class type, String fieldName) {
        fieldToAliasMap.put(key(type, fieldName), alias);
        aliasToFieldMap.put(key(type, alias), fieldName);
    }

    private Object key(Class type, String value) {
        return type.getName() + '.' + value;
    }

    public String serializedMember(Class type, String memberName) {
        String alias = (String) fieldToAliasMap.get(key(type, memberName));
        if (alias == null) {
            return super.serializedMember(type, memberName);
        } else {
            return alias;
        }
    }

    public String realMember(Class type, String serialized) {
        String real = (String) aliasToFieldMap.get(key(type, serialized));
        if (real == null) {
            return super.realMember(type, serialized);
        } else {
            return real;
        }
    }

    public boolean shouldSerializeMember(Class definedIn, String fieldName) {
        return !fieldsToOmit.contains(key(definedIn, fieldName));
    }

    public void omitField(Class type, String fieldName) {
        fieldsToOmit.add(key(type, fieldName));
    }
}
