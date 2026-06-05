package in.edu.kristujayanti.services;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import in.edu.kristujayanti.constants.CollectionNames;
import in.edu.kristujayanti.dbaccess.MongoDataAccess;
import in.edu.kristujayanti.exception.DataAccessException;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Projections.*;
import static com.mongodb.client.model.Sorts.ascending;

public class FacultyService extends MongoDataAccess {

    private static final Logger LOGGER = LoggerFactory.getLogger(FacultyService.class);
    private final MongoDatabase mongoDatabase;

    // Constructor — same pattern as HealthService
    public FacultyService(MongoDatabase mongoDatabase) {
        this.mongoDatabase = mongoDatabase;
    }

    // ─────────────────────────────────────────
    // 1. GET ALL FACULTY
    // Powers → Faculty Management screen
    // ─────────────────────────────────────────
    public List<Document> getAllFaculty() throws DataAccessException {
        LOGGER.info("Fetching all faculty");

        // filter — only Faculty role, not Admin
        Bson filter = eq("role", "Faculty");

        // projection — which fields to return
        Bson projection = fields(
                include("employeeId", "name", "email",
                        "department", "designation",
                        "role", "status"),
                excludeId()
        );

        // sort — alphabetical by name
        Bson sort = ascending("name");

        // call MongoDataAccess method
        FindIterable<Document> result = findDocuments(
                mongoDatabase,
                CollectionNames.FACULTY,
                filter,
                projection,
                sort
        );

        // convert to list and return
        List<Document> facultyList = new ArrayList<>();
        result.into(facultyList);
        return facultyList;
    }

    // ─────────────────────────────────────────
    // 2. GET FACULTY BY ID
    // Powers → View faculty profile
    // ─────────────────────────────────────────
    public Document getFacultyById(String id) throws DataAccessException {
        LOGGER.info("Fetching faculty by id: {}", id);

        Bson filter = eq("employeeId", id);

        Bson projection = fields(
                include("employeeId", "name", "email",
                        "department", "designation",
                        "role", "status"),
                excludeId()
        );

        Bson sort = new Document();

        FindIterable<Document> result = findDocuments(
                mongoDatabase,
                CollectionNames.FACULTY,
                filter,
                projection,
                sort
        );

        return result.first();
    }

    // ─────────────────────────────────────────
    // 3. GET FACULTY BY DEPARTMENT
    // Powers → Filter by department dropdown
    // ─────────────────────────────────────────
    public List<Document> getFacultyByDepartment(String department) throws DataAccessException {
        LOGGER.info("Fetching faculty by department: {}", department);

        Bson filter = and(
                eq("role", "Faculty"),
                eq("department", department)
        );

        Bson projection = fields(
                include("employeeId", "name", "email",
                        "department", "designation", "status"),
                excludeId()
        );

        Bson sort = ascending("name");

        FindIterable<Document> result = findDocuments(
                mongoDatabase,
                CollectionNames.FACULTY,
                filter,
                projection,
                sort
        );

        List<Document> facultyList = new ArrayList<>();
        result.into(facultyList);
        return facultyList;
    }

    // ─────────────────────────────────────────
    // 4. GET FACULTY BY STATUS
    // Powers → Filter Active/OnLeave/Unavailable
    // ─────────────────────────────────────────
    public List<Document> getFacultyByStatus(String status) throws DataAccessException {
        LOGGER.info("Fetching faculty by status: {}", status);

        Bson filter = and(
                eq("role", "Faculty"),
                eq("status", status)
        );

        Bson projection = fields(
                include("employeeId", "name", "email",
                        "department", "designation", "status"),
                excludeId()
        );

        Bson sort = ascending("name");

        FindIterable<Document> result = findDocuments(
                mongoDatabase,
                CollectionNames.FACULTY,
                filter,
                projection,
                sort
        );

        List<Document> facultyList = new ArrayList<>();
        result.into(facultyList);
        return facultyList;
    }

    // ─────────────────────────────────────────
    // 5. SEARCH FACULTY BY NAME
    // Powers → Search bar in Faculty Management
    // ─────────────────────────────────────────
    public List<Document> searchFacultyByName(String name) throws DataAccessException {
        LOGGER.info("Searching faculty by name: {}", name);

        // regex search — like $regex in MongoDB
        Bson filter = and(
                eq("role", "Faculty"),
                regex("name", name, "i")
        );

        Bson projection = fields(
                include("employeeId", "name", "email",
                        "department", "designation", "status"),
                excludeId()
        );

        Bson sort = ascending("name");

        FindIterable<Document> result = findDocuments(
                mongoDatabase,
                CollectionNames.FACULTY,
                filter,
                projection,
                sort
        );

        List<Document> facultyList = new ArrayList<>();
        result.into(facultyList);
        return facultyList;
    }

    // ─────────────────────────────────────────
    // 6. UPDATE FACULTY STATUS
    // Powers → Mark OnLeave / Unavailable / Active
    // ─────────────────────────────────────────


    // ─────────────────────────────────────────
    // 7. GET AVAILABILITY FOR ASSIGN DUTY SCREEN
    // Powers → Faculty list with Available/Busy badges
    // ─────────────────────────────────────────

    public List<Document> getFacultyAvailability(String date, String timeSlot) throws DataAccessException {
        LOGGER.info("Checking availability for date={}, timeSlot={}", date, timeSlot);

        List<Bson> pipeline = Arrays.asList(

                // Step 1 — only Faculty role
                new Document("$match",
                        new Document("role", "Faculty")
                ),

                // Step 2 — join with duty_assignments
                new Document("$lookup",
                        new Document("from", CollectionNames.DUTY_ASSIGNMENTS)
                                .append("let", new Document("fid", "$_id"))
                                .append("pipeline", List.of(
                                        new Document("$match",
                                                new Document("$expr",
                                                        new Document("$and", Arrays.asList(
                                                                new Document("$eq", Arrays.asList("$facultyId", "$$fid")),
                                                                new Document("$eq", Arrays.asList("$date", date)),
                                                                new Document("$eq", Arrays.asList("$timeSlot", timeSlot)),
                                                                new Document("$in", Arrays.asList(
                                                                        "$approvalStatus",
                                                                        Arrays.asList("Approved", "Pending")
                                                                ))
                                                        ))
                                                )
                                        )
                                ))
                                .append("as", "currentDuties")
                ),

                // Step 3 — calculate availability label
                new Document("$addFields",
                        new Document("availabilityStatus",
                                new Document("$switch",
                                        new Document("branches", Arrays.asList(
                                                new Document("case",
                                                        new Document("$eq", Arrays.asList("$status", "OnLeave")))
                                                        .append("then", "OnLeave"),
                                                new Document("case",
                                                        new Document("$eq", Arrays.asList("$status", "Unavailable")))
                                                        .append("then", "Unavailable"),
                                                new Document("case",
                                                        new Document("$gt", Arrays.asList(
                                                                new Document("$size", "$currentDuties"), 0)))
                                                        .append("then", "Busy")
                                        ))
                                                .append("default", "Available")
                                )
                        )
                ),

                // Step 4 — show only needed fields
                new Document("$project",
                        new Document("employeeId", 1)
                                .append("name", 1)
                                .append("department", 1)
                                .append("designation", 1)
                                .append("availabilityStatus", 1)
                ),

                // Step 5 — Available first
                new Document("$sort",
                        new Document("availabilityStatus", 1)
                )
        );

        AggregateIterable<Document> result = aggregateDocuments(
                mongoDatabase,
                CollectionNames.FACULTY,
                pipeline
        );

        List<Document> availabilityList = new ArrayList<>();
        result.into(availabilityList);
        return availabilityList;
    }


    // ─────────────────────────────────────────
// 6. ADD FACULTY
// Powers → Faculty Creation Screen
// ─────────────────────────────────────────
    public boolean addFaculty(Document facultyDoc) throws DataAccessException {

        LOGGER.info("Adding faculty: {}", facultyDoc.getString("employeeId"));

        // Check duplicate employeeId
        Bson filter = eq("employeeId", facultyDoc.getString("employeeId"));

        FindIterable<Document> existing = findDocuments(
                mongoDatabase,
                CollectionNames.FACULTY,
                filter,
                new Document(),
                new Document()
        );

        if (existing.first() != null) {
            return false;
        }

        mongoDatabase
                .getCollection(CollectionNames.FACULTY)
                .insertOne(facultyDoc);

        return true;
    }

    // ─────────────────────────────────────────
// 7. UPDATE FACULTY STATUS
// Powers → Active / OnLeave / Unavailable
// ─────────────────────────────────────────
    public boolean updateFacultyStatus(String employeeId,
                                       String status)
            throws DataAccessException {

        LOGGER.info("Updating status for {} to {}",
                employeeId,
                status);

        Document filter =
                new Document("employeeId", employeeId);

        Document update =
                new Document("$set",
                        new Document("status", status));

        var result = mongoDatabase
                .getCollection(CollectionNames.FACULTY)
                .updateOne(filter, update);

        return result.getModifiedCount() > 0;
    }
}