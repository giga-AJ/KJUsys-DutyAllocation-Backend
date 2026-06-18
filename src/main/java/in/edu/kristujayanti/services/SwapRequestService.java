package in.edu.kristujayanti.services;

import com.mongodb.client.MongoDatabase;
import in.edu.kristujayanti.constants.CollectionNames;
import in.edu.kristujayanti.dbaccess.MongoDataAccess;
import in.edu.kristujayanti.exception.DataAccessException;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class SwapRequestService extends MongoDataAccess {

    private final MongoDatabase mongoDatabase;

    public SwapRequestService(MongoDatabase mongoDatabase) {
        this.mongoDatabase = mongoDatabase;
    }

    public List<Document> getAllSwapRequests()
            throws DataAccessException {

        List<Document> requestList =
                new ArrayList<>();

        mongoDatabase
                .getCollection(CollectionNames.SWAP_REQUESTS)
                .find()
                .into(requestList);

        return requestList;
    }

    public boolean addSwapRequest(
            Document swapRequestDoc
    ) throws DataAccessException {

        mongoDatabase
                .getCollection(CollectionNames.SWAP_REQUESTS)
                .insertOne(swapRequestDoc);

        return true;
    }

    public List<Document> getSwapRequestsByStatus(
            String status
    ) throws DataAccessException {

        List<Document> requestList =
                new ArrayList<>();

        mongoDatabase
                .getCollection(CollectionNames.SWAP_REQUESTS)
                .find(new Document("status", status))
                .into(requestList);

        return requestList;
    }
    public boolean updateSwapStatus(
            String requestId,
            String status
    ) throws DataAccessException {

        Document filter =
                new Document(
                        "_id",
                        new org.bson.types.ObjectId(requestId)
                );

        Document update =
                new Document(
                        "$set",
                        new Document("status", status)
                );

        var result =
                mongoDatabase
                        .getCollection(
                                CollectionNames.SWAP_REQUESTS
                        )
                        .updateOne(filter, update);

        return result.getModifiedCount() > 0;
    }
}