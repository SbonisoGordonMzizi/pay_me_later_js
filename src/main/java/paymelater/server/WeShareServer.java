package paymelater.server;

/*
 ** DO NOT CHANGE!!
 */

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.javalin.Javalin;
import io.javalin.apibuilder.EndpointGroup;
import io.javalin.core.plugin.Plugin;
import io.javalin.http.HttpCode;
import io.javalin.http.staticfiles.Location;
import io.javalin.plugin.json.JsonMapper;
import io.javalin.plugin.openapi.OpenApiOptions;
import io.javalin.plugin.openapi.OpenApiPlugin;
import io.javalin.plugin.openapi.ui.ReDocOptions;
import io.javalin.plugin.openapi.ui.SwaggerOptions;
import io.swagger.v3.oas.models.info.Info;
import org.jetbrains.annotations.NotNull;
import paymelater.WeShareException;
import paymelater.api.*;
import paymelater.api.dto.NewPaymentDTO;
import paymelater.model.Expense;
import paymelater.model.PaymentRequest;
import paymelater.model.Person;
import paymelater.persistence.ExpenseDAO;
import paymelater.persistence.PaymentDAO;
import paymelater.persistence.PaymentRequestDAO;
import paymelater.persistence.PersonDAO;
import paymelater.persistence.collectionbased.ExpenseDAOImpl;
import paymelater.persistence.collectionbased.PaymentDAOImpl;
import paymelater.persistence.collectionbased.PaymentRequestDAOImpl;
import paymelater.persistence.collectionbased.PersonDAOImpl;

import java.util.Arrays;

import static io.javalin.apibuilder.ApiBuilder.*;
import static paymelater.model.DateHelper.*;
import static paymelater.model.MoneyHelper.amountOf;

public class WeShareServer {
    private static final String PUBLIC_DIR = "/public";
    private final Javalin appServer;

    public static void main(String[] args) {
        WeShareServer server = new WeShareServer();
        try {
            seedDemoData();
        } catch (WeShareException e) {
            throw new RuntimeException(e);
        }
        server.start(5050);
    }

    public WeShareServer() {
        ServiceRegistry.configure(PersonDAO.class, new PersonDAOImpl());
        ServiceRegistry.configure(ExpenseDAO.class, new ExpenseDAOImpl());
        ServiceRegistry.configure(PaymentRequestDAO.class, new PaymentRequestDAOImpl());
        ServiceRegistry.configure(PaymentDAO.class, new PaymentDAOImpl());

        appServer = Javalin.create(config -> {
            config.registerPlugin(getConfiguredOpenApiPlugin());
            config.addStaticFiles(PUBLIC_DIR, Location.CLASSPATH);
            config.jsonMapper(createGsonMapper());
        }).routes(configureApi());

        appServer.exception(WeShareException.class, (exception, ctx) -> {
            ctx.json(exception.getMessage());
            ctx.status(HttpCode.BAD_REQUEST);
        });
    }

    @NotNull
    private EndpointGroup configureApi() {
        return () -> {
            path("people", () -> {
                get(PersonApi::getAll);
                post(PersonApi::create);
                path("/{personId}", () -> get(PersonApi::getOne));
            });
            path("expenses", () -> {
                get(ExpenseApi::getAll);
                post(ExpenseApi::create);
                path("/person/{personId}", () -> get(ExpenseApi::findByPersonId));
                path("/{expenseId}", () -> get(ExpenseApi::getOne));
            });
            path("paymentrequests", () -> {
                get(PaymentRequestApi::getAll);
                post(PaymentRequestApi::create);
                path("/sent/{personId}", () -> get(PaymentRequestApi::findPaymentRequestsSent));
                path("/received/{personId}", () -> get(PaymentRequestApi::findPaymentRequestsReceived));
                path("{paymentRequestId}", () -> {
                    get(PaymentRequestApi::getOne);
                    delete(PaymentRequestApi::recall);
                });
            });
            path("payments", () -> {
                post(PaymentApi::pay);
                path("/madeby/{personId}", () -> get(PaymentApi::getPaymentsMadeBy));
            });
        };
    }

    private static Plugin getConfiguredOpenApiPlugin() {
        Info info = new Info().version("1.0").description("WeShare API");
        OpenApiOptions options = new OpenApiOptions(info)
                .activateAnnotationScanningFor("weshare.api")
                .path("/docs/openapi")
                .swagger(new SwaggerOptions("/docs/swagger"))
                .reDoc(new ReDocOptions("/docs/redoc"))
                .defaultDocumentation(doc -> {
                    doc.result("400");
                });
        return new OpenApiPlugin(options);
    }

    private static void seedDemoData() throws WeShareException {
        Person student1 = new Person("student1@wethinkcode.co.za");
        Person student2 = new Person("student2@wethinkcode.co.za");
        Person student3 = new Person("student3@wethinkcode.co.za");
        for (Person person : Arrays.asList(student1, student2, student3)) {
            WeShareService.savePerson(person);
        }

        Expense expense1 = new Expense(student1, "Lunch", amountOf(300), TODAY.minusDays(5));
        Expense expense2 = new Expense(student1, "Airtime", amountOf(100), YESTERDAY);
        Expense expense3 = new Expense(student2, "Movies", amountOf(150), TODAY.minusWeeks(1));
        Expense expense4 = new Expense(student3, "Ice cream", amountOf(50), TODAY.minusDays(3));
        for (Expense expense : Arrays.asList(expense1, expense2, expense3, expense4)) {
            WeShareService.saveExpense(expense);
        }

        PaymentRequest paymentRequest1 = expense1.requestPayment(student2, amountOf(100), YESTERDAY);
        PaymentRequest paymentRequest2 = expense1.requestPayment(student3, amountOf(100), TOMORROW);
        for (PaymentRequest paymentRequest : Arrays.asList(paymentRequest1, paymentRequest2)) {
            WeShareService.savePaymentRequest(paymentRequest);
        }

        NewPaymentDTO newPaymentDTO = new NewPaymentDTO(paymentRequest1.getExpenseId(),
                paymentRequest1.getId(), paymentRequest1.getPersonWhoShouldPayBack().getId());
        WeShareService.payPaymentRequest(newPaymentDTO);
    }

    /**
     * Use GSON for serialisation instead of Jackson
     * because GSON allows for serialisation of objects without noargs constructors.
     *
     * @return A JsonMapper for Javalin
     */
    private static JsonMapper createGsonMapper() {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return new JsonMapper() {
            @NotNull
            @Override
            public String toJsonString(@NotNull Object obj) {
                return gson.toJson(obj);
            }

            @NotNull
            @Override
            public <T> T fromJsonString(@NotNull String json, @NotNull Class<T> targetClass) {
                return gson.fromJson(json, targetClass);
            }
        };
    }

    public void start(int port) {
        this.appServer.start(port);
    }

    public void stop() {
        this.appServer.stop();
    }

    public int port() {
        return appServer.port();
    }
}
