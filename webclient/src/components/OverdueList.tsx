import React, { JSX, FC, useEffect, useState, ChangeEvent } from "react";
import { Box, CircularProgress, Typography, Pagination } from "@mui/material"; 
import { useQuery, useQueryClient } from "@tanstack/react-query";
import axios, { AxiosError } from "axios";

import OverdueCard from "./cards/OverdueCard";
import ErrorComponent from "./Error";
import { useAuth } from "../hooks/useAuth";

const OverdueList: FC = (): JSX.Element => {

    const [page, setPage] = useState<number>(0);
    const [rowsPerPage, setRowsPerPage] = useState<number>(10);

    const [pendingOverdue, setPendingOverdue] = useState<BookingEntry[]>([]);

    const auth = useAuth();
    const queryClient = useQueryClient();

    const { data: apiResponse, isPending, isError, error } = useQuery<PaginatedBookingApiResponse, AxiosError<ErrorMessage>>({
        queryKey: ["pendingOverdue", page, rowsPerPage],
        queryFn: async () => {
            const token = localStorage.getItem("token");
            if (!token) throw new Error("No token found");
        
            const url = `${import.meta.env.VITE_APP_SERVER_URL}/booking/overdue?page=${page}&size=${rowsPerPage}`;
            console.log("Fetching overdue bookings with URL:", url);
        
            const response = await axios.get<PaginatedBookingApiResponse>(url, {
                headers: { Authorization: `Bearer ${token}` }
            });
        
            if (response.data?.code !== 0 || !response.data?.result?.content || !response.data?.result?.page) {
                throw new Error(response.data?.message || "Invalid API response for overdue bookings.");
            }
        
            return response.data;
        },        
        keepPreviousData: true,
        enabled: !!auth.user, 
        refetchInterval: 10 * 1000,
    });

    useEffect(() => {
        if (!isPending && apiResponse?.result?.content) {
            setPendingOverdue(apiResponse.result.content);
        }
         else if (!isPending && !apiResponse?.result?.content) {
             setPendingOverdue([]);
         }
    }, [apiResponse, isPending]);

     const handlePageChange = (event: React.ChangeEvent<unknown>, newPage: number) => {
         setPage(newPage - 1); 
     };

    // SỬA: Xử lý lỗi truy cập đúng thuộc tính
    if (isError) {
        const errorData = error?.response?.data || { message: error.message, status: error.response?.status || 500 };
        return ( <ErrorComponent status={errorData.status ?? 500} message={errorData.message} /> );
    }

    // Hiển thị loading
    if (isPending && pendingOverdue.length === 0) {
        return ( <Box sx={{ minHeight: 'calc(100vh - 64px)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}> <CircularProgress /> </Box> );
    }

    const totalPages = apiResponse?.result?.page?.totalPages ?? 0;

    // --- Render JSX ---
    return (
        <Box sx={{ width: '100%', display: 'flex', flexDirection: 'column', alignItems: 'center', px: { xs: 2, md: 3 }, py: 4, mt: 8 }}>
            <Typography variant="h4" component="h1" gutterBottom>
                Yêu cầu quá hạn
            </Typography>
        
            {pendingOverdue.length === 0 && !isPending ? (
                <Typography variant="h6" component="h2" sx={{ marginTop: "2em", color: 'text.secondary' }}>
                    Không có yêu cầu quá hạn nào đang chờ xử lý.
                </Typography>
            ) : (
                 <Box sx={{ width: '100%', display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 3, mt: 2 }}>
                     {pendingOverdue.map((booking) => (
                         <OverdueCard
                             key={booking.id} 
                             booking={booking} 
                             onActionSuccess={() => { 
                                 queryClient.invalidateQueries({ queryKey: ["pendingOverdue", page, rowsPerPage] });
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

export default OverdueList; 