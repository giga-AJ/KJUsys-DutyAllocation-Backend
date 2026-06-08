package in.edu.kristujayanti.handlers;

import in.edu.kristujayanti.enums.ResponseType;
import in.edu.kristujayanti.enums.StatusCode;
import in.edu.kristujayanti.exception.DataAccessException;
import in.edu.kristujayanti.services.AuditLogService;
import in.edu.kristujayanti.util.ResponseUtil;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.bson.Document;

import java.util.List;

public class AuditLogHandler
        implements Handler<RoutingContext> {

    private final AuditLogService auditLogService;

    public AuditLogHandler(
            AuditLogService auditLogService
    ) {
        this.auditLogService = auditLogService;
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

            if (path.endsWith("/audit-logs")
                    && routingContext.request()
                    .method().name().equals("GET")) {

                handleGetAllAuditLogs(response);

            } else if (path.contains("/audit-logs/action/")
                    && routingContext.request()
                    .method().name().equals("GET")) {

                String action =
                        routingContext.pathParam("action");

                handleGetAuditLogsByAction(
                        response,
                        action
                );

            } else if (path.endsWith("/audit-logs/add")
                    && routingContext.request()
                    .method().name().equals("POST")) {

                handleCreateAuditLog(
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

    private void handleGetAllAuditLogs(
            HttpServerResponse response
    ) throws DataAccessException {

        List<Document> auditLogList =
                auditLogService.getAllAuditLogs();

        JsonArray jsonArray =
                new JsonArray();

        for (Document doc : auditLogList) {
            jsonArray.add(
                    new JsonObject(doc.toJson())
            );
        }

        ResponseUtil.createResponse(
                response,
                ResponseType.SUCCESS,
                StatusCode.TWOHUNDRED,
                new JsonObject()
                        .put("auditLogs", jsonArray),
                new JsonArray()
        );
    }

    private void handleGetAuditLogsByAction(
            HttpServerResponse response,
            String action
    ) throws DataAccessException {

        List<Document> auditLogList =
                auditLogService
                        .getAuditLogsByAction(action);

        JsonArray jsonArray =
                new JsonArray();

        for (Document doc : auditLogList) {
            jsonArray.add(
                    new JsonObject(doc.toJson())
            );
        }

        ResponseUtil.createResponse(
                response,
                ResponseType.SUCCESS,
                StatusCode.TWOHUNDRED,
                new JsonObject()
                        .put("auditLogs", jsonArray),
                new JsonArray()
        );
    }

    private void handleCreateAuditLog(
            RoutingContext routingContext,
            HttpServerResponse response
    ) {

        try {

            JsonObject body =
                    routingContext.body()
                            .asJsonObject();

            Document auditLogDoc =
                    Document.parse(body.encode());

            auditLogService
                    .addAuditLog(auditLogDoc);

            ResponseUtil.createResponse(
                    response,
                    ResponseType.SUCCESS,
                    StatusCode.TWOHUNDRED,
                    new JsonObject()
                            .put(
                                    "message",
                                    "Audit log created successfully"
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