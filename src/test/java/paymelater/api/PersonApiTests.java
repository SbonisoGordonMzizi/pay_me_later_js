package paymelater.api;

import kong.unirest.HttpResponse;
import kong.unirest.HttpStatus;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import paymelater.api.dto.LoginDTO;
import paymelater.model.Person;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


public class PersonApiTests extends ApiTestRunner {

    @Test
    @DisplayName("GET /people")
    public void getAll() {
        Collection<Person> expected = scenario.somePeople();
        Collection<Person> actual = List.of(Unirest.get("/people").asObject(Person[].class).getBody());
        assertThat(actual).containsAll(expected);
    }

    @Test
    @DisplayName("GET /people/{personId}")
    public void getPerson() {
        Person expected = scenario.somePerson();
        Person actual = Unirest.get("/people/{personId}")
                .routeParam("personId", expected.getId().toString())
                .asObject(Person.class)
                .getBody();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("404 GET /people/{personId}")
    public void getPerson404() {
        int id = scenario.getUnusedPersonId();
        HttpResponse<JsonNode> response = Unirest.get("/people/{personId}")
                .routeParam("personId", Integer.toString(id))
                .asJson();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Not exists POST /people")
    public void postPersonNotExists() {
        LoginDTO dto = new LoginDTO();
        int id = scenario.getUnusedPersonId();
        dto.setEmail("student" + id + "@wethinkcode.co.za");
        HttpResponse<JsonNode> response = Unirest.post("/people")
                .body(dto)
                .asJson();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getObject().getInt("id")).isEqualTo(id);
    }

    @Test
    @DisplayName("Exists POST /people")
    public void postPersonExists() {
        LoginDTO dto = new LoginDTO();
        Person person = scenario.somePerson();
        dto.setEmail(person.getEmail());
        HttpResponse<JsonNode> response = Unirest.post("/people")
            .body(dto)
            .asJson();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getObject().getInt("id")).isEqualTo(person.getId());
    }

    @Test
    @DisplayName("400 POST /people")
    public void post400() {
        LoginDTO dto = new LoginDTO();
        dto.setEmail("not an email");
        HttpResponse<JsonNode> response = Unirest.post("/people")
                .body(dto)
                .asJson();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    }}
