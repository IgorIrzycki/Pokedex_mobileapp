package com.wat.edu.pokedexmobileapp.Data;

public class LoginResponse {
    private String username;
    private String token;

    public LoginResponse(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

}

