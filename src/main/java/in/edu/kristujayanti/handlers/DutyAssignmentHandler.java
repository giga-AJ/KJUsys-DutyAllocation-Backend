package in.edu.kristujayanti.handlers;

import in.edu.kristujayanti.enums.ResponseType;
import in.edu.kristujayanti.enums.StatusCode;
import in.edu.kristujayanti.exception.DataAccessException;
import in.edu.kristujayanti.services.DutyAssignmentService;
import in.edu.kristujayanti.util.ResponseUtil;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.bson.Document;

import java.util.List;

public class DutyAssignmentHandler
        implements Handler<RoutingContext> {

    private final DutyAssignmentService dutyAssignmentService;

    public DutyAssignmentHandler(
            DutyAssignmentService dutyAssignmentService
    ) {
        this.dutyAssignmentService = dutyAssignmentService;
    }

    @Override
    public void handle(RoutingContext routingContext) {

        System.out.println("ASSIGNMENT HANDLER REACHED");

        String path = routingContext.normalizedPath();

        HttpServerResponse response = routingContext.response();

        try {

            if (path.endsWith("/assignments")
                    && routingContext.request().method().name().equals("GET")) {

                handleGetAllAssignments(response);



            // other conditions...

            } else if (path.contains("/assignments/faculty/")
                    && routingContext.request().method().name().equals("GET")) {

                String facultyId =
                        routingContext.pathParam("facultyId");

                handleGetAssignmentsByFaculty(
                        response,
                        facultyId
                );

            } else if (path.endsWith("/assignments/assign")
                    && routingContext.request().method().name().equals("POST")) {

                handleCreateAssignment(
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

    private void handleGetAllAssignments(
            HttpServerResponse response
    ) throws DataAccessException {

        List<Document> assignmentList =
                dutyAssignmentService.getAllAssignments();

        JsonArray jsonArray = new JsonArray();

        for (Document doc : assignmentList) {
            jsonArray.add(new JsonObject(doc.toJson()));
        }

        ResponseUtil.createResponse(
                response,
                ResponseType.SUCCESS,
                StatusCode.TWOHUNDRED,
                new JsonObject()
                        .put("assignments", jsonArray),
                new JsonArray()
        );
    }

    private void handleGetAssignmentsByFaculty(
            HttpServerResponse response,
            String facultyId
    ) throws DataAccessException {

        List<Document> assignmentList =
                dutyAssignmentService
                        .getAssignmentsByFaculty(facultyId);

        JsonArray jsonArray = new JsonArray();

        for (Document doc : assignmentList) {
            jsonArray.add(new JsonObject(doc.toJson()));
        }

        ResponseUtil.createResponse(
                response,
                ResponseType.SUCCESS,
                StatusCode.TWOHUNDRED,
                new JsonObject()
                        .put("assignments", jsonArray),
                new JsonArray()
        );
    }

    private void handleCreateAssignment(
            RoutingContext routingContext,
            HttpServerResponse response
    ) {

        try {

            JsonObject body =
                    routingContext.body().asJsonObject();

            Document assignmentDoc =
                    Document.parse(body.encode());

            dutyAssignmentService
                    .addAssignment(assignmentDoc);

            ResponseUtil.createResponse(
                    response,
                    ResponseType.SUCCESS,
                    StatusCode.TWOHUNDRED,
                    new JsonObject()
                            .put(
                                    "message",
                                    "Assignment created successfully"
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