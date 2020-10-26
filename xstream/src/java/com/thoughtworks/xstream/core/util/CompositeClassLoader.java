/*
 * Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package com.thoughtworks.xstream.core.util;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * ClassLoader that is composed of other classloaders. Each loader will be used to try to load the particular class,
 * until one of them succeeds. <b>Note:</b> The loaders will always be called in the REVERSE order they were added in.
 * <p>
 * The Composite class loader also has registered the classloader that loaded xstream.jar and (if available) the
 * thread's context classloader.
 * </p>
 * <h1>Example</h1>
 *
 * <pre>
 * <code>CompositeClassLoader loader = new CompositeClassLoader();
 * loader.add(MyClass.class.getClassLoader());
 * loader.add(new AnotherClassLoader());
 * &nbsp;
 * loader.loadClass("com.blah.ChickenPlucker");
 * </code>
 * </pre>
 * <p>
 * The above code will attempt to load a class from the following classloaders (in order):
 * </p>
 * <ul>
 * <li>AnotherClassLoader (and all its parents)</li>
 * <li>The classloader for MyClas (and all its parents)</li>
 * <li>The thread's context classloader (and all its parents)</li>
 * <li>The classloader for XStream (and all its parents)</li>
 * </ul>
 * <p>
 * The added classloaders are kept with weak references to allow an application container to reload classes.
 * </p>
 *
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 * @since 1.0.3
 */
public class CompositeClassLoader extends ClassLoader {
    static {
        // see http://www.cs.duke.edu/csed/java/jdk1.7/technotes/guides/lang/cl-mt.html
        registerAsParallelCapable();
    }

    private final ReferenceQueue<ClassLoader> queue = new ReferenceQueue<>();
    private final List<WeakReference<ClassLoader>> classLoaders = new ArrayList<>();

    public CompositeClassLoader() {
        addInternal(Object.class.getClassLoader()); // bootstrap loader.
        addInternal(getClass().getClassLoader()); // whichever classloader loaded this jar.
    }

    /**
     * Add a loader to the n
     *
     * @param classLoader
     */
    public synchronized void add(final ClassLoader classLoader) {
        cleanup();
        if (classLoader != null) {
            addInternal(classLoader);
        }
    }

    private void addInternal(final ClassLoader classLoader) {
        WeakReference<ClassLoader> refClassLoader = null;
        for (final Iterator<WeakReference<ClassLoader>> iterator = classLoaders.iterator(); iterator.hasNext();) {
            final WeakReference<ClassLoader> ref = iterator.next();
            final ClassLoader cl = ref.get();
            if (cl == null) {
                iterator.remove();
            } else if (cl == classLoader) {
                iterator.remove();
                refClassLoader = ref;
            }
        }
        classLoaders.add(0, refClassLoader != null ? refClassLoader : new WeakReference<>(classLoader, queue));
    }

    @Override
    public Class<?> loadClass(final String name) throws ClassNotFoundException {
        final List<ClassLoader> copy = new ArrayList<>(classLoaders.size());
        synchronized (this) {
            cleanup();
            for (final WeakReference<ClassLoader> ref : classLoaders) {
                final ClassLoader cl = ref.get();
                if (cl != null) {
                    copy.add(cl);
                }
            }
        }

        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        for (final ClassLoader classLoader : copy) {
            if (classLoader == contextClassLoader) {
                contextClassLoader = null;
            }
            try {
                return classLoader.loadClass(name);
            } catch (final ClassNotFoundException notFound) {
                // ok.. try another one
            }
        }

        // One last try - the context class loader associated with the current thread. Often used in j2ee servers.
        // Note: The contextClassLoader cannot be added to the classLoaders list up front as the thread that constructs
        // XStream is potentially different to thread that uses it.
        if (contextClassLoader != null) {
            return contextClassLoader.loadClass(name);
        } else {
            throw new ClassNotFoundException(name);
        }
    }

    private void cleanup() {
        Reference<? extends ClassLoader> ref;
        while ((ref = queue.poll()) != null) {
            classLoaders.remove(ref);
        }
    }
}
