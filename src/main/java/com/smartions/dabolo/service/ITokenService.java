package com.smartions.dabolo.service;

public interface ITokenService {
	public String createToken(String privateKey,String userId,String publicKey);
	public String refreshToken(String token);
}
