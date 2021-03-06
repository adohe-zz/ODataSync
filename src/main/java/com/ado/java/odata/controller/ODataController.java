package com.ado.java.odata.controller;

import com.ado.java.odata.service.ODataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ODataController {

    @Autowired
    private ODataService oDataService;

    @RequestMapping( value = "/")
    public String index() {
        return "index";
    }

    @RequestMapping( value = "data")
    @ResponseBody
    public String syncDataForTable(@RequestParam String name, @RequestParam String collection) {
        oDataService.syncData(name, collection);
        return "ok";
    }

    @RequestMapping( value = "metadata")
    @ResponseBody
    public String syncMetadataForTable(@RequestParam String name) {
        oDataService.syncMetadata(name);
        return "ok";
    }
}
