package com.ado.java.odata.pool;

import java.sql.Connection;
import java.sql.SQLException;

public interface Pool {

    /**
     * Make a new connection
     *
     * @throws SQLException
     */
    Connection makeConnection() throws SQLException;

    /**
     * Gets a valid connection from the connection pool
     *
     * @return a valid connection from the pool
     * @throws SQLException
     */
    Connection getConnection() throws SQLException;

    /**
     * Return a connection into the connection pool
     *
     * @param connection the connection to return to the pool
     * @throws SQLException
     */
    void returnConnection(Connection connection) throws SQLException;

}
