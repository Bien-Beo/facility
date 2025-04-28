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
// import PageNotFound from "./components/PageNotFound";
import { RequireAuth } from "./components/RequireAuth";
import RouteError from "./components/RouteError";
import HomePage from "./pages/HomePage";
import AdminBookingsPage from "./pages/AdminBookingsPage";
import AdminFacilitiesPage from "./pages/AdminFacilitiesPage";
import DashboardPage from "./pages/DashboardPage";
// import FMApprovalsPage from "./pages/FMApprovalsPage";
// import FMBookingsPage from "./pages/FMBookingsPage";
// import FMCancellationsPage from "./pages/FMCancellationPage";
// import FacilityPage from "./pages/FacilityPage";
// import GDApprovalsPage from "./pages/GDApprovalsPage";
// import GDBookingsPage from "./pages/GDBookingsPage";
// import GDCancellationsPage from "./pages/GDCancellationsPage";
import LoginPage from "./pages/LoginPage";
import MyBookingsPage from "./pages/MyBookingsPage";
// import ResetPasswordPage from "./pages/ResetPasswordPage";
import { AuthProvider } from "./utils/auth";
import { ThemeProvider, createTheme } from "@mui/material/styles";
import FacilityPage from "./pages/FacilityPage";


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

        {/* <Route path="bookings">
          <Route
            path="gd"
            element={
              <RequireAuth GD={true} FM={false} noAdmin={true}>
                <GDBookingsPage />
              </RequireAuth>
            }
            errorElement={<RouteError />}
          />
          <Route
            path="fm"
            element={
              <RequireAuth GD={false} FM={true} noAdmin={true}>
                <FMBookingsPage />
              </RequireAuth>
            }
            errorElement={<RouteError />}
          />
        </Route> */}

        <Route path="user">
          {/* <Route path="approvals">
            <Route
              path="gd"
              element={
                <RequireAuth GD={true} FM={false} noAdmin={true}>
                  <GDApprovalsPage />
                </RequireAuth>
              }
              errorElement={<RouteError />}
            />
            <Route
              path="fm"
              element={
                <RequireAuth GD={false} FM={true} noAdmin={true}>
                  <FMApprovalsPage />
                </RequireAuth>
              }
              errorElement={<RouteError />}
            />
          </Route>

          <Route path="cancellations">
            <Route
              path="gd"
              element={
                <RequireAuth GD={true} FM={false} noAdmin={true}>
                  <GDCancellationsPage />
                </RequireAuth>
              }
              errorElement={<RouteError />}
            />
            <Route
              path="fm"
              element={
                <RequireAuth GD={false} FM={true} noAdmin={true}>
                  <FMCancellationsPage />
                </RequireAuth>
              }
              errorElement={<RouteError />}
            />
          </Route> */}

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
      {/* <Route path="*" element={<PageNotFound />} /> */}
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