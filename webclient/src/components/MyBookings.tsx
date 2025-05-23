import React, { JSX, FC, useEffect, useState } from "react"; 
import { MenuItem, Select, FormControl, InputLabel, Box, CircularProgress, Typography, Pagination } from "@mui/material";
import { useQuery, useQueryClient } from "@tanstack/react-query"; 
import axios, { AxiosError } from "axios";

import ErrorComponent from "./Error";
import MyBookingCard from "./cards/MyBookingCard"; 
import { useAuth } from "../hooks/useAuth"; 

const MyBookings: FC = (): JSX.Element => {
    const [myBookings, setMyBookings] = useState<BookingEntry[]>([]);
    const queryClient = useQueryClient();
    const auth: AuthContextType = useAuth(); 
    const userId = auth.user?.id; 

    const [page, setPage] = useState(0);
    const [size, setSize] = useState(5); 

    const { data: apiResponse, isPending, isError, error } = useQuery<PaginatedBookingApiResponse | undefined, AxiosError<ErrorMessage>>({
        queryKey: ["myBookings", userId, page, size],
        queryFn: async (): Promise<PaginatedBookingApiResponse> => {
            if (!userId) throw new Error("User not logged in"); 
            const token = localStorage.getItem("token");
            if (!token) throw new Error("No token found");
    
            const response = await axios.get<PaginatedBookingApiResponse>(
                `${import.meta.env.VITE_APP_SERVER_URL}/booking/my?page=${page}&size=${size}&sort=plannedStartTime,desc`, 
                { headers: { Authorization: `Bearer ${token}` } }
            );
             if (response.data?.code !== 0 || !response.data?.result?.content || !response.data?.result?.page) {
                 console.error("Invalid API response structure for my bookings:", response.data);
                 throw new Error(response.data?.message || "Cấu trúc API response không hợp lệ.");
             }
            return response.data;
        },
        enabled: !!userId, 
        keepPreviousData: true, 
    });

    useEffect(() => {
        if (!isPending && apiResponse?.result?.content) {
            setMyBookings(apiResponse.result.content);
        }
         else if (!isPending && !apiResponse?.result?.content) {
             setMyBookings([]); 
         }
    }, [apiResponse, isPending]); 

    if (isError) {
        const errorData = error?.response?.data || { message: error.message, status: error.response?.status || 500 };
        return (
            <ErrorComponent
                status={errorData.status ?? 500} 
                message={errorData.message}
            />
        );
    }

    // Hiển thị loading
    if (isPending) {
        return (
            // Dùng Box và căn giữa chuẩn hơn
            <Box sx={{ minHeight: 'calc(100vh - 64px)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                <CircularProgress />
            </Box>
        );
    }

    // --- Render ---
    return (
        <Box sx={{ width: '100%', display: 'flex', flexDirection: 'column', alignItems: 'center', px: { xs: 2, md: 3 }, py: 4, mt: 8 }}>
            <Typography variant="h3" component="h1" gutterBottom>
                Lịch sử Đặt chỗ của tôi
            </Typography>

            {/* Sửa lại điều kiện kiểm tra mảng rỗng */}
            {myBookings.length === 0 ? (
                <Typography variant="h5" component="h2" sx={{ marginTop: "2em", color: 'text.secondary' }}>
                    Bạn chưa có yêu cầu đặt chỗ nào!
                </Typography>
            ) : (
                // Container cho các card booking
                 <Box sx={{ width: '100%', display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 3, mt: 2 }}>
                     {myBookings.map((booking) => (
                         <MyBookingCard
                             key={booking.id} 
                             booking={booking} 
                             onCancelSuccess={() => { 
                                  queryClient.invalidateQueries({ queryKey: ["myBookings", auth.user?.id] });
                             }}
                         />
                     ))}
                     <Box sx={{ width: '100%', mt: 4 }}>
                        <Box
                            sx={{
                                display: 'flex',
                                flexDirection: { xs: 'column', sm: 'row' },
                                justifyContent: 'space-between',
                                alignItems: { xs: 'flex-start', sm: 'center' },
                                gap: 2,
                                mb: 2,
                            }}
                        >
                            <Typography variant="body1" sx={{ color: 'text.secondary' }}>
                                Hiển thị {myBookings.length} trong tổng số {apiResponse?.result?.page?.totalElements ?? 0} yêu cầu
                            </Typography>

                            <FormControl size="small" sx={{ minWidth: 120 }}>
                                <InputLabel id="size-label">Mỗi trang</InputLabel>
                                <Select
                                    labelId="size-label"
                                    value={size}
                                    onChange={(e) => {
                                        setSize(Number(e.target.value));
                                        setPage(0);
                                    }}
                                    label="Mỗi trang"
                                >
                                    {[5, 10, 20, 50].map((s) => (
                                        <MenuItem key={s} value={s}>{s}</MenuItem>
                                    ))}
                                </Select>
                            </FormControl>
                        </Box>

                        <Box sx={{ display: 'flex', justifyContent: 'center', mt: 2, mb: 2 }}>
                            <Pagination
                                count={apiResponse?.result?.page?.totalPages ?? 0}
                                page={page + 1}
                                onChange={(e, newPage) => setPage(newPage - 1)}
                                color="primary"
                                variant="outlined"
                                shape="rounded"
                            />
                        </Box>
                    </Box>
                 </Box>
            )}
        </Box>
    );
};

export default MyBookings;