package in.edu.kristujayanti.services;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import in.edu.kristujayanti.constants.CollectionNames;
import in.edu.kristujayanti.dbaccess.MongoDataAccess;
import in.edu.kristujayanti.exception.DataAccessException;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Projections.excludeId;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;
import static com.mongodb.client.model.Sorts.ascending;

public class DutyService extends MongoDataAccess {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(DutyService.class);

    private final MongoDatabase mongoDatabase;

    public DutyService(MongoDatabase mongoDatabase) {
        this.mongoDatabase = mongoDatabase;
    }

    // ─────────────────────────────────────────
    // GET ALL DUTIES
    // ─────────────────────────────────────────
    public List<Document> getAllDuties() throws DataAccessException {

        LOGGER.info("Fetching all duties");

        FindIterable<Document> result = findDocuments(
                mongoDatabase,
                CollectionNames.DUTIES,
                new Document(),
                fields(
                        include(
                                "dutyType",
                                "eventName",
                                "department",
                                "date",
                                "timeSlot",
                                "venue",
                                "description",
                                "dutyStatus",
                                "requiredFaculty"
                        ),
                        excludeId()
                ),
                ascending("date")
        );

        List<Document> dutyList = new ArrayList<>();
        result.into(dutyList);

        return dutyList;
    }

    // ─────────────────────────────────────────
    // GET DUTY BY ID
    // ─────────────────────────────────────────
    public Document getDutyById(String id)
            throws DataAccessException {

        LOGGER.info("Fetching duty by id: {}", id);

        Bson filter = eq("_id", new org.bson.types.ObjectId(id));

        FindIterable<Document> result = findDocuments(
                mongoDatabase,
                CollectionNames.DUTIES,
                filter,
                new Document(),
                new Document()
        );

        return result.first();
    }

    // ─────────────────────────────────────────
    // FILTER BY DUTY TYPE
    // ─────────────────────────────────────────
    public List<Document> getDutiesByType(String dutyType)
            throws DataAccessException {

        Bson filter = eq("dutyType", dutyType);

        FindIterable<Document> result = findDocuments(
                mongoDatabase,
                CollectionNames.DUTIES,
                filter,
                new Document(),
                ascending("date")
        );

        List<Document> dutyList = new ArrayList<>();
        result.into(dutyList);

        return dutyList;
    }

    // ─────────────────────────────────────────
    // FILTER BY DEPARTMENT
    // ─────────────────────────────────────────
    public List<Document> getDutiesByDepartment(String department)
            throws DataAccessException {

        Bson filter = eq("department", department);

        FindIterable<Document> result = findDocuments(
                mongoDatabase,
                CollectionNames.DUTIES,
                filter,
                new Document(),
                ascending("date")
        );

        List<Document> dutyList = new ArrayList<>();
        result.into(dutyList);

        return dutyList;
    }

    // ─────────────────────────────────────────
    // FILTER BY DATE
    // ─────────────────────────────────────────
    public List<Document> getDutiesByDate(String date)
            throws DataAccessException {

        Bson filter = eq("date", date);

        FindIterable<Document> result = findDocuments(
                mongoDatabase,
                CollectionNames.DUTIES,
                filter,
                new Document(),
                ascending("date")
        );

        List<Document> dutyList = new ArrayList<>();
        result.into(dutyList);

        return dutyList;
    }

    // ─────────────────────────────────────────
    // ADD DUTY
    // ─────────────────────────────────────────
    public boolean addDuty(Document dutyDoc)
            throws DataAccessException {

        mongoDatabase
                .getCollection(CollectionNames.DUTIES)
                .insertOne(dutyDoc);

        return true;
    }

    public boolean updateDuty(
            String dutyId,
            Document updatedDuty
    ) throws DataAccessException {

        Document filter =
                new Document("_id",
                        new org.bson.types.ObjectId(dutyId));

        Document update =
                new Document("$set", updatedDuty);

        var result =
                mongoDatabase
                        .getCollection(CollectionNames.DUTIES)
                        .updateOne(filter, update);

        return result.getModifiedCount() > 0;
    }

    public boolean deleteDuty(
            String dutyId
    ) throws DataAccessException {

        Document filter =
                new Document("_id",
                        new org.bson.types.ObjectId(dutyId));

        var result =
                mongoDatabase
                        .getCollection(CollectionNames.DUTIES)
                        .deleteOne(filter);

        return result.getDeletedCount() > 0;
    }
}