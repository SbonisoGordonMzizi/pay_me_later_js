package paymelater.model;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import paymelater.WeShareException;

import javax.money.MonetaryAmount;
import java.time.LocalDate;

import static paymelater.model.DateHelper.TODAY;

public class PaymentRequest extends PersistentModel<PaymentRequest> {
    private final Expense expense;
    private final Person personWhoShouldPayBack;
    private final MonetaryAmount amountToPay;
    private final LocalDate dueDate;
    private boolean isPaid;
    private Payment payment;

    public PaymentRequest(Expense expense, Person personWhoShouldPayBack, MonetaryAmount amountToPay, LocalDate dueDate) {
        checkPaymentRequestAmount(expense, amountToPay);
        checkDueDate(expense, dueDate);
        checkPaymentRequestToSelf(expense, personWhoShouldPayBack);
        this.expense = expense;
        this.personWhoShouldPayBack = personWhoShouldPayBack;
        this.amountToPay = amountToPay;
        this.dueDate = dueDate;
        isPaid = false;
    }

    private void checkPaymentRequestAmount(Expense expense, MonetaryAmount amountToPay) {
        var maxAmountThatCanBeRequested = expense.getAmount()
                .subtract(expense.totalAmountOfPaymentsRequested());
        if (amountToPay.isGreaterThan(maxAmountThatCanBeRequested))
            throw new WeShareException("Total requested amount is more than the expense amount");
    }

    private void checkDueDate(Expense expense, LocalDate dueDate) throws WeShareException {
        if (dueDate.isBefore(expense.getDate()))
            throw new WeShareException("Payment request cannot be due before the expense was incurred");
    }

    private void checkPaymentRequestToSelf(Expense expense, Person personWhoShouldPayBack) {
        if (expense.getPerson().equals(personWhoShouldPayBack))
            throw new WeShareException("You cannot request payment from yourself");
    }

    public int daysLeftToPay() {
        return TODAY.until(this.dueDate).getDays();
    }

    public Payment pay(Person personPaying, LocalDate paymentDate) {
        checkPersonPaying(personPaying);
        checkPaymentDate(paymentDate);
        this.isPaid = true;
        this.payment = new Payment(this, personPaying, paymentDate);
        return this.payment;
    }

    public MonetaryAmount getAmountToPay() {
        return amountToPay;
    }

    public Integer getExpenseId() {
        return this.expense.getId();
    }

    public Person getPersonRequestingPayment() {
        return this.expense.getPerson();
    }

    public String getDescription() {
        return this.expense.getDescription();
    }

    public boolean isPaid() {
        return this.isPaid;
    }

    public Person getPersonWhoShouldPayBack() {
        return personWhoShouldPayBack;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public Expense getExpense() {
        return expense;
    }

    public Payment getPayment() {
        return payment;
    }

    private void checkPersonPaying(Person person) {
        if (!this.personWhoShouldPayBack.equals(person))
            throw new WeShareException("Wrong person is trying to pay the payment request");
    }

    private void checkPaymentDate(LocalDate date) {
        if (date.isAfter(TODAY))
            throw new WeShareException("Cannot make a payment in the future");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentRequest that = (PaymentRequest) o;
        return Objects.equal(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("expense", expense)
                .add("personWhoShouldPayBack", personWhoShouldPayBack)
                .add("amountToPay", amountToPay)
                .add("dueDate", dueDate)
                .add("id", id)
                .toString();
    }
}
