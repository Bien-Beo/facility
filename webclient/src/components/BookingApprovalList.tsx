import React, { JSX, FC, useEffect, useState, ChangeEvent } from "react"; 
import { Box, CircularProgress, Typography, Pagination } from "@mui/material"; 
import { useQuery, useQueryClient } from "@tanstack/react-query";
import axios, { AxiosError } from "axios";

import ApprovalCard from "./cards/ApprovalCard"; 
import ErrorComponent from "./Error";
import { useAuth } from "../hooks/useAuth";

// Component hiển thị danh sách booking chờ duyệt
const BookingApprovalList: FC = (): JSX.Element => { 

    // State cho phân trang
    const [page, setPage] = useState<number>(0);
    const [rowsPerPage, setRowsPerPage] = useState<number>(10); 

    // State lưu danh sách booking của trang hiện tại
    const [pendingBookings, setPendingBookings] = useState<BookingEntry[]>([]);

    const auth = useAuth();
    const queryClient = useQueryClient(); // Để refresh list khi có thay đổi

    // Query lấy danh sách booking đang chờ duyệt (PENDING_APPROVAL)
    const { data: apiResponse, isPending, isError, error } = useQuery<PaginatedBookingApiResponse, AxiosError<ErrorMessage>>({
        // Query key bao gồm cả trạng thái và phân trang
        queryKey: ["pendingApprovals", page, rowsPerPage], 
        queryFn: async () => {
            const token = localStorage.getItem("token");
            if (!token) throw new Error("No token found");

            const params = new URLSearchParams();
            params.append('page', String(page));
            params.append('size', String(rowsPerPage));
            params.append('status', 'PENDING_APPROVAL'); 
            params.append('sort', 'plannedStartTime,asc'); 

            const url = `${import.meta.env.VITE_APP_SERVER_URL || 'http://localhost:8080'}/booking?${params.toString()}`;
            console.log("Fetching pending bookings with URL:", url);

            const response = await axios.get<PaginatedBookingApiResponse>(url, {
                headers: { Authorization: `Bearer ${token}` }
            });

             if (response.data?.code !== 0 || !response.data?.result?.content || !response.data?.result?.page) {
                 throw new Error(response.data?.message || "Invalid API response for pending bookings.");
             }
            return response.data;
        },
        keepPreviousData: true,
        enabled: !!auth.user,
        refetchInterval: 15 * 1000, 
    });

    // Cập nhật state khi có dữ liệu mới
    useEffect(() => {
        if (!isPending && apiResponse?.result?.content) {
            setPendingBookings(apiResponse.result.content);
        }
         else if (!isPending && !apiResponse?.result?.content) {
             setPendingBookings([]);
         }
    }, [apiResponse, isPending]);

    // Handlers cho Pagination
    const handlePageChange = (event: React.ChangeEvent<unknown>, newPage: number) => {
        setPage(newPage - 1); // MUI Pagination là 1-based
    };
    // Handler đổi rowsPerPage nếu dùng TablePagination
    // const handleChangeRowsPerPage = (event: ChangeEvent<HTMLInputElement>) => { ... };

    // Xử lý lỗi (Sửa cách truy cập)
    if (isError) {
        const errorData = error?.response?.data || { message: error.message, status: error.response?.status || 500 };
        return ( <ErrorComponent status={errorData.status ?? 500} message={errorData.message} /> );
    }

    // Hiển thị loading
    // Chỉ hiển thị loading tròn khi fetch lần đầu (page 0)
    if (isPending && page === 0 && pendingBookings.length === 0) {
        return ( <Box sx={{ minHeight: 'calc(100vh - 64px)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}> <CircularProgress /> </Box> );
    }

    const totalPages = apiResponse?.result?.page?.totalPages ?? 0;

    // --- Render JSX ---
    return (
        <Box sx={{ width: '100%', display: 'flex', flexDirection: 'column', alignItems: 'center', px: { xs: 2, md: 3 }, py: 4, mt: 8 }}>
            <Typography variant="h4" component="h1" gutterBottom> {/* Sửa variant */}
                Yêu cầu Chờ duyệt {/* Sửa Title */}
            </Typography>

            {pendingBookings.length === 0 && !isPending ? (
                <Typography variant="h6" component="h2" sx={{ marginTop: "2em", color: 'text.secondary' }}>
                    Không có yêu cầu nào đang chờ duyệt.
                </Typography>
            ) : (
                 <Box sx={{ width: '100%', display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 3, mt: 2 }}>
                     {/* SỬA: Map qua pendingBookings và truyền prop booking */}
                     {pendingBookings.map((booking) => (
                         // Component Card để hiển thị và duyệt/từ chối
                         <ApprovalCard
                             key={booking.id} // <<< Dùng ID làm key
                             booking={booking} // <<< Truyền cả object booking
                             onActionSuccess={() => { // Callback để refresh lại list
                                 queryClient.invalidateQueries({ queryKey: ["pendingApprovals", page, rowsPerPage] });
                             }}
                         />
                     ))}

                      {/* Thêm Phân trang MUI */}
                      {totalPages > 1 && (
                          <Pagination
                              count={totalPages}
                              page={page + 1} // MUI Pagination là 1-based
                              onChange={handlePageChange}
                              color="primary"
                              sx={{ mt: 4 }} // Thêm margin top
                              showFirstButton showLastButton // Hiển thị nút đầu/cuối
                          />
                      )}
                 </Box>
            )}
        </Box>
    );
};

export default BookingApprovalList; 