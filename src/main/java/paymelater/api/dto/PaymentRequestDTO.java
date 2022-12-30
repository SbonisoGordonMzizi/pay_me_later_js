package paymelater.api.dto;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import paymelater.model.PaymentRequest;

import static paymelater.model.DateHelper.DD_MM_YYYY;

public class PaymentRequestDTO {
    private final Integer id;
    private final Integer expenseId;
    private final Integer fromPersonId;
    private final Integer toPersonId;
    private final String date;
    private final long amount;
    private final boolean isPaid;

    public PaymentRequestDTO(Integer id, Integer expenseId, Integer fromPersonId, Integer toPersonId,
                              String date, long amount, boolean isPaid) {
        this.id = id;
        this.expenseId = expenseId;
        this.fromPersonId = fromPersonId;
        this.toPersonId = toPersonId;
        this.date = date;
        this.amount = amount;
        this.isPaid = isPaid;
    }

    public static PaymentRequestDTO fromPaymentRequest(PaymentRequest paymentRequest) {
        return new PaymentRequestDTO(
                paymentRequest.getId(),
                paymentRequest.getExpenseId(),
                paymentRequest.getPersonRequestingPayment().getId(),
                paymentRequest.getPersonWhoShouldPayBack().getId(),
                DD_MM_YYYY.format(paymentRequest.getDueDate()),
                paymentRequest.getAmountToPay().getNumber().longValue(),
                paymentRequest.isPaid());
    }

    public Integer getId() {
        return id;
    }

    public Integer getExpenseId() {
        return expenseId;
    }

    public Integer getFromPersonId() {
        return fromPersonId;
    }

    public Integer getToPersonId() {
        return toPersonId;
    }

    public String getDate() {
        return date;
    }

    public long getAmount() {
        return amount;
    }

    public boolean isPaid() {
        return isPaid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentRequestDTO that = (PaymentRequestDTO) o;
        return amount == that.amount && isPaid == that.isPaid && Objects.equal(id, that.id) && Objects.equal(expenseId, that.expenseId) && Objects.equal(fromPersonId, that.fromPersonId) && Objects.equal(toPersonId, that.toPersonId) && Objects.equal(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, expenseId, fromPersonId, toPersonId, date, amount, isPaid);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("expenseId", expenseId)
                .add("fromPersonId", fromPersonId)
                .add("toPersonId", toPersonId)
                .add("date", date)
                .add("amount", amount)
                .add("isPaid", isPaid)
                .toString();
    }
}
