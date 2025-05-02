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
import AdminFacilitiesPage from "./pages/ManagementRoomsPage";
import TechnicianMaintenancePage from "./pages/TechnicianMaintenancePage";
import ManagementEquipmentsPage from "./pages/ManagementEquipmentsPage";
import ApprovalsPage from "./pages/ApprovalsPage";
import OverduePage from "./pages/OverduePage";
import LoginPage from "./pages/LoginPage";
import MyBookingsPage from "./pages/MyBookingsPage";
import ResetPasswordPage from "./pages/ResetPasswordPage";
import FacilityPage from "./pages/FacilityPage";
import { AuthProvider } from "./utils/auth";
import { ThemeProvider, createTheme } from "@mui/material/styles";
import ReportIssuePage from "./pages/ReportIssuePage";


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

      <Route
        path="/"
        element={
          <RequireAuth>
            <Layout />
          </RequireAuth>
        }
        errorElement={<RouteError />}
      >
        <Route
          path="auth/reset-password"
          element={
            <RequireAuth>
              <ResetPasswordPage />
            </RequireAuth>
          }
          errorElement={<RouteError />}
        />

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
        <Route
          path="management/equipments"
          element={
            <RequireAuth allowedRoles={["FACILITY_MANAGER", "ADMIN"]}>
              <ManagementEquipmentsPage />
            </RequireAuth>
          }
          errorElement={<RouteError />}
        />

        {/* Trang Chi tiết Phòng và Lịch */}
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
        </Route>

        <Route path="report">
            <Route path="room">
                <Route
                    path=":id"
                    element={
                        <RequireAuth>
                            <ReportIssuePage />
                        </RequireAuth>
                    }
                    errorElement={<RouteError />}
                />
            </Route>
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
        
        <Route path="bookings">
          <Route path="approvals"
              element={
                <RequireAuth allowedRoles={["FACILITY_MANAGER", "ADMIN"]}>
                  <ApprovalsPage />
                </RequireAuth>
              }
              errorElement={<RouteError />} />

          <Route path="overdue" element={
                <RequireAuth allowedRoles={["FACILITY_MANAGER", "ADMIN"]}>
                  <OverduePage />
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
        </Route>

         {/* Trang Quản lý của Technician */}
         <Route path="technician">
          <Route
            path="maintenance"
            element={
              <RequireAuth allowedRoles={["TECHNICIAN"]}>
                <TechnicianMaintenancePage />
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