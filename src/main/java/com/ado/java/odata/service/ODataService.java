package com.ado.java.odata.service;

public interface ODataService {

    void syncData(String tableName, String collection);

    void syncMetadata(String tableName);

}
