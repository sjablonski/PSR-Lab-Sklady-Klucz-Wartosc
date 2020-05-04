package LigaSportowaCosmosDB.Sql;

import LigaSportowaCosmosDB.IDocument;
import com.microsoft.azure.documentdb.*;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;
import org.beryx.textio.TextTerminal;

import java.io.IOException;
import java.util.List;

public abstract class SqlApi {
    private final static TextIO textIO = TextIoFactory.getTextIO();
    private final static TextTerminal<?> terminal = textIO.getTextTerminal();
    private static final String serviceEndpoint = "https://sjabl.documents.azure.com:443/";
    private static final String masterKey = "S9hAG9NItF1p8suTCTmBj1LCcxioiDg1JVCDtcsWsT9MUJMpTCXlcisug75vtjyARNKByAY9dBCr49JApcghwg==";
    private static final DocumentClient client = new DocumentClient(
            serviceEndpoint,
            masterKey,
            new ConnectionPolicy(),
            ConsistencyLevel.Session
    );

    public static void createDatabaseIfNotExists(String databaseName) throws DocumentClientException, IOException {
        String databaseLink = String.format("/dbs/%s", databaseName);

        try {
            client.readDatabase(databaseLink, null);
            terminal.println(String.format("Found %s", databaseName));
        } catch (DocumentClientException de) {
            if (de.getStatusCode() == 404) {
                Database database = new Database();
                database.setId(databaseName);
                client.createDatabase(database, null);
                terminal.println(String.format("Created %s", databaseName));
            } else {
                throw de;
            }
        }
    }

    public static void createDocumentCollectionIfNotExists(String databaseName, String collectionName) throws IOException,
            DocumentClientException {
        String databaseLink = String.format("/dbs/%s", databaseName);
        String collectionLink = String.format("/dbs/%s/colls/%s", databaseName, collectionName);

        try {
            client.readCollection(collectionLink, null);
            terminal.println(String.format("Found %s", collectionName));
        } catch (DocumentClientException de) {
            if (de.getStatusCode() == 404) {
                DocumentCollection collectionInfo = new DocumentCollection();
                collectionInfo.setId(collectionName);

                RangeIndex index = new RangeIndex(DataType.String);
                index.setPrecision(-1);

                collectionInfo.setIndexingPolicy(new IndexingPolicy(new Index[] { index }));

                RequestOptions requestOptions = new RequestOptions();
                requestOptions.setOfferThroughput(400);

                client.createCollection(databaseLink, collectionInfo, requestOptions);
                terminal.println(String.format("Created %s", collectionName));
            } else {
                throw de;
            }
        }

    }

    public static void createDocumentIfNotExists(String databaseName, String collectionName, IDocument document)
            throws DocumentClientException, IOException {
        try {
            String documentLink = String.format("/dbs/%s/colls/%s/docs/%s", databaseName, collectionName, document.getId());
            client.readDocument(documentLink, new RequestOptions());
        } catch (DocumentClientException de) {
            if (de.getStatusCode() == 404) {
                String collectionLink = String.format("/dbs/%s/colls/%s", databaseName, collectionName);
                client.createDocument(collectionLink, document, new RequestOptions(), true);
                terminal.println(String.format("Created document: %s", document.getId()));
            } else {
                throw de;
            }
        }
    }

    public static Document getDocumentById(String databaseName, String collectionName, String id) {
        String collectionLink = String.format("/dbs/%s/colls/%s", databaseName, collectionName);
        List<Document> documentList = client.queryDocuments(collectionLink,"SELECT * FROM root r WHERE r.id='" + id + "'", null)
                .getQueryIterable()
                .toList();

        if (documentList.size() > 0) {
            return documentList.get(0);
        } else {
            return null;
        }
    }

    public static FeedResponse<Document> executeSimpleQuery(String databaseName, String collectionName, String[] keys, String where) {
        FeedOptions queryOptions = new FeedOptions();
        queryOptions.setPageSize(-1);
        queryOptions.setEnableCrossPartitionQuery(true);

        for(int i=0; i<keys.length; i++) {
            if(!keys[i].equals("*")) {
                keys[i] = "c." + keys[i];
            }
        }

        if(where == null || where.equals("")) {
            where = "";
        } else {
            where = "WHERE " + where;
        }

        String collectionLink = String.format("/dbs/%s/colls/%s", databaseName, collectionName);
        String query = String.format("SELECT %s FROM c %s", String.join(", ", keys), where);
        return client.queryDocuments(collectionLink, query, queryOptions);
    }

    public static FeedResponse<Document> executeQuery(String databaseName, String collectionName, String query) {
        FeedOptions queryOptions = new FeedOptions();
        queryOptions.setPageSize(-1);
        queryOptions.setEnableCrossPartitionQuery(true);

        String collectionLink = String.format("/dbs/%s/colls/%s", databaseName, collectionName);
        return client.queryDocuments(collectionLink, query, queryOptions);
    }

    public static void replaceDocument(String databaseName, String collectionName, IDocument updatedDocument)
            throws IOException, DocumentClientException {
        try {
            client.replaceDocument(
                    String.format("/dbs/%s/colls/%s/docs/%s", databaseName, collectionName, updatedDocument.getId()), updatedDocument,
                    null);
        } catch (DocumentClientException de) {
            throw de;
        }
    }

    public static void deleteDocument(String databaseName, String collectionName, String documentName) throws IOException,
            DocumentClientException {
        try {
            client.deleteDocument(String.format("/dbs/%s/colls/%s/docs/%s", databaseName, collectionName, documentName), null);
            terminal.println(String.format("Deleted document: %s", documentName));
        } catch (DocumentClientException de) {
            throw de;
        }
    }
}
