package com.smartions.dabolo.service;

import java.io.UnsupportedEncodingException;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smartions.dabolo.model.Token;
import com.smartions.dabolo.utils.AES;
import com.smartions.dabolo.utils.RSAUtils;

import net.sf.json.JSONArray;

@Service
public class TokenService implements ITokenService {
	@Autowired
	private Token token;

	@Override
	public String createToken(String privateKey, String userId, String publicKey) {

		try {
			long time = System.currentTimeMillis() + Long.parseLong(token.getTimeOut()) * 1000;
			String tokenId = RSAUtils.md5(userId + String.valueOf(time));
			String tokenIdSign = RSAUtils.signStr(tokenId, privateKey);
			return tokenId + "." + tokenIdSign + "." + publicKey + "."
					+ AES.encrypt(Base64.encodeBase64(String.valueOf(time).getBytes("UTF-8")), token.getSecret());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String refreshToken(String tokenIn) {
		System.out.println(tokenIn);
		String[] tokens = tokenIn.split("\\.");
		System.out.println(JSONArray.fromObject(tokens));
		System.out.println(tokens.length);
		if (RSAUtils.verify(tokens[0], tokens[2], tokens[1])) {
			if (Long.parseLong(AES.decrypt(tokens[3], token.getSecret())) < System.currentTimeMillis())
				return null;
			try {
				long time = System.currentTimeMillis() + Long.parseLong(token.getTimeOut()) * 1000;
				return tokens[0] + "." + tokens[1] + "." + tokens[2] + "."
						+ AES.encrypt(Base64.encodeBase64(String.valueOf(time).getBytes("UTF-8")), token.getSecret());
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

}
