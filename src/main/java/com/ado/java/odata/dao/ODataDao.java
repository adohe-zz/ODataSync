package com.ado.java.odata.dao;

import com.ado.java.odata.mongo.MongoManager;
import com.ado.java.odata.parser.*;
import com.ado.java.odata.pool.ConnectionPool;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: nankonami
 * Date: 14-5-4
 * Time: 下午10:29
 * To change this template use File | Settings | File Templates.
 */
@Component
public class ODataDao {

    @Value("#{configProperties['db.user.name']}")
    private String userName;

    @Value("#{configProperties['db.user.password']}")
    private String password;

    @Value("#{configProperties['db.url']}")
    private String url;

    /**
     * Sync meta-data for a table
     *
     * @param tableName The table name
     */
    private void syncMetadata(String tableName) {
        try {
            ConnectionPool pool = ConnectionPool.getPool(userName, password, url);
            Connection connection = pool.getConnection();

            TableMetadata metadata = (TableMetadata)MetadataParser.parser(connection, tableName);


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sync data for {@link java.lang.String} table name
     * @param tableName The table name
     */
    public void syncData(String tableName) {
        try {
            ConnectionPool pool = ConnectionPool.getPool(userName, password, url);
            Connection connection = pool.getConnection();

            TableMetadata metadata = (TableMetadata)MetadataParser.parser(connection, tableName);

            Map<String, ColumnMetadata> columns = metadata.getColumns();
            Map<String, PrimaryKeyMetadata> pkMeta = metadata.getPrimaryKeys();
            Map<String, IndexMetadata> indexes = metadata.getIndexes();
            Map<String, ForeignKeyMetadata> fkMeta = metadata.getForeignKeys();

            // Update the entities collection
            MongoManager mongoManager = MongoManager.getInstance("localhost", 27017, 10, 10);
            DB odata = mongoManager.getDB("odata");

            // Fetch the entities collection
            DBCollection entities = odata.getCollection("entities");

            // Create a new document
            BasicDBObject object = new BasicDBObject("EntityName", "Buyer Insight")
                    .append("ProjectName", "FirstPro").append("Source", "buyer").append("Table", tableName)
                    .append("Cache", "no cache").append("Env", "dev").append("Status", "pending").append("RowCount", 0);
            BasicDBObject properties = new BasicDBObject();

            // Reverse the columns
            Iterator iterator = columns.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, ColumnMetadata> entry = (Map.Entry)iterator.next();
                String key = entry.getKey();
                ColumnMetadata columnMetadata = entry.getValue();

                BasicDBObject column = new BasicDBObject();
                column.append("type", columnMetadata.getTypeName())
                    .append("nullable", columnMetadata.getNullable() == "YES" ? true : false);
                properties.append(key, column);
            }

            object.append("Properties", properties);

            // Insert into collection
            entities.insert(object);
            Set<String> collections = odata.getCollectionNames();
            System.out.println(collections.size());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
