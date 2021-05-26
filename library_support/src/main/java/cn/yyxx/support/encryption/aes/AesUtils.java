package cn.yyxx.support.encryption.aes;


import cn.yyxx.support.encryption.Base64Utils;

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
}
