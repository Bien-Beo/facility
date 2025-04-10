// import { createContext, useState, useEffect, FC } from "react";
// import { CircularProgress } from "@mui/material";
// import { AuthContextType, AuthProviderProps, User } from "../types";

// // Tạo Context
// export const AuthContext = createContext<AuthContextType | null>(null);

// export const AuthProvider: FC<AuthProviderProps> = ({ children }) => {
//   const [user, setUser] = useState<User | null>(null);
//   const [loadingUser, setLoadingUser] = useState<boolean>(true); // Thêm state loading

//   const fetchUser = async (token: string) => {
//     console.log("Token gửi đi:", token);

//     try {
//       const response = await fetch(`${import.meta.env.VITE_APP_SERVER_URL}/users/myInfo`, {
//         method: "GET",
//         headers: {
//           Authorization: `Bearer ${token}`,
//           "Content-Type": "application/json",
//         },
//       });

//       if (!response.ok) throw new Error("Failed to fetch user");

//       const userData = await response.json();
//       if (userData.result) setUser(userData.result); // Kiểm tra dữ liệu trước khi set
//     } catch (error) {
//       console.error("Error fetching user:", error);
//       logout();
//     } finally {
//       setLoadingUser(false); // Đánh dấu đã xong
//     }
//   };

//   // HÀM ĐĂNG NHẬP
//   const login = async (authData: { token: string; authenticated: boolean }) => {
//     console.log("Lưu token vào localStorage:", authData.token);
//     localStorage.setItem("token", authData.token);
//     await fetchUser(authData.token);
//   };

//   // HÀM ĐĂNG XUẤT
//   const logout = () => {
//     localStorage.removeItem("token");
//     setUser(null);
//   };

//   // Kiểm tra token khi F5 trang
//   useEffect(() => {
//     const token = localStorage.getItem("token");
//     if (token) {
//       fetchUser(token);
//     } else {
//       setLoadingUser(false); // Nếu không có token, kết thúc loading ngay
//     }
//   }, []);

//   return (
//     <AuthContext.Provider value={{ user, login, logout, loadingUser }}>
//       {!loadingUser ? children : 
//         <div className="w-full min-h-screen h-full flex flex-col items-center justify-center">
//           <CircularProgress />
//         </div>
//       } 
//     </AuthContext.Provider>
//   );
// };

import React, { createContext, useState, useEffect, FC, useCallback, useMemo } from "react";
import { Box, CircularProgress } from "@mui/material"; 
import axios from "axios";

// --- Tạo Context ---
export const AuthContext = createContext<AuthContextType | null>(null);

// --- Component Provider ---
export const AuthProvider: FC<AuthProviderProps> = ({ children }) => {
    const [user, setUser] = useState<UserData | null>(null);
    const [loadingUser, setLoadingUser] = useState<boolean>(true); // Bắt đầu loading

    // --- Hàm Fetch thông tin User từ API /users/myInfo ---
    // Dùng useCallback để tối ưu, tránh tạo lại hàm không cần thiết
    const fetchUser = useCallback(async (token: string) => {
        console.log("AuthProvider: Fetching user info with token...");
        // Không cần setLoadingUser(true) ở đây vì useEffect và login đã gọi nó trước khi gọi hàm này
        try {
            const response = await axios.get<UserDetailApiResponse>( // Sử dụng kiểu ApiResponse<UserData>
                `${import.meta.env.VITE_APP_SERVER_URL}/users/myInfo`,
                {
                    headers: {
                        Authorization: `Bearer ${token}`
                    },
                }
            );

            // Kiểm tra response chuẩn { code, result }
            if (response.data && response.data.code === 0 && response.data.result) {
                console.log("AuthProvider: User info fetched:", response.data.result);
                setUser(response.data.result); // Cập nhật state user
            } else {
                 const errorMsg = response.data?.message || "Invalid response from /users/myInfo";
                 console.error("AuthProvider: Error in API response:", errorMsg, response.data);
                 throw new Error(errorMsg); // Ném lỗi nếu cấu trúc không đúng
            }
        } catch (error) {
            console.error("AuthProvider: Error fetching user:", error);
            // Nếu lỗi khi fetch user (token hết hạn, sai...), xóa token và user ở client
            localStorage.removeItem("token");
            localStorage.removeItem("refreshToken"); // Xóa cả refresh token nếu có
            setUser(null);
        } finally {
            // Luôn kết thúc trạng thái loading sau khi fetch xong, dù thành công hay thất bại
            console.log("AuthProvider: Finished fetching user info, loadingUser set to false.");
            setLoadingUser(false);
        }
    }, []); // Dependency rỗng vì hàm không phụ thuộc props/state ngoài

    // --- Hàm Đăng nhập ---
    const login = async (authData: { token: string; authenticated: boolean }) => {
         console.log("Lưu token vào localStorage:", authData.token);
         localStorage.setItem("token", authData.token);
    await fetchUser(authData.token);
    };

    // --- Hàm Đăng xuất ---
    // useCallback để ổn định
    const logout = useCallback(async () => {
        console.log("AuthProvider: Logging out...");
        const token = localStorage.getItem("token");

        // 1. Xóa state và localStorage ở client trước để UI phản hồi ngay lập tức
        setUser(null);
        localStorage.removeItem("token");
        localStorage.removeItem("refreshToken"); // Nếu có

        // 2. Gọi API backend để vô hiệu hóa token (nếu có token)
        if (token) {
            try {
                console.log("AuthProvider: Calling backend logout API...");
                const payload: LogoutRequest = { token };
                await axios.post(
                    `${import.meta.env.VITE_APP_SERVER_URL}/auth/logout`,
                    payload,
                    { headers: { 'Content-Type': 'application/json' } } // POST cần Content-Type
                );
                console.log("AuthProvider: Backend logout successful for token:", token.substring(0, 10) + "...");
            } catch (error) {
                // Lỗi gọi API logout không nên ngăn người dùng logout ở client
                // Chỉ cần log lỗi để biết
                console.error("AuthProvider: Backend logout API call failed:", error);
            }
        }
        // 3. (Tùy chọn) Điều hướng về trang login có thể thực hiện ở đây hoặc ở component dùng logout
        // ví dụ: window.location.href = '/auth/login';
    }, []); // Dependency rỗng

    // --- useEffect: Kiểm tra token khi tải lại trang ---
    useEffect(() => {
        console.log("AuthProvider: Initializing auth state on component mount...");
        const token = localStorage.getItem("token");
        if (token) {
            // Nếu có token, gọi fetchUser (fetchUser sẽ tự set loading)
            fetchUser(token);
        } else {
            // Không có token, không cần làm gì, kết thúc loading
            console.log("AuthProvider: No token found, initial loading finished.");
            setLoadingUser(false);
        }
        // Chỉ chạy 1 lần khi component mount
    }, [fetchUser]); // Phụ thuộc fetchUser vì nó được dùng bên trong

    // --- Tạo giá trị Context ---
    // Dùng useMemo để tối ưu, chỉ tạo object value mới khi các dependency thay đổi
    const contextValue = useMemo((): AuthContextType => ({
        user,
        login,
        logout,
        loadingUser
    }), [user, loadingUser, login, logout]);

    // --- Render Provider ---
    return (
        <AuthContext.Provider value={contextValue}>
            {/* Chỉ render children khi không còn loading user ban đầu */}
            {!loadingUser ? children : (
                // Hiển thị màn hình loading toàn trang
                <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh', width: '100vw', backgroundColor: '#f0f2f5' /* Màu nền nhẹ */ }}>
                    <CircularProgress size={60} />
                </Box>
            )}
        </AuthContext.Provider>
    );
};
