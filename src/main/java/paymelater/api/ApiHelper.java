package paymelater.api;

import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import org.apache.commons.validator.routines.EmailValidator;
import org.jetbrains.annotations.NotNull;
import paymelater.api.dto.*;
import paymelater.model.Expense;
import paymelater.model.PaymentRequest;
import paymelater.model.Person;

import java.util.Optional;

public class ApiHelper {
    public static Integer validPathParamId(Context ctx, String param) {
        return ctx.pathParamAsClass(param, Integer.class)
                .check(id -> id > 0, "ID must be greater than 0 ").get();
    }

    public static Integer validExpenseId(Context ctx) {
        return validPathParamId(ctx, "expenseId");
    }

    public static Integer validPersonId(Context ctx) {
        return validPathParamId(ctx, "personId");
    }

    @NotNull
    public static Integer validPaymentRequestId(Context ctx) {
        return validPathParamId(ctx, "paymentRequestId");
    }

    public static Optional<LoginDTO> validLogin(Context ctx) {
        LoginDTO loginDTO = ctx.bodyAsClass(LoginDTO.class);
        return EmailValidator.getInstance().isValid(loginDTO.getEmail())
                ? Optional.of(loginDTO) : Optional.empty();
    }

    public static NewExpenseDTO validNewExpenseDTO(Context ctx) {
        NewExpenseDTO newExpenseDTO = ctx.bodyAsClass(NewExpenseDTO.class);
        verifyPersonExists(newExpenseDTO.getPersonId());
        return newExpenseDTO;
    }

    private static Person verifyPersonExists(Integer personId) {
        return WeShareService.getPerson(personId)
                    .orElseThrow(() -> new NotFoundResponse("Person not found: " + personId));
    }

    private static Expense verifyExpenseExists(Integer expenseId) {
        return WeShareService.getExpense(expenseId)
                .orElseThrow(() -> new NotFoundResponse("Expense not found: " + expenseId));
    }

    public static NewPaymentRequestDTO validNewPaymentRequestDTO(Context ctx) {
        NewPaymentRequestDTO newPaymentRequestDTO = ctx.bodyAsClass(NewPaymentRequestDTO.class);
        verifyPersonExists(newPaymentRequestDTO.getFromPersonId());
        verifyPersonExists(newPaymentRequestDTO.getToPersonId());
        verifyExpenseExists(newPaymentRequestDTO.getExpenseId());
        return newPaymentRequestDTO;
    }

    public static Person validPerson(Context ctx) {
        int id = validPersonId(ctx);
        return verifyPersonExists(id);
    }

    public static Expense validExpense(Context ctx) {
        int id = validExpenseId(ctx);
        return verifyExpenseExists(id);
    }

    public static PaymentRequest validPaymentRequest(Context ctx) {
        int id = validPaymentRequestId(ctx);
        return verifyPaymentRequestExists(id);
    }

    private static PaymentRequest verifyPaymentRequestExists(Integer id) {
        return WeShareService.getPaymentRequest(id)
                .orElseThrow(() -> new NotFoundResponse("Payment Request not found: " + id));
    }

    public static NewPaymentDTO validNewPaymentDTO(Context ctx) {
        NewPaymentDTO newPaymentDTO = ctx.bodyAsClass(NewPaymentDTO.class);
        verifyExpenseExists(newPaymentDTO.getExpenseId());
        verifyPersonExists(newPaymentDTO.getPayingPersonId());
        verifyPaymentRequestExists(newPaymentDTO.getPaymentRequestId());
        return newPaymentDTO;
    }
}
