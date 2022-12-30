package paymelater.api.dto;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

public class NewPaymentDTO {
    private final Integer expenseId;
    private final Integer paymentRequestId;
    private final Integer payingPersonId;

    public NewPaymentDTO(Integer expenseId, Integer paymentRequestId, Integer payingPersonId) {
        this.expenseId = expenseId;
        this.paymentRequestId = paymentRequestId;
        this.payingPersonId = payingPersonId;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NewPaymentDTO that = (NewPaymentDTO) o;
        return Objects.equal(expenseId, that.expenseId) && Objects.equal(paymentRequestId, that.paymentRequestId) && Objects.equal(payingPersonId, that.payingPersonId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(expenseId, paymentRequestId, payingPersonId);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("expenseId", expenseId)
                .add("paymentRequestId", paymentRequestId)
                .add("payingPersonId", payingPersonId)
                .toString();
    }
}
