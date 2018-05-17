package com.framework.common.des;

public class TestDes {

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            System.out.println("---解密" + Des.decryptDES("eM5voH95aRCbepZx/TffCG+2NgH9dXq7BCSa0J4pqJVXo2Bv+uw="));
            System.out.println("----加密-" + Des.encryptDES("http://192.168.1.201:6303/platform-basesdk/"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
