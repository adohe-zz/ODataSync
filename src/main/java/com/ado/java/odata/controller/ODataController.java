package com.ado.java.odata.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created with IntelliJ IDEA.
 * User: nankonami
 * Date: 14-4-27
 * Time: 上午12:26
 * To change this template use File | Settings | File Templates.
 */
@Controller
public class ODataController {

    @RequestMapping( value = "/" )
    public String test() {
        return "test";
    }

    @RequestMapping( value = "test")
    public String index() {
        return "index";
    }
}
