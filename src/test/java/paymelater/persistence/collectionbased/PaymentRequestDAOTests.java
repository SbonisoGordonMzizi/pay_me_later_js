package paymelater.persistence.collectionbased;

/*
 ** DO NOT CHANGE!!
 */

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import paymelater.TestScenario;
import paymelater.model.PaymentRequest;
import paymelater.model.Person;
import paymelater.persistence.ExpenseDAO;
import paymelater.persistence.PaymentDAO;
import paymelater.persistence.PaymentRequestDAO;
import paymelater.persistence.PersonDAO;
import paymelater.server.ServiceRegistry;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

public class PaymentRequestDAOTests {
    private static PaymentRequestDAO paymentRequestDAO;
    private TestScenario scenario;

    @BeforeEach
    public void setup() {
        scenario = TestScenario.newScenario();
        ServiceRegistry.configure(PersonDAO.class, scenario.personDAO());
        ServiceRegistry.configure(ExpenseDAO.class, scenario.expenseDAO());
        ServiceRegistry.configure(PaymentRequestDAO.class, scenario.paymentRequestDAO());
        ServiceRegistry.configure(PaymentDAO.class, scenario.paymentDAO());
        scenario.generateSomeData();
        paymentRequestDAO = scenario.paymentRequestDAO();
    }

    @Test
    public void paymentRequestSentForPerson() {
        PaymentRequest paymentRequest = scenario.newPaymentRequest();
        Person person = paymentRequest.getPersonRequestingPayment();
        Collection<PaymentRequest> paymentRequests = paymentRequestDAO.findPaymentRequestsSent(person);
        assertThat(paymentRequests).contains(paymentRequest);
    }

    @Test
    public void paymentRequestReceivedForPerson() {
        PaymentRequest paymentRequest = scenario.newPaymentRequest();
        Person person = paymentRequest.getPersonWhoShouldPayBack();
        Collection<PaymentRequest> paymentRequests = paymentRequestDAO.findPaymentRequestsReceived(person);
        assertThat(paymentRequests).contains(paymentRequest);
    }
}
