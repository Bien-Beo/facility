package com.utc2.facilityui.utils; // Hoặc đặt trong package phù hợp

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import java.util.Base64;
import java.util.Optional;

public class TokenUtils {

    /**
     * Trích xuất giá trị 'scope' từ token JWT.
     * @param token Chuỗi JWT.
     * @return Optional chứa giá trị scope nếu có, ngược lại là Optional rỗng.
     */
    public static Optional<String> extractScope(String token) {
        if (token == null || token.isEmpty()) {
            return Optional.empty();
        }

        try {
            // JWT gồm 3 phần: header.payload.signature, phân tách bởi dấu "."
            String[] parts = token.split("\\.");
            if (parts.length < 2) {
                System.err.println("Định dạng token không hợp lệ.");
                return Optional.empty();
            }

            // Phần thứ hai là payload, được mã hóa Base64 URL
            String payloadBase64Url = parts[1];
            // Cần thay thế '-' bằng '+' và '_' bằng '/' để giải mã Base64 chuẩn
            // String payloadBase64 = payloadBase64Url.replace('-', '+').replace('_', '/');
            // Thêm padding nếu cần (không phải lúc nào cũng cần với Base64 URL)
            // switch (payloadBase64.length() % 4) {
            //     case 2: payloadBase64 += "=="; break;
            //     case 3: payloadBase64 += "="; break;
            // }

            // Sử dụng Base64 URL Decoder của Java 8+
            byte[] decodedBytes = Base64.getUrlDecoder().decode(payloadBase64Url);
            String decodedPayload = new String(decodedBytes);

            // Phân tích chuỗi JSON của payload
            JsonObject jsonObject = JsonParser.parseString(decodedPayload).getAsJsonObject();

            // Trích xuất trường 'scope'
            if (jsonObject.has("scope")) {
                return Optional.of(jsonObject.get("scope").getAsString());
            } else {
                System.err.println("Token không chứa trường 'scope'.");
                return Optional.empty();
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Lỗi giải mã Base64: " + e.getMessage());
            return Optional.empty();
        } catch (JsonParseException e) {
            System.err.println("Lỗi phân tích JSON payload: " + e.getMessage());
            return Optional.empty();
        } catch (Exception e) { // Bắt các lỗi khác có thể xảy ra
            System.err.println("Lỗi không xác định khi phân tích token: " + e.getMessage());
            e.printStackTrace(); // In stack trace để debug
            return Optional.empty();
        }
    }
}