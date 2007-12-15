/*
 * Copyright (c) 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 30. March 2007 by Joerg Schaible
 */
package com.thoughtworks.xstream.core.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import com.thoughtworks.xstream.converters.reflection.ObjectAccessException;


/**
 * A dependency injection factory.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.2.2
 */
public class DependencyInjectionFactory {

    /**
     * Create an instance with dependency injection. The given dependencies are used to match the parameters of the
     * constructors of the type. Constructors with most parameters are examined first. A parameter type sequence
     * matching the sequence of the dependencies' types match first. Otherwise all the types of the dependencies must
     * match one of the the parameters although no dependency is used twice. Use a {@link TypedNull} instance to inject
     * <code>null</code> as parameter.
     * 
     * @param type the type to create an instance of
     * @param dependencies the possible dependencies
     * @return the instantiated object
     * @throws ObjectAccessException if no instance can be generated
     */
    public static Object newInstance(final Class type, final Object[] dependencies) {
        // sort available ctors according their arity
        final Constructor[] ctors = type.getConstructors();
        if (ctors.length > 1) {
            Arrays.sort(ctors, new Comparator() {
                public int compare(final Object o1, final Object o2) {
                    return ((Constructor)o2).getParameterTypes().length - ((Constructor)o1).getParameterTypes().length;
                }
            });
        }

        final TypedValue[] typedDependencies = new TypedValue[dependencies.length];
        for (int i = 0; i < dependencies.length; i++) {
            Object dependency = dependencies[i];
            Class depType = dependency.getClass();
            if (depType.isPrimitive()) {
                depType = Primitives.box(depType);
            } else if (depType == TypedNull.class) {
                depType = ((TypedNull)dependency).getType();
                dependency = null;
            }

            typedDependencies[i] = new TypedValue(depType, dependency);
        }

        Constructor bestMatchingCtor = null;
        Constructor possibleCtor = null;
        int arity = Integer.MAX_VALUE;
        final List matchingDependencies = new ArrayList();
        for (int i = 0; bestMatchingCtor == null && i < ctors.length; i++) {
            final Constructor constructor = ctors[i];
            final Class[] parameterTypes = constructor.getParameterTypes();
            if (parameterTypes.length > dependencies.length) {
                continue;
            } else if (parameterTypes.length == 0) {
                bestMatchingCtor = constructor;
                break;
            }
            if (arity > parameterTypes.length) {
                if (possibleCtor != null) {
                    bestMatchingCtor = possibleCtor;
                    continue;
                }
                arity = parameterTypes.length;
            }

            for (int j = 0; j < parameterTypes.length; j++) {
                if (parameterTypes[j].isPrimitive()) {
                    parameterTypes[j] = Primitives.box(parameterTypes[j]);
                }
            }

            // first approach: test the ctor params against the dependencies in the sequence of the parameter
            // declaration
            matchingDependencies.clear();
            for (int j = 0, k = 0; j < parameterTypes.length
                    && parameterTypes.length + k - j <= typedDependencies.length; k++) {
                if (parameterTypes[j].isAssignableFrom(typedDependencies[k].type)) {
                    matchingDependencies.add(typedDependencies[k].value);
                    if (++j == parameterTypes.length) {
                        bestMatchingCtor = constructor;
                        break;
                    }
                }
            }

            if (bestMatchingCtor == null && possibleCtor == null) {
                possibleCtor = constructor; // assumption

                // try to match all dependencies in the sequence of the parameter declaration
                final TypedValue[] deps = new TypedValue[typedDependencies.length];
                System.arraycopy(typedDependencies, 0, deps, 0, deps.length);
                matchingDependencies.clear();
                for (int j = 0; j < parameterTypes.length; j++) {
                    int assignable = -1;
                    for (int k = 0; k < deps.length; k++) {
                        if (deps[k] == null) {
                            continue;
                        }
                        if (deps[k].type == parameterTypes[j]) {
                            assignable = k;
                            // optimal match
                            break;
                        } else if (parameterTypes[j].isAssignableFrom(deps[k].type)) {
                            // use most specific type
                            if (assignable < 0 || deps[assignable].type.isAssignableFrom(deps[k].type)) {
                                assignable = k;
                            }
                        }
                    }

                    if (assignable >= 0) {
                        matchingDependencies.add(deps[assignable].value);
                        deps[assignable] = null; // do not match same dep twice
                    } else {
                        possibleCtor = null;
                        break;
                    }
                }
            }
        }

        if (bestMatchingCtor == null) {
            if (possibleCtor == null) {
                throw new ObjectAccessException("Cannot construct "
                        + type.getName()
                        + ", none of the dependencies match any constructor's parameters");
            } else {
                bestMatchingCtor = possibleCtor;
            }
        }

        try {
            return bestMatchingCtor.newInstance(matchingDependencies.toArray());
        } catch (final InstantiationException e) {
            throw new ObjectAccessException("Cannot construct " + type.getName(), e);
        } catch (final IllegalAccessException e) {
            throw new ObjectAccessException("Cannot construct " + type.getName(), e);
        } catch (final InvocationTargetException e) {
            throw new ObjectAccessException("Cannot construct " + type.getName(), e);
        }
    }

    private static class TypedValue {
        final Class type;
        final Object value;

        public TypedValue(final Class type, final Object value) {
            super();
            this.type = type;
            this.value = value;
        }
    }

}
