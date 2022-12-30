package paymelater.api;

import kong.unirest.HttpResponse;
import kong.unirest.HttpStatus;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import paymelater.api.dto.NewPaymentRequestDTO;
import paymelater.api.dto.PaymentRequestDTO;
import paymelater.model.Expense;
import paymelater.model.PaymentRequest;
import paymelater.model.Person;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static paymelater.model.DateHelper.DD_MM_YYYY;
import static paymelater.model.DateHelper.TOMORROW;

public class PaymentRequestApiTests extends ApiTestRunner {

    @Test
    @DisplayName("GET /paymentrequests")
    public void getAll() {
        List<PaymentRequestDTO> actual = List.of(Unirest.get("/paymentrequests").asObject(PaymentRequestDTO[].class).getBody());
        assertThat(actual).isNotEmpty();
    }

    @Test
    @DisplayName("GET /paymentrequests/{paymentRequestId}")
    public void getPaymentRequest() {
        PaymentRequest expected = scenario.somePaymentRequest();
        PaymentRequestDTO actual = Unirest.get("/paymentrequests/{paymentRequestId}")
                .routeParam("paymentRequestId", expected.getId().toString())
                .asObject(PaymentRequestDTO.class)
                .getBody();
        assertThat(actual).isEqualTo(PaymentRequestDTO.fromPaymentRequest(expected));
    }

    @Test
    @DisplayName("404 GET /paymentrequests/{paymentRequestId}")
    public void getPaymentRequest404() {
        int id = scenario.getUnusedPaymentRequestId();
        HttpResponse<JsonNode> response = Unirest.get("/paymentrequests/{paymentRequestId}")
                .routeParam("paymentRequestId", Integer.toString(id))
                .asJson();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("POST /paymentrequests")
    public void post() {
        Expense expense = scenario.someExpense();
        Person person = scenario.personThatIsNot(expense.getPerson());
        NewPaymentRequestDTO newPaymentRequestDTO = new NewPaymentRequestDTO(expense.getId(),
                expense.getPerson().getId(), person.getId(), TOMORROW.format(DD_MM_YYYY), 100);
        PaymentRequestDTO actual = Unirest.post("/paymentrequests")
                .body(newPaymentRequestDTO)
                .asObject(PaymentRequestDTO.class)
                .getBody();
        assertThat(actual.getExpenseId()).isNotNull();
        assertThat(actual.getFromPersonId()).isEqualTo(newPaymentRequestDTO.getFromPersonId());
        assertThat(actual.getToPersonId()).isEqualTo(newPaymentRequestDTO.getToPersonId());
        assertThat(actual.getDate()).isEqualTo(newPaymentRequestDTO.getDate());
        assertThat(actual.getAmount()).isEqualTo(newPaymentRequestDTO.getAmount());
        assertThat(actual.isPaid()).isFalse();
    }

    @Test
    @DisplayName("404 POST /paymentrequests - expense")
    public void post404expense() {
        Integer id = scenario.getUnusedExpenseId();
        Person fromPerson = scenario.somePerson();
        Person toPerson = scenario.personThatIsNot(fromPerson);
        NewPaymentRequestDTO newPaymentRequestDTO = new NewPaymentRequestDTO(id,
                fromPerson.getId(), toPerson.getId(), TOMORROW.format(DD_MM_YYYY), 100);
        HttpResponse<JsonNode> response = Unirest.post("/paymentrequests")
                .body(newPaymentRequestDTO)
                .asJson();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("404 POST /paymentrequests - from")
    public void post404fromPerson() {
        Expense expense = scenario.someExpense();
        int id = scenario.getUnusedPersonId();
        Person person = scenario.somePerson();
        NewPaymentRequestDTO newPaymentRequestDTO = new NewPaymentRequestDTO(expense.getId(), id,
                person.getId(), TOMORROW.format(DD_MM_YYYY), 100);
        HttpResponse<JsonNode> response = Unirest.post("/paymentrequests")
                .body(newPaymentRequestDTO)
                .asJson();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("404 POST /paymentrequests - to")
    public void post404toPerson() {
        Expense expense = scenario.someExpense();
        int id = scenario.getUnusedPersonId();
        NewPaymentRequestDTO newPaymentRequestDTO = new NewPaymentRequestDTO(expense.getId(),
                expense.getPerson().getId(), id, TOMORROW.format(DD_MM_YYYY), 100);
        HttpResponse<JsonNode> response = Unirest.post("/paymentrequests")
                .body(newPaymentRequestDTO)
                .asJson();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("GET /paymentrequests/sent/{personId}")
    public void getSent() {
        PaymentRequest paymentRequest = scenario.somePaymentRequest();
        List<PaymentRequestDTO> actual = List.of(Unirest.get("/paymentrequests/sent/{personId}")
                        .routeParam("personId", Integer.toString(paymentRequest.getPersonRequestingPayment().getId()))
                .asObject(PaymentRequestDTO[].class).getBody());
        assertThat(actual).isNotEmpty();
    }

    @Test
    @DisplayName("404 GET /paymentrequests/sent/{personId}")
    public void getSent404() {
        int id = scenario.getUnusedPersonId();
        HttpResponse<JsonNode> response = Unirest.post("/paymentrequests/sent/{personId}")
                .routeParam("personId", Integer.toString(id))
                .asJson();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("GET /paymentrequests/received/{personId}")
    public void getReceived() {
        PaymentRequest paymentRequest = scenario.somePaymentRequest();
        List<PaymentRequestDTO> actual = List.of(Unirest.get("/paymentrequests/received/{personId}")
                .routeParam("personId", Integer.toString(paymentRequest.getPersonWhoShouldPayBack().getId()))
                .asObject(PaymentRequestDTO[].class).getBody());
        assertThat(actual).isNotEmpty();
    }

    @Test
    @DisplayName("404 GET /paymentrequests/received/{personId}")
    public void getReceived404() {
        int id = scenario.getUnusedPersonId();
        HttpResponse<JsonNode> response = Unirest.post("/paymentrequests/received/{personId}")
                .routeParam("personId", Integer.toString(id))
                .asJson();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("DELETE /paymentrequests/{paymentRequestId}")
    public void delete() {
        PaymentRequest expected = scenario.someUnpaidPaymentRequest();
        HttpResponse<JsonNode> response = Unirest.delete("/paymentrequests/{paymentRequestId}")
                .routeParam("paymentRequestId", expected.getId().toString())
                .asJson();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("404 DELETE /paymentrequests/{paymentRequestId}")
    public void delete404() {
        int id = scenario.getUnusedPaymentRequestId();
        HttpResponse<JsonNode> response = Unirest.delete("/paymentrequests/{paymentRequestId}")
                .routeParam("paymentRequestId", Integer.toString(id))
                .asJson();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("400 DELETE /paymentrequests/{paymentRequestId}")
    public void delete400() {
        PaymentRequest paymentRequest = scenario.somePaidPaymentRequest();
        HttpResponse<JsonNode> response = Unirest.delete("/paymentrequests/{paymentRequestId}")
                .routeParam("paymentRequestId", Integer.toString(paymentRequest.getId()))
                .asJson();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
