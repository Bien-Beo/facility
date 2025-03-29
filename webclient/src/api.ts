import axios from "axios";

// Lấy token từ localStorage
const getToken = () => localStorage.getItem("token");

export const API = axios.create({
  baseURL: import.meta.env.VITE_APP_SERVER_URL, // Base URL
  withCredentials: true, // Giữ cookies (nếu backend cần)
});

// Thêm token vào mọi request
API.interceptors.request.use((config) => {
  const token = getToken();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});
