package com.utc2.facilityui.auth;

/**
 * Lớp lưu trữ token xác thực đơn giản trong bộ nhớ.
 * Lưu ý: Token sẽ bị mất khi ứng dụng tắt.
 */
public class TokenStorage {
    private static String token = null; // Khởi tạo là null

    /**
     * Lưu trữ token mới.
     * @param newToken Token nhận được sau khi đăng nhập.
     */
    public static void setToken(String newToken) {
        System.out.println("TokenStorage: Setting new token.");
        token = newToken;
    }

    /**
     * Lấy token hiện tại đang được lưu trữ.
     * @return Token hiện tại, hoặc null nếu chưa có token nào được lưu.
     */
    public static String getToken() {
        // System.out.println("TokenStorage: Getting token: " + (token != null ? "Found" : "Not found"));
        return token;
    }

    /**
     * Xóa token hiện tại khỏi bộ nhớ.
     */
    public static void clearToken() {
        token = null;
        System.out.println("TokenStorage: Token cleared from memory.");
    }

    /**
     * Kiểm tra xem có token nào đang được lưu trữ hay không.
     * @return true nếu có token hợp lệ (khác null và không rỗng), false nếu không.
     */
    public static boolean hasToken() {
        return token != null && !token.trim().isEmpty(); // Thêm trim() để chắc chắn
    }
}