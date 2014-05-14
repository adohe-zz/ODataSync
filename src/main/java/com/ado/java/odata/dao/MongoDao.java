package com.ado.java.odata.dao;

import com.ado.java.odata.mongo.MongoManager;
import com.ado.java.odata.parser.*;
import com.ado.java.odata.pool.ConnectionPool;
import com.ado.java.odata.util.StringHelper;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: nankonami
 * Date: 14-5-10
 * Time: 下午5:34
 * To change this template use File | Settings | File Templates.
 */
public class MongoDao {


    /**
     * Insert a new document into entities collection
     *
     * @param metadata
     */
    public static void updateEntities(String tableName, TableMetadata metadata) {

        String pkName = null;

        Map<String, ColumnMetadata> columns = metadata.getColumns();
        Map<String, PrimaryKeyMetadata> pkMeta = metadata.getPrimaryKeys();
        Map<String, ForeignKeyMetadata> fkMeta  = metadata.getForeignKeys();

        // Update the entities collection
        MongoManager mongoManager = MongoManager.getInstance("localhost", 27017, 10, 10);
        DB odata = mongoManager.getDB("odata");

        // Fetch the entities collection
        DBCollection entities = odata.getCollection("entities");

        // Update the document
        BasicDBObject query = new BasicDBObject("Table", tableName);

        BasicDBObject object = new BasicDBObject();
        BasicDBObject properties = new BasicDBObject();

        // Reverse the primary key
        Iterator pkIterator = pkMeta.entrySet().iterator();
        while (pkIterator.hasNext()) {
            Map.Entry<String, PrimaryKeyMetadata> p = (Map.Entry<String, PrimaryKeyMetadata>)pkIterator.next();
            BasicDBObject pk = new BasicDBObject();
            PrimaryKeyMetadata pMeta = p.getValue();
            ColumnMetadata pColumn = pMeta.getColumnMetadata()[0];
            pkName = pColumn.getName();
            pk.append("type", "id").append("key", true).append("computed", true)
                .append("required", true).append("nullable", (pColumn.getNullable() == "YES" ? true : false))
                .append("maxLength", pColumn.getColumnSize());
            properties.append("id", pk);
        }

        // Reverse the columns
        Iterator clIterator = columns.entrySet().iterator();
        while (clIterator.hasNext()) {
            Map.Entry<String, ColumnMetadata> column = (Map.Entry<String, ColumnMetadata>)clIterator.next();
            ColumnMetadata cMeta = column.getValue();
            // Skip the primary key column
            if (cMeta.getName().equalsIgnoreCase(pkName))
                continue;
            BasicDBObject c = new BasicDBObject();

            c.append("type", StringHelper.getType(cMeta.getTypeName()))
                    .append("nullable", (cMeta.getNullable() == "YES" ? true : false));
            properties.append(cMeta.getName(), c);
        }

        // Append the properties sub-document
        object.put("Properties", properties);
        object.put("Status", "running");

        BasicDBObject objectSetValue = new BasicDBObject("$set", object);

        // Update the collection
        entities.update(query, objectSetValue);
        System.out.println("Meta data sync finish....");
    }

    private static boolean isFkRef(Map<String, ForeignKeyMetadata> fks, String key) {
        Iterator iterator = fks.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, ForeignKeyMetadata> fk = (Map.Entry<String, ForeignKeyMetadata>)iterator.next();
            ForeignKeyMetadata fkMeta = fk.getValue();
            Map<String, String> refs = fkMeta.getReferences();
            Set<String> keys = refs.keySet();
            if (keys.contains(key)) {
                return true;
            }
        }

        return false;
    }

    private static String getRefTable(Map<String, ForeignKeyMetadata> fks, String key) {
        Iterator iterator = fks.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, ForeignKeyMetadata> fk = (Map.Entry<String, ForeignKeyMetadata>)iterator.next();
            ForeignKeyMetadata fkMeta = fk.getValue();
            Map<String, String> refs = fkMeta.getReferences();
            Set<String> keys = refs.keySet();
            if (keys.contains(key)) {
                return fkMeta.getRefTable();
            }
        }

        return null;
    }

    /**
     * If the table exists
     * @param tableName
     * @return true if table exists otherwise false
     */
    public static boolean existsTable(String tableName) {
        MongoManager mongoManager = MongoManager.getInstance("localhost", 27017, 10, 10);
        DB odata = mongoManager.getDB("odata");

        DBCollection entities = odata.getCollection("entities");
        BasicDBObject query = new BasicDBObject("Table", tableName);
        DBCursor cursor = entities.find(query);
        if (cursor.hasNext()) {
            return true;
        }

        return false;
    }

    /**
     * Sync table data to mongodb collection
     * @param tableName {@link java.lang.String} table name
     * @param connection {@link java.sql.Connection} a valid connection
     * @param sql {@link java.lang.String} sql string
     */
    public static void syncData(String tableName, Connection connection, String sql) {
        if (tableName == null) {
            return;
        }

        if (connection == null) {
            return;
        }

        try {
            // Get the columns info
            TableMetadata metadata = (TableMetadata)MetadataParser.parser(connection, tableName);
            Map<String, ColumnMetadata> columns = metadata.getColumns();
            Map<String, PrimaryKeyMetadata> pkMeta = metadata.getPrimaryKeys();

            // Get the primary key column name
            Map.Entry<String, PrimaryKeyMetadata> primaryKey = pkMeta.entrySet().iterator().next();
            String primaryKeyName = primaryKey.getValue().getColumnMetadata()[0].getName();

            // Generate the column metadata list
            Iterator iterator = columns.entrySet().iterator();
            List<ColumnMetadata> names = new ArrayList<ColumnMetadata>();
            while (iterator.hasNext()) {
                Map.Entry<String, ColumnMetadata> columnMetadata = (Map.Entry<String, ColumnMetadata>)iterator.next();
                names.add(columnMetadata.getValue());
            }

            // Update the entities collection
            MongoManager mongoManager = MongoManager.getInstance("localhost", 27017, 10, 10);
            DB odata = mongoManager.getDB("odata");

            // Fetch the entities collection
            DBCollection collection = odata.getCollection("entities");

            BasicDBObject object = new BasicDBObject();
            Statement statement = connection.createStatement();
            String s = sql + tableName;

            ResultSet rs = statement.executeQuery(s);
            while (rs.next()) {
                for (int i = 0; i < names.size(); i++) {
                    if (names.get(i).getName().equals(primaryKeyName)) {
                        object.append("id", rs.getString(names.get(i).getName()));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
