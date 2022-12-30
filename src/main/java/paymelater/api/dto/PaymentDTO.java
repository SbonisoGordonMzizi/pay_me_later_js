package paymelater.api.dto;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import paymelater.model.Payment;

import static paymelater.model.DateHelper.DD_MM_YYYY;

public class PaymentDTO {
    private final Integer id;
    private final Integer expenseId;
    private final Integer paymentRequestId;
    private final Integer payingPersonId;
    private final Long amount;
    private final String date;

    private PaymentDTO(Integer id, Integer expenseId, Integer paymentRequestId, Integer payingPersonId, long amount, String date) {
        this.id = id;
        this.expenseId = expenseId;
        this.paymentRequestId = paymentRequestId;
        this.payingPersonId = payingPersonId;
        this.amount = amount;
        this.date = date;
    }

    public static PaymentDTO fromPayment(Payment payment) {
        return new PaymentDTO(
                payment.getId(),
                payment.getExpenseForPersonPaying().getId(),
                payment.getPaymentRequest().getId(),
                payment.getPersonPaying().getId(),
                payment.getAmountPaid().getNumber().longValue(),
                DD_MM_YYYY.format(payment.getPaymentDate())
        );
    }

    public Integer getId() {
        return id;
    }

    public Integer getExpenseId() {
        return expenseId;
    }

    public Integer getPaymentRequestId() {
        return paymentRequestId;
    }

    public Integer getPayingPersonId() {
        return payingPersonId;
    }

    public Long getAmount() {
        return amount;
    }

    public String getDate() {
        return date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentDTO that = (PaymentDTO) o;
        return Objects.equal(id, that.id) && Objects.equal(expenseId, that.expenseId) && Objects.equal(paymentRequestId, that.paymentRequestId) && Objects.equal(payingPersonId, that.payingPersonId) && Objects.equal(amount, that.amount) && Objects.equal(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, expenseId, paymentRequestId, payingPersonId, amount, date);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("expenseId", expenseId)
                .add("paymentRequestId", paymentRequestId)
                .add("payingPersonId", payingPersonId)
                .add("amount", amount)
                .add("date", date)
                .toString();
    }
}
