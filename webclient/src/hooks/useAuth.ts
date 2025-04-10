// import { useContext } from "react";
// import { AuthContext } from "../utils/auth";
// import { AuthContextType } from "../types";

// export const useAuth = (): AuthContextType | null => useContext(AuthContext);

import { useContext } from "react";
import { AuthContext } from "../utils/auth";

export const useAuth = (): AuthContextType => { // Luôn trả về AuthContextType
    const context = useContext(AuthContext);
    if (context === undefined || context === null) { // Kiểm tra kỹ hơn
        // Lỗi này xảy ra nếu dùng hook ở nơi không được bọc bởi AuthProvider
        throw new Error("useAuth must be used within an AuthProvider");
    }
    return context; // Trả về context trực tiếp
};