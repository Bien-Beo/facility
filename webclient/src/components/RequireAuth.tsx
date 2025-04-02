import { Navigate } from "react-router-dom";
import { FC, JSX } from "react";

import { useAuth } from "../hooks/useAuth";
import ErrorComponent from "./Error";

export const RequireAuth: FC<RequireAuthProps> = ({
  children,
  Technician,
  FacilityManagement,
  Admin,
  User
}): JSX.Element => {
  const auth = useAuth();

  if (!auth!.user) {
    return <Navigate to="/auth/login" />;
  }

  try {
    if (Technician && auth?.user?.role !== "TECHNICIAN") {
      return <Navigate to="/" />;
    } else if (FacilityManagement && auth?.user?.role !== "FACILITY_MANAGER") {
      return <Navigate to="/" />;
    } else if (Admin && auth?.user?.role !== "ADMIN") {
      return <Navigate to="/admin/facilities" />;
    } else if (User && auth?.user?.role !== "USER") {
      return <Navigate to="/" />;
    }
  } catch (err) {
    return (
      <ErrorComponent status={401} message="Please log in and try again" />
    );
  }

  return children;
};