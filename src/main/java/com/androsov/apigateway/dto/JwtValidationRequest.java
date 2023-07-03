package com.androsov.apigateway.dto;

public class JwtValidationRequest {
    public String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public JwtValidationRequest(String token) {
        this.token = token;
    }
}
