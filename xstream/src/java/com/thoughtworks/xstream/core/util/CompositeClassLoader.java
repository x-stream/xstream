package com.thoughtworks.xstream.core.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * ClassLoader that is composed of other classloaders. Each loader will be used to try to load the particular class, until
 * one of them succeeds. <b>Note:</b> The loaders will always be called in the REVERSE order they were added in.
 *
 * <p>The Composite class loader also has registered  the classloader that loaded xstream.jar
 * and (if available) the thread's context classloader.</p>
 *
 * <h1>Example</h1>
 * <pre><code>CompositeClassLoader loader = new CompositeClassLoader();
 * loader.add(MyClass.class.getClassLoader());
 * loader.add(new AnotherClassLoader());
 * &nbsp;
 * loader.loadClass("com.blah.ChickenPlucker");
 * </code></pre>
 *
 * <p>The above code will attempt to load a class from the following classloaders (in order):</p>
 *
 * <ul>
 *   <li>AnotherClassLoader (and all its parents)</li>
 *   <li>The classloader for MyClas (and all its parents)</li>
 *   <li>The thread's context classloader (and all its parents)</li>
 *   <li>The classloader for XStream (and all its parents)</li>
 * </ul>
 *
 * @author Joe Walnes
 * @since 1.0.3
 */
public class CompositeClassLoader extends ClassLoader {

    private final List classLoaders = Collections.synchronizedList(new ArrayList());

    public CompositeClassLoader() {
        add(Object.class.getClassLoader()); // bootstrap loader.
        add(getClass().getClassLoader()); // whichever classloader loaded this jar.
    }

    /**
     * Add a loader to the n
     * @param classLoader
     */
    public void add(ClassLoader classLoader) {
        if (classLoader != null) {
            classLoaders.add(0, classLoader);
        }
    }

    public Class loadClass(String name) throws ClassNotFoundException {
        for (Iterator iterator = classLoaders.iterator(); iterator.hasNext();) {
            ClassLoader classLoader = (ClassLoader) iterator.next();
            try {
                return classLoader.loadClass(name);
            } catch (ClassNotFoundException notFound) {
                // ok.. try another one
            }
        }
        // One last try - the context class loader associated with the current thread. Often used in j2ee servers.
        // Note: The contextClassLoader cannot be added to the classLoaders list up front as the thread that constructs
        // XStream is potentially different to thread that uses it.
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        if (contextClassLoader != null) {
            return contextClassLoader.loadClass(name);
        } else {
            throw new ClassNotFoundException(name);
        }
    }

}
