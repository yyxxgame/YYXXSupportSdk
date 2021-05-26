package cn.yyxx.support.encryption.aes;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author #Suyghur.
 * Created on 2020/7/29
 */
public class AesDecrypt extends AesCommon {
    private final byte[] aesKey;
    private final byte[] iv;
    private final Cipher aesCipher;

    public AesDecrypt(byte[] aesKey) {
        if (aesKey == null || !(aesKey.length == 16 || aesKey.length == 32)) {
            throw new RuntimeException("aes key should be 16 bytes(128bits) or 32 bytes(256bits)");
        }
        this.aesKey = aesKey;
        this.iv = generateIV(this.aesKey);
        try {
            this.aesCipher = Cipher.getInstance(AES_CBC_PKCS5PADDING);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] decrypt(byte[] encryptData, int offset, int length)
            throws InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        try {
            aesCipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(this.aesKey, "AES"), new IvParameterSpec(this.iv, 0, this.iv.length));
            return aesCipher.doFinal(encryptData, offset, length);
        } catch (InvalidKeyException e) {
            throw e;
        } catch (InvalidAlgorithmParameterException e) {
            throw e;
        } catch (IllegalBlockSizeException e) {
            throw e;
        } catch (BadPaddingException e) {
            throw e;
        }
    }

}
