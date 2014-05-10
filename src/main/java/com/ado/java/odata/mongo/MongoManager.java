package com.ado.java.odata.mongo;

import com.ado.java.odata.pool.Config;
import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoOptions;

import java.net.UnknownHostException;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: nankonami
 * Date: 14-5-10
 * Time: 下午12:58
 * To change this template use File | Settings | File Templates.
 */
public class MongoManager {

    private static volatile MongoManager instance = null;

    private Mongo mongo = null;

    private String host;
    private int port;
    private int blockSize;
    private int poolSize;

    public static MongoManager getInstance(String host, int port, int blockSize, int poolSize) {
        if (instance == null) {
            synchronized (MongoManager.class) {
                if (instance == null) {
                    instance = new MongoManager(host, port, blockSize, poolSize);
                }
            }
        }

        return instance;
    }

    private MongoManager(String host, int port, int blockSize, int poolSize) {
        this.host = host;
        this.port = port;
        this.blockSize = blockSize;
        this.poolSize = poolSize;
    }

    /**
     * Get the database instance
     *
     * @param dbName {@link java.lang.String} database name
     * @return {@link com.mongodb.DB} instance
     */
    public DB getDB(String dbName) {
        if (mongo == null) {
            init();
        }

        return mongo.getDB(dbName);
    }

    /**
     * Init the mongo instance
     *
     */
    private void init() {
        try {
            mongo = new Mongo(host, port);
            MongoOptions options = mongo.getMongoOptions();
            options.connectionsPerHost = poolSize;
            options.threadsAllowedToBlockForConnectionMultiplier = blockSize;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
