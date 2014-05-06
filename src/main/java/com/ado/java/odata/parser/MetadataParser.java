package com.ado.java.odata.parser;


import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.MySQLDialect;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: nankonami
 * Date: 14-4-28
 * Time: 下午11:20
 * To change this template use File | Settings | File Templates.
 */
public class MetadataParser {

    /**
     * Parser the {@link com.mysql.jdbc.DatabaseMetaData} for a table
     * to constructor it's own {@link com.ado.java.odata.parser.TableMetadata}
     * @param metaData {@link com.mysql.jdbc.DatabaseMetaData} the database metadata for a table
     * @param tableName {@link String} the table name
     * @return {@link com.ado.java.odata.parser.Metadata} a specific table metadata
     */
    public static Metadata parser(DatabaseMetaData metaData, String tableName) {

        /*try {
            ResultSet rs = metaData.getTables(null, null, tableName, new String[]{"TABLE"});
            if (rs.next()) {
                TableMetadata tableMetadata = new TableMetadata(tableName);

                // Parse the primary key
                rs = metaData.getPrimaryKeys("wecampus_dev", null, tableName);
                while (rs.next()) {
                    tableMetadata.setPrimaryKey(rs.getString(6));
                }

                // Parse the columns
                rs = metaData.getColumns("wecampus_dev", null, tableName, null);
                List<Column> columnList = new ArrayList<Column>();
                while (rs.next()) {
                    String name = rs.getString("COLUMN_NAME");
                    String type = rs.getString("TYPE_NAME");
                    int size = rs.getInt("COLUMN_SIZE");
                    boolean nullable = rs.getInt("NULLABLE") == 1 ? true : false;
                    columnList.add(new Column(name, type, size, nullable));
                }
                tableMetadata.setColumns(columnList);
                return tableMetadata;
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }*/
        return null;
    }

    public static Metadata parser(Connection connection, String tableName) throws SQLException {
        DatabaseMetadata databaseMetaData = new DatabaseMetadata(connection, new MySQLDialect());
        return databaseMetaData.getTableMetadata(connection.getCatalog(), connection.getSchema(), tableName, false);
    }
}
