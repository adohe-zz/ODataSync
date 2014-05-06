package com.ado.java.odata.parser;


import org.hibernate.mapping.Column;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: nankonami
 * Date: 14-5-5
 * Time: 下午11:04
 * To change this template use File | Settings | File Templates.
 */
public class ForeignKeyMetadata {

    private final String name;
    private final String refTable;
    private final Map<String, String> references = new HashMap<String, String>();

    public ForeignKeyMetadata(ResultSet rs) throws SQLException {
        name = rs.getString("FK_NAME");
        refTable = rs.getString("PKTABLE_NAME");
    }

    public String getName() {
        return name;
    }

    public String getRefTable() {
        return refTable;
    }

    public void addReference(ResultSet rs) throws SQLException {
        references.put(rs.getString("FKCOLUMN_NAME").toLowerCase(), rs.getString("FKCOLUMN_NAME"));
    }

    private boolean hasReference(Column column, Column ref) {
        String reference = (String)references.get(column.getName().toLowerCase());
        return ref.getName().equalsIgnoreCase(reference);
    }
}
