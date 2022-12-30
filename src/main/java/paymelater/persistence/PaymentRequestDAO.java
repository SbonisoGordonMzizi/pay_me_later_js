package paymelater.persistence;

import paymelater.model.PaymentRequest;
import paymelater.model.Person;

import java.util.Collection;

public interface PaymentRequestDAO extends DAO<PaymentRequest> {
    Collection<PaymentRequest> findPaymentRequestsSent(Person person);

    Collection<PaymentRequest> findPaymentRequestsReceived(Person person);
}
