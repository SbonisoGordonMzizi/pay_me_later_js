package paymelater.api;

import kong.unirest.HttpResponse;
import kong.unirest.HttpStatus;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import paymelater.api.dto.ExpenseDTO;
import paymelater.api.dto.NewExpenseDTO;
import paymelater.model.Expense;
import paymelater.model.Person;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static paymelater.model.DateHelper.DD_MM_YYYY;
import static paymelater.model.DateHelper.TODAY;

public class ExpenseApiTests extends ApiTestRunner {

    @Test
    @DisplayName("GET /expenses")
    public void getAll() {
        List<ExpenseDTO> actual = List.of(Unirest.get("/expenses").asObject(ExpenseDTO[].class).getBody());
        assertThat(actual).isNotEmpty();
    }

    @Test
    @DisplayName("GET /expenses/{expenseId}")
    public void getExpense() {
        Expense expected = scenario.someExpense();
        ExpenseDTO actual = Unirest.get("/expenses/{expenseId}")
                .routeParam("expenseId", expected.getId().toString())
                .asObject(ExpenseDTO.class)
                .getBody();
        assertThat(actual).isEqualTo(ExpenseDTO.fromExpense(expected));
    }

    @Test
    @DisplayName("404 GET /expenses/{expenseId}")
    public void getExpense04() {
        int id = scenario.getUnusedExpenseId();
        HttpResponse<JsonNode> response = Unirest.get("/expenses/{expenseId}")
                .routeParam("expenseId", Integer.toString(id))
                .asJson();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("POST /expenses")
    public void post() {
        Person person = scenario.somePerson();
        NewExpenseDTO newExpenseDTO = new NewExpenseDTO(person.getId(), "Some expense", TODAY.format(DD_MM_YYYY), 100);
        ExpenseDTO expenseDTO = Unirest.post("/expenses")
                .body(newExpenseDTO)
                .asObject(ExpenseDTO.class)
                .getBody();
        assertThat(expenseDTO.getExpenseId()).isNotNull();
        assertThat(expenseDTO.getPersonId()).isEqualTo(newExpenseDTO.getPersonId());
        assertThat(expenseDTO.getDescription()).isEqualTo(newExpenseDTO.getDescription());
        assertThat(expenseDTO.getDate()).isEqualTo(newExpenseDTO.getDate());
        assertThat(expenseDTO.getAmount()).isEqualTo(newExpenseDTO.getAmount());
    }

    @Test
    @DisplayName("POST /expenses")
    public void post404() {
        Integer id = scenario.getUnusedPersonId();
        NewExpenseDTO newExpenseDTO = new NewExpenseDTO(id, "Some expense", TODAY.format(DD_MM_YYYY), 100);
        HttpResponse<JsonNode> response = Unirest.post("/expenses")
                .body(newExpenseDTO)
                .asJson();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("GET /expenses/person/{personId}")
    public void getExpenseForPerson() {
        Person person = scenario.somePerson();
        List<ExpenseDTO> actual = List.of(Unirest.get("/expenses/person/{personId}")
                        .routeParam("personId", Integer.toString(person.getId()))
                .asObject(ExpenseDTO[].class).getBody());
        assertThat(actual).isNotEmpty();
    }
    @Test
    @DisplayName("404 GET /expenses/person/{personId}")
    public void getExpenseForPerson404() {
        int id = scenario.getUnusedPersonId();
        HttpResponse<JsonNode> response = Unirest.get("/expenses/person/{personId}")
                .routeParam("personId", Integer.toString(id))
                .asJson();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
