package cn.yyxx.support.encryption.aes;


import java.util.Locale;

import cn.yyxx.support.encryption.Base64Utils;
import cn.yyxx.support.encryption.HexUtils;

/**
 * @author #Suyghur.
 * Created on 2020/7/29
 */
public class AesUtils {

    /**
     * 加密
     *
     * @param aesKey
     * @param content
     * @return
     */
    public static String encrypt(String aesKey, String content) throws Exception {
        byte[] keyBytes = aesKey.getBytes("UTF-8");
        byte[] contentBytes = content.getBytes("UTF-8");

        //AES encrypt
        AesEncrypt aesEncrypt = new AesEncrypt(keyBytes);
        byte[] encryptedContent = aesEncrypt.encrypt(contentBytes, 0, contentBytes.length);
        return Base64Utils.encode(encryptedContent);
    }

    public static String encrypt2hex(String aesKey, String iv, String content) {
        try {
            byte[] keyBytes = aesKey.getBytes("UTF-8");
            byte[] ivBytes = iv.getBytes("UTF-8");
            byte[] contentBytes = content.getBytes("UTF-8");

            AesEncrypt aesEncrypt = new AesEncrypt(keyBytes, ivBytes);
            byte[] encryptedContent = aesEncrypt.encrypt(contentBytes, 0, contentBytes.length);
            return HexUtils.bytes2HexString(encryptedContent).toLowerCase(Locale.getDefault());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 解密
     *
     * @param aesKey
     * @param content
     * @return
     */
    public static String decrypt(String aesKey, String content) throws Exception {
        byte[] keyBytes = aesKey.getBytes("UTF-8");
        byte[] contentBytes = Base64Utils.decode(content);
        //AES decrypt
        AesDecrypt aesDecrypt = new AesDecrypt(keyBytes);
        byte[] decryptedContent = aesDecrypt.decrypt(contentBytes, 0, contentBytes.length);
        return new String(decryptedContent);
    }

    /**
     * 解密
     *
     * @param aesKey
     * @param content
     * @return
     */
    public static String decrypt2hex(String aesKey, String iv, String content) {
        try {
            byte[] keyBytes = aesKey.getBytes("UTF-8");
            byte[] ivBytes = iv.getBytes("UTF-8");
            byte[] contentBytes = HexUtils.hexString2Bytes(content);
            //AES decrypt
            AesDecrypt aesDecrypt = new AesDecrypt(keyBytes, ivBytes);
            byte[] decryptedContent = aesDecrypt.decrypt(contentBytes, 0, contentBytes.length);
            return new String(decryptedContent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
