package in.edu.kristujayanti.constants;

public interface MicroserviceRoutingURLNames {

    String API_WILDCARD_URL = "*";
    String HEALTH_URL       = "/health";

    // Faculty URLs
    String FACULTY_GET_ALL_URL = "/faculty";
    String FACULTY_GET_BY_ID_URL = "/faculty/:id";
    String FACULTY_ADD_URL = "/faculty/add";
    String FACULTY_UPDATE_STATUS_URL = "/faculty/update-status/:id";
    String FACULTY_DELETE_URL="/faculty/:id";
    String FACULTY_EDIT_URL="/faculty/edit/:id";

    // Duty URLs
    String DUTY_GET_ALL_URL              = "/duties";
    String DUTY_GET_BY_ID_URL            = "/duties/:id";
    String DUTY_ADD_URL                  = "/duties/add";
    String DUTY_UPDATE_URL               = "/duties/update/:id";
    String DUTY_DELETE_URL               = "/duties/:id";

    // Duty Assignment URLs
    String ASSIGNMENT_GET_ALL_URL = "/assignments";
    String ASSIGNMENT_ADD_URL = "/assignments/assign";
    String ASSIGNMENT_GET_BY_FACULTY_URL = "/assignments/faculty/:facultyId";

    // Swap Request URLs
    String SWAP_GET_ALL_URL = "/swap-requests";
    String SWAP_ADD_URL = "/swap-requests/add";
    String SWAP_GET_BY_STATUS_URL = "/swap-requests/status/:status";
    String SWAP_ADMIN_APPROVE_URL="/swap-requests/admin-approve/:id";
    String SWAP_ADMIN_REJECT_URL="/swap-requests/admin-reject/:id";

    // Audit Log URLs
    String AUDIT_GET_ALL_URL = "/audit-logs";
    String AUDIT_ADD_URL = "/audit-logs/add";
    String AUDIT_GET_BY_ACTION_URL = "/audit-logs/action/:action";
}