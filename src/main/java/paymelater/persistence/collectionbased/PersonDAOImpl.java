package paymelater.persistence.collectionbased;

/*
 ** DO NOT CHANGE!!
 */


import paymelater.model.Person;
import paymelater.persistence.PersonDAO;

import java.util.Optional;

/**
 * {@inheritDoc}
 */
public class PersonDAOImpl extends CollectionBasedDAO<Person> implements PersonDAO {

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<paymelater.model.Person> findByEmail(String email) {
        return findAll().stream()
                .filter(p -> p.getEmail().equals(email)).findFirst();
    }
}
