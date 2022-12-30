package paymelater;

import paymelater.api.WeShareService;
import paymelater.api.dto.NewPaymentDTO;
import paymelater.model.Expense;
import paymelater.model.Payment;
import paymelater.model.PaymentRequest;
import paymelater.model.Person;
import paymelater.persistence.ExpenseDAO;
import paymelater.persistence.PaymentDAO;
import paymelater.persistence.PaymentRequestDAO;
import paymelater.persistence.PersonDAO;
import paymelater.persistence.collectionbased.ExpenseDAOImpl;
import paymelater.persistence.collectionbased.PaymentDAOImpl;
import paymelater.persistence.collectionbased.PaymentRequestDAOImpl;
import paymelater.persistence.collectionbased.PersonDAOImpl;

import javax.money.MonetaryAmount;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.ThreadLocalRandom;

import static paymelater.model.DateHelper.*;
import static paymelater.model.MoneyHelper.amountOf;

public class TestScenario {
    private PersonDAO personDAO;
    private ExpenseDAO expenseDAO;
    private PaymentRequestDAO paymentRequestDAO;
    private PaymentDAO paymentDAO;
    private TestScenario() {
        personDAO = new PersonDAOImpl();
        expenseDAO = new ExpenseDAOImpl();
        paymentRequestDAO = new PaymentRequestDAOImpl();
        paymentDAO = new PaymentDAOImpl();
    }

    public void setPersonDAO(PersonDAO personDAO) {
        this.personDAO = personDAO;
    }

    public void setExpenseDAO(ExpenseDAO expenseDAO) {
        this.expenseDAO = expenseDAO;
    }

    public void setPaymentRequestDAO(PaymentRequestDAO paymentRequestDAO) {
        this.paymentRequestDAO = paymentRequestDAO;
    }

    public void setPaymentDAO(PaymentDAO paymentDAO) {
        this.paymentDAO = paymentDAO;
    }

    public static TestScenario newScenario() {
        return new TestScenario();
    }

    public Person newPerson() {
        Person person = new Person(generatedEmail(getUnusedPersonId()));
        this.personDAO.save(person);
        return person;
    }

    public static String generatedEmail(int i) {
        return "student" + i + "@wethinkcode.co.za";
    }

    public static String generatedDescription(Person p) {
        return p.getName() + " Expense";
    }

    public static MonetaryAmount randomAmount() {
        long amount = ThreadLocalRandom.current().nextLong(100L, 1000L);
        return amountOf(amount);
    }

    public Collection<Person> somePeople() {
        return personDAO.findAll();
    }

    public Collection<Expense> someExpenses() {
        return expenseDAO.findAll();
    }

    public Expense someExpense() {
        return expenseDAO.findAll().stream()
                .skip(ThreadLocalRandom.current().nextInt(expenseDAO.count()))
                .findFirst()
                .orElseThrow();
    }

    public Person somePerson() {
        return personDAO.findAll().stream()
                .findAny()
                .orElseThrow();
    }

    public PaymentRequest somePaymentRequest() {
        return paymentRequestDAO.findAll().stream()
                .skip(ThreadLocalRandom.current().nextInt(paymentDAO.count()))
                .findAny()
                .orElseThrow();
    }

    public PaymentRequest someUnpaidPaymentRequest() {
        return paymentRequestDAO.findAll().stream()
                .filter(paymentRequest -> !paymentRequest.isPaid())
                .findFirst()
                .orElseThrow();
    }
    public PaymentRequest somePaidPaymentRequest() {
        return paymentRequestDAO.findAll().stream()
                .filter(PaymentRequest::isPaid)
                .findFirst()
                .orElseThrow();
    }

    public Expense randomUnsavedExpense() {
        var person = unsavedPerson();
        var nextId = getUnusedExpenseId();
        var expense = new Expense(person, generatedDescription(person), randomAmount(), TODAY);
        expense.setId(nextId);
        return expense;
    }

    public int getUnusedExpenseId() {
        if (expenseDAO.count() == 0)
            return 1;
        return expenseDAO.findAll().stream().mapToInt(Expense::getId).max().orElseThrow() + 1;
    }

    public int getUnusedPaymentRequestId() {
        if (paymentRequestDAO.count() == 0)
            return 1;
        return paymentRequestDAO.findAll().stream().mapToInt(PaymentRequest::getId).max().orElseThrow() + 1;
    }

    public Person unsavedPerson() {
        var nextId = getUnusedPersonId();
        var person = new Person(generatedEmail(nextId));
        person.setId(nextId);
        return person;
    }

    public int getUnusedPersonId() {
        if (personDAO.count() == 0)
            return 1;
        return personDAO.findAll().stream().mapToInt(Person::getId).max().orElseThrow() + 1;
    }

    public Person personThatIsNot(Person person) {
        return personDAO.findAll().stream().filter(p -> !p.equals(person)).findFirst().orElseThrow();
    }

    public void generateSomeData() throws WeShareException {
        Person student1 = new Person("student1@wethinkcode.co.za");
        Person student2 = new Person("student2@wethinkcode.co.za");
        Person student3 = new Person("student3@wethinkcode.co.za");
        for (Person person : Arrays.asList(student1, student2, student3)) {
            WeShareService.savePerson(person);
        }

        Expense expense1 = new Expense(student1, "Lunch", amountOf(300), TODAY.minusDays(5));
        Expense expense2 = new Expense(student1, "Airtime", amountOf(100), YESTERDAY);
        Expense expense3 = new Expense(student2, "Movies", amountOf(150), TODAY.minusWeeks(1));
        Expense expense4 = new Expense(student3, "Ice cream", amountOf(50), TODAY.minusDays(3));
        for (Expense expense : Arrays.asList(expense1, expense2, expense3, expense4)) {
            WeShareService.saveExpense(expense);
        }

        PaymentRequest paymentRequest1 = expense1.requestPayment(student2, amountOf(100), YESTERDAY);
        PaymentRequest paymentRequest2 = expense1.requestPayment(student3, amountOf(100), TOMORROW);
        for (PaymentRequest paymentRequest : Arrays.asList(paymentRequest1, paymentRequest2)) {
            WeShareService.savePaymentRequest(paymentRequest);
        }

        NewPaymentDTO newPaymentDTO = new NewPaymentDTO(paymentRequest1.getExpenseId(),
                paymentRequest1.getId(), paymentRequest1.getPersonWhoShouldPayBack().getId());
        WeShareService.payPaymentRequest(newPaymentDTO);
    }

    public PersonDAO personDAO() {
        return this.personDAO;
    }

    public ExpenseDAO expenseDAO() {
        return this.expenseDAO;
    }

    public PaymentRequestDAO paymentRequestDAO() {
        return this.paymentRequestDAO;
    }

    public PaymentDAO paymentDAO() {
        return this.paymentDAO;
    }

    public PaymentRequest newPaymentRequest() {
        Person person1 = somePerson();
        Expense expense = new Expense(person1, generatedDescription(person1), randomAmount(), YESTERDAY);
        expenseDAO().save(expense);
        Person person2 = personThatIsNot(expense.getPerson());
        PaymentRequest paymentRequest = expense.requestPayment(person2, expense.getAmount(), TOMORROW);
        paymentRequestDAO().save(paymentRequest);
        return paymentRequest;
    }

    public Payment somePayment() {
        return paymentDAO.findAll().stream()
                .findFirst()
                .orElseThrow();
    }

    public Expense newExpense() {
        Person person = somePerson();
        Expense expense = new Expense(person, generatedDescription(person), randomAmount(), YESTERDAY);
        expenseDAO().save(expense);
        return expense;
    }
}
