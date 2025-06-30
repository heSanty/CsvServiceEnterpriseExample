import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;
import java.security.spec.*;
import java.util.Base64;

public class AESDecryptExample {
    public static void main(String[] args) throws Exception {
        String algorithm = "AES/CBC/PKCS5PADDING";
        String secretKeyBase64 = "J3U+30ZVxm1ONDS40kYAag==";
        String saltBase64 = "mX4Gb9I5Gj4WIH8OX2UDg==";
        String ivBase64 = "J3U+30ZVxm1ONDS40kYAag==";
        String encryptedTextBase64 = "2ySj1BRHSGy1AGXy56YBRBNS3VTPJsRjn3C1YaGGQYo=";

        byte[] salt = Base64.getDecoder().decode(saltBase64);
        byte[] iv = Base64.getDecoder().decode(ivBase64);
        char[] password = new String(Base64.getDecoder().decode(secretKeyBase64)).toCharArray();

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password, salt, 65536, 256);
        SecretKey tmp = factory.generateSecret(spec);
        SecretKeySpec secret = new SecretKeySpec(tmp.getEncoded(), "AES");

        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));

        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedTextBase64));
        String decryptedText = new String(decryptedBytes);

        System.out.println("Texto desencriptado: " + decryptedText);
    }
}
