package in.edu.kristujayanti.router;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import in.edu.kristujayanti.constants.CommonKeys;
import in.edu.kristujayanti.constants.ContextRoutingURLName;
import in.edu.kristujayanti.constants.MicroserviceRoutingURLNames;
import in.edu.kristujayanti.services.HealthService;
import in.edu.kristujayanti.handlers.HealthHandler;
import in.edu.kristujayanti.services.FacultyService;
import in.edu.kristujayanti.handlers.FacultyHandler;
import in.edu.kristujayanti.services.DutyService;
import in.edu.kristujayanti.handlers.DutyHandler;
import in.edu.kristujayanti.services.DutyAssignmentService;
import in.edu.kristujayanti.handlers.DutyAssignmentHandler;
import in.edu.kristujayanti.services.SwapRequestService;
import in.edu.kristujayanti.handlers.SwapRequestHandler;
import in.edu.kristujayanti.services.AuditLogService;
import in.edu.kristujayanti.handlers.AuditLogHandler;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.redis.client.Redis;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * MicroserviceRouter sets up the main application routes and handlers.
 * It extends the RouterBase to utilize common properties and methods for
 * routing.
 */
public class MicroserviceRouter extends RouterBase {

        /**
         * Constructs a ReportOrchestratorRouter with necessary dependencies.
         *
         * @param router                 the Vert.x router
         * @param redisCommandConnection the Redis connection
         * @param mongoDatabase          the MongoDB database
         * @param mongoClient            the MongoDB client
         * @param client                 the Vert.x WebClient
         */
        public MicroserviceRouter(Router router, Redis redisCommandConnection, MongoDatabase mongoDatabase,
                        MongoClient mongoClient, WebClient client, JsonObject apiInfo, Vertx vertx) {
                super(router, redisCommandConnection, mongoDatabase, mongoClient, client, apiInfo, vertx);
        }

        /**
         * Sets up the application routes with handlers.
         */
        public void setUpRouters() {
                // Define allowed headers for CORS
                Set<String> allowHeaders = Stream.of(
                        CommonKeys.CONTENT_TYPE,
                        CommonKeys.X_AUTH_CORRELATION_ID,
                        HttpHeaders.AUTHORIZATION.toString(),
                        "Access-Control-Allow-Origin").collect(Collectors.toSet());

                // Define allowed HTTP methods for CORS
                Set<HttpMethod> allowMethods = Stream.of(
                        HttpMethod.GET,
                        HttpMethod.POST,
                        HttpMethod.PUT).collect(Collectors.toSet());

                // Setup CORS and Body Handlers
                this.router.route().handler(CorsHandler.create()
                        .addOrigin("*")
                        .allowCredentials(true)
                        .allowedHeaders(allowHeaders)
                        .allowedMethods(allowMethods));

                // add routes here
// health route
                HealthService healthService = new HealthService(this.mongoDatabase);
                addRoute(HttpMethod.GET, MicroserviceRoutingURLNames.HEALTH_URL, new HealthHandler(healthService));

// ── Faculty routes ──────────────────────────────
                FacultyService facultyService = new FacultyService(this.mongoDatabase);
                DutyService dutyService = new DutyService(this.mongoDatabase);
                DutyAssignmentService dutyAssignmentService = new DutyAssignmentService(this.mongoDatabase);
                SwapRequestService swapRequestService = new SwapRequestService(this.mongoDatabase);
                AuditLogService auditLogService = new AuditLogService(this.mongoDatabase);

                addRoute(HttpMethod.GET, MicroserviceRoutingURLNames.FACULTY_GET_ALL_URL, new FacultyHandler(facultyService));
                addRoute(HttpMethod.GET, MicroserviceRoutingURLNames.FACULTY_GET_BY_ID_URL, new FacultyHandler(facultyService));
                addRoute(
                        HttpMethod.POST,
                        MicroserviceRoutingURLNames.FACULTY_ADD_URL,
                        new FacultyHandler(facultyService)
                );
                addRoute(
                        HttpMethod.PUT,
                        MicroserviceRoutingURLNames.FACULTY_UPDATE_STATUS_URL,
                        new FacultyHandler(facultyService)
                );
                addRoute(
                        HttpMethod.GET,
                        MicroserviceRoutingURLNames.DUTY_GET_ALL_URL,
                        new DutyHandler(dutyService)
                );

                addRoute(
                        HttpMethod.GET,
                        MicroserviceRoutingURLNames.DUTY_GET_BY_ID_URL,
                        new DutyHandler(dutyService)
                );

                addRoute(
                        HttpMethod.POST,
                        MicroserviceRoutingURLNames.DUTY_ADD_URL,
                        new DutyHandler(dutyService)
                );

                addRoute(
                        HttpMethod.GET,
                        MicroserviceRoutingURLNames.ASSIGNMENT_GET_ALL_URL,
                        new DutyAssignmentHandler(dutyAssignmentService)
                );

                addRoute(
                        HttpMethod.GET,
                        MicroserviceRoutingURLNames.ASSIGNMENT_GET_BY_FACULTY_URL,
                        new DutyAssignmentHandler(dutyAssignmentService)
                );

                addRoute(
                        HttpMethod.POST,
                        MicroserviceRoutingURLNames.ASSIGNMENT_ADD_URL,
                        new DutyAssignmentHandler(dutyAssignmentService)
                );
                addRoute(
                        HttpMethod.GET,
                        MicroserviceRoutingURLNames.SWAP_GET_ALL_URL,
                        new SwapRequestHandler(swapRequestService)
                );

                addRoute(
                        HttpMethod.GET,
                        MicroserviceRoutingURLNames.SWAP_GET_BY_STATUS_URL,
                        new SwapRequestHandler(swapRequestService)
                );

                addRoute(
                        HttpMethod.POST,
                        MicroserviceRoutingURLNames.SWAP_ADD_URL,
                        new SwapRequestHandler(swapRequestService)
                );

                addRoute(
                        HttpMethod.GET,
                        MicroserviceRoutingURLNames.AUDIT_GET_ALL_URL,
                        new AuditLogHandler(auditLogService)
                );

                addRoute(
                        HttpMethod.GET,
                        MicroserviceRoutingURLNames.AUDIT_GET_BY_ACTION_URL,
                        new AuditLogHandler(auditLogService)
                );

                addRoute(
                        HttpMethod.POST,
                        MicroserviceRoutingURLNames.AUDIT_ADD_URL,
                        new AuditLogHandler(auditLogService)
                );
        }
        /**
         * Helper method to add routes with the specified method, path, and handler.
         *
         * @param method  the HTTP method
         * @param path    the URL path
         * @param handler the request handler
         */
        private void addRoute(HttpMethod method, String path, Handler<RoutingContext> handler) {
                this.router.route(method, ContextRoutingURLName.MICROSERVICE_CONTEXT_URL_NAME.concat(path))
                                .handler(BodyHandler.create())
                                .blockingHandler(handler);
        }
}
