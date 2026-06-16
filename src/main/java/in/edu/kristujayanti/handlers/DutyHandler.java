package in.edu.kristujayanti.handlers;

import in.edu.kristujayanti.enums.ResponseType;
import in.edu.kristujayanti.enums.StatusCode;
import in.edu.kristujayanti.exception.DataAccessException;
import in.edu.kristujayanti.services.DutyService;
import in.edu.kristujayanti.util.ResponseUtil;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class DutyHandler implements Handler<RoutingContext> {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(DutyHandler.class);

    private final DutyService dutyService;

    public DutyHandler(DutyService dutyService) {
        this.dutyService = dutyService;
    }

    @Override
    public void handle(RoutingContext routingContext) {

        String path = routingContext.normalizedPath();
        HttpServerResponse response = routingContext.response();

        try {

            if (path.endsWith("/duties")
                    && routingContext.request().method().name().equals("GET")) {

                handleGetAllDuties(routingContext, response);

            } else if (path.contains("/duties/")
                    && routingContext.request().method().name().equals("GET")) {

                String id = routingContext.pathParam("id");
                handleGetDutyById(response, id);

            } else if (path.endsWith("/duties/add")
                    && routingContext.request().method().name().equals("POST")) {

                handleCreateDuty(routingContext, response);

            } else if (path.contains("/duties/update/")
                    && routingContext.request().method().name().equals("PUT")) {

                String id = routingContext.pathParam("id");

                handleUpdateDuty(
                        routingContext,
                        response,
                        id
                );

            } else if (path.contains("/duties/")
                    && routingContext.request().method().name().equals("DELETE")) {

                String id = routingContext.pathParam("id");

                handleDeleteDuty(
                        response,
                        id
                );



            } else {

                ResponseUtil.createResponse(
                        response,
                        ResponseType.ERROR,
                        StatusCode.FILE_NOT_FOUND,
                        new JsonObject().put("error", "Route not found"),
                        new JsonArray()
                );
            }

        } catch (Exception e) {

            LOGGER.error("Error in DutyHandler", e);

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

    private void handleGetAllDuties(
            RoutingContext routingContext,
            HttpServerResponse response
    ) throws DataAccessException {

        String dutyType =
                routingContext.request().getParam("dutyType");

        String department =
                routingContext.request().getParam("department");

        String date =
                routingContext.request().getParam("date");

        List<Document> dutyList;

        if (dutyType != null && !dutyType.isEmpty()) {

            dutyList =
                    dutyService.getDutiesByType(dutyType);

        } else if (department != null && !department.isEmpty()) {

            dutyList =
                    dutyService.getDutiesByDepartment(department);

        } else if (date != null && !date.isEmpty()) {

            dutyList =
                    dutyService.getDutiesByDate(date);

        } else {

            dutyList =
                    dutyService.getAllDuties();
        }

        JsonArray jsonArray = new JsonArray();

        for (Document doc : dutyList) {
            jsonArray.add(new JsonObject(doc.toJson()));
        }

        ResponseUtil.createResponse(
                response,
                ResponseType.SUCCESS,
                StatusCode.TWOHUNDRED,
                new JsonObject().put("duties", jsonArray),
                new JsonArray()
        );
    }

    private void handleGetDutyById(
            HttpServerResponse response,
            String id
    ) throws DataAccessException {

        Document duty =
                dutyService.getDutyById(id);

        if (duty == null) {

            ResponseUtil.createResponse(
                    response,
                    ResponseType.ERROR,
                    StatusCode.FILE_NOT_FOUND,
                    new JsonObject()
                            .put("error", "Duty not found"),
                    new JsonArray()
            );

            return;
        }

        ResponseUtil.createResponse(
                response,
                ResponseType.SUCCESS,
                StatusCode.TWOHUNDRED,
                new JsonObject(duty.toJson()),
                new JsonArray()
        );
    }

    private void handleCreateDuty(
            RoutingContext routingContext,
            HttpServerResponse response
    ) {

        try {

            System.out.println("=== CREATE DUTY REACHED ===");

            JsonObject body =
                    routingContext.body().asJsonObject();

            System.out.println(body.encodePrettily());

            Document dutyDoc =
                    Document.parse(body.encode());

            System.out.println("DOCUMENT CREATED");

            dutyService.addDuty(dutyDoc);

            System.out.println("DUTY SAVED");

            ResponseUtil.createResponse(
                    response,
                    ResponseType.SUCCESS,
                    StatusCode.TWOHUNDRED,
                    new JsonObject()
                            .put("message",
                                    "Duty added successfully"),
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

    private void handleUpdateDuty(
            RoutingContext routingContext,
            HttpServerResponse response,
            String id
    ) {

        try {

            JsonObject body =
                    routingContext.body().asJsonObject();

            Document updateDoc =
                    Document.parse(body.encode());

            boolean updated =
                    dutyService.updateDuty(
                            id,
                            updateDoc
                    );

            if (!updated) {

                ResponseUtil.createResponse(
                        response,
                        ResponseType.ERROR,
                        StatusCode.FILE_NOT_FOUND,
                        new JsonObject()
                                .put("error", "Duty not found"),
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
                                    "Duty updated successfully"
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

    private void handleDeleteDuty(
            HttpServerResponse response,
            String id
    ) {

        try {

            boolean deleted =
                    dutyService.deleteDuty(id);

            if (!deleted) {

                ResponseUtil.createResponse(
                        response,
                        ResponseType.ERROR,
                        StatusCode.FILE_NOT_FOUND,
                        new JsonObject()
                                .put("error", "Duty not found"),
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
                                    "Duty deleted successfully"
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