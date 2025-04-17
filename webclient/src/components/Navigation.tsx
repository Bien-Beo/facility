import React, { FC, useEffect, useState, JSX } from "react";
import { useMutation, useQuery } from "@tanstack/react-query";
import { NavLink } from "react-router-dom";
import axios from "axios";
import {
  Avatar,
  Badge,
  Divider,
  ListItemIcon,
  Typography,
} from "@mui/material";
import BookmarksIcon from "@mui/icons-material/Bookmarks";
import WorkspacePremiumIcon from "@mui/icons-material/WorkspacePremium";
import SummarizeIcon from "@mui/icons-material/Summarize";
import LogoutIcon from "@mui/icons-material/Logout";
import List from "@mui/material/List";
import ListItemButton from "@mui/material/ListItemButton";
import ListItemText from "@mui/material/ListItemText";
import ApprovalIcon from "@mui/icons-material/Approval";
import EventBusyIcon from "@mui/icons-material/EventBusy";
import PasswordIcon from "@mui/icons-material/Password";

import ErrorComponent from "./Error";
import { useAuth } from "../hooks/useAuth";
import { API } from "../api";

const Navigation: FC = (): JSX.Element => {
  const auth = useAuth();
  const role = auth?.user?.roleName;
  const [approvalCount, setApprovalCount] = useState<number>(0);
  const [cancellationCount, setCancellationCount] = useState<number>(0);

  const mutation = useMutation({
    mutationFn: () => {
      const token = localStorage.getItem("token"); // Lấy token từ localStorage
  
      if (!token) {
        return Promise.reject(new Error("No token found"));
      }
  
      return axios.post(
        `${import.meta.env.VITE_APP_SERVER_URL}/auth/logout`,
        { token }, // Gửi token vào body
        {
          headers: {
            "Content-Type": "application/json",
          },
        }
      );
    },
    onError: (error) => {
      console.log("Logout error:", error);
    },
  });
  

  const { data, isPending, isError, error } = useQuery<NavigationProps>({
    queryKey: ["navigation"],
    queryFn: async () => {
      const response = await API.get<NavigationProps>(
        `${import.meta.env.VITE_APP_SERVER_URL}/dashboard/count/${
          auth?.user?.userId
        }`,
        {
          withCredentials: true,
        }
      );
      return response.data;
    },
    refetchInterval: 5 * 1000,
    gcTime: 0,
  });

  useEffect(() => {
    if (!isPending) {
      setApprovalCount(data!.approvalCount!);
      setCancellationCount(data!.cancellationCount!);
    }
  }, [data, isPending]);

  if (isError) {
    const errorData = error.response!.data as ErrorMessage;
    return (
      <ErrorComponent
        status={errorData.status!}
        message={errorData.message}
      />
    );
  }

  return (
    <div className="w-[400px] h-[100dvh]  h-{100vh} bg-[#271756] text-white pt-5 overflow-y-scroll sticky top-0">
      <div className="w-full flex flex-col justify-between items-center pt-4 pb-8 gap-2 flex-wrap">
        <Avatar
          sx={{ width: "80px", height: "80px" }}
          src={auth?.user?.avatar}
          alt="avatar-image"
        />
        <div className="w-fit flex flex-col justify-center">
          <Typography variant="h5" className="text-center">{auth?.user?.username}</Typography>
          <Typography variant="subtitle1" className="font-normal text-center">
            ID: {auth?.user?.userId}
          </Typography>
        </div>
      </div>
      <Divider color="#0c0051" />

      {/* Facilities */}
      <List component="nav" disablePadding>
        {role !== "ADMIN" && role !== "TECHNICIAN" && (
          <>
            <NavLink to="/">
              {({ isActive }) => (
                <ListItemButton
                  className="flex gap-3"
                  sx={{
                    paddingLeft: "1.4em",
                    paddingBlock: "1.4em",
                    borderLeft: isActive ? "4px solid white" : "",
                    color: "white",
                    backgroundColor: isActive
                      ? " rgb(255, 255, 255, 0.02)"
                      : "",
                  }}
                >
                  <ListItemIcon sx={{ minWidth: "0px" }}>
                    <WorkspacePremiumIcon
                      sx={{ width: "26px", height: "26px", color: "white" }}
                    />
                  </ListItemIcon>
                  <ListItemText
                    primaryTypographyProps={{
                      variant: "body1",
                      component: "li",
                    }}
                    primary="Phòng"
                  />
                </ListItemButton>
              )}
            </NavLink>
            <Divider color="#0c0051" />
          </>
        )}

        {role !== "ADMIN" && role !== "TECHNICIAN" && (
          <>
            <NavLink to="/dashboard/equipment">
              {({ isActive }) => (
                <ListItemButton
                  className="flex gap-3"
                  sx={{
                    paddingLeft: "1.4em",
                    paddingBlock: "1.4em",
                    borderLeft: isActive ? "4px solid white" : "",
                    color: "white",
                    backgroundColor: isActive
                      ? " rgb(255, 255, 255, 0.02)"
                      : "",
                  }}
                >
                  <ListItemIcon sx={{ minWidth: "0px" }}>
                    <WorkspacePremiumIcon
                      sx={{ width: "26px", height: "26px", color: "white" }}
                    />
                  </ListItemIcon>
                  <ListItemText
                    primaryTypographyProps={{
                      variant: "body1",
                      component: "li",
                    }}
                    primary="Thiết bị"
                  />
                </ListItemButton>
              )}
            </NavLink>
            <Divider color="#0c0051" />
          </>
        )}

        {/* Admin management facilities */}
        {role === "ADMIN" && (
          <>
            <NavLink to="/admin/rooms">
              {({ isActive }) => (
                <ListItemButton
                  className={"flex gap-3"}
                  sx={{
                    paddingLeft: "1.4em",
                    paddingBlock: "1.4em",
                    borderLeft: isActive ? "4px solid white" : "",
                    color: "white",
                    backgroundColor: isActive
                      ? " rgb(255, 255, 255, 0.02)"
                      : "",
                  }}
                >
                  <ListItemIcon sx={{ minWidth: "0px" }}>
                    <WorkspacePremiumIcon
                      sx={{ width: "26px", height: "26px", color: "white" }}
                    />
                  </ListItemIcon>
                  <ListItemText
                    primaryTypographyProps={{
                      variant: "body1",
                      component: "li",
                    }}
                    primary="Quản lý phòng"
                  />
                </ListItemButton>
              )}
            </NavLink>
            <Divider color="#0c0051" />
            <NavLink to="/admin/bookings">
              {({ isActive }) => (
                <ListItemButton
                  className="flex gap-3"
                  sx={{
                    paddingLeft: "1.4em",
                    paddingBlock: "1.4em",
                    borderLeft: isActive ? "4px solid white" : "",
                    color: "white",
                    backgroundColor: isActive
                      ? " rgb(255, 255, 255, 0.02)"
                      : "",
                  }}
                >
                  <ListItemIcon sx={{ minWidth: "0px" }}>
                    <BookmarksIcon
                      sx={{ width: "26px", height: "26px", color: "white" }}
                    />
                  </ListItemIcon>
                  <ListItemText
                    primaryTypographyProps={{
                      variant: "body1",
                      component: "li",
                    }}
                    primary="Quản lý đặt phòng"
                  />
                </ListItemButton>
              )}
            </NavLink>
            <Divider color="#0c0051" />
          </>
        )}

        {/* Technician */}
        {role === "TECHNICIAN" && (
          <>
            <NavLink to="/technician/facilities">
              {({ isActive }) => (
                <ListItemButton
                  className={"flex gap-3"}
                  sx={{
                    paddingLeft: "1.4em",
                    paddingBlock: "1.4em",
                    borderLeft: isActive ? "4px solid white" : "",
                    color: "white",
                    backgroundColor: isActive
                      ? " rgb(255, 255, 255, 0.02)"
                      : "",
                  }}
                >
                  <ListItemIcon sx={{ minWidth: "0px" }}>
                    <WorkspacePremiumIcon
                      sx={{ width: "26px", height: "26px", color: "white" }}
                    />
                  </ListItemIcon>
                  <ListItemText
                    primaryTypographyProps={{
                      variant: "body1",
                      component: "li",
                    }}
                    primary="Maintenance Facilities"
                  />
                </ListItemButton>
              )}
            </NavLink>
            <Divider color="#0c0051" />
            <NavLink to="/technician/bookings">
              {({ isActive }) => (
                <ListItemButton
                  className="flex gap-3"
                  sx={{
                    paddingLeft: "1.4em",
                    paddingBlock: "1.4em",
                    borderLeft: isActive ? "4px solid white" : "",
                    color: "white",
                    backgroundColor: isActive
                      ? " rgb(255, 255, 255, 0.02)"
                      : "",
                  }}
                >
                  <ListItemIcon sx={{ minWidth: "0px" }}>
                    <BookmarksIcon
                      sx={{ width: "26px", height: "26px", color: "white" }}
                    />
                  </ListItemIcon>
                  <ListItemText
                    primaryTypographyProps={{
                      variant: "body1",
                      component: "li",
                    }}
                    primary="Maintenance"
                  />
                </ListItemButton>
              )}
            </NavLink>
            <Divider color="#0c0051" />
          </>
        )}

        {/* My bookings */}
        {role !== "ADMIN" && role !== "TECHNICIAN" && (
          <>
            <NavLink to="/user/mybookings">
              {({ isActive }) => (
                <ListItemButton
                  className="flex gap-3"
                  sx={{
                    paddingLeft: "1.4em",
                    paddingBlock: "1.4em",
                    borderLeft: isActive ? "4px solid white" : "",
                    color: "white",
                    backgroundColor: isActive
                      ? " rgb(255, 255, 255, 0.02)"
                      : "",
                  }}
                >
                  <ListItemIcon sx={{ minWidth: "0px" }}>
                    <BookmarksIcon
                      sx={{ width: "26px", height: "26px", color: "white" }}
                    />
                  </ListItemIcon>
                  <ListItemText
                    primaryTypographyProps={{
                      variant: "body1",
                      component: "li",
                    }}
                    primary="Đặt phòng của tôi"
                  />
                </ListItemButton>
              )}
            </NavLink>
            <Divider color="#0c0051" />
          </>
        )}

        {/* Facility Manager borrow */}
        {role !== "USER" && role !== "TECHNICIAN" && (
          <>
            <NavLink
              to={`/facility-manager/approvals/${
                role === "ADMIN"
                  ? "ad"
                  : role === "FACILITY_MANAGER"
                  ? "fm"
                  : ""
              }`}
            >
              {({ isActive }) => (
                <ListItemButton
                  className="flex gap-3"
                  sx={{
                    paddingLeft: "1.4em",
                    paddingBlock: "1.4em",
                    borderLeft: isActive ? "4px solid white" : "",
                    color: "white",
                    backgroundColor: isActive
                      ? " rgb(255, 255, 255, 0.02)"
                      : "",
                  }}
                >
                  <ListItemIcon sx={{ minWidth: "0px" }}>
                    <Badge badgeContent={approvalCount} color="primary">
                      <ApprovalIcon
                        sx={{ width: "26px", height: "26px", color: "white" }}
                      />
                    </Badge>
                  </ListItemIcon>
                  <ListItemText
                    primaryTypographyProps={{
                      variant: "body1",
                      component: "li",
                    }}
                    primary="Yêu cầu phê duyệt"
                  />  
                </ListItemButton>
              )}
            </NavLink>
            <Divider color="#0c0051" />
          </>
        )}
        {role !== "USER" && role !== "TECHNICIAN" && (
          <>
            <NavLink
              to={`/facility-management/cancellations/${
                role === "ADMIN"
                  ? "ad"
                  : role === "FACILITY_MANAGER"
                  ? "fm"
                  : ""
              }`}
            >
              {({ isActive }) => (
                <ListItemButton
                  className="flex gap-3"
                  sx={{
                    paddingLeft: "1.4em",
                    paddingBlock: "1.4em",
                    borderLeft: isActive ? "4px solid white" : "",
                    color: "white",
                    backgroundColor: isActive
                      ? " rgb(255, 255, 255, 0.02)"
                      : "",
                  }}
                >
                  <ListItemIcon sx={{ minWidth: "0px" }}>
                    <Badge badgeContent={cancellationCount} color="primary">
                      <EventBusyIcon
                        sx={{ width: "26px", height: "26px", color: "white" }}
                      />
                    </Badge>
                  </ListItemIcon>
                  <ListItemText
                    primaryTypographyProps={{
                      variant: "body1",
                      component: "li",
                    }}
                    primary="Yêu cầu hủy"
                  />
                </ListItemButton>
              )}
            </NavLink>
            <Divider color="#0c0051" />
          </>
        )}

        {/* Bookings ADMIN and FM */}
        {(role === "ADMIN" || role === "FACILITY_MANAGER") && (
          <>
            <NavLink
              to={`/bookings/${
                role === "ADMIN"
                  ? "ad"
                  : role === "FACILITY_MANAGER"
                  ? "fm"
                  : ""
              }`}
            >
              {({ isActive }) => (
                <ListItemButton
                  className="flex gap-3"
                  sx={{
                    paddingLeft: "1.4em",
                    paddingBlock: "1.4em",
                    borderLeft: isActive ? "4px solid white" : "",
                    color: "white",
                    backgroundColor: isActive
                      ? " rgb(255, 255, 255, 0.02)"
                      : "",
                  }}
                >
                  <ListItemIcon sx={{ minWidth: "0px" }}>
                    <SummarizeIcon
                      sx={{ width: "26px", height: "26px", color: "white" }}
                    />
                  </ListItemIcon>
                  <ListItemText
                    primaryTypographyProps={{
                      variant: "body1",
                      component: "li",
                    }}
                    primary="Báo cáo"
                  />
                </ListItemButton>
              )}
            </NavLink>
            <Divider color="#0c0051" />
          </>
        )}

        <NavLink to="/auth/reset-password">
          {({ isActive }) => (
            <ListItemButton
              className="flex gap-3"
              sx={{
                paddingLeft: "1.4em",
                paddingBlock: "1.4em",
                borderLeft: isActive ? "4px solid white" : "",
                color: "white",
                backgroundColor: isActive ? " rgb(255, 255, 255, 0.02)" : "",
              }}
            >
              <ListItemIcon sx={{ minWidth: "0px" }}>
                <PasswordIcon
                  sx={{ width: "26px", height: "26px", color: "white" }}
                />
              </ListItemIcon>
              <ListItemText
                primaryTypographyProps={{
                  variant: "body1",
                  component: "li",
                }}
                primary="Đặt lại mật khẩu"
              />
            </ListItemButton>
          )}
        </NavLink>
        <Divider color="#0c0051" />


        {/* Logout */}
        <ListItemButton
          className="flex gap-3"
          sx={{
            paddingLeft: "1.4em",
            paddingBlock: "1.4em",
            color: "white",
          }}
          onClick={async () => {
            try {
              await mutation.mutateAsync(); // Chờ API logout hoàn thành
              auth?.logout(); // Sau đó mới logout client
            } catch (error) {
              console.log("Logout failed:", error);
            }
          }}
        >
          <ListItemIcon sx={{ minWidth: "0px" }}>
            <LogoutIcon
              sx={{ width: "26px", height: "26px", color: "white" }}
            />
          </ListItemIcon>
          <ListItemText
            primaryTypographyProps={{
              variant: "body1",
              component: "li",
            }}
            primary="Đăng xuất"
          />
        </ListItemButton>
        <Divider color="#0c0051" />
      </List>
    </div>
  );
};

export default Navigation;
