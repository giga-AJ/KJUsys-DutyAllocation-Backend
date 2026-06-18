package in.edu.kristujayanti.handlers;

import in.edu.kristujayanti.enums.ResponseType;
import in.edu.kristujayanti.enums.StatusCode;
import in.edu.kristujayanti.exception.DataAccessException;
import in.edu.kristujayanti.services.SwapRequestService;
import in.edu.kristujayanti.util.ResponseUtil;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.bson.Document;

import java.util.List;

public class SwapRequestHandler
        implements Handler<RoutingContext> {

    private final SwapRequestService swapRequestService;

    public SwapRequestHandler(
            SwapRequestService swapRequestService
    ) {
        this.swapRequestService = swapRequestService;
    }

    @Override
    public void handle(
            RoutingContext routingContext
    ) {

        String path =
                routingContext.normalizedPath();

        HttpServerResponse response =
                routingContext.response();

        try {

            if (path.endsWith("/swap-requests")
                    && routingContext.request()
                    .method().name().equals("GET")) {

                handleGetAllSwapRequests(response);

            } else if (path.contains("/swap-requests/status/")
                    && routingContext.request()
                    .method().name().equals("GET")) {

                String status =
                        routingContext.pathParam("status");

                handleGetSwapRequestsByStatus(
                        response,
                        status
                );

            } else if (path.contains("/admin-approve/")
                    && routingContext.request()
                    .method().name().equals("PUT")) {

                String id =
                        routingContext.pathParam("id");

                handleAdminApproveSwap(
                        response,
                        id
                );

            } else if (path.contains("/admin-reject/")
                    && routingContext.request()
                    .method().name().equals("PUT")) {

                String id =
                        routingContext.pathParam("id");

                handleAdminRejectSwap(
                        response,
                        id
                );

            } else if (path.endsWith("/swap-requests/add")
                    && routingContext.request()
                    .method().name().equals("POST")) {

                handleCreateSwapRequest(
                        routingContext,
                        response
                );
            }

        } catch (Exception e) {

            ResponseUtil.createResponse(
                    response,
                    ResponseType.ERROR,
                    StatusCode.INTERNAL_SERVER_ERROR,
                    new JsonObject()
                            .put("error", e.getMessage()),
                    new JsonArray()
            );
        }
    }

    private void handleGetAllSwapRequests(
            HttpServerResponse response
    ) throws DataAccessException {

        List<Document> requestList =
                swapRequestService.getAllSwapRequests();

        JsonArray jsonArray =
                new JsonArray();

        for (Document doc : requestList) {
            jsonArray.add(
                    new JsonObject(doc.toJson())
            );
        }

        ResponseUtil.createResponse(
                response,
                ResponseType.SUCCESS,
                StatusCode.TWOHUNDRED,
                new JsonObject()
                        .put("swapRequests", jsonArray),
                new JsonArray()
        );
    }

    private void handleGetSwapRequestsByStatus(
            HttpServerResponse response,
            String status
    ) throws DataAccessException {

        List<Document> requestList =
                swapRequestService
                        .getSwapRequestsByStatus(status);

        JsonArray jsonArray =
                new JsonArray();

        for (Document doc : requestList) {
            jsonArray.add(
                    new JsonObject(doc.toJson())
            );
        }

        ResponseUtil.createResponse(
                response,
                ResponseType.SUCCESS,
                StatusCode.TWOHUNDRED,
                new JsonObject()
                        .put("swapRequests", jsonArray),
                new JsonArray()
        );
    }

    private void handleCreateSwapRequest(
            RoutingContext routingContext,
            HttpServerResponse response
    ) {

        try {

            JsonObject body =
                    routingContext.body()
                            .asJsonObject();

            Document swapRequestDoc =
                    Document.parse(body.encode());

            swapRequestService
                    .addSwapRequest(
                            swapRequestDoc
                    );

            ResponseUtil.createResponse(
                    response,
                    ResponseType.SUCCESS,
                    StatusCode.TWOHUNDRED,
                    new JsonObject()
                            .put(
                                    "message",
                                    "Swap request created successfully"
                            ),
                    new JsonArray()
            );

        } catch (Exception e) {

            ResponseUtil.createResponse(
                    response,
                    ResponseType.ERROR,
                    StatusCode.INTERNAL_SERVER_ERROR,
                    new JsonObject()
                            .put("error", e.getMessage()),
                    new JsonArray()
            );
        }


    }private void handleAdminApproveSwap(
            HttpServerResponse response,
            String id
    ) {

        try {

            boolean updated =
                    swapRequestService
                            .updateSwapStatus(
                                    id,
                                    "ADMIN_APPROVED"
                            );

            if (!updated) {

                ResponseUtil.createResponse(
                        response,
                        ResponseType.ERROR,
                        StatusCode.FILE_NOT_FOUND,
                        new JsonObject()
                                .put(
                                        "error",
                                        "Swap request not found"
                                ),
                        new JsonArray()
                );

                return;
            }

            ResponseUtil.createResponse(
                    response,
                    ResponseType.SUCCESS,
                    StatusCode.TWOHUNDRED,
                    new JsonObject()
                            .put(
                                    "message",
                                    "Swap request approved"
                            ),
                    new JsonArray()
            );

        } catch (Exception e) {

            ResponseUtil.createResponse(
                    response,
                    ResponseType.ERROR,
                    StatusCode.INTERNAL_SERVER_ERROR,
                    new JsonObject()
                            .put("error", e.getMessage()),
                    new JsonArray()
            );
        }
    }

    private void handleAdminRejectSwap(
            HttpServerResponse response,
            String id
    ) {

        try {

            boolean updated =
                    swapRequestService
                            .updateSwapStatus(
                                    id,
                                    "ADMIN_REJECTED"
                            );

            if (!updated) {

                ResponseUtil.createResponse(
                        response,
                        ResponseType.ERROR,
                        StatusCode.FILE_NOT_FOUND,
                        new JsonObject()
                                .put(
                                        "error",
                                        "Swap request not found"
                                ),
                        new JsonArray()
                );

                return;
            }

            ResponseUtil.createResponse(
                    response,
                    ResponseType.SUCCESS,
                    StatusCode.TWOHUNDRED,
                    new JsonObject()
                            .put(
                                    "message",
                                    "Swap request rejected"
                            ),
                    new JsonArray()
            );

        } catch (Exception e) {

            ResponseUtil.createResponse(
                    response,
                    ResponseType.ERROR,
                    StatusCode.INTERNAL_SERVER_ERROR,
                    new JsonObject()
                            .put("error", e.getMessage()),
                    new JsonArray()
            );
        }
    }
}




