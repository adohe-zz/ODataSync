package com.ado.java.odata.service;

/**
 * Created with IntelliJ IDEA.
 * User: nankonami
 * Date: 14-4-27
 * Time: 上午1:05
 * To change this template use File | Settings | File Templates.
 */
public interface ODataService {

    void syncData(String tableName);

    void syncMetadata(String tableName);

}
