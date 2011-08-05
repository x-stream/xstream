/*
 * Copyright (C) 2011 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 21. April 2011 by Joerg Schaible
 */
package acceptance.hibernate;

import com.thoughtworks.acceptance.AbstractAcceptanceTest;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.hibernate.converter.HibernatePersistentCollectionConverter;
import com.thoughtworks.xstream.hibernate.converter.HibernatePersistentMapConverter;
import com.thoughtworks.xstream.hibernate.converter.HibernatePersistentSortedMapConverter;
import com.thoughtworks.xstream.hibernate.converter.HibernatePersistentSortedSetConverter;
import com.thoughtworks.xstream.hibernate.converter.HibernateProxyConverter;
import com.thoughtworks.xstream.hibernate.mapper.HibernateMapper;
import com.thoughtworks.xstream.mapper.MapperWrapper;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;


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

    protected XStream createXStream() {
        final XStream xstream = new XStream() {

            protected MapperWrapper wrapMapper(final MapperWrapper next) {
                return new HibernateMapper(next);
            }

        };
        xstream.registerConverter(new HibernateProxyConverter());
        xstream.registerConverter(new HibernatePersistentCollectionConverter(xstream
            .getMapper()));
        xstream.registerConverter(new HibernatePersistentMapConverter(xstream.getMapper()));
        xstream
            .registerConverter(new HibernatePersistentSortedMapConverter(xstream.getMapper()));
        xstream
            .registerConverter(new HibernatePersistentSortedSetConverter(xstream.getMapper()));

        return xstream;
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
