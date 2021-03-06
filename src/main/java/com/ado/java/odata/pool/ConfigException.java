package com.ado.java.odata.pool;

import java.sql.SQLException;

public class ConfigException extends SQLException {

    private static final long serialVersionUID = 7526472295622776147L;

    /**
     * Implement all Constructors in {@link java.sql.SQLException}
     */

    public ConfigException() {
    }

    public ConfigException(String msg) {
        super(msg);
    }

    public ConfigException(String msg, String sqlState) {
        super(msg, sqlState);
    }

    public ConfigException(String msg, String sqlState, int vendorCode) {
        super(msg, sqlState, vendorCode);
    }

    public ConfigException(String msg, String sqlState, int vendorCode, Throwable cause) {
        super(msg, sqlState, vendorCode, cause);
    }

    public ConfigException(String msg, String sqlState, Throwable cause) {
        super(msg, sqlState, cause);
    }

    public ConfigException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
