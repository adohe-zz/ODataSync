package com.ado.java.odata.parser;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: nankonami
 * Date: 14-4-28
 * Time: 下午11:20
 * To change this template use File | Settings | File Templates.
 */
public class TableMetadata implements Metadata {

    private final String category;
    private final String schema;
    private final String name;
    private final Map<String, ColumnMetadata> columns = new HashMap<String, ColumnMetadata>();
    private final Map<String, IndexMetadata> indexes = new HashMap<String, IndexMetadata>();

    public TableMetadata(ResultSet rs, DatabaseMetaData meta, boolean extras) throws SQLException {
        category = rs.getString("TABLE_CAT");
        schema = rs.getString("TABLE_SCHEM");
        name = rs.getString("TABLE_NAME");
        initColumns(meta);
    }

    private ColumnMetadata getColumnMetadata(String columnNmae) {
        return columns.get(columnNmae.toLowerCase());
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
}
