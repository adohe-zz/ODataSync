package com.ado.java.odata.dao;

import com.ado.java.odata.mongo.MongoManager;
import com.ado.java.odata.parser.ColumnMetadata;
import com.ado.java.odata.parser.ForeignKeyMetadata;
import com.ado.java.odata.parser.PrimaryKeyMetadata;
import com.ado.java.odata.parser.TableMetadata;
import com.ado.java.odata.util.StringHelper;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

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

        // Create a new document
        BasicDBObject object = new BasicDBObject("EntityName", "Buyer Insight")
                .append("ProjectName", "FirstPro").append("Source", "buyer").append("Table", tableName)
                .append("Cache", "no cache").append("Env", "dev").append("Status", "pending").append("RowCount", 0);
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
            String ck = column.getKey();
            ColumnMetadata cMeta = column.getValue();
            // Skip the primary key column
            if (cMeta.getName().equalsIgnoreCase(pkName))
                continue;
            BasicDBObject c = new BasicDBObject();

            // Handle foreign key column
            if (isFkRef(fkMeta, ck)) {
                String refTable = getRefTable(fkMeta, ck);
                if (refTable != null) {
                    c.append("type", refTable)
                            .append("nullable", (cMeta.getNullable() == "YES" ? true : false));
                }
            } else {
                c.append("type", StringHelper.getType(cMeta.getTypeName()))
                        .append("nullable", (cMeta.getNullable() == "YES" ? true : false));
            }
            properties.append(cMeta.getName(), c);
        }

        // Append the properties sub-document
        object.append("Properties", properties);

        // Insert into collection
        entities.insert(object);
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
}
