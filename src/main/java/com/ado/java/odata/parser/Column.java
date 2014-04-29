package com.ado.java.odata.parser;

/**
 * Created with IntelliJ IDEA.
 * User: nankonami
 * Date: 14-4-29
 * Time: 下午6:19
 * To change this template use File | Settings | File Templates.
 */
public class Column {

    /**
     * Column name
     */
    private String name;

    /**
     * Column type
     */
    private String type;

    /**
     * Column size
     */
    private int size;

    /**
     * Whether column is nullable
     */
    private boolean nullable;

    /**
     * Whether the column is primary key
     */
    private boolean primaryKey;

    /**
     * Whether the key is primary key
     */
    private boolean index;

    public Column(String name, String type, int size, boolean nullable) {
        this.name = name;
        this.type = type;
        this.size = size;
        this.nullable = nullable;
    }

    public Column(String name, String type, int size, boolean nullable, boolean primaryKey, boolean index) {
        this(name, type, size, nullable);
        this.primaryKey = primaryKey;
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public boolean isNullable() {
        return nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
    }

    public boolean isIndex() {
        return index;
    }

    public void setIndex(boolean index) {
        this.index = index;
    }
}
