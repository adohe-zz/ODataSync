package com.ado.java.odata.parser;

import org.hibernate.mapping.ForeignKey;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TableMetadata implements Metadata {

    private final String category;
    private final String schema;
    private final String name;

    private final Map<String, ColumnMetadata> columns = new HashMap<String, ColumnMetadata>();
    private final Map<String, IndexMetadata> indexes = new HashMap<String, IndexMetadata>();
    private final Map<String, ForeignKeyMetadata> foreignKeys = new HashMap<String, ForeignKeyMetadata>();

    private final Map<String, PrimaryKeyMetadata> primaryKeys = new HashMap<String, PrimaryKeyMetadata>();

    public TableMetadata(ResultSet rs, DatabaseMetaData meta, boolean extras) throws SQLException {
        category = rs.getString("TABLE_CAT");
        schema = rs.getString("TABLE_SCHEM");
        name = rs.getString("TABLE_NAME");
        initColumns(meta);
        initPrimaryKey(meta);
        if (extras) {
            initIndexes(meta);
            initForeignKeys(meta);
        }
    }

    private ColumnMetadata getColumnMetadata(String columnName) {
        return columns.get(columnName.toLowerCase());
    }

    private IndexMetadata getIndexMetadata(String indexName) {
        return indexes.get(indexName.toLowerCase());
    }

    private ForeignKeyMetadata getForeignKeyMetadata(String foreignKeyName) {
        return foreignKeys.get(foreignKeyName.toLowerCase());
    }

    public ForeignKeyMetadata getForeignKeyMetadata(ForeignKey fk) {
        Iterator iterator = foreignKeys.values().iterator();
        while (iterator.hasNext()) {
            ForeignKeyMetadata existingFk = (ForeignKeyMetadata)iterator.next();
            if (existingFk.matches(fk)) {
                return existingFk;
            }
        }

        return null;
    }

    private PrimaryKeyMetadata getPrimaryKeyMetadata(String primaryKey) {
        return primaryKeys.get(primaryKey.toLowerCase());
    }

    private void initPrimaryKey(DatabaseMetaData metaData) throws SQLException {
        ResultSet rs = null;

        try {
            rs = metaData.getPrimaryKeys(category, schema, name);
            while (rs.next()) {
                addPrimaryKey(rs);
            }
        }
        finally {
            if (rs != null) {
                rs.close();
            }
        }
    }


    private void addPrimaryKey(ResultSet rs) throws SQLException {
        String name = rs.getString("PK_NAME");

        if (name == null) {
            return;
        }
        PrimaryKeyMetadata metadata = getPrimaryKeyMetadata(name);
        if (metadata == null) {
            metadata = new PrimaryKeyMetadata(rs);
            primaryKeys.put(metadata.getName().toLowerCase(), metadata);
        }

        metadata.addColumn(getColumnMetadata(rs.getString("COLUMN_NAME")));
    }

    private void initColumns(DatabaseMetaData metaData) throws SQLException {
        ResultSet rs = null;

        try {
            rs = metaData.getColumns(category, schema, name, "%");
            while (rs.next()) {
                addColumn(rs);
            }
        }
        finally {
            if (rs != null) {
                rs.close();
            }
        }
    }

    private void addColumn(ResultSet rs) throws SQLException {
        String column = rs.getString("COLUMN_NAME");

        if (column == null) {
            return;
        }

        if (getColumnMetadata(column) == null) {
            ColumnMetadata info = new ColumnMetadata(rs);
            columns.put(info.getName().toLowerCase(), info);
        }
    }

    private void initIndexes(DatabaseMetaData metaData) throws SQLException {
        ResultSet rs = null;

        try {
            rs = metaData.getIndexInfo(category, schema, name, false, true);

            while (rs.next()) {
                if (rs.getShort("TYPE") == DatabaseMetaData.tableIndexStatistic)
                    continue;
                addIndex(rs);
            }
        }
        finally {
            if (rs != null) {
                rs.close();
            }
        }
    }

    private void addIndex(ResultSet rs) throws SQLException {
        String indexName = rs.getString("INDEX_NAME");

        if (indexName == null) {
            return;
        }

        IndexMetadata metadata = getIndexMetadata(indexName);
        if (metadata == null) {
            metadata = new IndexMetadata(rs);
            indexes.put(metadata.getName().toLowerCase(), metadata);
        }

        metadata.addColumn(getColumnMetadata(rs.getString("COLUMN_NAME")));
    }

    private void initForeignKeys(DatabaseMetaData metaData) throws SQLException {
        ResultSet rs = null;

        try {
            rs = metaData.getImportedKeys(category, schema, name);

            while (rs.next()) {
                addForeignKey(rs);
            }
        }
        finally {
            if (rs != null) {
                rs.close();
            }
        }
    }

    private void addForeignKey(ResultSet rs) throws SQLException {
        String fk = rs.getString("FK_NAME");

        if (fk == null) {
            return;
        }

        ForeignKeyMetadata metadata = getForeignKeyMetadata(fk);
        if (metadata == null) {
            metadata = new ForeignKeyMetadata(rs);
            foreignKeys.put(metadata.getName().toLowerCase(), metadata);
        }

        metadata.addReference(rs);
    }

    public String getCategory() {
        return category;
    }

    public String getSchema() {
        return schema;
    }

    public String getName() {
        return name;
    }

    public Map<String, ColumnMetadata> getColumns() {
        return columns;
    }

    public Map<String, IndexMetadata> getIndexes() {
        return indexes;
    }

    public Map<String, ForeignKeyMetadata> getForeignKeys() {
        return foreignKeys;
    }

    public Map<String, PrimaryKeyMetadata> getPrimaryKeys() {
        return primaryKeys;
    }
}
