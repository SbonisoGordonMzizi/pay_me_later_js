package paymelater.api.dto;

import org.junit.jupiter.api.Test;
import paymelater.model.Expense;
import paymelater.model.Person;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static paymelater.model.MoneyHelper.amountOf;

public class ExpenseDTOTests {
    Person p = new Person("student@wethinkcode.co.za");
    Expense e = new Expense(p, "Lunch", amountOf(100), LocalDate.of(2022, 1, 31));

    @Test
    public void fromExpense() {
        e.setId(1);
        ExpenseDTO dto = ExpenseDTO.fromExpense(e);
        assertThat(dto.getExpenseId()).isEqualTo(e.getId());
        assertThat(dto.getPersonId()).isEqualTo(p.getId());
        assertThat(dto.getDescription()).isEqualTo("Lunch");
        assertThat(dto.getAmount()).isEqualTo(100);
        assertThat(dto.getDate()).isEqualTo("31/01/2022");
        assertThat(dto.getTotalPaymentsReceived()).isZero();
        assertThat(dto.getTotalPaymentsRequested()).isZero();
        assertThat(dto.getNettAmount()).isEqualTo(100);
    }
}
