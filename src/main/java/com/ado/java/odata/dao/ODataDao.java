package com.ado.java.odata.dao;

import com.ado.java.odata.parser.MetadataParser;
import com.ado.java.odata.parser.TableMetadata;
import com.ado.java.odata.pool.ConnectionPool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;

@Component
public class ODataDao {

    @Value("#{configProperties['db.user.name']}")
    private String userName;

    @Value("#{configProperties['db.user.password']}")
    private String password;

    @Value("#{configProperties['db.url']}")
    private String url;

    @Value("#{configProperties['db.sql']}")
    private String sql;

    /**
     * Sync meta-data for a table
     *
     * @param tableName The table name
     */
    public void syncMetadata(String tableName) {

        try {
            ConnectionPool pool = ConnectionPool.getPool(userName, password, url);
            Connection connection = pool.getConnection();

            TableMetadata metadata = (TableMetadata)MetadataParser.parser(connection, tableName);
            MongoDao.updateEntities(tableName, metadata);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sync data for {@link java.lang.String} table name
     * @param tableName The table name
     */
    public void syncData(String tableName, String collection) {
        try {
            ConnectionPool pool = ConnectionPool.getPool(userName, password, url);
            Connection connection = pool.getConnection();

            MongoDao.syncData(tableName, collection, connection, sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
