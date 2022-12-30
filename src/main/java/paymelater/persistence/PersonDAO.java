package paymelater.persistence;

import paymelater.model.Person;

import java.util.Optional;

/**
 * Data Access Object for Person objects
 */
public interface PersonDAO extends DAO<Person> {

    /**
     * Find a person using their email address
     * @param email the email address of the person
     * @return Person wrapped in an Optional, will be empty if the person was not found.
     */
    Optional<Person> findByEmail(String email);
}
