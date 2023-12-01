package Min.app.plus.utils;

import android.util.Base64;
import android.util.Log;

import java.nio.charset.StandardCharsets;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 作者：daboluo on 2023/8/21 16:54
 * Email:daboluo719@gmail.com
 */
//加密类
public class AESEncryption {

    public static String Password="WWUmgP6vzkmdrwPk";//密钥
    private static final String AES_ALGORITHM = "AES";
    private static final String AES_TRANSFORMATION = "AES/ECB/PKCS5Padding";

    public static String decrypt(String encryptedData)  {
        try {
            Key aesKey = new SecretKeySpec(Password.getBytes(StandardCharsets.UTF_8), AES_ALGORITHM);
            Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, aesKey);
            byte[] decryptedBytes = cipher.doFinal(Base64.decode(encryptedData, Base64.DEFAULT));
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e){
            Log.d("ss","郑绍敏"+e);
            e.printStackTrace();
            return null;
        }
    }

}

