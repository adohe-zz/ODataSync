package com.ado.java.odata.dao;

import com.ado.java.odata.pool.ConnectionPool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;

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
     * Sync data for {@link java.lang.String} table name
     * @param tableName The table name
     */
    public void syncData(String tableName) {
        try {
            ConnectionPool pool = ConnectionPool.getPool(userName, password, url);
            Connection connection = pool.getConnection();
            System.out.println(connection.getCatalog());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
