package example_app;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

class AesEncryption {

	private static final String keyString = "a1f256d8190d4fc48895692460b98869"; // 32 byte key for AES-128
	private static final String ivString = "45aa18a4565b93d545aa18a4565b93d5"; // 16 byte initialization vector

	static String encryptString(String plainText) throws Exception {

		byte[] key = hexStringToByteArray(keyString);
		byte[] iv = hexStringToByteArray(ivString);

		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
		IvParameterSpec ivSpec = new IvParameterSpec(iv);
		cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);

		byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
		return Base64.getEncoder().encodeToString(encrypted); // Base64 for better readability
	}

	static String decryptString(String cipherText) throws Exception {

		byte[] key = hexStringToByteArray(keyString);
		byte[] iv = hexStringToByteArray(ivString);

		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
		IvParameterSpec ivSpec = new IvParameterSpec(iv);
		cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);

		byte[] decoded = Base64.getDecoder().decode(cipherText); // Base64 for better readability
		byte[] decrypted = cipher.doFinal(decoded);
		return new String(decrypted, StandardCharsets.UTF_8);
	}

	// Helper method for converting hex string to byte array
	static byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
		}
		return data;
	}
}
