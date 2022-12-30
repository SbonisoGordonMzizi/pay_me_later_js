package paymelater.api;

import paymelater.WeShareException;
import paymelater.api.dto.*;
import paymelater.model.Expense;
import paymelater.model.Payment;
import paymelater.model.PaymentRequest;
import paymelater.model.Person;
import paymelater.persistence.ExpenseDAO;
import paymelater.persistence.PaymentDAO;
import paymelater.persistence.PaymentRequestDAO;
import paymelater.persistence.PersonDAO;
import paymelater.server.ServiceRegistry;

import javax.money.MonetaryAmount;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import static paymelater.model.DateHelper.DD_MM_YYYY;
import static paymelater.model.DateHelper.TODAY;
import static paymelater.model.MoneyHelper.amountOf;

public class WeShareService {

    private static PersonDAO personDAO() {
        return ServiceRegistry.lookup(PersonDAO.class);
    }

    private static ExpenseDAO expenseDAO() {
        return ServiceRegistry.lookup(ExpenseDAO.class);
    }

    private static PaymentRequestDAO paymentRequestDAO() {
        return ServiceRegistry.lookup(PaymentRequestDAO.class);
    }

    private static PaymentDAO paymentDAO() {
        return ServiceRegistry.lookup(PaymentDAO.class);
    }



    public static Collection<Expense> findAllExpenses() {
        return expenseDAO().findAll();
    }

    public static Optional<Expense> getExpense(int id) {
        return expenseDAO().findById(id);
    }

    public static Collection<Expense> findExpensesForPerson(int id) {
        Person person = verifyPerson(id);
        return expenseDAO().findExpensesForPerson(person);
    }

    public static Expense saveExpense(Expense expense) {
        verifyPerson(expense.getPerson().getId());
        return expenseDAO().save(expense);
    }

    public static ExpenseDTO createNewExpense(NewExpenseDTO newExpenseDTO) {
        Person person = verifyPerson(newExpenseDTO.getPersonId());
        Expense expense = new Expense(person, newExpenseDTO.getDescription(),
                amountOf(newExpenseDTO.getAmount()), LocalDate.parse(newExpenseDTO.getDate(), DD_MM_YYYY));
        return ExpenseDTO.fromExpense(expenseDAO().save(expense));
    }

    public static PaymentRequest savePaymentRequest(PaymentRequest paymentRequest) {
        verifyExpense(paymentRequest.getExpense().getId());
        verifyPerson(paymentRequest.getPersonRequestingPayment().getId());
        verifyPerson(paymentRequest.getPersonWhoShouldPayBack().getId());
        return paymentRequestDAO().save(paymentRequest);
    }

    public static PaymentRequestDTO createNewPaymentRequest(NewPaymentRequestDTO dto) {
        Expense expense = verifyExpense(dto.getExpenseId());
        verifyPerson(dto.getFromPersonId());
        Person toPerson = verifyPerson(dto.getToPersonId());
        MonetaryAmount amount = amountOf(dto.getAmount());
        LocalDate date = LocalDate.parse(dto.getDate(), DD_MM_YYYY);
        PaymentRequest paymentRequest = expense.requestPayment(toPerson, amount, date);
        return PaymentRequestDTO.fromPaymentRequest(savePaymentRequest(paymentRequest));
    }

    private static Person verifyPerson(int id) {
        return personDAO().findById(id)
                .orElseThrow(() -> new WeShareException("Person not found: " + id));
    }

    private static Expense verifyExpense(int id) {
        return expenseDAO().findById(id)
                .orElseThrow(() -> new WeShareException("Expense not found: " + id));
    }

    private static PaymentRequest verifyPaymentRequest(int id) {
        return paymentRequestDAO().findById(id)
                .orElseThrow(() -> new WeShareException("Payment Request not found: " + id));
    }

    public static Collection<PaymentRequest> findPaymentRequestsSentBy(int id) {
        Person person = verifyPerson(id);
        return paymentRequestDAO().findPaymentRequestsSent(person);
    }

    public static Collection<PaymentRequest> findPaymentRequestsReceivedBy(int id) {
        Person person = verifyPerson(id);
        return paymentRequestDAO().findPaymentRequestsReceived(person);
    }

    public static Person savePerson(Person person) {
        return personDAO().save(person);
    }

    public static Collection<Person> findAllPeople() {
        return personDAO().findAll();
    }

    public static Optional<Person> getPerson(int id) {
        return personDAO().findById(id);
    }

    public static Collection<PaymentRequest> findAllPaymentRequests() {
        return paymentRequestDAO().findAll();
    }

    public static Optional<PaymentRequest> getPaymentRequest(int id) {
        return paymentRequestDAO().findById(id);
    }

    public static void recallPaymentRequest(int id) {
        PaymentRequest paymentRequest = verifyPaymentRequest(id);
        Expense expense = verifyExpense(paymentRequest.getExpenseId());
        if (paymentRequest.isPaid()) {
            throw new WeShareException("Payment Request has already been paid: " + id);
        }
        expense.recallPaymentRequest(paymentRequest.getId());
        expenseDAO().save(expense);
        paymentRequestDAO().delete(paymentRequest.getId());
    }

    public static Payment payPaymentRequest(NewPaymentDTO newPaymentDTO) {
        Person person = verifyPerson(newPaymentDTO.getPayingPersonId());
        PaymentRequest paymentRequest = verifyPaymentRequest(newPaymentDTO.getPaymentRequestId());
        if (paymentRequest.isPaid()) {
            throw new WeShareException("Payment Request has already been paid: " + paymentRequest.getId());
        }
        Payment payment = paymentRequest.pay(person, TODAY);
        paymentDAO().save(payment);
        paymentRequestDAO().save(paymentRequest);
        expenseDAO().save(payment.getExpenseForPersonPaying());
        return payment;
    }

    public static Collection<Payment> findAllPaymentsMadeBy(int id) {
        Person person = verifyPerson(id);
        return paymentDAO().findByPerson(person);
    }

    @Deprecated
    public static Optional<Person> findPersonByEmail(String email) {
        return personDAO().findByEmail(email);
    }

    public static Person findPersonByEmailOrCreate(String email) {
        Optional<Person> maybePerson = personDAO().findByEmail(email);
        return maybePerson.orElse(new Person(email));
    }

}
