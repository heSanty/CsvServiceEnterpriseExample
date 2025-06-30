import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.spec.*;
import java.util.Base64;
import java.nio.charset.StandardCharsets;

public class AESDecryptExample {
    public static void main(String[] args) throws Exception {
        String algorithm = "AES/CBC/PKCS5PADDING";
        String paddingAlgorithm = "PBKDF2WithHmacSHA256";
        String secretKey = "J3U+30ZVxm1ONDS40kYAag==";  // codificado como texto
        String salt = "mX4Gb9I5Gj4WIH8OX2UDg==";
        String iv = "J3U+30ZVxm1ONDS40kYAag==";
        String encryptedBase64 = "2ySj1BRHSGy1AGXy56YBRBNS3VTPJsRjn3C1YaGGQYo=";

        // Decodifica el IV y salt desde Base64
        byte[] ivBytes = Base64.getDecoder().decode(iv);
        byte[] saltBytes = Base64.getDecoder().decode(salt);

        // Deriva la clave con PBKDF2 usando la contraseña (des-codificada)
        char[] password = new String(Base64.getDecoder().decode(secretKey), StandardCharsets.UTF_8).toCharArray();
        SecretKeyFactory factory = SecretKeyFactory.getInstance(paddingAlgorithm);
        KeySpec spec = new PBEKeySpec(password, saltBytes, 65536, 256);
        SecretKey tmp = factory.generateSecret(spec);
        SecretKeySpec secret = new SecretKeySpec(tmp.getEncoded(), "AES");

        // Desencripta
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(ivBytes));
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedBase64));
        String decryptedText = new String(decryptedBytes, StandardCharsets.UTF_8);

        System.out.println("Texto desencriptado: " + decryptedText);
    }
}