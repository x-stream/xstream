package com.thoughtworks.xstream.alias;

import java.util.Map;
import java.util.Collections;
import java.util.HashMap;

public class AliasingMapper extends ClassMapperWrapper {

    protected final Map typeToNameMap = Collections.synchronizedMap(new HashMap());
    protected final Map nameToTypeMap = Collections.synchronizedMap(new HashMap());

    public AliasingMapper(ClassMapper wrapped) {
        super(wrapped);
    }

    public void alias(String elementName, Class type, Class defaultImplementation) {
        nameToTypeMap.put(elementName, type.getName());
        typeToNameMap.put(type.getName(), elementName);
        if (!type.equals(defaultImplementation)) {
            typeToNameMap.put(defaultImplementation.getName(), elementName);
        }
        super.alias(elementName, type, defaultImplementation);
    }

    public String lookupName(Class type) {
        String name = super.lookupName(type);
        String alias = (String) typeToNameMap.get(type.getName());
        if (alias != null) {
            return alias;
        } else {
            return name;
        }
    }

    public Class lookupType(String elementName) {
        if (elementName.equals("null")) { // TODO: This should be elsewhere
            return null;
        }

        String mappedName = (String) nameToTypeMap.get(mapNameFromXML(elementName));

        if (mappedName != null) {
            elementName = mappedName;
        }

        return super.lookupType(elementName);
    }

}
