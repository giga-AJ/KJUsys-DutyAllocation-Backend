package in.edu.kristujayanti.constants;

public interface MicroserviceRoutingURLNames {

    String API_WILDCARD_URL = "*";
    String HEALTH_URL       = "/health";

    // Faculty URLs
    String FACULTY_GET_ALL_URL = "/faculty";
    String FACULTY_GET_BY_ID_URL = "/faculty/:id";
    String FACULTY_ADD_URL = "/faculty/add";
    String FACULTY_UPDATE_STATUS_URL = "/faculty/update-status/:id";
}