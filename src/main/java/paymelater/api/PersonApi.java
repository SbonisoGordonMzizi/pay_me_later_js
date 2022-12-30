package paymelater.api;

import io.javalin.http.Context;
import io.javalin.http.HttpCode;
import io.javalin.plugin.openapi.annotations.*;
import paymelater.api.dto.LoginDTO;
import paymelater.model.Person;

import java.util.Optional;

public class PersonApi {
    @OpenApi(
            summary = "Find all people that use WeShare",
            operationId = "findAllPeople",
            path = "/people",
            method = HttpMethod.GET,
            tags = {"People"},
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = Person[].class)})
            })
    public static void getAll(Context ctx) {
        ctx.json(WeShareService.findAllPeople());
    }

    @OpenApi(
            summary = "Login to WeShare",
            operationId = "login",
            path = "/people",
            method = HttpMethod.POST,
            tags = {"People"},
            requestBody = @OpenApiRequestBody(content = {@OpenApiContent(from = LoginDTO.class)}),
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = Person.class)}),
                    @OpenApiResponse(status = "400", description = "Bad email address")
            }
    )
    public static void create(Context ctx) {
        Optional<LoginDTO> loginDTO = ApiHelper.validLogin(ctx);
        if (loginDTO.isPresent()) {
            Person person = WeShareService.findPersonByEmailOrCreate(loginDTO.get().getEmail());
            ctx.json(WeShareService.savePerson(person));
            ctx.status(HttpCode.OK);
        } else {
            ctx.status(HttpCode.BAD_REQUEST);
        }
    }

    @OpenApi(
            summary = "Find a person by ID",
            operationId = "findPersonById",
            path = "/people/{personId}",
            method = HttpMethod.GET,
            pathParams = {@OpenApiParam(name = "personId", description = "The ID of the person",
                                        type = Integer.class, required = true )},
            tags = {"People"},
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = Person.class)}),
                    @OpenApiResponse(status = "404", description = "Person not found")
            }
    )
    public static void getOne(Context ctx) {
        Person person = ApiHelper.validPerson(ctx);
        ctx.json(person);
        ctx.status(HttpCode.OK);
    }
}
