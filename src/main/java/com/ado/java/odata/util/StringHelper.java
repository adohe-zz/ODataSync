package com.ado.java.odata.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: nankonami
 * Date: 14-5-6
 * Time: 下午10:51
 * To change this template use File | Settings | File Templates.
 */
public class StringHelper {

    private static Map<String, String> types = new HashMap<String, String>();

    static {

    }

    public static String toUpperCase(String str) {
        return str == null ? null : str.toUpperCase();
    }

    public static String toLowerCase(String str) {
        return str == null ? null : str.toLowerCase();
    }
}
