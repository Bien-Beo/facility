package com.utc2.facilityui.auth;

import com.utc2.facilityui.model.User; // Model User phía client

public class TokenStorage {
    private static String token = null;
    private static User currentUser = null;

    public static synchronized void login(String newToken, User user) {
        System.out.println("TokenStorage: Logging in user. Token and User details set.");
        token = newToken;
        currentUser = user;
        if (user != null) {
            // Log cả hai loại ID để kiểm tra
            System.out.println("TokenStorage: User Logged In - Business UserID (e.g., ST12345678): " + user.getUserId() +
                    ", Database PrimaryKey ID (UUID): " + user.getId() + // Giả sử user.getId() trả về UUID
                    ", Username: " + user.getUsername());
        }
    }

    public static String getToken() {
        return token;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    /**
     * Lấy UserID nghiệp vụ (ví dụ: ST12345678) của người dùng hiện tại.
     * @return UserID nghiệp vụ, hoặc null nếu chưa đăng nhập.
     */
    public static String getCurrentBusinessUserId() { // Đổi tên cho rõ ràng
        return (currentUser != null) ? currentUser.getUserId() : null;
    }

    /**
     * Lấy ID khóa chính (UUID) của người dùng hiện tại để dùng cho các API call.
     * @return Database ID (UUID), hoặc null nếu chưa đăng nhập hoặc user object không có ID này.
     */
    public static String getCurrentUserDatabaseId() {
        // Giả sử model User của bạn có phương thức getId() trả về UUID khóa chính
        return (currentUser != null) ? currentUser.getId() : null;
    }


    public static String getCurrentUsername() {
        return (currentUser != null) ? currentUser.getUsername() : null;
    }

    public static synchronized void logout() {
        token = null;
        currentUser = null;
        System.out.println("TokenStorage: Token and User details cleared (logout).");
    }

    public static boolean isLoggedIn() {
        // Cập nhật điều kiện isLoggedIn nếu cần, ví dụ kiểm tra currentUserDatabaseId
        return token != null && !token.trim().isEmpty() &&
                currentUser != null &&
                getCurrentUserDatabaseId() != null && !getCurrentUserDatabaseId().trim().isEmpty(); // Kiểm tra ID chính
    }

    // Các phương thức cũ hơn có thể không cần thiết nếu bạn chuẩn hóa việc dùng login/logout
    public static void setOnlyToken(String newToken) {
        System.out.println("TokenStorage: Setting only token. User details not affected by this call.");
        token = newToken;
        if (newToken == null) {
            System.out.println("TokenStorage: Token cleared by setOnlyToken(null). User details remain if previously set.");
        }
    }
    public static void clearToken() {
        logout();
    }

    public static boolean hasToken() {
        return token != null && !token.trim().isEmpty();
    }
}