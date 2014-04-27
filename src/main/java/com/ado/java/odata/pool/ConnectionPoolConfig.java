package com.ado.java.odata.pool;

import org.apache.log4j.Logger;

import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: nankonami
 * Date: 14-4-27
 * Time: 下午2:55
 * To change this template use File | Settings | File Templates.
 */
public class ConnectionPoolConfig implements Config {

    /**
     * DEFAULT Values
     */
    public static final String DEFAULT_DRIVERNAME = "com.mysql.jdbc.Driver";
    public static final int DEFAULT_MAX_CONNECTIONS = 20;
    public static final int DEFAULT_INITIAL_SIZE = 10;
    public static final int DEFAULT_MAX_WAIT = 30000; // 30 seconds

    /**
     * Logger
     */
    public static final Logger logger = Logger.getLogger(ConnectionPoolConfig.class);

    private volatile String driverName;
    private volatile int maxConnections;
    private volatile int initialSize;
    private volatile int maxWait;
    private volatile Properties properties;

    /**
     * Constructor with default properties for the pool
     *
     * @param - useDefault: boolean value if true, sets the default properties
     */
    public ConnectionPoolConfig(boolean useDefault) {
        if (useDefault) {
            this.setDefaults();
        }
    }

    /**
     * Constructor using {@link java.util.Properties}
     *
     * @param - properties: {@link java.util.Properties} to be used to set the pool properties
     */
    public ConnectionPoolConfig(Properties properties) {
        this.setUsingProperties(properties);
    }

    /**
     * Blank constructor
     */
    public ConnectionPoolConfig() {
    }

    @Override
    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    @Override
    public String getDriverName() {
        return this.driverName;
    }

    @Override
    public void setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
    }

    @Override
    public int getMaxConnections() {
        return this.maxConnections;
    }

    @Override
    public void setInitialSize(int initialSize) {
        this.initialSize = initialSize;
    }

    @Override
    public int getInitialSize() {
        return this.initialSize;
    }

    @Override
    public void setMaxWait(int maxWait) {
        this.maxWait = maxWait;
    }

    @Override
    public int getMaxWait() {
        return maxWait;
    }

    @Override
    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    @Override
    public Properties getProperties() {
        return this.properties;
    }

    @Override
    public void setDefaults() {
        this.driverName = DEFAULT_DRIVERNAME;
        this.maxConnections = DEFAULT_MAX_CONNECTIONS;
        this.initialSize = DEFAULT_INITIAL_SIZE;
        this.maxWait = DEFAULT_MAX_WAIT;
    }

    @Override
    public void setUsingProperties(Properties props) {
        this.driverName = props.getProperty("DRIVER_NAME", DEFAULT_DRIVERNAME);
        this.maxConnections = Integer.parseInt(props.getProperty("MAX_CONNECTIONS",
                "" + DEFAULT_MAX_CONNECTIONS));
        this.initialSize = Integer.parseInt(props.getProperty("INITIAL_SIZE",
                "" + DEFAULT_INITIAL_SIZE));
        this.maxWait = Integer.parseInt(props.getProperty("MAX_WAIT",
                "" + DEFAULT_MAX_WAIT));
    }

    @Override
    public void updateProperties(String user, String password) {
        if (this.properties != null) {
            this.properties = new Properties(this.properties);
        } else {
            this.properties = new Properties();
        }

        this.properties.setProperty(RECONNECT_USER_NAME, user);
        this.properties.setProperty(RECONNECT_USER_PWD, password);
    }
}
