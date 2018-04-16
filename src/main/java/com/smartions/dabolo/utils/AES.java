package com.smartions.dabolo.utils;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class AES {
	public static final String KEY_GENERATION_ALG = "PBKDF2WithHmacSHA1";
	public static final int HASH_ITERATIONS = 10000;
	public static final int KEY_LENGTH = 256;
	public static byte[] salt = { 1, 3, 9, 6, 9, 4, 4, 4, 0, 2, 0xA, 0xB, 0xC, 0xD, 0xE, 0xF };
	public static final String CIPHERMODEPADDING = "AES/CBC/PKCS5Padding";
	public static SecretKeyFactory keyfactory = null;
	public static IvParameterSpec ivSpec = new IvParameterSpec(
			new byte[] { 0xA, 1, 0xB, 5, 4, 0xF, 7, 9, 0x17, 3, 1, 6, 8, 0xC, 0xD, 91 });

	public synchronized static void createSecretKeyFactory() {
		if (keyfactory == null) {
			try {
				keyfactory = SecretKeyFactory.getInstance(KEY_GENERATION_ALG);
			} catch (NoSuchAlgorithmException e) {
				System.out.println("no key factory support for PBEWITHSHAANDTWOFISH-CBC");
			}
		}
	}

	public static String parseByte2HexStr(byte buf[]) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < buf.length; i++) {
			String hex = Integer.toHexString(buf[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			sb.append(hex.toUpperCase());
		}
		return sb.toString();
	}

	public static byte[] parseHexStr2Byte(String hexStr) {
		if (hexStr.length() < 1)
			return null;
		byte[] result = new byte[hexStr.length() / 2];
		for (int i = 0; i < hexStr.length() / 2; i++) {
			int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
			int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
			result[i] = (byte) (high * 16 + low);
		}
		return result;
	}

	public static SecretKeySpec aesKeyConvert(String key) {
		try {
			PBEKeySpec myKeyspec = new PBEKeySpec(key.toCharArray(), salt, HASH_ITERATIONS, KEY_LENGTH);
			if (keyfactory == null)
				createSecretKeyFactory();
			SecretKey sk = keyfactory.generateSecret(myKeyspec);
			byte[] skAsByteArray = sk.getEncoded();
			SecretKeySpec skforAES = new SecretKeySpec(skAsByteArray, "AES");
			return skforAES;
		} catch (InvalidKeySpecException ikse) {
			System.out.println("invalid key spec for PBEWITHSHAANDTWOFISH-CBC");
		}
		return null;
	}

	public static String encrypt(byte[] plaintext, String password) {
		SecretKeySpec skforAES = aesKeyConvert(password);
		byte[] ciphertext = encrypt(CIPHERMODEPADDING, skforAES, ivSpec, plaintext);
		String base64_ciphertext = parseByte2HexStr(ciphertext);
		return base64_ciphertext;
	}

	public static String decrypt(String ciphertext_base64, String password) {
		byte[] s = parseHexStr2Byte(ciphertext_base64);
		SecretKeySpec skforAES = aesKeyConvert(password);
		String decrypted = new String(decrypt(CIPHERMODEPADDING, skforAES, ivSpec, s));
		return decrypted;
	}


	public static byte[] encrypt(String cmp, SecretKey sk, IvParameterSpec IV, byte[] msg) {
		try {
			Cipher c = Cipher.getInstance(cmp);
			c.init(Cipher.ENCRYPT_MODE, sk, IV);
			return c.doFinal(msg);
		} catch (NoSuchAlgorithmException e) {
			System.out.println(e.getMessage());
		} catch (NoSuchPaddingException e) {
			System.out.println(e.getMessage());
		} catch (InvalidKeyException e) {
			System.out.println(e.getMessage());
		} catch (InvalidAlgorithmParameterException e) {
			System.out.println(e.getMessage());
		} catch (IllegalBlockSizeException e) {
			System.out.println(e.getMessage());
		} catch (BadPaddingException e) {
			System.out.println(e.getMessage());
		}
		return null;
	}

	public static byte[] decrypt(String cmp, SecretKey sk, IvParameterSpec IV, byte[] ciphertext) {
		try {
			Cipher c = Cipher.getInstance(cmp);
			c.init(Cipher.DECRYPT_MODE, sk, IV);
			return c.doFinal(ciphertext);
		} catch (NoSuchAlgorithmException nsae) {
			System.out.println(nsae.getMessage());
		} catch (NoSuchPaddingException nspe) {
			System.out.println(nspe.getMessage());
		} catch (InvalidKeyException e) {
			System.out.println(e.getMessage());
		} catch (InvalidAlgorithmParameterException e) {
			System.out.println(e.getMessage());
		} catch (IllegalBlockSizeException e) {
			System.out.println(e.getMessage());
		} catch (BadPaddingException e) {
			System.out.println(e.getMessage());
		}
		return null;
	}
}