package cn.yyxx.support.encryption;

/**
 * @author #Suyghur.
 * Created on 2021/06/19
 */
public class HexUtils {

    /**
     * @param src 16进制字符串
     * @return 字节数组
     */
    public static byte[] hexString2Bytes(String src) {
        int l = src.length() / 2;
        byte[] ret = new byte[l];
        for (int i = 0; i < l; i++) {
            ret[i] = Integer.valueOf(src.substring(i * 2, i * 2 + 2), 16).byteValue();
        }
        return ret;
    }

    public static String bytes2HexString(byte[] b) {
        StringBuilder result = new StringBuilder();
        for (byte value : b) {
            result.append(String.format("%02X", value));
        }
        return result.toString();
    }
}
