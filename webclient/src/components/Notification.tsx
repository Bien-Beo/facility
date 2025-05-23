import React, { JSX, FC, useEffect, useState, ChangeEvent } from "react";
import { Box, CircularProgress, Typography, Pagination, Button } from "@mui/material"; 
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import axios, { AxiosError } from "axios";

import ErrorComponent from "./Error";
import { useAuth } from "../hooks/useAuth";

const markAsRead = async (notificationId: string) => {
    const token = localStorage.getItem("token");
    if (!token) throw new Error("No token found");

    const url = `${import.meta.env.VITE_APP_SERVER_URL}/api/notifications/${notificationId}/read`;
    await axios.put(url, {}, {
        headers: { Authorization: `Bearer ${token}` }
    });
};

const Notification: FC = (): JSX.Element => {
    // --- State and Hooks ---
    const [page, setPage] = useState<number>(0);
    const [rowsPerPage, setRowsPerPage] = useState<number>(10);

    const [notifications, setNotifications] = useState<Notification[]>([]);

    const auth = useAuth();
    const queryClient = useQueryClient();
    

    // --- Fetch Notifications ---
    const { data: apiResponse, isPending, isError, error } = useQuery<PaginatedNotificationApiResponse, AxiosError<ErrorMessage>>({
        queryKey: ["notifications", page, rowsPerPage],
        queryFn: async () => {
            const token = localStorage.getItem("token");
            if (!token) throw new Error("No token found");

            const url = `${import.meta.env.VITE_APP_SERVER_URL}/api/notifications?page=${page}&size=${rowsPerPage}&userId=${auth.user?.id}`;
            console.log("Fetching notifications with URL:", url);

            const response = await axios.get<PaginatedNotificationApiResponse>(url, {
                headers: { Authorization: `Bearer ${token}` }
            });

            if (response.data?.code !== 0 || !response.data?.result?.content || !response.data?.result?.page) {
                throw new Error(response.data?.message || "Invalid API response for notifications.");
            }

            return response.data;
        },
        keepPreviousData: true,
        enabled: !!auth.user,
        refetchInterval: 10 * 1000,
    });

    const { mutate: markNotificationAsRead, isLoading: isMarking } = useMutation({
        mutationFn: markAsRead,
        onSuccess: () => {
            queryClient.invalidateQueries(["notifications"]);
        },
    });      

    useEffect(() => {
        if (!isPending && apiResponse?.result?.content) {
            setNotifications(apiResponse.result.content);
        } else if (!isPending && !apiResponse?.result?.content) {
            setNotifications([]);
        }
    }, [apiResponse, isPending]);

    const handlePageChange = (event: React.ChangeEvent<unknown>, newPage: number) => {
        setPage(newPage - 1); // Adjust for zero-based index
    }

    const handleMarkAsRead = (notificationId: string) => {
        markNotificationAsRead(notificationId);
    };

    if (isError) {
        const errorData = error?.response?.data || { message: error.message, status: error.response?.status || 500 };
        return ( <ErrorComponent status={errorData.status ?? 500} message={errorData.message} /> );
    }

    // Hiển thị loading
    if (isPending && notifications.length === 0) {
        return ( <Box sx={{ minHeight: 'calc(100vh - 64px)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}> <CircularProgress /> </Box> );
    }

    const totalPages = apiResponse?.result?.page?.totalPages ?? 0;
    
    // --- Render JSX ---
    return (
        <Box sx={{ width: '100%', maxWidth: 800, mx: 'auto', px: { xs: 2, md: 3 }, py: 4 }}>
            <Typography variant="h4" fontWeight="bold" gutterBottom textAlign="center">
                Thông báo
            </Typography>
    
            {notifications.length === 0 ? (
                <Typography variant="body1" sx={{ mt: 4, textAlign: "center", color: "text.secondary" }}>
                    Không có thông báo nào.
                </Typography>
            ) : (
                <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
                    {notifications.map((notification) => (
                        <Box
                            key={notification.id}
                            sx={{
                                border: '1px solid',
                                borderColor: 'divider',
                                borderRadius: 2,
                                px: 2,
                                py: 2,
                                boxShadow: 1,
                                backgroundColor: 'background.paper',
                                transition: 'all 0.3s',
                                '&:hover': {
                                    boxShadow: 3,
                                    transform: 'translateY(-2px)'
                                }
                            }}
                        >
                            <Typography variant="body1" sx={{ fontWeight: 'medium' }}>
                                {notification.message}
                            </Typography>
                            <Typography variant="caption" color="text.secondary">
                                {new Date(notification.createdAt).toLocaleString("vi-VN")}
                            </Typography>

                            {notification.status !== "READ" && (
                            <Box sx={{ mt: 1 }}>
                                <Button
                                    disabled={isMarking}
                                    onClick={() => markNotificationAsRead(notification.id)}
                                    variant="contained"
                                    color="primary"
                                    sx={{ mt: 1 }}
                                >
                                    Đã đọc
                                </Button>
                            </Box>
        )}
                        </Box>
                    ))}
                </Box>
            )}
    
            {totalPages > 1 && (
                <Pagination
                    count={totalPages}
                    page={page + 1}
                    onChange={handlePageChange}
                    sx={{ my: 4, display: 'flex', justifyContent: 'center' }}
                    color="primary"
                    shape="rounded"
                />
            )}
        </Box>
    );
    
};

export default Notification; 