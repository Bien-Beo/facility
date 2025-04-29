package com.utc2.facilityui.helper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    private static final Properties properties = new Properties();
    private static final String CONFIG_FILE = "config.properties"; // Tên file cấu hình

    static {
        // Sử dụng try-with-resources để đảm bảo InputStream được đóng
        try (InputStream input = Config.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                // Thay vì printStackTrace, nên log lỗi hoặc ném RuntimeException rõ ràng hơn
                System.err.println("FATAL ERROR: Cannot find the configuration file '" + CONFIG_FILE + "' in classpath.");
                // Hoặc ném lỗi để ứng dụng không thể khởi động nếu thiếu file config quan trọng
                // throw new RuntimeException("Configuration file '" + CONFIG_FILE + "' not found.");
            } else {
                properties.load(input); // Tải thuộc tính từ file
                System.out.println("Configuration file '" + CONFIG_FILE + "' loaded successfully.");
            }
        } catch (IOException e) {
            // Log lỗi khi đọc file
            System.err.println("FATAL ERROR: Could not read the configuration file '" + CONFIG_FILE + "'.");
            e.printStackTrace();
            // Hoặc ném lỗi
            // throw new RuntimeException("Could not read configuration file '" + CONFIG_FILE + "'.", e);
        }
    }

    /**
     * Lấy giá trị cấu hình dựa vào key.
     *
     * @param key Key của thuộc tính cần lấy.
     * @return Giá trị của thuộc tính, hoặc null nếu key không tồn tại.
     */
    public static String get(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            System.err.println("Configuration key '" + key + "' not found in " + CONFIG_FILE);
        }
        return value;
    }

    /**
     * Lấy giá trị cấu hình, trả về giá trị mặc định nếu key không tồn tại.
     *
     * @param key Key của thuộc tính cần lấy.
     * @param defaultValue Giá trị trả về nếu key không tìm thấy.
     * @return Giá trị của thuộc tính, hoặc defaultValue nếu key không tồn tại.
     */
    public static String getOrDefault(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
}