package com.springapp.mvc;

import com.ado.java.odata.pool.ConnectionPool;
import com.ado.java.odata.pool.ConnectionPoolConfig;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;

/**
 * Created with IntelliJ IDEA.
 * User: nankonami
 * Date: 14-5-3
 * Time: 下午10:06
 * To change this template use File | Settings | File Templates.
 */
public class ConnectionPoolTest {

    private ConnectionPool connectionPool;

    @Before
    public void setUp() throws Exception {
        ConnectionPoolConfig config = new ConnectionPoolConfig(true);
        //connectionPool = new ConnectionPool(config, "root", "root", "jdbc:mysql//localhost:3306/wecampus_dev");
    }

    @Test
    public void testGetConnection() throws Exception {
        /*Connection connection = connectionPool.getConnection();
        if (connection != null) {
            System.out.println(connection.getCatalog());
        } else {
            System.out.println("null connection");
        }*/
    }
}
