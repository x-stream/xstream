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

import org.hibernate.Session;
import org.hibernate.proxy.HibernateProxy;

import acceptance.hibernate.reference.BaseDomainObject;
import acceptance.hibernate.reference.Department;
import acceptance.hibernate.reference.Division;
import acceptance.hibernate.reference.Person;
import acceptance.hibernate.reference.Site;


/**
 * @author Jaime Metcher
 * @author J&ouml;rg Schaible
 */
public class HibernateReferenceTest extends AbstractHibernateAcceptanceTest {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // don't write the primary keys in this test
        xstream.omitField(BaseDomainObject.class, "id");
        xstream.alias("department", Department.class);
        xstream.alias("division", Division.class);
        xstream.alias("person", Person.class);
        xstream.alias("site", Site.class);
    }

    @Override
    protected void tearDown() {
        try {
            final Session session = getSessionFactory().getCurrentSession();
            session.beginTransaction();
            final Division div = (Division)session.createQuery("from Division").uniqueResult();
            session.delete(div);
            session.getTransaction().commit();
        } catch (final RuntimeException e) {
            e.printStackTrace();
        }
    }

    public void testObjectGraphWithReferences() {
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

    public void testLazyProxyWithReferences() {
        setupPersistentDivision();

        final Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        final Division loaded = (Division)session.createQuery("from Division").uniqueResult();
        final Department dept = loaded.getDepartments().iterator().next();
        final Person person = dept.getPeople().iterator().next();
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
    private Division setupPersistentDivision() {
        final Session session = getSessionFactory().getCurrentSession();
        session.beginTransaction();
        final Division div = new Division("Div1");
        final Department dep = new Department("Dep1", div);
        final Site site = new Site("Site1");
        /* This save is necessitated by the fact that Hibernate's transitive persistence is depth-first and does not do
         * a full graph analysis. Therefore it would be possible for Hibernate to try to save the person record before
         * the site record, which would throw an error if the person.site FK is non-nullable. */
        session.save(site);
        new Person("Tom", dep, site);
        session.save(div);
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
