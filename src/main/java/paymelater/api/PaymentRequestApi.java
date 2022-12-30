package paymelater.api;

import io.javalin.http.Context;
import io.javalin.http.HttpCode;
import io.javalin.plugin.openapi.annotations.*;
import paymelater.api.dto.NewPaymentRequestDTO;
import paymelater.api.dto.PaymentRequestDTO;
import paymelater.model.PaymentRequest;
import paymelater.model.Person;

import java.util.Collection;

public class PaymentRequestApi {

    @OpenApi(
            summary = "Find all payment requests",
            operationId = "findAllPaymentRequests",
            path = "/paymentrequests",
            method = HttpMethod.GET,
            tags = {"Payment Requests"},
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = PaymentRequestDTO[].class)})
            })
    public static void getAll(Context ctx) {
        ctx.json(mapToDTO(WeShareService.findAllPaymentRequests()));
        ctx.status(HttpCode.OK);
    }

    @OpenApi(
            summary = "Create a new payment request",
            operationId = "createPaymentRequest",
            path = "/paymentrequests",
            method = HttpMethod.POST,
            tags = {"Payment Requests"},
            requestBody = @OpenApiRequestBody(content = {@OpenApiContent(from = NewPaymentRequestDTO.class)}),
            responses = {
                    @OpenApiResponse(status = "201", content = {@OpenApiContent(from = PaymentRequestDTO.class)}),
                    @OpenApiResponse(status = "404", description = "Persons or Expense not found")
            }
    )
    public static void create(Context ctx) {
        NewPaymentRequestDTO unsaved = ApiHelper.validNewPaymentRequestDTO(ctx);
        PaymentRequestDTO saved = WeShareService.createNewPaymentRequest(unsaved);
        ctx.json(saved);
        ctx.status(HttpCode.CREATED);
    }

    @OpenApi(
            summary = "Get payment request by ID",
            operationId = "getPaymentRequestById",
            path = "/paymentrequests/{paymentRequestId}",
            method = HttpMethod.GET,
            pathParams = {@OpenApiParam(name = "paymentRequestId", description = "The payment request ID",
                    type = Integer.class, required = true)},
            tags = {"Payment Requests"},
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = PaymentRequestDTO.class)}),
                    @OpenApiResponse(status = "404", description = "PaymentRequest not found")
            }
    )
    public static void getOne(Context ctx) {
        PaymentRequest paymentRequest = ApiHelper.validPaymentRequest(ctx);
        PaymentRequestDTO dto = PaymentRequestDTO.fromPaymentRequest(paymentRequest);
        ctx.json(dto);
        ctx.status(HttpCode.OK);
    }

    @OpenApi(
            summary = "Find payment requests sent by a person",
            operationId = "findPaymentRequestsSent",
            path = "/paymentrequests/sent/{personId}",
            method = HttpMethod.GET,
            pathParams = {@OpenApiParam(name = "personId", description = "The ID of the person that sent payment requests",
                    type = Integer.class, required = true)},
            tags = {"Payment Requests"},
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = PaymentRequestDTO[].class)}),
                    @OpenApiResponse(status = "404", description = "Person not found")
            })
    public static void findPaymentRequestsSent(Context ctx) {
        Person person = ApiHelper.validPerson(ctx);
        Collection<PaymentRequest> paymentRequests = WeShareService.findPaymentRequestsSentBy(person.getId());
        ctx.json(mapToDTO(paymentRequests));
        ctx.status(HttpCode.OK);
    }

    @OpenApi(
            summary = "Find payment requests received by a person",
            operationId = "findPaymentRequestsReceived",
            path = "/paymentrequests/received/{personId}",
            method = HttpMethod.GET,
            pathParams = {@OpenApiParam(name = "personId", description = "The ID of the person that received payment requests",
                    type = Integer.class, required = true)},
            tags = {"Payment Requests"},
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = PaymentRequestDTO[].class)}),
                    @OpenApiResponse(status = "404", description = "Person not found")
            })
    public static void findPaymentRequestsReceived(Context ctx) {
        Integer id = ApiHelper.validPersonId(ctx);
        Collection<PaymentRequest> paymentRequests = WeShareService.findPaymentRequestsReceivedBy(id);
        ctx.json(mapToDTO(paymentRequests));
        ctx.status(HttpCode.OK);
    }

    @OpenApi(
            summary = "Recall an unpaid payment request",
            operationId = "recallUnpaidPaymentRequest",
            path = "/paymentrequests/{paymentRequestId}",
            method = HttpMethod.DELETE,
            pathParams = {@OpenApiParam(name = "paymentRequestId", description = "The payment request ID",
                    type = Integer.class, required = true)},
            tags = {"Payment Requests"},
            responses = {
                    @OpenApiResponse(status = "204", description = "Recalled"),
                    @OpenApiResponse(status = "400", description = "Payment Request could not be recalled"),
                    @OpenApiResponse(status = "404", description = "Payment Request not found")
            }
    )
    public static void recall(Context ctx) {
        PaymentRequest paymentRequest = ApiHelper.validPaymentRequest(ctx);
        WeShareService.recallPaymentRequest(paymentRequest.getId());
        ctx.status(HttpCode.NO_CONTENT);
    }


    private static Collection<PaymentRequestDTO> mapToDTO(Collection<PaymentRequest> all) {
        return all.stream().map(PaymentRequestDTO::fromPaymentRequest).toList();
    }
}
