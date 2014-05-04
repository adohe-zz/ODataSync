package com.ado.java.odata.pool;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created with IntelliJ IDEA.
 * User: nankonami
 * Date: 14-4-27
 * Time: 下午2:34
 * To change this template use File | Settings | File Templates.
 */
public class ConnectionPool implements Pool {

    /**
     * URL with [host] [port] [database]
     * @example
     *    <code>String url = "jdbc:mysql://localhost:3306/***"</code>
     */
    private String url;

    /**
     * Username connect to database
     */
    private String username;

    /**
     * User password connect to database
     */
    private String password;

    /**
     * {@link java.sql.Driver} used by this pool to make a reconnection
     * and get a {@link java.sql.Connection}
     */
    private Driver driver;

    /**
     * Logger
     */
    public static final Logger logger = Logger.getLogger(ConnectionPool.class);

    /**
     * Size of the pool at any given time.
     * Incremented only at two places. One at {@link #makeConnection()}, when we create new connection
     * if no connection is available and another at {@link #initPool()}, when a new
     * {@link ConnectionPool} is instantiated.
     */
    private AtomicInteger size = new AtomicInteger(0);

    /**
     * Atomic Flag to see if pool is closed
     */
    private AtomicBoolean closed = new AtomicBoolean(false);

    /**
     * Thread safe list of connections currently used by the clients
     */
    private BlockingQueue<Connection> activeConnections;

    /**
     * Thread safe list of connections available to the clients
     */
    private BlockingQueue<Connection> availableConnections;

    /**
     * Config file
     */
    private Config config;

    /**
     * Singleton Instance
     */
    private volatile static ConnectionPool pool;

    /**
     * The only public get method
     * @param username Username access to database
     * @param password Password access to database
     * @param url Jdbc connect url
     * @return A valid ConnectionPool instance
     * @throws SQLException
     */
    public static ConnectionPool getPool(String username, String password, String url) throws SQLException {
        if (pool == null) {
            synchronized (ConnectionPool.class) {
                if (pool == null) {
                    pool = new ConnectionPool(username, password, url);
                }
            }
        }

        return pool;
    }

    /**
     * Constructor with a given {@link Config}
     *
     *
     * @param config - {@link Config} defining pool properties
     * @param url - url String used to make a {@link java.sql.Connection}
     * @param username - user String used to make a {@link java.sql.Connection}
     * @param password - password String used to make a {@link java.sql.Connection}
     * @throws SQLException - if the properties do not pass sanity check by {@link #propertiesValidate()} ()}
     *                        or failures occur while making a {@link java.sql.Connection}
     */
    private ConnectionPool(Config config, String username, String password, String url) throws SQLException {
        this.url = url;
        this.username = username;
        this.password = password;
        this.config = config;
        this.initPool();
    }

    /**
     * Constructor with a given {@link java.util.Properties}
     *
     *
     * @param properties - {@link java.util.Properties} defining pool properties
     * @param url - url String used to make a {@link java.sql.Connection}
     * @param username - user String used to make a {@link java.sql.Connection}
     * @param password - password String used to make a {@link java.sql.Connection}
     * @throws SQLException - if the properties do not pass sanity check by {@link #propertiesValidate()} ()}
     *                        or failures occur while making a {@link java.sql.Connection}
     */
    private ConnectionPool(Properties properties, String username, String password, String url) throws SQLException {
        this(new ConnectionPoolConfig(properties), username, password, url);
    }

    /**
     * Constructor with a default {@link ConnectionPoolConfig}
     *
     *
     * @param url - url String used to make a {@link java.sql.Connection}
     * @param username - user String used to make a {@link java.sql.Connection}
     * @param password - password String used to make a {@link java.sql.Connection}
     * @throws SQLException - if the properties do not pass sanity check by {@link #propertiesValidate()} ()}
     *                        or failures occur while making a {@link java.sql.Connection}
     */
    private ConnectionPool(String username, String password, String url) throws SQLException {
        this(new ConnectionPoolConfig(true), username, password, url);
    }

    /**
     * Initialize the pool with {@link ConnectionPoolConfig#initialSize} of connections
     * available to Clients
     *
     * {@link #availableConnections} is set to {@link ConnectionPoolConfig#initialSize} and
     * {@link #size} is incremented.
     *
     * @throws SQLException - if the properties do not pass sanity check by {@link #propertiesValidate()} ()}
     *                        or failures occur while making a {@link java.sql.Connection}
     */
    protected void initPool() throws SQLException {
        try {
            this.propertiesValidate();
        } catch (ConfigException e) {
            throw new SQLException("Failed to parse the configuration");
        }

        this.availableConnections = new ArrayBlockingQueue<Connection>(this.config.getMaxConnections(), true);
        this.activeConnections = new ArrayBlockingQueue<Connection>(this.config.getMaxConnections(), false);

        for (int i = 0; i < this.config.getInitialSize(); i++) {
            this.availableConnections.offer(this.makeConnection());
            this.size.addAndGet(1);
        }
    }

    /**
     * Sanity check for the current {@link ConnectionPoolConfig} properties.
     *
     * <p>
     * {@link ConnectionPoolConfig#getMaxConnections()} needs to be more than 0. {@link ConnectionPoolConfig#getInitialSize()}
     * needs to be more than 0 and lesser than {@link ConnectionPoolConfig#getMaxConnections()}. Note that
     * {@link com.ado.java.odata.pool.ConnectionPoolConfig#getMaxWait()} ()} is in milliseconds (Typical values are from 10000 to 60000). If
     * the interval between which it checks for closed connections by the clients must be atleast 3 times {@link ConnectionPoolConfig#getMaxWait()},
     * so clients wait for enough time before a connection is released or size of the pool is decremented.
     * </p>
     *
     */
    protected void propertiesValidate() throws ConfigException {
        if (this.config == null) {
            throw new ConfigException("No configuration available");
        }

        try {
            // Register the driver
            this.driver = (Driver)Class.forName(this.config.getDriverName(),
                    true, this.getClass().getClassLoader()).newInstance();
        } catch (Exception e) {
            throw new ConfigException("Driver can't be load", e);
        }

        if (this.config.getMaxConnections() <= 0) {
            logger.warn("Maximum connection is setting to less than 0. Setting it to default: " + ConnectionPoolConfig.DEFAULT_MAX_CONNECTIONS);
            this.config.setMaxConnections(ConnectionPoolConfig.DEFAULT_MAX_CONNECTIONS);
        }

        if (this.config.getInitialSize() > this.config.getMaxConnections() || this.config.getInitialSize() < 0) {
            logger.warn("Initial size is set to wrong");
            this.config.setInitialSize(ConnectionPoolConfig.DEFAULT_INITIAL_SIZE);
        }

        if (this.config.getMaxWait() <= 10) {
            logger.warn("Max wait is set to wrong");
            this.config.setMaxWait(ConnectionPoolConfig.DEFAULT_MAX_WAIT);
        }
    }

    /**
     * {@inheritDoc}
     *
     * Create a brand new connection.
     *
     * @return {@link java.sql.Connection} a valid new connection.
     * @throws {@link java.sql.SQLException} if failure occurs while trying to get a connection.
     */
    @Override
    public Connection makeConnection() throws SQLException {
        return DriverManager.getConnection(this.url, this.username, this.password);
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * When trying to get a connection, pool manager looks for an available valid connectioni and if available
     * returns immediately. If not, tries to create a new connection if the {@link #size} has not exceeded the
     * {@link ConnectionPoolConfig#getMaxConnections()}. If unsuccessful, it waits for {@link ConnectionPoolConfig#getMaxWait()}
     * milliseconds and throws a timed out {@link java.sql.SQLException} if unsuccessful again.
     * </p>
     *
     */
    @Override
    public Connection getConnection() throws SQLException {
        if (this.isClosed()) {
            throw new SQLException("Connection pool is closed");
        }

        // Immediately return if a connection is available
        Connection connection = this.waitAndGet(0);
        if (connection != null) {
            return  connection;
        } else {
            connection = this.createAndGet();
            if (connection != null) {
                return connection;
            } else {
                long start = System.currentTimeMillis();
                connection = this.waitAndGet(this.config.getMaxWait());
                if (connection == null) {
                    if (System.currentTimeMillis() - start > this.config.getMaxWait()) {
                        throw new SQLException("Time out");
                    }
                }
            }
        }

        if (this.isClosed()) {
            this.disconnect(connection);
            throw new SQLException("Connection pool is closed");
        } else {
            return connection;
        }
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * If the {@link ConnectionPool} is closed or If {@link #activeConnections} does not
     * contain the passed {@link java.sql.Connection}, just disconnects the connection and returns.
     * If successfully removed, offers it to the {@link #availableConnections} and returns.
     * Atomically decrements {@link #size} iff the connection is removed and not added to
     * {@link #availableConnections}
     * </p>
     *
     */
    @Override
    public void returnConnection(Connection connection) throws SQLException {
        if (connection == null) {
            return;
        }

        if (this.isClosed()) {
            this.disconnect(connection);
            return;
        }

        boolean closeConnection = true;
        if (this.activeConnections.remove(connection)) {
            if (this.size.get() > this.config.getMaxConnections()) {

            } else if (!this.availableConnections.offer(connection)) {
                this.size.decrementAndGet();
            } else {
                closeConnection = false;
            }
        } else {/**/}

        if (closeConnection) {
            this.disconnect(connection);
        }
    }

    /**
     * See if the current pool is closed and not usable.
     * Thread safe read on the {@link #closed close} of the pool
     *
     * @return true if the connection pool has been closed
     */
    public boolean isClosed() {
        return this.closed.get();
    }

    /**
     * Wait for an available valid connection and return one, if any.
     * If the connection is closed, try to reconnect it.
     * Otherwise remove it from busy queue and
     * return null, so the calling function can attempt to create a new one.
     *
     * @return {@link java.sql.Connection}
     */
    protected Connection waitAndGet(int wait) throws SQLException{
        Connection connection;
        try {
            connection = this.availableConnections.poll(wait, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new SQLException("Interrupted exception");
        }

        boolean decrement = false;
        if (connection != null && connection.isClosed()) {
            decrement = true;
            connection = this.reconnect(connection);
        }

        if (!offerToActive(connection)) {
            connection = null;
        }

        if (decrement && connection == null) {
            this.size.decrementAndGet();
        }

        return connection;
    }

    /**
     *  Creates a new connection iff the {@link #size} has not exceeded
     *  {@link ConnectionPoolConfig#getMaxConnections} and tries to offer it
     *  to {@link #activeConnections}.
     *
     * @return a new {@link java.sql.Connection}, null if unsuccessful
     */
    protected Connection createAndGet() throws SQLException {
        if (this.size.get() > this.config.getMaxConnections()) {
            return null;
        }

        Connection connection = this.makeConnection();
        if (!offerToActive(connection)) {
            return null;
        } else {
            this.size.addAndGet(1);
            return connection;
        }
    }

    /**
     * Tries to offer a connection to {@link #activeConnections}.
     * This happens only when a new connection is created or a connection is
     * obtained from {@link #availableConnections} after polling.
     *
     * @param connection A connection to be added to active queue.
     * @return true if added to active queue
     */
    protected boolean offerToActive(Connection connection) throws SQLException {
        if (connection == null || connection.isClosed()) {
            return false;
        }

        if (!this.activeConnections.offer(connection)) {
            // Capacity exceeded
            return false;
        } else {
            return true;
        }
    }

    /**
     * Tries to reconnect using the {@link java.sql.Driver} {@link #driver}
     * using the {@link com.ado.java.odata.pool.ConnectionPoolConfig#getProperties()}.
     *
     * This is so that pool manager doesn't have to poll again for a connection.
     *
     * @param connection Connection that needs to be closed
     * @return a new {@link java.sql.Connection}
     */
    protected Connection reconnect(Connection connection) throws SQLException {
        this.disconnect(connection);
        connection = null;
        if (this.driver != null) {
            try {
                connection = this.driver.connect(this.url, null);
            } catch (SQLException e) {
                logger.error("Failed to reconnect");
            }
            return connection;
        }

        return connection;
    }

    /**
     * Disconnect a connection.
     *
     * @param connection {@link java.sql.Connection} to be disconnected
     */
    protected boolean disconnect(Connection connection) throws SQLException{
        if (connection != null && !connection.isClosed()) {
            connection.close();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Closes all available connections and clears all connections owned by this pool.
     *
     */
    public void close() throws SQLException {
        if (this.isClosed()) {
            return;
        }

        this.closed.set(true);
        this.size.set(this.config.getMaxConnections());

        BlockingQueue<Connection> pooledConnections = this.availableConnections;
        if (pooledConnections.size() == 0) {
            pooledConnections = this.activeConnections;
        }

        while (pooledConnections.size() > 0) {
            Connection connection = null;
            try {
                connection = pooledConnections.poll(1, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                throw new SQLException("Can't close connection pool");
            }

            if (pooledConnections == this.availableConnections) {
                this.disconnect(connection);
                if (pooledConnections.size() == 0) {
                    pooledConnections = this.activeConnections;
                }
            }
        }
    }

    /**
     * Check if a connection belongs to a pool
     *
     * @param connection - connection to check
     * @return boolean true if the connection belongs to the pool
     */
    public boolean containsConnection(Connection connection) {
        if (this.activeConnections.contains(connection)) {
            return true;
        } else if (this.availableConnections.contains(connection))  {
            return true;
        }
        return false;
    }
}
