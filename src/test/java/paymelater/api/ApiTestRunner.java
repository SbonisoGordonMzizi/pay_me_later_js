package paymelater.api;

import kong.unirest.Unirest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import paymelater.TestScenario;
import paymelater.persistence.ExpenseDAO;
import paymelater.persistence.PaymentDAO;
import paymelater.persistence.PaymentRequestDAO;
import paymelater.persistence.PersonDAO;
import paymelater.server.ServiceRegistry;
import paymelater.server.WeShareServer;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ApiTestRunner {
    protected TestScenario scenario;

    private static WeShareServer server;

    @AfterAll
    public static void stopServer() {
        server.stop();
        Unirest.shutDown();
    }

    @BeforeAll
    void startServer() {
        server = new WeShareServer();
        server.start(0);
        Unirest.config().defaultBaseUrl("http://localhost:" + server.port());
        scenario = TestScenario.newScenario();
        scenario.setPersonDAO(ServiceRegistry.lookup(PersonDAO.class));
        scenario.setExpenseDAO(ServiceRegistry.lookup(ExpenseDAO.class));
        scenario.setPaymentRequestDAO(ServiceRegistry.lookup(PaymentRequestDAO.class));
        scenario.setPaymentDAO(ServiceRegistry.lookup(PaymentDAO.class));
        scenario.generateSomeData();
    }

}
