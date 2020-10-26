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

package acceptance.hibernate;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.internal.BootstrapServiceRegistryImpl;

import com.thoughtworks.acceptance.AbstractAcceptanceTest;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.hibernate.converter.HibernatePersistentCollectionConverter;
import com.thoughtworks.xstream.hibernate.converter.HibernatePersistentMapConverter;
import com.thoughtworks.xstream.hibernate.converter.HibernatePersistentSortedMapConverter;
import com.thoughtworks.xstream.hibernate.converter.HibernatePersistentSortedSetConverter;
import com.thoughtworks.xstream.hibernate.converter.HibernateProxyConverter;
import com.thoughtworks.xstream.hibernate.mapper.HibernateMapper;
import com.thoughtworks.xstream.mapper.MapperWrapper;


/**
 * @author J&ouml;rg Schaible
 * @author Jaime Metcher
 */
public abstract class AbstractHibernateAcceptanceTest extends AbstractAcceptanceTest {

    private static final SessionFactory sessionFactory;
    static {
        try {
            // Create the SessionFactory from hibernate.cfg.xml
            sessionFactory = new Configuration().configure().buildSessionFactory();
        } catch (final Throwable ex) {
            // Make sure you log the exception, as it might be swallowed
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    /**
     * Construct a AbstractHibernateAcceptanceTest.
     */
    public AbstractHibernateAcceptanceTest() {
        super();
    }

    @Override
    protected XStream createXStream() {
        final XStream xstream = new XStream() {

            @Override
            protected MapperWrapper wrapMapper(final MapperWrapper next) {
                return new HibernateMapper(next);
            }

        };
        xstream.registerConverter(new HibernateProxyConverter());
        xstream.registerConverter(new HibernatePersistentCollectionConverter(xstream.getMapper()));
        xstream.registerConverter(new HibernatePersistentMapConverter(xstream.getMapper()));
        xstream.registerConverter(new HibernatePersistentSortedMapConverter(xstream.getMapper()));
        xstream.registerConverter(new HibernatePersistentSortedSetConverter(xstream.getMapper()));

        return xstream;
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
