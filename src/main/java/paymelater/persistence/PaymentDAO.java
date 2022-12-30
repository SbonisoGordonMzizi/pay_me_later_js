package paymelater.persistence;

import paymelater.model.Payment;
import paymelater.model.Person;

import java.util.Collection;

public interface PaymentDAO extends DAO<Payment> {
    Collection<Payment> findByPerson(Person person);
}
