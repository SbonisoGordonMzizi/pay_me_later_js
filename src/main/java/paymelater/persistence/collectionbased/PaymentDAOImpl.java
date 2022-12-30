package paymelater.persistence.collectionbased;

import paymelater.model.Payment;
import paymelater.model.Person;
import paymelater.persistence.PaymentDAO;

import java.util.Collection;
import java.util.stream.Collectors;

public class PaymentDAOImpl extends CollectionBasedDAO<Payment> implements PaymentDAO {

    @Override
    public Collection<Payment> findByPerson(Person person) {
        return storage.values().stream()
                .filter(p -> p.getPersonPaying().equals(person))
                .collect(Collectors.toUnmodifiableList());
    }
}
