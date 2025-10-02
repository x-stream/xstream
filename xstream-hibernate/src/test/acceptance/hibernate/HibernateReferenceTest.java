/*
 * Copyright (C) 2011, 2012, 2025 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 25. March 2011 by Jaime Metcher
 */

package acceptance.hibernate;

import acceptance.hibernate.reference.BaseDomainObject;
import acceptance.hibernate.reference.Department;
import acceptance.hibernate.reference.Division;
import acceptance.hibernate.reference.Person;
import acceptance.hibernate.reference.Site;

import java.lang.reflect.Method;

import org.hibernate.Session;
import org.hibernate.proxy.HibernateProxy;


/**
 * @author Jaime Metcher
 * @author J&ouml;rg Schaible
 */
public class HibernateReferenceTest extends AbstractHibernateAcceptanceTest {

    private final static Method persist;
    private final static Method remove;
    static {
        Method method;
        try {
            method = Session.class.getMethod("save", new Class[]{Object.class});
        } catch (Exception e1) {
            try {
                // Available since Hibernate 6.0
                method = Session.class.getMethod("persist", new Class[]{Object.class});
            } catch (Exception e2) {
                throw new ExceptionInInitializerError("Cannot find method in "
                    + Session.class.getName()
                    + " to persist/save objects");
            }
        }
        persist = method;
        try {
            method = Session.class.getMethod("delete", new Class[]{Object.class});
        } catch (Exception e1) {
            try {
                // Available since Hibernate 6.0
                method = Session.class.getMethod("remove", new Class[]{Object.class});
            } catch (Exception e2) {
                throw new ExceptionInInitializerError("Cannot find method in "
                    + Session.class.getName()
                    + " to remove/delete objects");
            }
        }
        remove = method;
    }

    protected void setUp() throws Exception {
        super.setUp();
        // don't write the primary keys in this test
        xstream.omitField(BaseDomainObject.class, "id");
        xstream.alias("department", Department.class);
        xstream.alias("division", Division.class);
        xstream.alias("person", Person.class);
        xstream.alias("site", Site.class);
    }

    protected void tearDown() {
        try {
            final Session session = getSessionFactory().getCurrentSession();
            session.beginTransaction();
            final Division div = (Division)session.createQuery("from Division").uniqueResult();
            remove.invoke(session, new Object[]{div});
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testObjectGraphWithReferences() throws Exception {
        final Division memory = setupNonpersistentDivision();
        final Division persisted = setupPersistentDivision();

        final String expectedXml = xstream.toXML(memory);
        final String persistedXml = xstream.toXML(persisted);

        final Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        final Division loaded = (Division)session.createQuery("from Division").uniqueResult();
        final String loadedXml = xstream.toXML(loaded);
        session.flush();
        session.getTransaction().commit();
        assertEquals(expectedXml, persistedXml);
        assertEquals(expectedXml, loadedXml);
    }

    public void testLazyProxyWithReferences() throws Exception {
        setupPersistentDivision();

        final Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        final Division loaded = (Division)session.createQuery("from Division").uniqueResult();
        final Department dept = (Department)loaded.getDepartments().iterator().next();
        final Person person = (Person)dept.getPeople().iterator().next();
        final Site site = person.getSite();
        assertTrue(HibernateProxy.class.isAssignableFrom(site.getClass()));
        final String loadedXml = xstream.toXML(site);
        session.flush();
        session.getTransaction().commit();

        final String expectedXml = ""
            + "<site>\n"
            + "  <name>Site1</name>\n"
            + "  <people>\n"
            + "    <person>\n"
            + "      <name>Tom</name>\n"
            + "      <department>\n"
            + "        <name>Dep1</name>\n"
            + "        <division>\n"
            + "          <name>Div1</name>\n"
            + "          <departments>\n"
            + "            <department reference=\"../../..\"/>\n"
            + "          </departments>\n"
            + "        </division>\n"
            + "        <people>\n"
            + "          <person reference=\"../../..\"/>\n"
            + "        </people>\n"
            + "      </department>\n"
            + "      <site reference=\"../../..\"/>\n"
            + "    </person>\n"
            + "  </people>\n"
            + "</site>";
        assertEquals(expectedXml, loadedXml);
    }

    /**
     * Create the object within a Hibernate session and persist it.
     */
    private Division setupPersistentDivision() throws Exception {
        final Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        final Division div = new Division("Div1");
        final Department dep = new Department("Dep1", div);
        final Site site = new Site("Site1");
        /* This save is necessitated by the fact that Hibernate's transitive persistence is depth-first and does not do
         * a full graph analysis. Therefore it would be possible for Hibernate to try to save the person record before
         * the site record, which would throw an error if the person.site FK is non-nullable. */
        persist.invoke(session, new Object[]{site});
        new Person("Tom", dep, site);
        persist.invoke(session, new Object[]{div});
        session.flush();
        session.getTransaction().commit();
        return div;
    }

    /**
     * Create the object graph in-memory without Hibernate.
     */
    private Division setupNonpersistentDivision() {
        final Division div = new Division("Div1");
        final Department dep = new Department("Dep1", div);
        final Site site = new Site("Site1");
        new Person("Tom", dep, site);
        return div;
    }

    /**
     * Load object graph with Hibernate from the database.
     */
    private Division getPersistentDivision() {
        final Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        final Division div = (Division)session.createQuery("from Division").uniqueResult();
        session.getTransaction().commit();
        return div;
    }
}
