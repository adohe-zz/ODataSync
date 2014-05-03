package com.ado.java.odata.pool;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: nankonami
 * Date: 14-5-3
 * Time: 下午10:25
 * To change this template use File | Settings | File Templates.
 */
public class Test {

    public static void main(String[] argv) {
        ConnectionPoolConfig config = new ConnectionPoolConfig(true);
        try {
            ConnectionPool connectionPool = new ConnectionPool(config, "root", "root", "jdbc:mysql://localhost:3306/wecampus_dev");
            Connection connection = connectionPool.getConnection();
            if (connection != null) {
                System.out.println("not null connection");
                System.out.println(connection.getCatalog());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
