package paymelater.api.dto;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

public class NewExpenseDTO {
    private final Integer personId;
    private final String date;
    private final String description;
    private final long amount;

    public NewExpenseDTO(Integer personId, String description, String date, long amount) {
        this.personId = personId;
        this.date = date;
        this.description = description;
        this.amount = amount;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NewExpenseDTO that = (NewExpenseDTO) o;
        return amount == that.amount && Objects.equal(personId, that.personId) && Objects.equal(date, that.date) && Objects.equal(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(personId, date, description, amount);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("personId", personId)
                .add("date", date)
                .add("description", description)
                .add("amount", amount)
                .toString();
    }
}
