package paymelater.api;

import io.javalin.http.Context;
import io.javalin.http.HttpCode;
import io.javalin.plugin.openapi.annotations.*;
import paymelater.api.dto.ExpenseDTO;
import paymelater.api.dto.NewExpenseDTO;
import paymelater.model.Expense;
import paymelater.model.Person;

import java.util.Collection;
import java.util.List;

public class ExpenseApi {

    @OpenApi(
            summary = "Create a new expense for a person",
            tags = {"Expenses"},
            operationId = "createExpense",
            path = "/expenses",
            method = HttpMethod.POST,
            requestBody = @OpenApiRequestBody(content = {@OpenApiContent(from = NewExpenseDTO.class)}),
            responses = {
                    @OpenApiResponse(status = "201", content = {@OpenApiContent(from = ExpenseDTO.class)}),
                    @OpenApiResponse(status = "404", description = "Person not found")
            }
    )
    public static void create(Context ctx) {
        NewExpenseDTO unsaved = ApiHelper.validNewExpenseDTO(ctx);
        ExpenseDTO saved = WeShareService.createNewExpense(unsaved);
        ctx.json(saved);
        ctx.status(HttpCode.CREATED);
    }

    @OpenApi(
            summary = "Find all expenses",
            operationId = "findAllExpenses",
            path = "/expenses",
            method = HttpMethod.GET,
            tags = {"Expenses"},
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = ExpenseDTO[].class)})
            })
    public static void getAll(Context ctx) {
        ctx.json(mapExpenses(WeShareService.findAllExpenses()));
        ctx.status(HttpCode.OK);
    }

    @OpenApi(
            summary = "Find an expense by ID",
            operationId = "findExpenseById",
            path = "/expenses/{expenseId}",
            method = HttpMethod.GET,
            pathParams = {@OpenApiParam(name = "expenseId", description = "The expense ID",
                                        type = Integer.class, required = true)},
            tags = {"Expenses"},
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = ExpenseDTO.class)}),
                    @OpenApiResponse(status = "404", description = "Expense not found")
            }
    )
    public static void getOne(Context ctx) {
        Expense expense = ApiHelper.validExpense(ctx);
        ctx.json(ExpenseDTO.fromExpense(expense));
        ctx.status(HttpCode.OK);
    }

    @OpenApi(
            summary = "Find expenses for a person",
            operationId = "findExpensesByPerson",
            path = "/expenses/person/{personId}",
            method = HttpMethod.GET,
            pathParams = {@OpenApiParam(name = "personId",
                    type = Integer.class,
                    description = "The ID of the person",
                    required = true)},
            tags = {"Expenses"},
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = ExpenseDTO[].class)})
            })
    public static void findByPersonId(Context ctx) {
        Person person = ApiHelper.validPerson(ctx);
        Collection<Expense> expenses = WeShareService.findExpensesForPerson(person.getId());
        ctx.json(mapExpenses(expenses));
        ctx.status(HttpCode.OK);
    }

    private static List<ExpenseDTO> mapExpenses(Collection<Expense> all) {
        return all.stream().map(ExpenseDTO::fromExpense).toList();
    }
}
