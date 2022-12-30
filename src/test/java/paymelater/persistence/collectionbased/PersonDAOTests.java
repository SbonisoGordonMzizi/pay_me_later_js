package paymelater.persistence.collectionbased;

/*
 ** DO NOT CHANGE!!
 */

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import paymelater.model.Person;
import paymelater.persistence.PersonDAO;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class PersonDAOTests {
    private PersonDAO dao;

    @BeforeEach
    public void newRepository() {
        this.dao = new PersonDAOImpl();
        this.dao.save(new Person("student1@wethinkcode.co.za"));
    }

    @Test
    public void findPersonByEmail() {
        Optional<Person> retrievedPerson = dao.findByEmail("student1@wethinkcode.co.za");
        assertThat(retrievedPerson.isPresent()).isTrue();
    }

    @Test
    public void personNotFound() {
        Optional<Person> retrievedPerson = dao.findByEmail("student@wethinkcode.co.za");
        assertThat(retrievedPerson.isEmpty()).isTrue();
    }
}
