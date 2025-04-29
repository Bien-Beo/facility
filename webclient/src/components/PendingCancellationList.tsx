import React, { JSX, FC, useEffect, useState, ChangeEvent } from "react";
import { Box, CircularProgress, Typography, Pagination } from "@mui/material"; 
import { useQuery, useQueryClient } from "@tanstack/react-query";
import axios, { AxiosError } from "axios";

import CancellationCard from "./cards/CancellationCard";
import ErrorComponent from "./Error";
import { useAuth } from "../hooks/useAuth";

const PendingCancellationList: FC = (): JSX.Element => {

    // State cho phân trang
    const [page, setPage] = useState<number>(0);
    const [rowsPerPage, setRowsPerPage] = useState<number>(10);

    const [pendingCancellations, setPendingCancellations] = useState<BookingEntry[]>([]);

    const auth = useAuth();
    const queryClient = useQueryClient();

    // --- Query lấy danh sách booking chờ hủy ---
    const { data: apiResponse, isPending, isError, error } = useQuery<PaginatedBookingApiResponse, AxiosError<ErrorMessage>>({
        queryKey: ["pendingCancellations", page, rowsPerPage],
        queryFn: async () => {
            const token = localStorage.getItem("token");
            if (!token) throw new Error("No token found");

            // API endpoint để lấy booking chờ hủy, có phân trang
            const params = new URLSearchParams();
            params.append('page', String(page));
            params.append('size', String(rowsPerPage));
             // *** Cần xác định status hoặc cách lọc đúng cho yêu cầu chờ hủy ***
             // Ví dụ nếu dùng status PENDING_CANCELLATION:
            params.append('status', "PENDING_CANCELLATION");
            // Hoặc nếu bạn có một trường riêng để đánh dấu yêu cầu hủy: params.append('cancellationRequested', 'true');
            params.append('sort', 'createdAt,asc'); // Sắp xếp yêu cầu cũ lên trước

            const url = `${import.meta.env.VITE_APP_SERVER_URL || 'http://localhost:8080'}/booking?${params.toString()}`;
            console.log("Fetching pending cancellations with URL:", url);

            const response = await axios.get<PaginatedBookingApiResponse>(url, {
                headers: { Authorization: `Bearer ${token}` }
            });

             if (response.data?.code !== 0 || !response.data?.result?.content || !response.data?.result?.page) {
                 throw new Error(response.data?.message || "Invalid API response for pending cancellations.");
             }
            return response.data;
        },
        keepPreviousData: true,
        enabled: !!auth.user, 
        refetchInterval: 10 * 1000,
    });

    // SỬA: Cập nhật state từ response API đúng cấu trúc
    useEffect(() => {
        if (!isPending && apiResponse?.result?.content) {
            setPendingCancellations(apiResponse.result.content);
        }
         else if (!isPending && !apiResponse?.result?.content) {
             setPendingCancellations([]);
         }
    }, [apiResponse, isPending]);

    // Handlers cho Pagination
     const handlePageChange = (event: React.ChangeEvent<unknown>, newPage: number) => {
         setPage(newPage - 1); // MUI Pagination là 1-based
     };

    // SỬA: Xử lý lỗi truy cập đúng thuộc tính
    if (isError) {
        const errorData = error?.response?.data || { message: error.message, status: error.response?.status || 500 };
        return ( <ErrorComponent status={errorData.status ?? 500} message={errorData.message} /> );
    }

    // Hiển thị loading
    if (isPending && pendingCancellations.length === 0) {
        return ( <Box sx={{ minHeight: 'calc(100vh - 64px)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}> <CircularProgress /> </Box> );
    }

    const totalPages = apiResponse?.result?.page?.totalPages ?? 0;

    // --- Render JSX ---
    return (
        <Box sx={{ width: '100%', display: 'flex', flexDirection: 'column', alignItems: 'center', px: { xs: 2, md: 3 }, py: 4, mt: 8 }}>
            <Typography variant="h4" component="h1" gutterBottom>
                Yêu cầu Hủy Chờ Xử lý {/* Sửa Title */}
            </Typography>

             {/* SỬA: Kiểm tra mảng pendingCancellations */}
            {pendingCancellations.length === 0 && !isPending ? (
                <Typography variant="h6" component="h2" sx={{ marginTop: "2em", color: 'text.secondary' }}>
                    Không có yêu cầu hủy nào đang chờ xử lý.
                </Typography>
            ) : (
                 <Box sx={{ width: '100%', display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 3, mt: 2 }}>
                     {/* SỬA: Map qua pendingCancellations và truyền prop booking */}
                     {pendingCancellations.map((booking) => (
                         // Component Card để hiển thị và xử lý yêu cầu hủy
                         <CancellationCard
                             key={booking.id} // <<< Dùng ID làm key
                             booking={booking} // <<< Truyền cả object booking
                             onActionSuccess={() => { // Callback để refresh lại list
                                 queryClient.invalidateQueries({ queryKey: ["pendingCancellations", page, rowsPerPage] });
                             }}
                         />
                     ))}

                      {totalPages > 1 && (
                          <Pagination
                              count={totalPages}
                              page={page + 1} 
                              onChange={handlePageChange}
                              color="primary"
                              sx={{ mt: 4 }}
                              showFirstButton showLastButton
                          />
                      )}
                 </Box>
            )}
        </Box>
    );
};

export default PendingCancellationList; 