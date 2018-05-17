package com.xiaolanba.passenger.bluetooth;

/**
 * Created by admin on 2018/5/10.
 */

public class StringUtil {

    public static String tenChangeToHex(int x){
        String hex = Integer.toHexString(x);
        if (hex.length()%2==1){
            hex = "0"+hex;
        }
        return hex.toUpperCase();
    }

    public static int hexChangeToTen(String hex){
        if (hex.startsWith("0x")||hex.startsWith("0X")){
            Integer x = Integer.parseInt(hex.substring(2),16);//从第2个字符开始截取
            return x;
        } else {
            Integer x = Integer.parseInt(hex,16);
            return x;
        }
    }
}
