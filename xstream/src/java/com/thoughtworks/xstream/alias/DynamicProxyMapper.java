package com.thoughtworks.xstream.alias;

import java.lang.reflect.Proxy;

public class DynamicProxyMapper extends ClassMapperWrapper {

    private String alias = "dynamic-proxy";

    public DynamicProxyMapper(ClassMapper wrapped) {
        super(wrapped);
    }

    public String lookupName(Class type) {
        if (Proxy.isProxyClass(type)) {
            return alias;
        } else {
            return super.lookupName(type);
        }
    }

    public Class lookupType(String elementName) {
        if (elementName.equals("dynamic-proxy")) {
            return DynamicProxy.class;   
        } else {
            return super.lookupType(elementName);
        }
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    /**
     * Place holder type used for dynamic proxies.
     */
    public static class DynamicProxy {}

}
