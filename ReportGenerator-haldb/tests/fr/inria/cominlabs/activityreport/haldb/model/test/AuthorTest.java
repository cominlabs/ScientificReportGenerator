/**
 * 
 */
package fr.inria.cominlabs.activityreport.haldb.model.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.inria.cominlabs.activityreport.haldb.model.Author;

/**
 * @author william
 *
 */
public class AuthorTest {
    private static EntityManager entityManager;
    private static EntityTransaction entityTransaction;
    

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
	entityManager = Persistence.createEntityManagerFactory("ReportGenerator-haldb").createEntityManager();
	entityTransaction = entityManager.getTransaction();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
	entityManager.close();
    }

    @Before
    public void setUp() throws Exception {
	
    }
    
    @Test
    public void createAuthor() {
	//create Author instance
	Author author = new Author();
	author.setAuthorSurName("Benveniste");
	author.setAuthorForeName("Albert");
	entityTransaction.begin();
	entityManager.persist(author);
	entityTransaction.commit();
	assertNotNull("authorID should not be null ", author.getAuthorID());
        List<Author> authors = entityManager.createQuery("select a from Author a",Author.class
        	).getResultList();
        assertTrue("Author table resultSet size must be > 0", authors.size()>0);
    }

}
