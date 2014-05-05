package com.ado.java.odata.parser;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: nankonami
 * Date: 14-4-28
 * Time: 下午11:20
 * To change this template use File | Settings | File Templates.
 */
public class TableMetadata implements Metadata {

    /**
     * Table category name
     */
    private String category;

    /**
     * Table schema name
     */
    private String schema;

    /**
     * Table name
     */
    private String name;

    /**
     * Table columns
     */
    private List<Column> columns;

    /**
     * The primary key
     */
    private String primaryKey;

    /**
     * Indexes
     */
    private List<String> indexes;

    public TableMetadata(String tableName) {
        this.name = tableName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }

    public List<String> getIndexes() {
        return indexes;
    }

    public void setIndexes(List<String> indexes) {
        this.indexes = indexes;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
