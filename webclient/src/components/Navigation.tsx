import { FC, useState, JSX } from "react";
import { useMutation } from "@tanstack/react-query";
import { NavLink } from "react-router-dom";
import axios from "axios";
import {
  Avatar,
  Badge,
  Divider,
  ListItemIcon,
  Typography,
} from "@mui/material";
import ConstructionIcon from '@mui/icons-material/Construction';
import NotificationImportantIcon from '@mui/icons-material/NotificationImportant';
import ManageAccountsIcon from '@mui/icons-material/ManageAccounts';
import BookmarksIcon from "@mui/icons-material/Bookmarks";
import LogoutIcon from "@mui/icons-material/Logout";
import List from "@mui/material/List";
import ListItemButton from "@mui/material/ListItemButton";
import ListItemText from "@mui/material/ListItemText";
import ConfirmationNumberIcon from '@mui/icons-material/ConfirmationNumber';
import ApprovalIcon from "@mui/icons-material/Approval";
import EventBusyIcon from "@mui/icons-material/EventBusy";
import PasswordIcon from "@mui/icons-material/Password";
import TableRestaurantIcon from '@mui/icons-material/TableRestaurant';
import HomeWorkIcon from '@mui/icons-material/HomeWork';
import { useAuth } from "../hooks/useAuth";

const Navigation: FC = (): JSX.Element => {
  const auth = useAuth();
  const role = auth?.user?.roleName;
  const [approvalCount] = useState<number>(0);
  const [cancellationCount] = useState<number>(0);

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

  return (
    <div className="min-w-[250px] max-w-[400px] w-1/4 h-[100dvh] bg-[#271756] text-white pt-5 overflow-y-scroll sticky top-0">
      <div className="w-full flex flex-col justify-between items-center pt-4 pb-8 gap-2 flex-wrap">
        <Avatar
          sx={{ width: "80px", height: "80px" }}
          src={`http://localhost:8080/facility/images/${auth?.user?.avatar}`}
          alt="avatar-image"
        />
        <div className="w-fit flex flex-col justify-center">
          <Typography variant="h5" className="text-center">{auth?.user?.fullName}</Typography>
          <Typography variant="subtitle1" className="font-normal text-center">
            ID: {auth?.user?.userId}
          </Typography>
        </div>
      </div>
      <Divider color="#0c0051" />

      {/* Facilities */}
      <List component="nav" disablePadding>
        {role !== "ADMIN" && (
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
                    <HomeWorkIcon
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
                    <HomeWorkIcon
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
                    <ConfirmationNumberIcon
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

            <NavLink to="/admin/accounts">
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
                    <ManageAccountsIcon
                      sx={{ width: "26px", height: "26px", color: "white" }}
                    />
                  </ListItemIcon>
                  <ListItemText
                    primaryTypographyProps={{
                      variant: "body1",
                      component: "li",
                    }}
                    primary="Quản lý tài khoản"
                  />
                </ListItemButton>
              )}
            </NavLink>
            <Divider color="#0c0051" />
          </>
        )}

        {role !== "USER" && role !== "TECHNICIAN" && (
          <>
            <NavLink to="/management/equipments">
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
                    <TableRestaurantIcon
                      sx={{ width: "26px", height: "26px", color: "white" }}
                    />
                  </ListItemIcon>
                  <ListItemText
                    primaryTypographyProps={{
                      variant: "body1",
                      component: "li",
                    }}
                    primary="Quản lý thiết bị"
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
            <NavLink to="/technician/maintenance">
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
                    <ConstructionIcon
                      sx={{ width: "26px", height: "26px", color: "white" }}
                    />
                  </ListItemIcon>
                  <ListItemText
                    primaryTypographyProps={{
                      variant: "body1",
                      component: "li",
                    }}
                    primary="Bảo trì"
                  />
                </ListItemButton>
              )}
            </NavLink>
            <Divider color="#0c0051" />
          </>
        )}

        {/* My bookings */}
        {role !== "ADMIN" && role !== "FACILITY_MANAGER" && (
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

        {role !== "ADMIN" && role !== "FACILITY_MANAGER" && (
          <>
            <NavLink to="/notification">
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
                    <NotificationImportantIcon
                      sx={{ width: "26px", height: "26px", color: "white" }}
                    />
                  </ListItemIcon>
                  <ListItemText
                    primaryTypographyProps={{
                      variant: "body1",
                      component: "li",
                    }}
                    primary="Thông báo"
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
              to={`/bookings/approvals/`}
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
              to={`/bookings/overdue/`}
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
                    primary="Yêu cầu quá hạn"
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
