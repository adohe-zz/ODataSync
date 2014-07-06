package com.ado.java.odata.pool;

import java.util.Properties;

public interface Config {

    /**
     * Minimum default values a {@link java.sql.Driver} would expect to reconnect
     * and return a {@link java.sql.Connection}
     */
    public static final String RECONNECT_USER_NAME = "user";
    public static final String RECONNECT_USER_PWD = "password";

    /**
     * Drivername to be used by the pool to make a connection.
     * Make sure you have the corresponding jar file in the classpath.
     *
     * @param - driverName to be used by this pool
     * @example - com.mysql.jdbc.Driver
     */
    public void setDriverName(String driverName);

    /**
     * Drivername to be used by the pool to make a connection.
     * Make sure you have the corresponding jar file in the classpath.
     *
     * @return - driverName used by this pool
     * @example - com.mysql.jdbc.Driver
     */
    public String getDriverName();

    /**
     * Maximum number of connections that a {@link ConnectionPool} can hold,
     * beyond which a client has to wait at least {@link #setMaxWait(int)} milliseconds value.
     * Typical values are from 10-100
     *
     * @param - maxConnections for this pool
     */
    public void setMaxConnections(int maxConnections);

    /**
     * Maximum number of connections that a {@link ConnectionPool} can hold,
     * beyond which a client has to wait at least {@link #setMaxWait(int)} milliseconds value.
     * Typical values are from 10-100
     *
     * @return - maximum number of connections of this pool
     */
    public int getMaxConnections();

    /**
     * Initial number of connections created by the connection pool.
     * These number of connections are created once a {@link ConnectionPool} is instantiated.
     * This is to ensure that once a {@link ConnectionPool} is created, clients
     * can query for a connection and get a valid one immediately.
     *
     * @param - initialSize for this pool
     */
    public void setInitialSize(int initialSize);

    /**
     * Initial number of connections created by the connection pool.
     * These connections are created once a {@link ConnectionPool} is instantiated.
     * This is to ensure that once a {@link ConnectionPool} is created, clients
     * can query for a connection and get a valid one immediately.
     *
     * @return - initial size of this pool
     */
    public int getInitialSize();

    /**
     * Time in milliseconds {@link ConnectionPool} waits for a connection
     * to be available before throwing an exception {@link java.sql.SQLException},
     * when maximum number of connections is reached.
     * Typical values are from 10,000 to 60,000
     *
     * @param - maxWait in milliseconds to be used by this pool
     */
    public void setMaxWait(int maxWait);

    /**
     * Time in milliseconds {@link ConnectionPool} waits for a connection
     * to be available before throwing an exception {@link java.sql.SQLException},
     * when maximum number of connections is reached.
     * Typical values are from 10,000 to 60,000
     *
     * @return - maxWait in milliseconds used by this pool
     */
    public int getMaxWait();

    /**
     * Interval for the {@link ConnectionReleaser} to peek into
     * busy connections, if they are closed or not.
     * {@link #setRunReleaser()} needs to be set to true, in order for this
     * value to be effective.
     *
     * @param - releaserInterval in milliseconds to be used by this pool
     *//*
    public void setReleaserInterval(int releaserInterval);

    *//**
     * Interval for the {@link ConnectionReleaser} to peek into
     * busy connections, if they are closed or not.
     * {@link #setRunReleaser()} needs to be set to true, in order for this
     * value to be effective.
     *
     * @return - releaserInterval in milliseconds used by this pool
     *//*
    public int getReleaserInterval();

    *//**
     * Specifies whether to run {@link ConnectionReleaser}.
     *
     * @param - runReleaser boolean value. True if {@link ConnectionReleaser} needs
     * to be run.
     *//*
    public void setRunReleaser(boolean runReleaser);

    *//**
     * Specifies whether to run {@link ConnectionReleaser}.
     *
     * @return - boolean value. True if {@link ConnectionReleaser} is running or needs
     * to be run.
     *//*
    public boolean getRunReleaser();*/

    /**
     * {@link java.util.Properties} required by the {@link ConnectionPool}
     * to reconnect using {@link java.sql.Driver} and get a valid {@link java.sql.Connection}.
     *
     * @param properties {@link java.util.Properties} with atleaset username and password for reconnection.
     * @example
     * <code>
     *    Properties urlProperties = new Properties();
     *    urlProperties.setProperty(PoolConfiguration.RECONNECT_USER_PROP, "andy");
     *    urlProperties.setProperty(PoolConfiguration.RECONNECT_PASSWORD_PROP, "$andy#!");
     * </code>
     */
    public void setProperties(Properties properties);

    /**
     * {@link java.util.Properties} required by the {@link ConnectionPool}
     * to reconnect using {@link java.sql.Driver} and get a valid {@link java.sql.Connection}
     *
     * @return properties used by the {@link ConnectionPool} to reconnect
     */
    public Properties getProperties();

    /**
     * Set all the default properties for this pool.
     */
    public void setDefaults();


    /**
     * Set this configuration properties using {@link java.util.Properties}.
     *
     * @param - props: {@link java.util.Properties} to be used to set the pool properties
     */
    public void setUsingProperties(Properties props);

    /**
     * Update with user and password for reconnection
     *
     * @param user user to be set
     * @param pass password to be set
     */
    public void updateProperties(String user, String pass);

}
