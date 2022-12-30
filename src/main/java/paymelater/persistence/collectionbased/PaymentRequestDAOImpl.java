package paymelater.persistence.collectionbased;

import paymelater.model.PaymentRequest;
import paymelater.model.Person;
import paymelater.persistence.PaymentRequestDAO;

import java.util.Collection;
import java.util.stream.Collectors;

public class PaymentRequestDAOImpl extends CollectionBasedDAO<PaymentRequest> implements PaymentRequestDAO {
    @Override
    public Collection<PaymentRequest> findPaymentRequestsSent(Person person) {
        return findAll().stream()
                .filter(pr -> pr.getPersonRequestingPayment().equals(person))
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public Collection<PaymentRequest> findPaymentRequestsReceived(Person person) {
        return findAll().stream()
                .filter(pr -> pr.getPersonWhoShouldPayBack().equals(person))
                .collect(Collectors.toUnmodifiableList());
    }
}
