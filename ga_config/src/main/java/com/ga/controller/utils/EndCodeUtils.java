package com.ga.controller.utils;

import android.util.Base64;

public class EndCodeUtils {

    public static String decode(String param) {
        try {
            byte[] dataGetBytes = param.getBytes("UTF-8");
            byte[] byteBase64 = Base64.decode(dataGetBytes, Base64.DEFAULT);
            String strResult = "";
            try {
                if (byteBase64 != null) {
                    strResult = new String(byteBase64);
                }
            } catch (Exception e) {
                return "";
            }
            return strResult;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
