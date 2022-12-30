package paymelater.model;

/*
 ** DO NOT CHANGE!!
 */

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import paymelater.WeShareException;

import javax.money.MonetaryAmount;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static paymelater.model.DateHelper.TODAY;
import static paymelater.model.DateHelper.TOMORROW;
import static paymelater.model.MoneyHelper.ZERO_RANDS;
import static paymelater.model.MoneyHelper.amountOf;

public class PaymentTests {
    private final LocalDate IN_FIVE_DAYS = TODAY.plusDays(5);
    private final MonetaryAmount R100 = amountOf(100);
    private final Person personWhoPaidForSomeone = new Person("ipaidforyou@wethinkcode.co.za");
    private final Person personWhoShouldPayBack = new Person("ioweyou@wethinkcode.co.za");
    private Expense expense;

    @BeforeEach
    public void createPaymentRequest() {
        expense = new Expense(personWhoPaidForSomeone, "Airtime", R100, TODAY.minusDays(1));
    }

    @Test
    public void payThePaymentRequest() {
        PaymentRequest paymentRequest = expense.requestPayment(personWhoShouldPayBack, R100, TODAY);
        Payment payment = paymentRequest.pay(personWhoShouldPayBack, TODAY);

        // check the payment details
        assertThat(payment.getPersonPaying()).isEqualTo(personWhoShouldPayBack);
        assertThat(payment.getAmountPaid()).isEqualTo(paymentRequest.getAmountToPay());
        assertThat(paymentRequest.isPaid()).isTrue();

        // should have an expense for person paying
        Expense expenseForPersonPaying = payment.getExpenseForPersonPaying();
        assertThat(expenseForPersonPaying).isNotNull();
        assertThat(expenseForPersonPaying.getPerson()).isEqualTo(personWhoShouldPayBack);
        assertThat(expenseForPersonPaying.getDescription()).isEqualTo(paymentRequest.getDescription());
        assertThat(expenseForPersonPaying.getAmount()).isEqualTo(paymentRequest.getAmountToPay());
        assertThat(expenseForPersonPaying.getDate()).isEqualTo(payment.getPaymentDate());

        // check original expense totals
        assertThat(expense.getAmount()).isEqualTo(R100);
        assertThat(expense.totalAmountForPaymentsReceived()).isEqualTo(R100);
        assertThat(expense.amountLessPaymentsReceived()).isEqualTo(ZERO_RANDS);
        assertThat(expense.isFullyPaidByOthers()).isTrue();
    }

    @Test
    public void wrongPersonPaysTheRequest() {
        PaymentRequest paymentRequest = expense.requestPayment(personWhoShouldPayBack, R100, TODAY);
        Person anotherPerson = new Person("another@wethinkcode.co.za");
        assertThatThrownBy(() ->
                paymentRequest.pay(anotherPerson, TODAY))
                .isInstanceOf(WeShareException.class)
                .hasMessageContaining("Wrong person is trying to pay the payment request");
    }

    @Test
    public void cannotPayInTheFuture() {
        PaymentRequest paymentRequest = expense.requestPayment(personWhoShouldPayBack, R100, IN_FIVE_DAYS);

        assertThatThrownBy(() -> paymentRequest.pay(personWhoShouldPayBack, TOMORROW)).isInstanceOf(WeShareException.class).hasMessageContaining("Cannot make a payment in the future");
    }
}
