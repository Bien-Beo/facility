package com.utc2.facility.controller;

public class TokenStorage {
    private static String token;

    public static void setToken(String newToken) {
        token = newToken;
    }

    public static String getToken() {
        return token;
    }
}
