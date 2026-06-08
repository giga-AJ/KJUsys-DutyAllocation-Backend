package in.edu.kristujayanti.services;

import com.mongodb.client.MongoDatabase;
import in.edu.kristujayanti.constants.CollectionNames;
import in.edu.kristujayanti.dbaccess.MongoDataAccess;
import in.edu.kristujayanti.exception.DataAccessException;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class DutyAssignmentService extends MongoDataAccess {

    private final MongoDatabase mongoDatabase;

    public DutyAssignmentService(MongoDatabase mongoDatabase) {
        this.mongoDatabase = mongoDatabase;
    }

    public List<Document> getAllAssignments()
            throws DataAccessException {

        List<Document> assignmentList = new ArrayList<>();

        mongoDatabase
                .getCollection(CollectionNames.DUTY_ASSIGNMENTS)
                .find()
                .into(assignmentList);

        return assignmentList;
    }

    public boolean addAssignment(Document assignmentDoc)
            throws DataAccessException {

        mongoDatabase
                .getCollection(CollectionNames.DUTY_ASSIGNMENTS)
                .insertOne(assignmentDoc);

        return true;
    }

    public List<Document> getAssignmentsByFaculty(
            String facultyId
    ) throws DataAccessException {

        List<Document> assignmentList = new ArrayList<>();

        mongoDatabase
                .getCollection(CollectionNames.DUTY_ASSIGNMENTS)
                .find(new Document("facultyId", facultyId))
                .into(assignmentList);

        return assignmentList;
    }
}