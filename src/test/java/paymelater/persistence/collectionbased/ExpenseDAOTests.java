package paymelater.persistence.collectionbased;

/*
 ** DO NOT CHANGE!!
 */

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import paymelater.model.Expense;
import paymelater.model.Person;
import paymelater.persistence.ExpenseDAO;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static paymelater.model.DateHelper.TODAY;
import static paymelater.model.DateHelper.TOMORROW;
import static paymelater.model.MoneyHelper.amountOf;

public class ExpenseDAOTests {

    private ExpenseDAO dao;

    @BeforeEach
    public void newRepository() {
        Person student1 = new Person("student1@wethinkcode.co.za");
        Person student2 = new Person("student2@wethinkcode.co.za");
        Person student3 = new Person("student3@wethinkcode.co.za");

        Expense expense1 = new Expense(student1, "Lunch", amountOf(300), TODAY);
        Expense expense2 = new Expense(student1, "Airtime", amountOf(100), TODAY);
        Expense expense3 = new Expense(student2, "Movies", amountOf(150), TODAY.minusWeeks(1));
        Expense expense4 = new Expense(student3, "Ice cream", amountOf(50), TODAY.minusDays(3));

        expense1.requestPayment(student2, amountOf(100), TOMORROW);
        expense1.requestPayment(student3, amountOf(100), TOMORROW);
        expense2.requestPayment(student2, amountOf(100), TOMORROW);

        this.dao = new ExpenseDAOImpl();
        Stream.of(expense1, expense2, expense3, expense4)
                .forEach(expense -> this.dao.save(expense));
    }

    @Test
    public void findExpensesForPerson() {
        Person p = new Person("student1@wethinkcode.co.za");
        Collection<Expense> expenses = dao.findExpensesForPerson(p);
        assertThat(expenses).isNotEmpty();
        List<String> descriptions = List.of("Lunch", "Airtime");
        assertThat(descriptions).hasSameElementsAs(expenses.stream().map(Expense::getDescription).collect(Collectors.toList()));
    }

    @Test
    public void noExpensesForPerson() {
        Person p = new Person("unknownstudent@wethinkcode.co.za");
        Collection<Expense> expenses = dao.findExpensesForPerson(p);
        assertThat(expenses).isEmpty();
    }

    @Test
    public void saveExpense() {
        Person p = new Person("newstudent@wethinkcode.co.za");
        Expense e = new Expense(p, "Some expense", amountOf(100), TODAY);
        Expense saved = dao.save(e);
        assertThat(saved).isEqualTo(e);
        assertThat(saved).isEqualTo(dao.findById(e.getId()).orElseThrow());
    }

    @Test
    public void getExpense() {
        Person p = new Person("student1@wethinkcode.co.za");
        Expense expenseToFind = dao.findExpensesForPerson(p).stream().findFirst().orElseThrow();
        Expense foundExpense = dao.findById(expenseToFind.getId()).orElseThrow();
        assertThat(foundExpense).isEqualTo(expenseToFind);
    }
}
