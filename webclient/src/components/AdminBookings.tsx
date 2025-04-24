// import { JSX, ChangeEvent, FC, useEffect, useRef, useState } from "react";
// import {
//   Button,
//   Chip,
//   CircularProgress,
//   FormControl,
//   InputLabel,
//   MenuItem,
//   Select,
//   SelectChangeEvent,
//   TextField,
//   Typography,
// } from "@mui/material";
// import generatePDF, { Options } from "react-to-pdf";
// import { useQuery } from "@tanstack/react-query";
// import axios from "axios";
// import DownloadIcon from "@mui/icons-material/Download";

// import AdminBookingsTable from "./tables/AdminBookingsTable";
// import AdminBookingsReport from "../reports/AdminBookingsReport";
// import ErrorComponent from "./Error";
// import { months } from "../../constants/months";

// const AdminBookings: FC = (): JSX.Element => {
//   const [bookingsData, setBookingsData] = useState<AdminBookingsData>({
//     bookings: [],
//     facilities: [],
//   });
//   const [timeFilter, setTimeFilter] = useState<boolean>(false);
//   const [selectValue, setSelectValue] = useState<string>("");
//   const [enabled, setEnabled] = useState<boolean>(true);
//   const [slug, setSlug] = useState<string>("");
//   const [selectedMonth, setSelectedMonth] = useState<string>("");
//   const [selectedYear, setSelectedYear] = useState<string>("");
//   const [selectedUser, setSelectedUser] = useState<string>("");
//   const [isPrint, setIsPrint] = useState<boolean>(false);

//   const targetRef = useRef<HTMLDivElement>(null);

//   const d = new Date();

//   const { data, isPending, refetch, isError, error } = useQuery({
//     queryKey: ["adminbookings"],
//     queryFn: async () => {
//       let url = `${import.meta.env.VITE_APP_SERVER_URL}/admin/bookings`;

//       if (selectValue) {
//         url += `?facility=${slug}`;
//       }

//       if (timeFilter) {
//         if (selectValue) {
//           url += `&month=${d.getMonth() + 1}`;
//         } else {
//           url += `?month=${d.getMonth() + 1}`;
//         }
//       }

//       if (selectedMonth) {
//         if (selectValue) {
//           url += `&month=${months.indexOf(selectedMonth) + 1}`;
//         } else {
//           url += `?month=${months.indexOf(selectedMonth) + 1}`;
//         }
//       }

//       if (selectedYear) {
//         if (selectValue || timeFilter || selectedMonth) {
//           url += `&year=${selectedYear}`;
//         } else {
//           url += `?year=${selectedYear}`;
//         }
//       }

//       if (selectedUser) {
//         if (selectValue || timeFilter || selectedMonth || selectedYear) {
//           url += `&user=${selectedUser}`;
//         } else {
//           url += `?user=${selectedUser}`;
//         }
//       }

//       const response = await axios.get(url, {
//         withCredentials: true,
//       });
//       return response.data;
//     },
//     enabled: enabled,
//     refetchInterval: 5 * 1000,
//     retry: 1,
//     gcTime: 0,
//   });

//   useEffect(() => {
//     selectedMonth && timeFilter && setTimeFilter(false);
//   }, [selectedMonth, timeFilter]);

//   useEffect(() => {
//     if (!isPending) {
//       setBookingsData(data);
//     }
//   }, [data, isPending]);

//   useEffect(() => {
//     if (isPrint) {
//       setTimeout(() => {
//         setIsPrint(false);
//       }, 3000);
//     }

//     if (isPrint) {
//       document.body.style.overflowY = "hidden";
//     } else {
//       document.body.style.overflowY = "auto";
//     }
//   }, [isPrint]);

//   if (isError) {
//     const errorData = error.response!.data as ErrorMessage;
//     return (
//       <ErrorComponent
//         status={errorData.error.status!}
//         message={errorData.error.message}
//       />
//     );
//   }

//   if (isPending)
//     return (
//       <div className="w-[74vw] min-h-screen h-full flex flex-col items-center justify-center">
//         <CircularProgress />
//       </div>
//     );

//   const options: Options = {
//     filename: "admin-bookings-report.pdf",
//     page: {
//       orientation: "landscape",
//     },
//   };

//   return (
//     <div className="w-full flex flex-col px-6 pt-8 gap-6 overflow-hidden">
//       <div className="w-full flex justify-between items-center">
//         <Typography variant="h3" component="h1">
//           Manage bookings
//         </Typography>
//         <Button
//           variant="contained"
//           color="primary"
//           endIcon={<DownloadIcon sx={{ height: "20px", width: "20px" }} />}
//           sx={{ paddingX: "2em", height: "45px" }}
//           size="large"
//           onClick={() => {
//             setIsPrint(true);
//             setTimeout(() => {
//               generatePDF(targetRef, options);
//             }, 1000);
//           }}
//         >
//           Export
//         </Button>
//       </div>
//       <div className="w-full flex justify-center">
//         <div className="w-full flex gap-4 flex-wrap">
//           <Chip
//             label="All"
//             clickable={true}
//             sx={{
//               minWidth: "100px",
//               minHeight: "40px",
//               fontSize: "1rem",
//               borderRadius: "4px",
//             }}
//             variant={timeFilter ? "outlined" : "filled"}
//             onClick={() => setTimeFilter(false)}
//           />
//           <Chip
//             label="This month"
//             clickable={true}
//             sx={{
//               minWidth: "100px",
//               minHeight: "40px",
//               fontSize: "1rem",
//               borderRadius: "4px",
//             }}
//             variant={timeFilter ? "filled" : "outlined"}
//             onClick={() => {
//               setSelectedMonth("");
//               setTimeFilter(true);
//             }}
//           />
//           <FormControl size="small" className="w-[150px]">
//             <InputLabel>Select month</InputLabel>
//             <Select
//               label="Select month"
//               size="small"
//               value={selectedMonth}
//               onChange={(e: SelectChangeEvent<string | null>) => {
//                 setSelectedMonth(e.target.value!);
//               }}
//             >
//               {months.map((month) => (
//                 <MenuItem key={month} value={month}>
//                   {month}
//                 </MenuItem>
//               ))}
//             </Select>
//           </FormControl>

//           <FormControl size="small" className="w-[150px]">
//             <TextField
//               id="year"
//               label="Enter year"
//               variant="outlined"
//               className="w-full transition-all duration-200 ease-in"
//               value={selectedYear}
//               onChange={(e: ChangeEvent<HTMLInputElement>) => {
//                 setSelectedYear(e.target.value);
//               }}
//               size="small"
//               autoComplete="off"
//             />
//           </FormControl>

//           <FormControl size="small" className="w-[150px]">
//             <InputLabel>Select facility</InputLabel>
//             <Select
//               label="Select facility"
//               size="small"
//               value={selectValue}
//               onChange={(e: SelectChangeEvent<string | null>) => {
//                 setSelectValue(e.target.value!);
//                 setSlug(
//                   bookingsData.facilities.find(
//                     (facility) => facility.name === e.target.value
//                   )!.slug
//                 );
//               }}
//             >
//               {bookingsData.facilities.map((facility) => (
//                 <MenuItem key={facility.name} value={facility.name}>
//                   {facility.name}
//                 </MenuItem>
//               ))}
//             </Select>
//           </FormControl>

//           <FormControl size="small" className="w-[170px]">
//             <TextField
//               id="user"
//               label="Enter employeeId"
//               variant="outlined"
//               className="w-full transition-all duration-200 ease-in"
//               value={selectedUser}
//               onChange={(e: ChangeEvent<HTMLInputElement>) => {
//                 setSelectedUser(e.target.value);
//               }}
//               size="small"
//               autoComplete="off"
//             />
//           </FormControl>
//           <Button
//             variant="contained"
//             onClick={() => {
//               setSelectValue("");
//               setSelectedMonth("");
//               setTimeFilter(false);
//               setSelectedYear("");
//               setSelectedUser("");
//               setSlug("");
//               enabled && setEnabled(false);
//               refetch();
//             }}
//           >
//             Reset
//           </Button>
//           <Button
//             variant="contained"
//             onClick={() => {
//               enabled && setEnabled(false);
//               refetch();
//             }}
//           >
//             Filter
//           </Button>
//         </div>
//       </div>
//       {!isPending && (
//         <AdminBookingsTable bookingsData={bookingsData.bookings} />
//       )}
//       {isPrint && (
//         <div className="mt-[100dvh]">
//           <AdminBookingsReport
//             bookingsData={bookingsData.bookings}
//             forwardedRef={targetRef}
//           />
//         </div>
//       )}
//     </div>
//   );
// };

// export default AdminBookings;

import React, {
  JSX,
  FC,
  useEffect,
  useRef,
  useState,
  ChangeEvent,
  useCallback,
} from "react";
import DownloadIcon from "@mui/icons-material/Download";
// import InsertInvitationIcon from "@mui/icons-material/InsertInvitation"; // Không dùng nút Add ở đây
import {
  Alert,
  Box,
  Button,
  CircularProgress,
  Snackbar,
  Typography,
  Chip,
  FormControl,
  InputLabel,
  MenuItem,
  Select,
  SelectChangeEvent,
  TextField,
  Grid,
  Paper,
} from "@mui/material";
import { useQuery, useQueryClient } from "@tanstack/react-query";
import axios, { AxiosError } from "axios";
import generatePDF, { Options } from "react-to-pdf";

// Import components và types
import AdminBookingsTable from "./tables/AdminBookingsTable"; // Đảm bảo component này nhận props mới
import AdminBookingsReport from "../reports/AdminBookingsReport"; // Đảm bảo component này nhận props mới
import ErrorComponent from "./Error";
import { months } from "../../constants/months";
import { useAuth } from "../hooks/useAuth";

import { hasText } from "../utils/stringUtils";

const AdminBookings: FC = (): JSX.Element => {
  // --- State cho Filters ---
  const [selectedRoomId, setSelectedRoomId] = useState<string>(""); // Lưu Room ID
  const [selectedMonth, setSelectedMonth] = useState<string>(""); // Tên tháng
  const [selectedYear, setSelectedYear] = useState<string>(
    String(new Date().getFullYear())
  ); // Năm hiện tại
  const [selectedUserId, setSelectedUserId] = useState<string>(""); // Lưu User ID (UUID)
  const [openSnackbar, setOpenSnackbar] = useState<boolean>(false);

  // --- State cho UI ---
  const [isPrint, setIsPrint] = useState<boolean>(false);
  // Bỏ state bookingsData

  // === State cho Pagination ===
  const [page, setPage] = useState<number>(0);
  const [rowsPerPage, setRowsPerPage] = useState<number>(10);

  const targetRef = useRef<HTMLDivElement>(null);
  const queryClient = useQueryClient();
  const auth = useAuth();

  // --- Fetch dữ liệu cho các Filter Dropdowns ---
  const { data: roomsData, isLoading: isLoadingRooms } = useQuery<
    ApiResponse<RoomData[]>,
    AxiosError
  >({
    queryKey: ["adminFilterRoomsList"],
    queryFn: async () => {
      /* ... fetch /rooms ... */
      const token = localStorage.getItem("token");
      if (!token) throw new Error("No token");
      const response = await axios.get<ApiResponse<RoomData[]>>(
        `${import.meta.env.VITE_APP_SERVER_URL}/rooms?size=1000`,
        { headers: { Authorization: `Bearer ${token}` } }
      ); // Lấy nhiều phòng cho filter
      if (response.data?.code !== 0 || !response.data?.result)
        throw new Error("Invalid rooms response");
      return response.data;
    },
    staleTime: 300000, // Cache 5 phút
  });
  const roomListForFilter = roomsData?.result?.content || [];

  const { data: usersApiResponse, isLoading: isLoadingUsers } = useQuery<
    PaginatedUserApiResponse,
    AxiosError<ErrorMessage>
  >({
    // <<< SỬA: Dùng PaginatedUserApiResponse
    queryKey: ["adminFilterUsersList"], // Có thể thêm page/size nếu muốn filter user có phân trang
    queryFn: async () => {
      const token = localStorage.getItem("token");
      if (!token) throw new Error("No token found");
      // Endpoint này có thể cần hỗ trợ phân trang nếu danh sách user quá lớn
      const response = await axios.get<PaginatedUserApiResponse>( // <<< SỬA: Dùng PaginatedUserApiResponse
        `${
          import.meta.env.VITE_APP_SERVER_URL || "http://localhost:8080"
        }/users`, // Endpoint ví dụ, tạm lấy 200 users
        { headers: { Authorization: `Bearer ${token}` } }
      );
      // Kiểm tra cấu trúc đúng với kiểu Paginated
      if (
        response.data?.code !== 0 ||
        !response.data?.result?.content /*|| !response.data?.result?.page*/
      ) {
        throw new Error(
          response.data?.message || "Invalid response for users list"
        );
      }
      return response.data;
    },
    staleTime: 300000, // Cache 5 phút
  });
  const userListForFilter: UserData[] = usersApiResponse?.result?.content || [];

  // --- Fetch Bookings Data (Paginated & Filtered) ---
  const {
    data: apiResponse,
    isPending,
    isError,
    error,
  } = useQuery<PaginatedBookingApiResponse, AxiosError<ErrorMessage>>({
    // SỬA: Query key bao gồm tất cả state filter và pagination
    queryKey: [
      "adminBookings",
      page,
      rowsPerPage,
      selectedRoomId,
      selectedMonth,
      selectedYear,
      selectedUserId,
    ],
    queryFn: async () => {
      const token = localStorage.getItem("token");
      if (!token) throw new Error("No token found");

      const params = new URLSearchParams();
      params.append("page", String(page));
      params.append("size", String(rowsPerPage));
      params.append("sort", "plannedStartTime,desc"); // Sort mặc định

      if (hasText(selectedRoomId))
        params.append("roomId", selectedRoomId);
      if (hasText(selectedMonth)) {
        const monthNumber = months.indexOf(selectedMonth) + 1;
        if (monthNumber > 0) params.append("month", String(monthNumber));
      }
      if (
        hasText(selectedYear) &&
        !isNaN(parseInt(selectedYear, 10))
      )
        params.append("year", selectedYear);
      if (hasText(selectedUserId))
        params.append("userId", selectedUserId); // Gửi userId (UUID)

      const url = `${
        import.meta.env.VITE_APP_SERVER_URL || "http://localhost:8080"
      }/booking?${params.toString()}`; // Endpoint của Admin
      console.log("Fetching admin bookings with URL:", url);

      const response = await axios.get<PaginatedBookingApiResponse>(url, {
        headers: { Authorization: `Bearer ${token}` },
      });

      if (
        response.data?.code !== 0 ||
        !response.data?.result?.content ||
        !response.data?.result?.page
      ) {
        console.error("Invalid API response for bookings:", response.data);
        throw new Error(
          response.data?.message ||
            "Cấu trúc API response booking không hợp lệ."
        );
      }
      return response.data;
    },
    keepPreviousData: true,
    // Bỏ enabled: enabled
    retry: 1,
  });

  // Bỏ useEffect setBookingsData

  // === Trích xuất Dữ liệu ===
  const bookingsForCurrentPage: BookingEntry[] =
    apiResponse?.result?.content || [];
  const totalBookingCount: number =
    apiResponse?.result?.page?.totalElements || 0;

  // === Handlers Phân trang ===
  const handleChangePage = (event: unknown, newPage: number) => {
    setPage(newPage);
  };
  const handleChangeRowsPerPage = (event: ChangeEvent<HTMLInputElement>) => {
    setRowsPerPage(parseInt(event.target.value, 10));
    setPage(0);
  };

  // === Handlers Filter ===
  const handleFilterChange =
    (setter: React.Dispatch<React.SetStateAction<string>>) =>
    (
      e:
        | SelectChangeEvent<string>
        | ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
    ) => {
      setter(e.target.value);
      setPage(0); // Reset về trang đầu khi filter
    };

  const handleResetFilters = () => {
    setSelectedRoomId("");
    setSelectedMonth("");
    setSelectedYear(String(new Date().getFullYear()));
    setSelectedUserId("");
    setPage(0);
    // Query sẽ tự chạy lại do queryKey thay đổi (các state filter về giá trị ban đầu)
  };

  // --- Logic PDF (Giữ nguyên) ---
  useEffect(() => {
    /* ... isPrint logic ... */
  }, [isPrint]);
  const options: Options = {
    filename: "admin-bookings-report.pdf",
    page: { orientation: "landscape" },
  };
  const handleExportPdf = () => {
    /* ... logic export ... */
  };
  const handleCloseSnackbar = (): void => {
    setOpenSnackbar(false);
  };

  // --- Render Logic ---
  if (isError) {
    // Xử lý lỗi fetch bookings
    const errorData = error?.response?.data || {
      message: error?.message || "Unknown error",
      status: error?.response?.status || 500,
    };
    return (
      <ErrorComponent
        status={errorData.status ?? 500}
        message={errorData.message}
      />
    );
  }

  return (
    <Box className="w-full flex flex-col px-6 pt-8 gap-4">
      {" "}
      {/* Giảm gap */}
      {/* Có thể thêm modal Add Booking ở đây nếu Admin có quyền tạo */}
      {/* {isAddBookingModalOpen && <AddBookingModal ... /> } */}
      <Box className="w-full flex justify-between items-center flex-wrap gap-2">
        <Typography variant="h4" component="h1">
          Quản lý Đặt phòng
        </Typography>
        <Button
          variant="contained"
          color="secondary"
          startIcon={<DownloadIcon />}
          onClick={handleExportPdf}
        >
          Xuất Báo cáo PDF
        </Button>
      </Box>
      {/* Filter Controls */}
      <Paper sx={{ p: 2, mb: 2 }}>
        {" "}
        {/* Bọc filter trong Paper */}
        <Grid container spacing={2} alignItems="center">
          <Grid item xs={12} sm={6} md={3}>
            <FormControl size="small" fullWidth>
              <InputLabel id="room-filter-label">Lọc theo phòng</InputLabel>
              <Select
                labelId="room-filter-label"
                label="Lọc theo phòng"
                name="selectedRoomId"
                value={selectedRoomId}
                onChange={handleFilterChange(setSelectedRoomId)}
                disabled={isLoadingRooms} // Disable khi đang load danh sách phòng
              >
                <MenuItem value="">
                  <em>Tất cả phòng</em>
                </MenuItem>
                {roomListForFilter.map((room) => (
                  <MenuItem key={room.id} value={room.id}>
                    {room.name} ({room.buildingName})
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
          </Grid>
          <Grid item xs={6} sm={3} md={2}>
            <FormControl size="small" fullWidth>
              <InputLabel id="month-filter-label">Lọc theo tháng</InputLabel>
              <Select
                labelId="month-filter-label"
                label="Lọc theo tháng"
                value={selectedMonth}
                onChange={handleFilterChange(setSelectedMonth)}
              >
                <MenuItem value="">
                  <em>Tất cả tháng</em>
                </MenuItem>
                {months.map((month) => (
                  <MenuItem key={month} value={month}>
                    {month}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
          </Grid>
          <Grid item xs={6} sm={3} md={2}>
            <TextField
              fullWidth
              size="small"
              type="number"
              label="Năm"
              name="selectedYear"
              value={selectedYear}
              onChange={handleFilterChange(setSelectedYear)}
            />
          </Grid>
          <Grid item xs={12} sm={6} md={3}>
            <FormControl size="small" fullWidth>
              <InputLabel id="user-filter-label">Lọc theo người đặt</InputLabel>
              <Select
                labelId="user-filter-label"
                label="Lọc theo người đặt"
                name="selectedUserId"
                value={selectedUserId}
                onChange={handleFilterChange(setSelectedUserId)}
                disabled={isLoadingUsers} // Disable khi đang load user
              >
                <MenuItem value="">
                  <em>Tất cả người đặt</em>
                </MenuItem>
                {/* Giờ đây userListForFilter là một mảng UserData[] hợp lệ */}
                {userListForFilter.map((user) => (
                  <MenuItem key={user.id} value={user.id}>
                    {user.fullName || user.username} ({user.userId})
                  </MenuItem>
                ))}
                {isLoadingUsers && (
                  <MenuItem disabled>
                    <em>Đang tải...</em>
                  </MenuItem>
                )}
              </Select>
            </FormControl>
          </Grid>
          <Grid
            item
            xs={12}
            sm={6}
            md={2}
            sx={{ display: "flex", gap: 1, alignItems: "center" }}
          >
            <Button
              variant="outlined"
              onClick={handleResetFilters}
              size="medium"
              sx={{ height: "40px" }}
            >
              Reset
            </Button>
            {/* Nút filter không cần thiết vì query tự chạy */}
          </Grid>
        </Grid>
      </Paper>
      {/* Bảng dữ liệu */}
      <Box sx={{ width: "100%" }}>
        {isPending ? (
          <Box sx={{ display: "flex", justifyContent: "center", p: 5 }}>
            <CircularProgress />
          </Box>
        ) : bookingsForCurrentPage.length === 0 && page === 0 ? (
          <Typography sx={{ textAlign: "center", p: 5 }}>
            Không tìm thấy lượt đặt phòng nào khớp.
          </Typography>
        ) : (
          // Truyền đúng props phân trang
          <AdminBookingsTable
            bookings={bookingsForCurrentPage}
            totalBookingCount={totalBookingCount}
            page={page}
            rowsPerPage={rowsPerPage}
            onPageChange={handleChangePage}
            onRowsPerPageChange={handleChangeRowsPerPage}
          />
        )}
      </Box>
      {/* PDF Target */}
      {isPrint && bookingsForCurrentPage.length > 0 && (
        <div ref={targetRef} className="absolute -left-[9999px] top-0">
          {/* Sửa: Truyền đúng prop */}
          <AdminBookingsReport
            bookings={bookingsForCurrentPage}
            forwardedRef={targetRef}
          />
        </div>
      )}
      {/* Snackbar */}
      <Snackbar
        open={openSnackbar}
        autoHideDuration={3000}
        onClose={handleCloseSnackbar}
      >
        <Alert severity="success" variant="filled" sx={{ width: "100%" }}>
          Action successful!
        </Alert>
      </Snackbar>
    </Box>
  );
};

export default AdminBookings;
