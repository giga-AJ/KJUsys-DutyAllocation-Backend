package in.edu.kristujayanti.handlers;

import in.edu.kristujayanti.enums.ResponseType;
import in.edu.kristujayanti.enums.StatusCode;
import in.edu.kristujayanti.exception.DataAccessException;
import in.edu.kristujayanti.services.FacultyService;
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

public class FacultyHandler implements Handler<RoutingContext> {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(FacultyHandler.class);

    private final FacultyService facultyService;

    // ─────────────────────────────────────────
    // Constructor
    // ─────────────────────────────────────────
    public FacultyHandler(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    // ─────────────────────────────────────────
    // Main handle method — entry point
    // decides which method to call
    // ─────────────────────────────────────────
    @Override
    public void handle(RoutingContext routingContext) {

        String path = routingContext.normalizedPath();
        HttpServerResponse response = routingContext.response();


        try {

            // GET /faculty — get all faculty
            if (path.endsWith("/faculty")
                    && routingContext.request().method().name().equals("GET")) {

                handleGetAllFaculty(routingContext, response);

                // GET /faculty/:id — get one faculty
            } else if (path.contains("/faculty/")
                    && routingContext.request().method().name().equals("GET")) {

                String id = routingContext.pathParam("id");
                handleGetFacultyById(response, id);



            } else if (path.endsWith("/faculty/add")
                    && routingContext.request().method().name().equals("POST")) {

                handleCreateFaculty(routingContext, response);

            } else if (path.contains("/faculty")
                    && routingContext.request().method().name().equals("PUT")) {

                String id = routingContext.pathParam("id");
                handleUpdateFaculty(routingContext, response, id);

            } else if (path.contains("/faculty/")
                    && routingContext.request().method().name().equals("DELETE")) {

                String id = routingContext.pathParam("id");
                handleDeleteFaculty(response, id);

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
            LOGGER.error("Error in FacultyHandler: {}", e.getMessage(), e);
            ResponseUtil.createResponse(
                    response,
                    ResponseType.ERROR,
                    StatusCode.INTERNAL_SERVER_ERROR,
                    new JsonObject()
                            .put("error", "Internal Server Error")
                            .put("detail", e.getMessage()),
                    new JsonArray()
            );
        }
    } // ← end of handle method


    // ─────────────────────────────────────────
    // Handle GET all faculty
    // ─────────────────────────────────────────
    private void handleGetAllFaculty(
            RoutingContext routingContext,
            HttpServerResponse response
    ) throws DataAccessException {

        String department = routingContext.request().getParam("department");
        String status = routingContext.request().getParam("status");
        String search = routingContext.request().getParam("search");

        List<Document> facultyList;

        if (search != null && !search.isEmpty()) {
            facultyList = facultyService.searchFacultyByName(search);

        } else if (department != null && !department.isEmpty()) {
            facultyList = facultyService.getFacultyByDepartment(department);

        } else if (status != null && !status.isEmpty()) {
            facultyList = facultyService.getFacultyByStatus(status);

        } else {
            facultyList = facultyService.getAllFaculty();
        }

        JsonArray jsonArray = new JsonArray();

        for (Document doc : facultyList) {
            jsonArray.add(new JsonObject(doc.toJson()));
        }

        ResponseUtil.createResponse(
                response,
                ResponseType.SUCCESS,
                StatusCode.TWOHUNDRED,
                new JsonObject().put("faculty", jsonArray),
                new JsonArray()
        );
    }

    private void handleGetFacultyById(
            HttpServerResponse response,
            String id
    ) throws DataAccessException {

        Document faculty = facultyService.getFacultyById(id);

        if (faculty == null) {
            ResponseUtil.createResponse(
                    response,
                    ResponseType.ERROR,
                    StatusCode.FILE_NOT_FOUND,
                    new JsonObject().put("error", "Faculty not found"),
                    new JsonArray()
            );
            return;
        }

        ResponseUtil.createResponse(
                response,
                ResponseType.SUCCESS,
                StatusCode.TWOHUNDRED,
                new JsonObject(faculty.toJson()),
                new JsonArray()
        );
    }

    private void handleCreateFaculty(
            RoutingContext routingContext,
            HttpServerResponse response
    ) {

        try {

            JsonObject body = routingContext.body().asJsonObject();

            Document facultyDoc =
                    Document.parse(body.encode());

            boolean created =
                    facultyService.addFaculty(facultyDoc);

            if (!created) {

                ResponseUtil.createResponse(
                        response,
                        ResponseType.ERROR,
                        StatusCode.BAD_REQUEST,
                        new JsonObject()
                                .put("error",
                                        "Employee ID already exists"),
                        new JsonArray()
                );
                return;
            }

            ResponseUtil.createResponse(
                    response,
                    ResponseType.SUCCESS,
                    StatusCode.TWOHUNDRED,
                    new JsonObject()
                            .put("message",
                                    "Faculty added successfully"),
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

    private void handleUpdateFaculty(
            RoutingContext routingContext,
            HttpServerResponse response,
            String id
    ) {

        try {

            JsonObject body =
                    routingContext.body().asJsonObject();

            String status =
                    body.getString("status");

            boolean updated =
                    facultyService.updateFacultyStatus(
                            id,
                            status
                    );

            if (!updated) {

                ResponseUtil.createResponse(
                        response,
                        ResponseType.ERROR,
                        StatusCode.FILE_NOT_FOUND,
                        new JsonObject()
                                .put("error",
                                        "Faculty not found"),
                        new JsonArray()
                );
                return;
            }

            ResponseUtil.createResponse(
                    response,
                    ResponseType.SUCCESS,
                    StatusCode.TWOHUNDRED,
                    new JsonObject()
                            .put("message",
                                    "Faculty status updated"),
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

    private void handleDeleteFaculty(
            HttpServerResponse response,
            String id
    ) {

    }}// ← end of class