package paymelater.persistence.collectionbased;

/*
 ** DO NOT CHANGE!!
 */


import paymelater.model.Expense;
import paymelater.model.Person;
import paymelater.persistence.ExpenseDAO;

import java.util.Collection;
import java.util.stream.Collectors;

public class ExpenseDAOImpl extends CollectionBasedDAO<Expense> implements ExpenseDAO {

    @Override
    public Collection<Expense> findExpensesForPerson(Person person) {
        return findAll().stream()
                .filter(e -> e.getPerson().equals(person))
                .collect(Collectors.toUnmodifiableList());
    }
}
