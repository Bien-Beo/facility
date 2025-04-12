package com.utc2.facilityui.auth;

public class TokenStorage {
    private static String token;

    public static void setToken(String newToken) {
        token = newToken;
    }

    public static String getToken() {
        return token;
    }
    public static void clearToken() {
        token = null;
        // Xóa khỏi Preferences hoặc file nếu cần
        System.out.println("Token cleared.");
    }
}
