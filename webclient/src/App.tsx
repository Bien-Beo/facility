import "@tanstack/react-query";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { AxiosError } from "axios";
import {
  Route,
  RouterProvider,
  createBrowserRouter,
  createRoutesFromElements,
} from "react-router-dom";

import { useMemo } from "react";
import useMediaQuery from "@mui/material/useMediaQuery";

import Layout from "./components/Layout";
// import PageNotFound from "./components/PageNotFound";
import { RequireAuth } from "./components/RequireAuth";
import RouteError from "./components/RouteError";
// import AdminBookingsPage from "./pages/AdminBookingsPage";
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
// import MyBookingsPage from "./pages/MyBookingsPage";
// import ResetPasswordPage from "./pages/ResetPasswordPage";
import { AuthProvider } from "./utils/auth";
import { ThemeProvider, createTheme, PaletteOptions } from "@mui/material/styles";
import CssBaseline from '@mui/material/CssBaseline';
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
        {/* Chỉ cho phép ADMIN và FACILITY_MANAGER */}
        <Route
          index
          element={
            <RequireAuth allowedRoles={[]}>
              <DashboardPage type="room" />
            </RequireAuth>
          }
          errorElement={<RouteError />}
        />

        {/* Dashboard Equipment */}
        {/* Chỉ cho phép ADMIN và FACILITY_MANAGER */}
        <Route
          path="dashboard/equipment"
          element={
            <RequireAuth allowedRoles={["ADMIN", "FACILITY_MANAGER"]}>
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
        </Route>

        <Route path="employee">
          <Route path="approvals">
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
          </Route>

          <Route
            path="mybookings"
            element={
              <RequireAuth GD={false} FM={false} noAdmin={true}>
                <MyBookingsPage />
              </RequireAuth>
            }
            errorElement={<RouteError />}
          />
        </Route>
         */}

         {/* Trang Quản lý của Admin */}
         <Route path="admin">
          {/* <Route
            path="bookings"
            element={
              <RequireAuth Technician={false} FacilityManager={false} Admin={true}>
                <AdminBookingsPage />
              </RequireAuth>
            }
            errorElement={<RouteError />}
          /> */}

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

// const theme = createTheme({
//   typography: {
//     fontFamily: "Poppins, sans-serif",
//   },
// });

const queryClient = new QueryClient();

// function App() {
//   const prefersDarkMode = useMediaQuery("(prefers-color-scheme: dark)");

//   const theme = useMemo(
//     () =>
//       createTheme({
//         palette: {
//           mode: prefersDarkMode ? "dark" : "light",
//         },
//         typography: {
//           fontFamily: "Poppins, sans-serif",
//         },
//       }),
//     [prefersDarkMode]
//   );

function App() {
    const prefersDarkMode = useMediaQuery("(prefers-color-scheme: dark)");

    const theme = useMemo(() => {
        // Định nghĩa palette cơ bản cho light mode
        const lightPalette: PaletteOptions = {
            mode: 'light',
            primary: {
                main: '#1976d2',
            },
            secondary: {
                main: '#dc004e', 
            },
            background: {
                 default: '#f4f6f8', 
                 paper: '#ffffff',
            },
            text: {
              primary: '#000', 
              secondary: '#000',  
              disabled: '#000',
          },
        };

        // Định nghĩa palette chi tiết cho dark mode 
        const darkPalette: PaletteOptions = {
            mode: 'dark',
            primary: {
                main: '#90caf9', 
            },
            secondary: {
                main: '#f48fb1',
            },
            background: {
                default: '#121212', 
                paper: '#1e1e1e',   
            },
            text: {
                primary: 'rgba(255, 255, 255, 0.87)', 
                secondary: 'rgba(255, 255, 255, 0.6)',  
                disabled: 'rgba(255, 255, 255, 0.38)',
            },
            divider: 'rgba(255, 255, 255, 0.12)', 
             action: { 
                active: '#ffffff',
                hover: 'rgba(255, 255, 255, 0.08)',
                selected: 'rgba(255, 255, 255, 0.16)',
                disabled: 'rgba(255, 255, 255, 0.3)',
                disabledBackground: 'rgba(255, 255, 255, 0.12)',
                focus: 'rgba(255, 255, 255, 0.12)',
            },
            error: { main: '#f44336' },
            warning: { main: '#ffa726' },
            info: { main: '#29b6f6' },
            success: { main: '#66bb6a' },
        };

        return createTheme({
            // Chọn palette dựa trên prefersDarkMode
            palette: prefersDarkMode ? darkPalette : lightPalette,
            typography: {
                fontFamily: "Poppins, sans-serif",
            },
        });
    }, [prefersDarkMode]); // Phụ thuộc vào prefersDarkMode

  return (
    <QueryClientProvider client={queryClient}>
      <AuthProvider>
        <ThemeProvider theme={theme}>
        <CssBaseline />
          <RouterProvider router={router} />
        </ThemeProvider>
      </AuthProvider>
    </QueryClientProvider>
  );
}

export default App;