package com.sizzler.common.utils;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.FileInputStream;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

public class EncryptTools {
  private static final String DEFAULT_PRIVATE_KEY_STRING = "MIIBVAIBADANBgkqhkiG9w0BAQEFAASCAT4wggE6AgEAAkEAocbCrurZGbC5GArEHKlAfDSZi7gFBnd4yxOt0rwTqKBFzGyhtQLu5PRKjEiOXVa95aeIIBJ6OhC2f8FjqFUpawIDAQABAkAPejKaBYHrwUqUEEOe8lpnB6lBAsQIUFnQI/vXU4MV+MhIzW0BLVZCiarIQqUXeOhThVWXKFt8GxCykrrUsQ6BAiEA4vMVxEHBovz1di3aozzFvSMdsjTcYRRo82hS5Ru2/OECIQC2fAPoXixVTVY7bNMeuxCP4954ZkXp7fEPDINCjcQDywIgcc8XLkkPcs3Jxk7uYofaXaPbg39wuJpEmzPIxi3k0OECIGubmdpOnin3HuCP/bbjbJLNNoUdGiEmFL5hDI4UdwAdAiEAtcAwbm08bKN7pwwvyqaCBC//VnEWaq39DCzxr+Z2EIk=";
  public static final String DEFAULT_PUBLIC_KEY_STRING = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAKHGwq7q2RmwuRgKxBypQHw0mYu4BQZ3eMsTrdK8E6igRcxsobUC7uT0SoxIjl1WveWniCASejoQtn/BY6hVKWsCAwEAAQ==";

  public static void main(String[] args) throws Exception {
    String password = args[0];
    System.out.println(encrypt(password));
  }

  public static String generateApiKey(String uuid) throws Exception {
    return encrypt(uuid);
  }

  public static String decrypt(String cipherText) throws Exception {
    return decrypt((String) null, cipherText);
  }

  public static String decrypt(String publicKeyText, String cipherText) throws Exception {
    PublicKey publicKey = getPublicKey(publicKeyText);

    return decrypt(publicKey, cipherText);
  }

  public static PublicKey getPublicKeyByX509(String x509File) {
    if (x509File == null || x509File.length() == 0) {
      return getPublicKey(null);
    }

    FileInputStream in = null;
    try {
      in = new FileInputStream(x509File);

      CertificateFactory factory = CertificateFactory.getInstance("X.509");
      Certificate cer = factory.generateCertificate(in);
      return cer.getPublicKey();
    } catch (Exception e) {
      throw new IllegalArgumentException("Failed to get public key", e);
    } finally {
      EncryptTools.close(in);
    }
  }

  public static PublicKey getPublicKey(String publicKeyText) {
    if (publicKeyText == null || publicKeyText.length() == 0) {
      publicKeyText = EncryptTools.DEFAULT_PUBLIC_KEY_STRING;
    }

    try {
      byte[] publicKeyBytes = Base64.base64ToByteArray(publicKeyText);
      X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(publicKeyBytes);

      KeyFactory keyFactory = KeyFactory.getInstance("RSA");
      return keyFactory.generatePublic(x509KeySpec);
    } catch (Exception e) {
      throw new IllegalArgumentException("Failed to get public key", e);
    }
  }

  public static PublicKey getPublicKeyByPublicKeyFile(String publicKeyFile) {
    if (publicKeyFile == null || publicKeyFile.length() == 0) {
      return EncryptTools.getPublicKey(null);
    }

    FileInputStream in = null;
    try {
      in = new FileInputStream(publicKeyFile);
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      int len = 0;
      byte[] b = new byte[512 / 8];
      while ((len = in.read(b)) != -1) {
        out.write(b, 0, len);
      }

      byte[] publicKeyBytes = out.toByteArray();
      X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKeyBytes);
      KeyFactory factory = KeyFactory.getInstance("RSA");
      return factory.generatePublic(spec);
    } catch (Exception e) {
      throw new IllegalArgumentException("Failed to get public key", e);
    } finally {
      EncryptTools.close(in);
    }
  }

  public static String decrypt(PublicKey publicKey, String cipherText) throws Exception {
    Cipher cipher = Cipher.getInstance("RSA");
    try {
      cipher.init(Cipher.DECRYPT_MODE, publicKey);
    } catch (InvalidKeyException e) {
      // 鍥犱负 IBM JDK 涓嶆敮鎸佺閽ュ姞瀵� 鍏挜瑙ｅ瘑, 鎵�互瑕佸弽杞叕绉侀挜
      // 涔熷氨鏄瀵逛簬瑙ｅ瘑, 鍙互閫氳繃鍏挜鐨勫弬鏁颁吉閫犱竴涓閽ュ璞℃楠�IBM JDK
      RSAPublicKey rsaPublicKey = (RSAPublicKey) publicKey;
      RSAPrivateKeySpec spec = new RSAPrivateKeySpec(rsaPublicKey.getModulus(),
          rsaPublicKey.getPublicExponent());
      Key fakePrivateKey = KeyFactory.getInstance("RSA").generatePrivate(spec);
      cipher = Cipher.getInstance("RSA"); // It is a stateful object. so we need
                                          // to get new one.
      cipher.init(Cipher.DECRYPT_MODE, fakePrivateKey);
    }

    if (cipherText == null || cipherText.length() == 0) {
      return cipherText;
    }

    byte[] cipherBytes = Base64.base64ToByteArray(cipherText);
    byte[] plainBytes = cipher.doFinal(cipherBytes);

    return new String(plainBytes);
  }

  public static String encrypt(String plainText) throws Exception {
    return encrypt((String) null, plainText);
  }

  public static String encrypt(String key, String plainText) throws Exception {
    if (key == null) {
      key = DEFAULT_PRIVATE_KEY_STRING;
    }

    byte[] keyBytes = Base64.base64ToByteArray(key);
    return encrypt(keyBytes, plainText);
  }

  public static String encrypt(byte[] keyBytes, String plainText) throws Exception {
    PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
    KeyFactory factory = KeyFactory.getInstance("RSA");
    PrivateKey privateKey = factory.generatePrivate(spec);
    Cipher cipher = Cipher.getInstance("RSA");
    try {
      cipher.init(Cipher.ENCRYPT_MODE, privateKey);
    } catch (InvalidKeyException e) {
      // For IBM JDK, 鍘熷洜璇风湅瑙ｅ瘑鏂规硶涓殑璇存槑
      RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) privateKey;
      RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(rsaPrivateKey.getModulus(),
          rsaPrivateKey.getPrivateExponent());
      Key fakePublicKey = KeyFactory.getInstance("RSA").generatePublic(publicKeySpec);
      cipher = Cipher.getInstance("RSA");
      cipher.init(Cipher.ENCRYPT_MODE, fakePublicKey);
    }

    byte[] encryptedBytes = cipher.doFinal(plainText.getBytes("UTF-8"));
    String encryptedString = Base64.byteArrayToBase64(encryptedBytes);

    return encryptedString;
  }

  public static byte[][] genKeyPairBytes(int keySize) throws NoSuchAlgorithmException {
    byte[][] keyPairBytes = new byte[2][];

    KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
    gen.initialize(keySize, new SecureRandom());
    KeyPair pair = gen.generateKeyPair();

    keyPairBytes[0] = pair.getPrivate().getEncoded();
    keyPairBytes[1] = pair.getPublic().getEncoded();

    return keyPairBytes;
  }

  public static String[] genKeyPair(int keySize) throws NoSuchAlgorithmException {
    byte[][] keyPairBytes = genKeyPairBytes(keySize);
    String[] keyPairs = new String[2];

    keyPairs[0] = Base64.byteArrayToBase64(keyPairBytes[0]);
    keyPairs[1] = Base64.byteArrayToBase64(keyPairBytes[1]);

    return keyPairs;
  }

  public final static void close(Closeable x) {
    if (x != null) {
      try {
        x.close();
      } catch (Exception e) {

      }
    }
  }
}
