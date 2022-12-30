package paymelater.api.dto;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import paymelater.model.Expense;

import static paymelater.model.DateHelper.DD_MM_YYYY;

public class ExpenseDTO {
    private final Integer expenseId;
    private final Integer personId;
    private final String date;
    private final String description;
    private final long amount;
    private final long totalPaymentsRequested;
    private final long totalPaymentsReceived;
    private final long nettAmount;

    private ExpenseDTO(Integer expenseId, Integer personId, String description, String date,
                       long amount, long totalPaymentsRequested, long totalPaymentsReceived,
                       long nettAmount) {
        this.expenseId = expenseId;
        this.personId = personId;
        this.date = date;
        this.description = description;
        this.amount = amount;
        this.totalPaymentsRequested = totalPaymentsRequested;
        this.totalPaymentsReceived = totalPaymentsReceived;
        this.nettAmount = nettAmount;
    }

    public static ExpenseDTO fromExpense(Expense e) {
        return new ExpenseDTO(
                e.getId(),
                e.getPerson().getId(),
                e.getDescription(),
                DD_MM_YYYY.format(e.getDate()),
                e.getAmount().getNumber().longValue(),
                e.totalAmountOfPaymentsRequested().getNumber().longValue(),
                e.totalAmountForPaymentsReceived().getNumber().longValue(),
                e.amountLessPaymentsReceived().getNumber().longValue());
    }

    public Integer getExpenseId() {
        return expenseId;
    }

    public Integer getPersonId() {
        return personId;
    }

    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public long getAmount() {
        return amount;
    }

    public long getTotalPaymentsRequested() {
        return totalPaymentsRequested;
    }

    public long getTotalPaymentsReceived() {
        return totalPaymentsReceived;
    }

    public long getNettAmount() {
        return nettAmount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExpenseDTO that = (ExpenseDTO) o;
        return amount == that.amount && totalPaymentsRequested == that.totalPaymentsRequested && totalPaymentsReceived == that.totalPaymentsReceived && nettAmount == that.nettAmount && Objects.equal(expenseId, that.expenseId) && Objects.equal(personId, that.personId) && Objects.equal(date, that.date) && Objects.equal(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(expenseId, personId, date, description, amount, totalPaymentsRequested, totalPaymentsReceived, nettAmount);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("expenseId", expenseId)
                .add("personId", personId)
                .add("date", date)
                .add("description", description)
                .add("amount", amount)
                .add("totalPaymentsRequested", totalPaymentsRequested)
                .add("totalPaymentsReceived", totalPaymentsReceived)
                .add("nettAmount", nettAmount)
                .toString();
    }
}
