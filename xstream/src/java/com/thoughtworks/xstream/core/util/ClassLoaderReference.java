package com.thoughtworks.xstream.core.util;

/**
 * ClassLoader that refers to another ClassLoader, allowing a single instance to be passed around the codebase that
 * can later have its destination changed.
 *
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 * @since 1.1.1
 */
public class ClassLoaderReference extends ClassLoader {

    private transient ClassLoader reference;

    public ClassLoaderReference(ClassLoader reference) {
        this.reference = reference;
    }

    public Class loadClass(String name) throws ClassNotFoundException {
        return reference.loadClass(name);
    }

    public ClassLoader getReference() {
        return reference;
    }

    public void setReference(ClassLoader reference) {
        this.reference = reference;
    }
    
    private Object writeReplace() {
        return new Replacement();
    }
    
    static class Replacement {
        
        private Object readResolve() {
            return new ClassLoaderReference(new CompositeClassLoader());
        }
        
    };
}
