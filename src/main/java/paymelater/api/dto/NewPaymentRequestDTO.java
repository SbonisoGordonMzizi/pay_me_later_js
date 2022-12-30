package paymelater.api.dto;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

public class NewPaymentRequestDTO {
    private final Integer expenseId;
    private final Integer fromPersonId;
    private final Integer toPersonId;
    private final String date;
    private final long amount;

    public NewPaymentRequestDTO(Integer expenseId, Integer fromPersonId, Integer toPersonId,
                                String date, long amount) {
        this.expenseId = expenseId;
        this.fromPersonId = fromPersonId;
        this.toPersonId = toPersonId;
        this.date = date;
        this.amount = amount;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NewPaymentRequestDTO that = (NewPaymentRequestDTO) o;
        return amount == that.amount && Objects.equal(expenseId, that.expenseId) && Objects.equal(fromPersonId, that.fromPersonId) && Objects.equal(toPersonId, that.toPersonId) && Objects.equal(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(expenseId, fromPersonId, toPersonId, date, amount);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("expenseId", expenseId)
                .add("fromPersonId", fromPersonId)
                .add("toPersonId", toPersonId)
                .add("date", date)
                .add("amount", amount)
                .toString();
    }
}
