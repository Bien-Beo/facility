// import { Navigate } from "react-router-dom";
// import { FC, JSX } from "react";

// import { useAuth } from "../hooks/useAuth";
// import ErrorComponent from "./Error";

// export const RequireAuth: FC<RequireAuthProps> = ({
//   children,
//   Technician,
//   FacilityManager,
//   Admin,
//   User,
// }): JSX.Element => {
//   const auth = useAuth();

//   if (!auth!.user) {
//     return <Navigate to="/auth/login" />;
//   }

//   try {
//     if (Technician && auth?.user?.roleName !== "TECHNICIAN") {
//       return <Navigate to="/" />;
//     } else if (FacilityManager && auth?.user?.roleName !== "FACILITY_MANAGER") {
//       return <Navigate to="/" />;
//     } else if (Admin && auth?.user?.roleName !== "ADMIN") {
//       return <Navigate to="/admin/facilities" />;
//     } else if (User && auth?.user?.roleName !== "USER") {
//       return <Navigate to="/" />;
//     }
//   } catch {
//     return (
//       <ErrorComponent status={401} message="Please log in and try again" />
//     );
//   }

//   return children;
// };

import React, { FC, JSX } from "react"; // Import React
import { Navigate, useLocation } from "react-router-dom";
import { Box, CircularProgress } from "@mui/material"; // Để hiển thị loading
import { useAuth } from "../hooks/useAuth"; // Đường dẫn đến hook useAuth
import { RequireAuthProps } from "../types";
// import ErrorComponent from "./Error"; // Có thể không cần nếu redirect

// Đảm bảo bạn có trang Unauthorized hoặc route "/" để redirect
// Ví dụ: tạo component UnauthorizedPage đơn giản

// Component đã sửa đổi
export const RequireAuth: FC<RequireAuthProps> = ({ children, allowedRoles }): JSX.Element => {
    const auth = useAuth();
    const location = useLocation(); // Để lưu lại trang người dùng muốn vào

    // 1. Xử lý trạng thái Loading (Quan trọng)
    // Giả sử useAuth() cung cấp cờ loadingUser
    if (auth.loadingUser) {
        return (
            <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh' }}>
                <CircularProgress />
            </Box>
        );
    }

    // 2. Kiểm tra đã xác thực (đăng nhập) chưa
    if (!auth.user) {
        // Chưa đăng nhập, điều hướng về trang login
        // Lưu lại trang định đến để quay lại sau khi login thành công
        console.warn("RequireAuth: User not authenticated. Redirecting to login.");
        return <Navigate to="/auth/login" state={{ from: location }} replace />;
    }

    // 3. Kiểm tra quyền truy cập (nếu allowedRoles được cung cấp và không rỗng)
    const currentUserRoleName = auth.user.roleName; // Lấy tên vai trò từ user đã đăng nhập

    if (allowedRoles && allowedRoles.length > 0) {
        // Kiểm tra xem vai trò của user có nằm trong danh sách được phép không
        if (!currentUserRoleName || !allowedRoles.includes(currentUserRoleName)) {
            // Đã đăng nhập nhưng không có quyền truy cập route này
            console.warn(`RequireAuth: User role "${currentUserRoleName}" not in allowed roles [${allowedRoles.join(', ')}]. Redirecting.`);
            // Điều hướng đến trang báo lỗi không có quyền hoặc trang chủ
            return <Navigate to="/unauthorized" replace />; // Tạo route "/unauthorized"
            // Hoặc return <Navigate to="/" replace />;
        }
    }

    // 4. Đã xác thực và có quyền (hoặc không yêu cầu quyền cụ thể) -> Render nội dung
    return <>{children}</>;
};