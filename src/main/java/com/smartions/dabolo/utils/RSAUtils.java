package com.smartions.dabolo.utils;

import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;

import org.apache.tomcat.util.codec.binary.Base64;


public class RSAUtils {
	public static final String CHARSET = "UTF-8";
	public static final String RSA_ALGORITHM = "RSA";
	public static final int KEY_SIZE = 2048;
	public static final String PULIBC_KEY = "publicKey";
	public static final String PRIVATE_KEY = "privateKey";
	

	public static void main(String[] args) {
		
			RSAUtils.createKeys();
		
		
	}
	public static String signStr(String l_String_content, String privateKey) {
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.update(l_String_content.getBytes("utf-8")); // 使用指定的字节更新摘要。
			byte[] signsMD5 = messageDigest.digest(); // 通过执行诸如填充之类的最终操作完成哈希计算。
			
			PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey));
			KeyFactory keyf = KeyFactory.getInstance("RSA");
			PrivateKey priKey = keyf.generatePrivate(priPKCS8);
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.ENCRYPT_MODE, priKey);
			return Base64.encodeBase64String(cipher.doFinal(signsMD5));
		} catch (Exception e) {
		}
		return null;
	}
	
	public static boolean verify(String l_String_content, String publicKey, String l_String_sign) {
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.update(l_String_content.getBytes("utf-8")); // 使用指定的字节更新摘要。
			byte[] signsMD5 = messageDigest.digest(); // 通过执行诸如填充之类的最终操作完成哈希计算。
			
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			byte[] encodedKey = Base64.decodeBase64(publicKey);
			PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.DECRYPT_MODE, pubKey);
			byte[] tmp = cipher.doFinal(Base64.decodeBase64(l_String_sign));
			boolean flag = new String(tmp).equals(new String(signsMD5));
			return flag;
		} catch (Exception e) {
			 e.printStackTrace();
		}
		return false;
	}
	// 生成rsa秘钥对
	public static Map<String, String> createKeys() {
		Map<String, String> keyPairMap = new HashMap<String, String>();
		try {
			KeyPairGenerator kpg = KeyPairGenerator.getInstance(RSA_ALGORITHM);
			SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG" );
			kpg.initialize(KEY_SIZE, secureRandom);
			KeyPair keyPair = kpg.generateKeyPair();
			Key publicKey = keyPair.getPublic();
			Key privateKey = keyPair.getPrivate();
			String publicKeyStr = Base64.encodeBase64URLSafeString(publicKey.getEncoded());
			String privateKeyStr = Base64.encodeBase64URLSafeString(privateKey.getEncoded());
			keyPairMap.put(PULIBC_KEY, publicKeyStr);
			keyPairMap.put(PRIVATE_KEY, privateKeyStr);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return keyPairMap;
	}
	//md5
	public static String md5(String src) throws NoSuchAlgorithmException {
		MessageDigest md=MessageDigest.getInstance("MD5");
		return new String(AES.parseByte2HexStr(md.digest(src.getBytes())));
	}
}
