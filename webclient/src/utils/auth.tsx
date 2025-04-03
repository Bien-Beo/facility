import { createContext, useState, useEffect, FC } from "react";

// Tạo Context
export const AuthContext = createContext<AuthContextType | null>(null);

export const AuthProvider: FC<AuthProviderProps> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null);

  const fetchUser = async (token: string) => {
    console.log("Token gửi đi:", token); // Kiểm tra xem token có đúng chưa

    try {
      const response = await fetch(`${import.meta.env.VITE_APP_SERVER_URL}/users/myInfo`, {
        method: "GET",
        headers: {
          Authorization: `Bearer ${token}`, // Đúng format
          "Content-Type": "application/json",
        },
      });

      if (!response.ok) throw new Error("Failed to fetch user");

      const userData = await response.json();
      setUser(userData.result);
    } catch (error) {
      console.error("Error fetching user:", error);
      logout();
    }
  };

  // HÀM ĐĂNG NHẬP
  const login = async (authData: { token: string; authenticated: boolean }) => {
    console.log("Lưu token vào localStorage:", authData.token);
    localStorage.setItem("token", authData.token); // Chỉ lưu token
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
    if (token) fetchUser(token);
  }, []);

  return (
    <AuthContext.Provider value={{ user, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
};