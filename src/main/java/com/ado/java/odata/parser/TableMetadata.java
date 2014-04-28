package com.ado.java.odata.parser;

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

    /**
     * Table name
     */
    private String name;

    /**
     * Table columns
     */
    private Map<String, String> columns;

    /**
     * The primary key
     */
    private String primaryKey;

    /**
     * Indexes
     */
    private List<String> indexes;
}
