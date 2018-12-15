/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Inc.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.hibernate.tutorial.em;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class FamilyTest {
    private static EntityManagerFactory entityManagerFactory;

    @BeforeClass
    public static void setUp() throws Exception {
        entityManagerFactory = Persistence.createEntityManagerFactory("org.hibernate.tutorial.jpa");
        EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        Family family = getFamily("Family for the Knopfs");
        em.persist(family);
        createFamilyMembers(em, family);
        em.persist(family);
        em.getTransaction().commit();
        em.close();
    }

    private static void createFamilyMembers(EntityManager em, Family family) {
        for (int i = 0; i < 40; i++) {
            Person person = new Person();
            person.setFirstName("Jim_" + i);
            person.setLastName("Knopf_" + i);
            person.setFamily(family);
            // now persists the family person relationship
            family.getMembers().add(person);
            em.persist(person);
        }
    }

    @AfterClass
    public static void tearDown() throws Exception {
        entityManagerFactory.close();
    }

    private static Family getFamily(String description) {
        Family family = new Family();
        family.setDescription(description);
        return family;
    }

    @Test
    public void checkAvailablePeople() {

        // now lets check the database and see if the created entries are there
        // create a fresh, new EntityManager
        EntityManager em = entityManagerFactory.createEntityManager();

        // Perform a simple query for all the Message entities
        Query q = em.createQuery("select m from Person m");
        List resultList = q.getResultList();
        // We should have 40 Persons in the database
        assertEquals(40, q.getResultList().size());
        em.close();
    }

    @Test
    public void checkFamily() {
        EntityManager em = entityManagerFactory.createEntityManager();
        // Go through each of the entities and print out each of their
        // messages, as well as the date on which it was created
        Query q = em.createQuery("select f from Family f");

        // We should have one family with 40 persons
//        assertEquals(1, q.getResultList().size());
        Family family = (Family) q.getSingleResult();
        assertEquals(40, family.getMembers().size());
        em.close();
    }

    @Test(expected = javax.persistence.NoResultException.class)
    public void deletePerson() {
        EntityManager em = entityManagerFactory.createEntityManager();
        try {
            // Begin a new local transaction so that we can persist a new entity
            em.getTransaction().begin();
            Query q = em
                    .createQuery("SELECT p FROM Person p WHERE p.firstName = :firstName AND p.lastName = :lastName");
            q.setParameter("firstName", "Jim_1");
            q.setParameter("lastName", "Knopf_!");
            Person user = (Person) q.getSingleResult();
        } finally {
            em.close();
        }
    }
}
