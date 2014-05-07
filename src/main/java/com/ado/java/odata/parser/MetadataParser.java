package com.ado.java.odata.parser;


import org.hibernate.dialect.MySQLDialect;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

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
        return null;
    }

    /**
     * Parser the {@link com.mysql.jdbc.DatabaseMetaData} for a table
     * to constructor it's own {@link com.ado.java.odata.parser.TableMetadata}
     * @param connection {@link java.sql.Connection} the database metadata for a table
     * @param tableName {@link String} the table name
     * @return {@link com.ado.java.odata.parser.Metadata} a specific table metadata
     */
    public static Metadata parser(Connection connection, String tableName) throws SQLException {
        DatabaseMetadata databaseMetaData = new DatabaseMetadata(connection, new MySQLDialect());
        return databaseMetaData.getTableMetadata(connection.getCatalog(), connection.getSchema(), tableName, false);
    }
}
