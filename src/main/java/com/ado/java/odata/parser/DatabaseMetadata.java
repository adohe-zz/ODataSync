package com.ado.java.odata.parser;

import com.ado.java.odata.util.StringHelper;
import org.hibernate.dialect.Dialect;
import org.hibernate.mapping.Table;

import java.sql.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: nankonami
 * Date: 14-5-6
 * Time: 下午10:31
 * To change this template use File | Settings | File Templates.
 */
public class DatabaseMetadata {

    private final boolean extras;

    private Map<Object, TableMetadata> tables = new HashMap<Object, TableMetadata>();

    private DatabaseMetaData metaData;

    private Set<String> sequences = new HashSet<String>();

    private static final String[] TYPES = {"TABLE", "VIEW"};

    public DatabaseMetadata(Connection connection, Dialect dialect) throws SQLException {
        this(connection, dialect, true);
    }

    public DatabaseMetadata(Connection connection, Dialect dialect, boolean extras) throws SQLException {
        metaData = connection.getMetaData();
        this.extras = extras;
        initSequences(connection, dialect);

    }

    public TableMetadata getTableMetadata(String category, String schema, String name, boolean isQuoted) throws SQLException {
        Object identifier = identifier(category, schema, name);
        TableMetadata tableMetadata = tables.get(identifier);
        if (tableMetadata != null) {
            return  tableMetadata;
        }

        try {
            ResultSet rs = null;
            try {
                if ( (isQuoted && metaData.storesMixedCaseQuotedIdentifiers())) {
                    rs = metaData.getTables(category, schema, name, TYPES);
                } else if ((isQuoted && metaData.storesUpperCaseQuotedIdentifiers())
                        || (!isQuoted && metaData.storesUpperCaseIdentifiers())) {
                    rs = metaData.getTables(StringHelper.toUpperCase(category),
                            StringHelper.toUpperCase(schema),
                            StringHelper.toUpperCase(name),
                            TYPES);
                } else if ((isQuoted && metaData.storesLowerCaseQuotedIdentifiers())
                        || (!isQuoted && metaData.storesUpperCaseIdentifiers())) {
                    rs = metaData.getTables(StringHelper.toLowerCase(category),
                            StringHelper.toLowerCase(schema),
                            StringHelper.toLowerCase(name),
                            TYPES);
                } else {
                    rs = metaData.getTables(category, schema, name, TYPES);
                }

                while (rs.next()) {
                    String tableName = rs.getString("TABLE_NAME");
                    if (tableName.equalsIgnoreCase(name)) {
                        tableMetadata = new TableMetadata(rs, metaData, extras);
                        tables.put(identifier, tableMetadata);
                        return tableMetadata;
                    }
                }

                return null;
            }
            finally {
                if (rs != null) {
                    rs.close();
                }
            }
        } catch (SQLException e) {

        }

        return tableMetadata;
    }

    private Object identifier(String category, String schema, String name) {
        return Table.qualify(category, schema, name);
    }

    private void initSequences(Connection connection, Dialect dialect) throws SQLException {
        if (dialect.supportsSequences()) {
            String sql = dialect.getQuerySequencesString();
            if (sql != null) {

                PreparedStatement ps = null;
                ResultSet rs = null;

                try {
                    ps = connection.prepareStatement(sql);
                    rs = ps.executeQuery();

                    while (rs.next()) {
                        sequences.add(rs.getString(1).toLowerCase().trim());
                    }
                }
                finally {
                    if (ps != null)
                        ps.close();
                    if (rs != null)
                        rs.close();
                }
            }
        }
    }
}
