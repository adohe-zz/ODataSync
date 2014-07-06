package com.ado.java.odata.parser;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class IndexMetadata {

    private final String name;
    private final List<ColumnMetadata> columns = new ArrayList<ColumnMetadata>();

    public IndexMetadata(ResultSet rs) throws SQLException {
        name = rs.getString("INDEX_NAME");
    }

    public String getName() {
        return name;
    }

    public void addColumn(ColumnMetadata metadata) {
        if (metadata != null) {
            columns.add(metadata);
        }
    }

    public ColumnMetadata[] getColumns() {
        return (ColumnMetadata[])columns.toArray(new ColumnMetadata[0]);
    }

    @Override
    public String toString() {
        return "IndexMetadata(" + name + ')';
    }
}
