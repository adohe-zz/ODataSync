package com.ado.java.odata.service.impl;

import com.ado.java.odata.dao.ODataDao;
import com.ado.java.odata.service.ODataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA.
 * User: nankonami
 * Date: 14-4-27
 * Time: 上午1:06
 * To change this template use File | Settings | File Templates.
 */
@Service("odataservice")
public class ODataServiceImpl implements ODataService {

    @Autowired
    private ODataDao oDataDao;

    @Override
    public void syncData(String tableName, String collection) {
        oDataDao.syncData(tableName, collection);
    }

    @Override
    public void syncMetadata(String tableName) {
        oDataDao.syncMetadata(tableName);
    }
}
