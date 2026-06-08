package in.edu.kristujayanti.services;

import com.mongodb.client.MongoDatabase;
import in.edu.kristujayanti.constants.CollectionNames;
import in.edu.kristujayanti.dbaccess.MongoDataAccess;
import in.edu.kristujayanti.exception.DataAccessException;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class AuditLogService extends MongoDataAccess {

    private final MongoDatabase mongoDatabase;

    public AuditLogService(MongoDatabase mongoDatabase) {
        this.mongoDatabase = mongoDatabase;
    }

    public List<Document> getAllAuditLogs()
            throws DataAccessException {

        List<Document> auditLogList =
                new ArrayList<>();

        mongoDatabase
                .getCollection(CollectionNames.AUDIT_LOGS)
                .find()
                .into(auditLogList);

        return auditLogList;
    }

    public boolean addAuditLog(
            Document auditLogDoc
    ) throws DataAccessException {

        mongoDatabase
                .getCollection(CollectionNames.AUDIT_LOGS)
                .insertOne(auditLogDoc);

        return true;
    }

    public List<Document> getAuditLogsByAction(
            String action
    ) throws DataAccessException {

        List<Document> auditLogList =
                new ArrayList<>();

        mongoDatabase
                .getCollection(CollectionNames.AUDIT_LOGS)
                .find(new Document("action", action))
                .into(auditLogList);

        return auditLogList;
    }
}