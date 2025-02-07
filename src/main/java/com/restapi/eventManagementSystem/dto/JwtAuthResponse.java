package com.restapi.eventManagementSystem.dto;

public class JwtAuthResponse {

	private String token;
	
	private String tokenType="Bearer";

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getTokenType() {
		return tokenType;
	}

	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}
	
	

	public JwtAuthResponse(String token) {
		super();
		this.token = token;
	}

	@Override
	public String toString() {
		return "JwtAuthResponse [token=" + token + ", tokenType=" + tokenType + "]";
	}
}
