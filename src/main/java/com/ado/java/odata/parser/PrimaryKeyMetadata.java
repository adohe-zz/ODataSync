package com.ado.java.odata.parser;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PrimaryKeyMetadata {

    private final String name;
    private final short keySeq;
    private final List<ColumnMetadata> columnMetadata = new ArrayList<ColumnMetadata>();

    public PrimaryKeyMetadata(ResultSet rs) throws SQLException {
        name = rs.getString("PK_NAME");
        keySeq = rs.getShort("KEY_SEQ");
    }

    public String getName() {
        return name;
    }

    public short getKeySeq() {
        return keySeq;
    }

    public ColumnMetadata[] getColumnMetadata() {
        return (ColumnMetadata[])columnMetadata.toArray(new ColumnMetadata[0]);
    }

    public void addColumn(ColumnMetadata metadata) {
        if (columnMetadata != null) {
            columnMetadata.add(metadata);
        }
    }

    @Override
    public String toString() {
        return "PrimaryKey: " + name;
    }
}
