package com.budoudoh.codetalker.crypto;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import android.content.Context;
import android.util.Base64;

public class Crypto {

   private static final String engine = "AES";
   private static final String crypto = "AES/CBC/PKCS5Padding";
   private static Context ctx;
   private static KeyManager km;
   public Crypto(Context cntx, KeyManager km) {
     ctx = cntx;
     this.km = km;
   }

   public byte[] cipher(byte[] data, int mode) throws NoSuchAlgorithmException,NoSuchPaddingException,InvalidKeyException,IllegalBlockSizeException,BadPaddingException,InvalidAlgorithmParameterException {
     SecretKeySpec sks = new SecretKeySpec(km.getKey().getBytes(), engine);
     IvParameterSpec iv = new IvParameterSpec(km.getIv().getBytes());
     Cipher c = Cipher.getInstance(crypto);
     c.init(mode, sks, iv);
     return c.doFinal(data);
   }

   public byte[] encrypt(byte[] data) throws InvalidKeyException,
NoSuchAlgorithmException, NoSuchPaddingException,
IllegalBlockSizeException, BadPaddingException,
InvalidAlgorithmParameterException {
     return cipher(data, Cipher.ENCRYPT_MODE);
   }

   public byte[] decrypt(byte[] data) throws InvalidKeyException,
NoSuchAlgorithmException, NoSuchPaddingException,
IllegalBlockSizeException, BadPaddingException,
InvalidAlgorithmParameterException {
     return cipher(data, Cipher.DECRYPT_MODE);
   }

public String armorEncrypt(byte[] data) throws InvalidKeyException,NoSuchAlgorithmException,
NoSuchPaddingException,IllegalBlockSizeException,
BadPaddingException,InvalidAlgorithmParameterException {
         return Base64.encodeToString(encrypt(data), Base64.DEFAULT);
       }

 public String armorDecrypt(String data) throws InvalidKeyException,NoSuchAlgorithmException,
NoSuchPaddingException,IllegalBlockSizeException,
BadPaddingException,InvalidAlgorithmParameterException {
         return new String(decrypt(Base64.decode(data, Base64.DEFAULT)));
       }
}
