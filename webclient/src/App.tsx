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
// import AdminFacilitiesPage from "./pages/AdminFacilitiesPage";
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
          <RequireAuth Technician={false} FacilityManagement={false}>
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

        <Route>
          <Route
            index
            element={
              <RequireAuth Technician={false} FacilityManagement={false} User={false}>
                <DashboardPage type="room" />
              </RequireAuth>
            }
            errorElement={<RouteError />}
          />

          <Route
            path="dashboard/equipment"
            element={
              <RequireAuth Technician={false} FacilityManagement={false} User={false}>
                <DashboardPage type="equipment" />
              </RequireAuth>
            }
            errorElement={<RouteError />}
          />
        </Route>

        <Route path="room">
          <Route
            path=":id"
            element={
              <RequireAuth Technician={false} FacilityManagement={false} Admin={false}>
                <FacilityPage />
              </RequireAuth>
            }
            errorElement={<RouteError />}
          />
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

        <Route path="admin">
          <Route
            path="bookings"
            element={
              <RequireAuth GD={false} FM={false} Admin={true}>
                <AdminBookingsPage />
              </RequireAuth>
            }
            errorElement={<RouteError />}
          />
          <Route
            path="facilities"
            element={
              <RequireAuth GD={false} FM={false} Admin={true}>
                <AdminFacilitiesPage />
              </RequireAuth>
            }
            errorElement={<RouteError />}
          />
        </Route> */}
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

function App() {
  const prefersDarkMode = useMediaQuery("(prefers-color-scheme: dark)");

  const theme = useMemo(
    () =>
      createTheme({
        palette: {
          mode: prefersDarkMode ? "dark" : "light",
        },
        typography: {
          fontFamily: "Poppins, sans-serif",
        },
      }),
    [prefersDarkMode]
  );

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