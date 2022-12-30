package paymelater.persistence;

/*
 ** DO NOT CHANGE!!
 */


import paymelater.model.Expense;
import paymelater.model.Person;

import java.util.Collection;

public interface ExpenseDAO extends DAO<Expense> {
    Collection<Expense> findExpensesForPerson(Person person);
}
