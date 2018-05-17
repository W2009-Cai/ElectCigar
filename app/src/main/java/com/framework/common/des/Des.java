package com.framework.common.des;

import android.text.TextUtils;

import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * DES加密操作
 *
 * @author xutingz
 */
public class Des {


    private static byte[] iv = {1, 2, 3, 4, 5, 6, 7, 8};


    public static String initkey() {
        String[] keys = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
                "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l",
                "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x",
                "y", "z", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
                "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
                "W", "X", "Y", "Z"};
        StringBuffer kstr = new StringBuffer();
        for (int i = 0; i < 8; i++) {
            int d = new Random().nextInt(62);
            kstr.append(keys[d]);
        }
        return kstr.toString();
    }

    public static String encryptDES(String encryptString, String encryptKey)
            throws Exception {
        // IvParameterSpec zeroIv = new IvParameterSpec(new byte[8]);
        IvParameterSpec zeroIv = new IvParameterSpec(iv);
        SecretKeySpec key = new SecretKeySpec(encryptKey.getBytes(), "DES");
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);
        byte[] encryptedData = cipher.doFinal(encryptString.getBytes());

        return Base64.encode(encryptedData);
    }

    public static String encryptDES(String encryptString) throws Exception {
        String encryptKey = initkey();
        return encryptKey.concat(encryptDES(encryptString, encryptKey));
    }

    /****
     * 公益分享的 用户id进行加密，
     * 直接使用Base64.encode
     * @param encryptString
     * @return
     */
    public static String encryptDESByNotNull(String encryptString) {
        try {
            if (TextUtils.isEmpty(encryptString)) {
                return "";
            }
            return Base64.encode(encryptString.getBytes());
        } catch (Exception e) {
            return "";
        }
    }

    public static String decryptDES(String decryptString, String decryptKey)
            throws Exception {
        byte[] byteMi = Base64.decode(decryptString);
        IvParameterSpec zeroIv = new IvParameterSpec(iv);
        // IvParameterSpec zeroIv = new IvParameterSpec(new byte[8]);
        SecretKeySpec key = new SecretKeySpec(decryptKey.getBytes(), "DES");
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, zeroIv);
        byte decryptedData[] = cipher.doFinal(byteMi);

        return new String(decryptedData);
    }

    public static String decryptDES(String src) throws Exception {
        String urlKey = src.substring(0, 8);
        String readlyContent = src.substring(8);
        return decryptDES(readlyContent, urlKey);
    }

}
