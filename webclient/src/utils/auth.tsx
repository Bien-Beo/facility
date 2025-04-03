import { createContext, useState, useEffect, FC } from "react";

// Tạo Context
export const AuthContext = createContext<AuthContextType | null>(null);

export const AuthProvider: FC<AuthProviderProps> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null);
  const [loadingUser, setLoadingUser] = useState<boolean>(true); // Thêm state loading

  const fetchUser = async (token: string) => {
    console.log("Token gửi đi:", token);

    try {
      const response = await fetch(`${import.meta.env.VITE_APP_SERVER_URL}/users/myInfo`, {
        method: "GET",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
      });

      if (!response.ok) throw new Error("Failed to fetch user");

      const userData = await response.json();
      if (userData.result) setUser(userData.result); // Kiểm tra dữ liệu trước khi set
    } catch (error) {
      console.error("Error fetching user:", error);
      logout();
    } finally {
      setLoadingUser(false); // Đánh dấu đã xong
    }
  };

  // HÀM ĐĂNG NHẬP
  const login = async (authData: { token: string; authenticated: boolean }) => {
    console.log("Lưu token vào localStorage:", authData.token);
    localStorage.setItem("token", authData.token);
    await fetchUser(authData.token);
  };

  // HÀM ĐĂNG XUẤT
  const logout = () => {
    localStorage.removeItem("token");
    setUser(null);
  };

  // Kiểm tra token khi F5 trang
  useEffect(() => {
    const token = localStorage.getItem("token");
    if (token) {
      fetchUser(token);
    } else {
      setLoadingUser(false); // Nếu không có token, kết thúc loading ngay
    }
  }, []);

  return (
    <AuthContext.Provider value={{ user, login, logout, loadingUser }}>
      {!loadingUser ? children : <div>Loading...</div>} 
    </AuthContext.Provider>
  );
};
