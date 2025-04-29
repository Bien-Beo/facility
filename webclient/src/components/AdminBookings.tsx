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
import html2canvas from 'html2canvas-pro'; 
import jsPDF from 'jspdf';

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
      const token = localStorage.getItem("token");
      if (!token) throw new Error("No token");
      const response = await axios.get<ApiResponse<RoomData[]>>(
        `${import.meta.env.VITE_APP_SERVER_URL}/rooms?size=1000`,
        { headers: { Authorization: `Bearer ${token}` } }
      ); 
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
    queryKey: ["adminFilterUsersList"], 
    queryFn: async () => {
      const token = localStorage.getItem("token");
      if (!token) throw new Error("No token found");
      const response = await axios.get<PaginatedUserApiResponse>( 
        `${
          import.meta.env.VITE_APP_SERVER_URL || "http://localhost:8080"
        }/users`, 
        { headers: { Authorization: `Bearer ${token}` } }
      );
      // Kiểm tra cấu trúc đúng với kiểu Paginated
      if (
        response.data?.code !== 0 ||
        !response.data?.result?.content 
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
      params.append("sort", "plannedStartTime,desc"); 

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
        params.append("userId", selectedUserId); 

      const url = `${
        import.meta.env.VITE_APP_SERVER_URL || "http://localhost:8080"
      }/booking?${params.toString()}`; 
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
  };

  const handleCloseSnackbar = (): void => { setOpenSnackbar(false); };

// --- Logic PDF ---
const pdfOptions = {
    filename: "admin-bookings-report.pdf",
    page: {
        orientation: "landscape",
        format: 'a4',
        unit: 'pt' 
    }
};

// Hàm xử lý khi nhấn nút Export
const handleExportPdfClick = () => {
    if (bookingsForCurrentPage.length === 0) {
        alert("Không có dữ liệu đặt phòng để xuất báo cáo.");
        return;
    }
    // Chỉ set isPrint = true để component Report được render
    console.log("Setting isPrint to true...");
    setIsPrint(true);
};

// useEffect sẽ chạy sau khi component render lại với isPrint = true
useEffect(() => {
    // Chỉ thực hiện khi isPrint là true VÀ ref đã được gắn
    if (isPrint && targetRef.current) {
        const elementToCapture = targetRef.current;
        console.log("Report component rendered, starting PDF generation...");

        const canvasOptions = { scale: 2, useCORS: true, logging: false };

        // Gọi html2canvas
        html2canvas(elementToCapture, canvasOptions)
            .then((canvas) => {
                console.log("Canvas generated.");
                try {
                    const imgData = canvas.toDataURL('image/png');
                    const pdf = new jsPDF(pdfOptions.page.orientation, pdfOptions.page.unit, pdfOptions.page.format);

                    // Tính toán kích thước ảnh vừa trang PDF (có lề)
                    const margin = 40; // lề pt
                    const pdfWidth = pdf.internal.pageSize.getWidth();
                    const pdfHeight = pdf.internal.pageSize.getHeight();
                    const availableWidth = pdfWidth - margin * 2;
                    const availableHeight = pdfHeight - margin * 2;
                    const imgProps = pdf.getImageProperties(imgData);
                    const imgRatio = imgProps.width / imgProps.height;
                    let imgRenderWidth = imgProps.width;
                    let imgRenderHeight = imgProps.height;

                    if (imgRenderWidth > availableWidth) {
                         imgRenderWidth = availableWidth;
                         imgRenderHeight = imgRenderWidth / imgRatio;
                    }
                     if (imgRenderHeight > availableHeight) {
                         imgRenderHeight = availableHeight;
                         imgRenderWidth = imgRenderHeight * imgRatio;
                     }

                     // Canh giữa ảnh
                     const imgX = margin + (availableWidth - imgRenderWidth) / 2;
                     const imgY = margin;

                    // Thêm ảnh vào PDF
                    pdf.addImage(imgData, 'PNG', imgX, imgY, imgRenderWidth, imgRenderHeight);
                    console.log("Image added to PDF.");

                    // Lưu PDF
                    pdf.save(pdfOptions.filename);
                    console.log("PDF saved.");

                } catch (pdfError) {
                    console.error("Error generating PDF from canvas:", pdfError);
                    alert("Đã xảy ra lỗi khi tạo file PDF."); // Thông báo lỗi đơn giản
                } finally {
                    // Luôn reset trạng thái print sau khi hoàn tất (kể cả lỗi)
                    console.log("Resetting isPrint to false.");
                    setIsPrint(false);
                }
            })
            .catch(canvasError => {
                console.error("html2canvas failed:", canvasError);
                setIsPrint(false); // Reset nếu chụp ảnh lỗi
                alert("Đã xảy ra lỗi khi xử lý giao diện báo cáo.");
            });
    }

    // Quản lý overflow cho body (giữ nguyên từ code gốc)
    const overflowY = isPrint ? "hidden" : "auto";
    document.body.style.overflowY = overflowY;
    return () => { document.body.style.overflowY = "auto"; }; // Cleanup

}, [isPrint]); // Chạy lại khi isPrint thay đổi
  

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
        <Button variant="contained" color="secondary" startIcon={<DownloadIcon />} onClick={handleExportPdfClick}> 
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
