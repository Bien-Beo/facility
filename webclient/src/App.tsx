import "@tanstack/react-query";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { AxiosError } from "axios";
import {
  Route,
  RouterProvider,
  createBrowserRouter,
  createRoutesFromElements,
} from "react-router-dom";

import Layout from "./components/Layout";
import PageNotFound from "./components/PageNotFound";
import { RequireAuth } from "./components/RequireAuth";
import RouteError from "./components/RouteError";
import HomePage from "./pages/HomePage";
import AdminBookingsPage from "./pages/AdminBookingsPage";
import AdminFacilitiesPage from "./pages/AdminFacilitiesPage";
import DashboardPage from "./pages/DashboardPage";
import ApprovalsPage from "./pages/ApprovalsPage";
import CancellationsPage from "./pages/CancellationPage";
import LoginPage from "./pages/LoginPage";
import MyBookingsPage from "./pages/MyBookingsPage";
// import ResetPasswordPage from "./pages/ResetPasswordPage";
import FacilityPage from "./pages/FacilityPage";
import { AuthProvider } from "./utils/auth";
import { ThemeProvider, createTheme } from "@mui/material/styles";


declare module "@tanstack/react-query" {
  interface Register {
    defaultError: AxiosError;
  }
}

const router = createBrowserRouter(
  createRoutesFromElements(
    <>
    {/* Route Đăng nhập */}
      <Route path = "/auth">
        <Route
          path = "login"
          element = {<LoginPage />}
          errorElement = {<RouteError />}
        />
      </Route>

      {/* Route Không có quyền truy cập
      <Route
                path="/unauthorized"
                element={<UnauthorizedPage />} // Component hiển thị thông báo lỗi 403
                errorElement={<RouteError />}
             /> */}

      <Route
        path="/"
        element={
          <RequireAuth>
            <Layout />
          </RequireAuth>
        }
        errorElement={<RouteError />}
      >
        {/* <Route
          path="auth/reset-password"
          element={
            <RequireAuth GD={false} FM={false}>
              <ResetPasswordPage />
            </RequireAuth>
          }
          errorElement={<RouteError />}
        /> */}

        {/* Trang chủ (index) - Dashboard Rooms */}
        <Route
          index
          element={
            <RequireAuth>
              <HomePage />
            </RequireAuth>
          }
          errorElement={<RouteError />}
        />

        {/* Dashboard Equipment */}
        {/* Chỉ cho phép ADMIN và FACILITY_MANAGER */}
        <Route
          path="dashboard/equipment"
          element={
            <RequireAuth>
              <DashboardPage type="equipment" />
            </RequireAuth>
          }
          errorElement={<RouteError />}
        />

        {/* Trang Chi tiết Phòng và Lịch */}
        {/* Cho phép tất cả các vai trò đã đăng nhập */}
        <Route path="rooms">
          <Route
            path=":id"
            element={
              <RequireAuth>
                <FacilityPage />
              </RequireAuth>
            }
            errorElement={<RouteError />}
          />
          {/* Có thể thêm route index cho "/rooms" nếu cần trang danh sách chung */}
          {/* <Route index element={<RequireAuth><RoomListPage /></RequireAuth>} /> */}
        </Route>

        <Route path="user">
          <Route
            path="mybookings"
            element={
              <RequireAuth>
                <MyBookingsPage />
              </RequireAuth>
            }
            errorElement={<RouteError />}
          />
        </Route>
        
        <Route path="facility-manager">
          <Route path="approvals"
              element={
                <RequireAuth allowedRoles={["FACILITY_MANAGER", "ADMIN"]}>
                  <ApprovalsPage />
                </RequireAuth>
              }
              errorElement={<RouteError />} />

          <Route path="cancellations" element={
                <RequireAuth allowedRoles={["FACILITY_MANAGER", "ADMIN"]}>
                  <CancellationsPage />
                </RequireAuth>
              }
              errorElement={<RouteError />} />
        </Route>

         {/* Trang Quản lý của Admin */}
        <Route path="admin">
          <Route
            path="bookings"
            element={
              <RequireAuth>
                <AdminBookingsPage />
              </RequireAuth>
            }
            errorElement={<RouteError />}
          />

          {/* Trang Quản lý Phòng/Thiết bị của Admin */}
          <Route
            path="rooms"
            element={
              <RequireAuth allowedRoles={["ADMIN"]}>
                <AdminFacilitiesPage />
              </RequireAuth>
            }
            errorElement={<RouteError />}
          />

          {/* Các route admin khác nếu có */}
                    {/* Ví dụ: Quản lý User */}
                     {/* <Route
                         path="users"
                         element={ <RequireAuth allowedRoles={[UserRoleType.ADMIN]}><AdminUserPage /></RequireAuth> }
                         errorElement={<RouteError />}
                     /> */}
        </Route>
      </Route>  
      <Route path="*" element={<PageNotFound />} />
    </>
  )
);

const theme = createTheme({
  typography: {
    fontFamily: "Be Vietnam Pro",
  },
  palette: {
    primary: {
      main: "#FACB01", 
      contrastText: "#161616", 
    },
    secondary: {
      main: "#271756",
    }
  },
});

const queryClient = new QueryClient();
function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <AuthProvider>
        <ThemeProvider theme={theme}>
          <RouterProvider router={router} />
        </ThemeProvider>
      </AuthProvider>
    </QueryClientProvider>
  );
}

export default App;