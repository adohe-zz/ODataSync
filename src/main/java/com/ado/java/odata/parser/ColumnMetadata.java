package com.ado.java.odata.parser;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.StringTokenizer;

/**
 * Created with IntelliJ IDEA.
 * User: nankonami
 * Date: 14-5-5
 * Time: 下午11:03
 * To change this template use File | Settings | File Templates.
 */
public class ColumnMetadata {

    private final String name;
    private final String typeName;
    private final int columnSize;
    private final int decimalDigits;
    private final String isNullable;
    private final int typeCode;

    public ColumnMetadata(ResultSet rs) throws SQLException {
        name = rs.getString("COLUMN_NAME");
        columnSize = rs.getInt("COLUMN_SIZE");
        decimalDigits = rs.getInt("DECIMAL_DIGITS");
        isNullable = rs.getString("IS_NULLABLE");
        typeCode = rs.getInt("DATA_TYPE");
        typeName = new StringTokenizer(rs.getString("TYPE_NAME"), "() ").nextToken();
    }

    @Override
    public String toString() {
        return "ColumnMetadata(" + name + ')';
    }

    public String getName() {
        return name;
    }

    public String getTypeName() {
        return typeName;
    }

    public int getColumnSize() {
        return columnSize;
    }

    public int getDecimalDigits() {
        return decimalDigits;
    }

    public String getNullable() {
        return isNullable;
    }

    public int getTypeCode() {
        return typeCode;
    }
}
