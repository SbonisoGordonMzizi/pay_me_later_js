package paymelater.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import paymelater.TestScenario;
import paymelater.api.dto.ExpenseDTO;
import paymelater.api.dto.NewExpenseDTO;
import paymelater.api.dto.NewPaymentDTO;
import paymelater.model.Expense;
import paymelater.model.Payment;
import paymelater.model.PaymentRequest;
import paymelater.model.Person;
import paymelater.persistence.ExpenseDAO;
import paymelater.persistence.PaymentDAO;
import paymelater.persistence.PaymentRequestDAO;
import paymelater.persistence.PersonDAO;
import paymelater.server.ServiceRegistry;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static paymelater.model.DateHelper.*;

public class WeShareServiceTests {

    private TestScenario scenario;

    @BeforeEach
    public void setup() {
        scenario = TestScenario.newScenario();
        ServiceRegistry.configure(PersonDAO.class, scenario.personDAO());
        ServiceRegistry.configure(ExpenseDAO.class, scenario.expenseDAO());
        ServiceRegistry.configure(PaymentRequestDAO.class, scenario.paymentRequestDAO());
        ServiceRegistry.configure(PaymentDAO.class, scenario.paymentDAO());
        scenario.generateSomeData();
    }

    @Test
    public void allPeople() {
        Collection<Person> people = scenario.somePeople();
        assertThat(WeShareService.findAllPeople()).containsAll(people);
    }

    @Test
    public void onePerson() {
        Person expected = scenario.somePerson();
        Person actual = WeShareService.getPerson(expected.getId()).orElseThrow();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void personNotFound() {
        int id = scenario.getUnusedPersonId();
        assertThat(WeShareService.getPerson(id)).isEmpty();
    }

    @Test
    public void findPersonByEmail() {
        Person expected = scenario.somePerson();
        Person actual = WeShareService.findPersonByEmailOrCreate(expected.getEmail());
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @Disabled
    public void findPersonByEmailNotFound() {
        String email = TestScenario.generatedEmail(scenario.getUnusedPersonId());
        assertThat(WeShareService.findPersonByEmailOrCreate(email));
    }

    @Test
    public void savePerson() {
        Person p = scenario.unsavedPerson();
        WeShareService.savePerson(p);
        assertThat(p.getId()).isNotNull();
    }

    @Test
    public void allExpenses() {
        Collection<Expense> expenses = scenario.someExpenses();
        assertThat(WeShareService.findAllExpenses()).containsAll(expenses);
    }

    @Test
    public void oneExpense() {
        Expense expected = scenario.someExpense();
        Expense actual = WeShareService.getExpense(expected.getId()).orElseThrow();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void expenseNotFound() {
        int unknownExpenseId = scenario.getUnusedExpenseId();
        assertThat(WeShareService.getExpense(unknownExpenseId)).isEmpty();
    }

    @Test
    public void expensesForPerson() {
        Person person = scenario.somePerson();
        Collection<Expense> expenses = WeShareService.findExpensesForPerson(person.getId());
        assertThat(expenses).allMatch(e -> e.getPerson().equals(person));
    }

    @Test
    public void noExpensesForPerson() {
        Person person = scenario.newPerson();
        Collection<Expense> expenses = WeShareService.findExpensesForPerson(person.getId());
        assertThat(expenses).isEmpty();
    }

    @Test
    public void newExpenseWithExpense() {
        Person person = scenario.somePerson();
        Expense expense = new Expense(person, TestScenario.generatedDescription(person), TestScenario.randomAmount(), TODAY);
        WeShareService.saveExpense(expense);
        assertThat(expense.getId()).isNotNull();
    }

    @Test
    public void createNewExpense() {
        Person person = scenario.somePerson();
        NewExpenseDTO unsaved = new NewExpenseDTO(person.getId(), TestScenario.generatedDescription(person),
                TODAY.format(DD_MM_YYYY), TestScenario.randomAmount().getNumber().longValue() );
        ExpenseDTO saved = WeShareService.createNewExpense(unsaved);
        assertThat(saved.getExpenseId()).isNotNull();
    }

    @Test
    public void newExpenseWithBadPerson() {
        Person person = scenario.unsavedPerson();
        Expense expense = new Expense(person, TestScenario.generatedDescription(person), TestScenario.randomAmount(), TODAY);
        assertThatThrownBy(() -> WeShareService.saveExpense(expense))
                .hasMessageContaining("Person not found");
    }

    @Test
    public void newPaymentRequest() {
        PaymentRequest paymentRequest = scenario.newPaymentRequest();

        WeShareService.savePaymentRequest(paymentRequest);
        assertThat(paymentRequest.getId()).isNotNull();

        Expense updatedExpense = WeShareService.getExpense(paymentRequest.getExpenseId()).orElseThrow();
        assertThat(updatedExpense.listOfPaymentRequests()).contains(paymentRequest);
    }

    @Test
    public void onePaymentRequest() {
        PaymentRequest expected = scenario.newPaymentRequest();
        PaymentRequest actual = WeShareService.getPaymentRequest(expected.getId()).orElseThrow();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void newPaymentRequestWithBadExpense() {
        Expense expense = scenario.randomUnsavedExpense();
        Person person = scenario.personThatIsNot(expense.getPerson());
        PaymentRequest paymentRequest = expense.requestPayment(person, expense.getAmount(), TOMORROW);
        assertThatThrownBy(() -> WeShareService.savePaymentRequest(paymentRequest))
                .hasMessageContaining("Expense not found");
    }

    @Test
    public void newPaymentRequestWithBadReceiver() {
        Expense expense = scenario.newExpense();
        Person personToPay = scenario.unsavedPerson();
        PaymentRequest paymentRequest = expense.requestPayment(personToPay, expense.getAmount(), TOMORROW);
        assertThatThrownBy(() -> WeShareService.savePaymentRequest(paymentRequest))
                .hasMessageContaining("Person not found");
    }

    @Test
    public void paymentRequestsSent() {
        PaymentRequest paymentRequest = scenario.newPaymentRequest();
        Person person = paymentRequest.getPersonRequestingPayment();
        Collection<PaymentRequest> paymentRequests = WeShareService.findPaymentRequestsSentBy(person.getId());
        assertThat(paymentRequests).contains(paymentRequest);
    }

    @Test
    public void paymentRequestsSentHasNone() {
        Person person = scenario.newPerson();
        Collection<PaymentRequest> paymentRequests = WeShareService.findPaymentRequestsSentBy(person.getId());
        assertThat(paymentRequests).isEmpty();
    }

    @Test
    public void paymentRequestsReceived() {
        PaymentRequest paymentRequest = scenario.newPaymentRequest();
        Person person = paymentRequest.getPersonWhoShouldPayBack();

        Collection<PaymentRequest> paymentRequests = WeShareService.findPaymentRequestsReceivedBy(person.getId());
        assertThat(paymentRequests).contains(paymentRequest);
    }

    @Test
    public void paymentRequestsReceivedHasNone() {
        Person person = scenario.newPerson();
        Collection<PaymentRequest> paymentRequests = WeShareService.findPaymentRequestsReceivedBy(person.getId());
        assertThat(paymentRequests).isEmpty();
    }

    @Test
    public void recallPaymentRequest() {
        PaymentRequest paymentRequest = scenario.newPaymentRequest();
        WeShareService.recallPaymentRequest(paymentRequest.getId());
        assertThat(WeShareService.getPaymentRequest(paymentRequest.getId())).isEmpty();
        Expense e = WeShareService.getExpense(paymentRequest.getExpenseId()).orElseThrow();
        assertThat(e.listOfPaymentRequests()).doesNotContain(paymentRequest);
    }

    @Test
    public void recallPaidPaymentRequest() {
        PaymentRequest paymentRequest = scenario.newPaymentRequest();
        paymentRequest.pay(paymentRequest.getPersonWhoShouldPayBack(), TODAY);
        WeShareService.savePaymentRequest(paymentRequest);
        assertThatThrownBy(() -> WeShareService.recallPaymentRequest(paymentRequest.getId()))
                .hasMessageContaining("Payment Request has already been paid");
    }

    @Test
    public void payPaymentRequest() {
        PaymentRequest paymentRequest = scenario.newPaymentRequest();
        Person person = paymentRequest.getPersonWhoShouldPayBack();
        NewPaymentDTO newPaymentDTO = new NewPaymentDTO(paymentRequest.getExpenseId(),
                paymentRequest.getId(), paymentRequest.getPersonWhoShouldPayBack().getId());
        Payment payment = WeShareService.payPaymentRequest(newPaymentDTO);

        Collection<Payment> payments = WeShareService.findAllPaymentsMadeBy(person.getId());
        assertThat(payments).contains(payment);

        Collection<Expense> expenses = WeShareService.findExpensesForPerson(person.getId());
        assertThat(expenses).contains(payment.getExpenseForPersonPaying());
    }
}
