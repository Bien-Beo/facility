import React, { JSX, FC, useState, useCallback, useMemo } from "react";
import { Box, Button, CircularProgress, Paper, Slide, Snackbar, Alert, Typography, Chip, Tooltip, IconButton } from "@mui/material"; 
import CheckCircleOutlineIcon from '@mui/icons-material/CheckCircleOutline'; 
import CancelIcon from '@mui/icons-material/Cancel'; 
import { useMutation, useQueryClient } from "@tanstack/react-query";
import axios, { AxiosError } from "axios";

import isoToDate from "../../utils/isoToDate";
import isoToTime from "../../utils/isoToTime";

// --- Helper Status ---
const getDisplayStatus = (status: BookingStatusType | string): { text: string; color: "warning" | "success" | "error" | "info" | "default" } => {
    switch(status) {
       case "PENDING_APPROVAL": return { text: "Chờ duyệt", color: "warning" };
       default: return { text: status?.replace(/_/g, ' ') || 'N/A', color: "default" };
    }
};

// --- Component ---
const CancellationCard: FC<CancellationCardProps> = ({
    booking,
    onActionSuccess
}): JSX.Element => {

    const [openSnackbar, setOpenSnackbar] = useState<boolean>(false);
    const [snackbarMessage, setSnackbarMessage] = useState<string>("");
    const [snackbarSeverity, setSnackbarSeverity] = useState<"success" | "error" | "warning">("success");
    const [backendError, setBackendError] = useState<ErrorMessage | null>(null); 

    const queryClient = useQueryClient();

    // === Mutation Chấp thuận Hủy ===
    const approveCancelMutation = useMutation<ApiResponse<BookingEntry> | void, AxiosError<ErrorMessage>, void>({
        mutationFn: async () => {
            const token = localStorage.getItem("token");
            if (!token) throw new Error("No token found");
            console.log(`Sending cancellation approval for booking ${booking.id}`);
            await axios.put( 
                `${import.meta.env.VITE_APP_SERVER_URL || 'http://localhost:8080'}/booking/${booking.id}/cancel/approve`, 
                null,
                { headers: { Authorization: `Bearer ${token}` } }
            );
        },
        onSuccess: () => {
            console.log("Cancellation approved:", booking.id);
            setSnackbarMessage("Đã chấp thuận yêu cầu hủy.");
            setSnackbarSeverity("success");
            setOpenSnackbar(true);
            queryClient.invalidateQueries({ queryKey: ["adminBookings"] });
            queryClient.invalidateQueries({ queryKey: ["pendingCancellations"] }); 
            if (onActionSuccess) onActionSuccess();
            setBackendError(null);
        },
        onError: (error) => {
            console.error("Approve cancellation failed:", error);
            const errMsg = error.response?.data?.message || error.message || "Lỗi khi duyệt hủy";
            setBackendError({ message: errMsg });
            setSnackbarMessage(`Duyệt hủy thất bại: ${errMsg}`);
            setSnackbarSeverity("error");
            setOpenSnackbar(true);
        },
    });

    // === Mutation Từ chối Hủy ===
    const rejectCancelMutation = useMutation<ApiResponse<BookingEntry> | void, AxiosError<ErrorMessage>, void>({
        mutationFn: async () => {
            const token = localStorage.getItem("token");
            if (!token) throw new Error("No token found");
            console.log(`Sending cancellation rejection for booking ${booking.id}`);
             await axios.put( 
                 `${import.meta.env.VITE_APP_SERVER_URL || 'http://localhost:8080'}/booking/${booking.id}/cancel/reject`, 
                 null,
                 { headers: { Authorization: `Bearer ${token}` } }
             );
        },
        onSuccess: () => {
            console.log("Cancellation rejected:", booking.id);
             setSnackbarMessage("Đã từ chối yêu cầu hủy.");
             setSnackbarSeverity("warning");
            setOpenSnackbar(true);
            queryClient.invalidateQueries({ queryKey: ["adminBookings"] });
             queryClient.invalidateQueries({ queryKey: ["pendingCancellations"] }); 
            if (onActionSuccess) onActionSuccess();
             setBackendError(null);
        },
        onError: (error) => {
             console.error("Reject cancellation failed:", error);
             const errMsg = error.response?.data?.message || error.message || "Lỗi khi từ chối hủy";
             setBackendError({ message: errMsg });
             setSnackbarMessage(`Từ chối hủy thất bại: ${errMsg}`);
             setSnackbarSeverity("error");
             setOpenSnackbar(true);
        },
    });

    // --- Handlers ---
    const handleApproveCancellation = () => {
        setBackendError(null);
        approveCancelMutation.mutate();
    };

    const handleRejectCancellation = () => {
        setBackendError(null);
        rejectCancelMutation.mutate();
    };

    const handleCloseSnackbar = (): void => { setOpenSnackbar(false); };

    if (booking.status !== "PENDING_CANCELLATION") { 
       return null;
    }

    return (
        <Slide direction="up" in={true} mountOnEnter unmountOnExit>
             <Paper elevation={2} sx={{ display: 'flex', flexDirection: { xs: 'column', md: 'row' }, alignItems: 'flex-start', justifyContent: 'space-between', p: { xs: 2, sm: 2.5 }, my: 2, width: '100%', maxWidth: '950px', borderLeft: 5, borderColor: 'info.main' }}> {/* Ví dụ màu khác cho chờ hủy */}
                 {/* Phần hiển thị thông tin booking và yêu cầu hủy */}
                 <Box sx={{ flexGrow: 1, pr: { md: 3 }, mb: { xs: 2, md: 0 } }}>
                      <Typography variant="h6" component="div" sx={{ fontWeight: 600, mb: 1 }}>
                           Phòng: {booking.roomName || 'N/A'} - {booking.purpose}
                      </Typography>
                      <Typography variant="body2">Người đặt: <strong>{booking.userName}</strong></Typography>
                      <Typography variant="body2">Thời gian đặt: <strong>{`${isoToDate(booking.plannedStartTime)} (${isoToTime(booking.plannedStartTime)} - ${isoToTime(booking.plannedEndTime)})`}</strong></Typography>
                      <Typography variant="body2" sx={{mt: 1.5, fontWeight: 'bold', color: 'error.main'}}>Yêu cầu hủy bởi: {booking.cancelledByUserName || 'N/A'}</Typography>
                      <Typography variant="body2" sx={{fontStyle: 'italic'}}>Lý do: "{booking.cancellationReason || 'Không có lý do'}"</Typography>
                      <Typography variant="body2" sx={{mt: 0.5}}>Trạng thái hiện tại: <Chip label={getDisplayStatus(booking.status as BookingStatusType).text} color={getDisplayStatus(booking.status as BookingStatusType).color} size="small" /></Typography>
                 </Box>

                  {/* Phần Actions: Approve/Reject Cancellation */}
                  <Box sx={{ width: { xs: '100%', md: '30%' }, pl: { md: 3 }, borderLeft: { md: `1px solid` }, display: 'flex', flexDirection: 'column', justifyContent: 'center', gap: 1.5, alignItems: 'center' }}>
                      <Typography variant="subtitle2" align="center" sx={{fontWeight: 'bold', mb:1}}>Xử lý Yêu cầu Hủy:</Typography>
                       {/* Hiển thị lỗi nếu có */}
                        {backendError && (<Alert severity="error" size="small" sx={{mb: 1, width: '100%'}}>{backendError.message}</Alert>)}
                      <Box sx={{ display: 'flex', gap: 1.5, justifyContent: 'space-between', width: '100%' }}>
                           <Button
                              variant="contained" startIcon={<CheckCircleOutlineIcon />} color="success" size="medium"
                              onClick={handleApproveCancellation}
                              disabled={approveCancelMutation.isPending || rejectCancelMutation.isPending}
                              sx={{ flexGrow: 1 }}
                          >
                               {approveCancelMutation.isPending ? <CircularProgress size={20} color="inherit"/> : "Chấp thuận Hủy"}
                          </Button>
                          <Button
                              variant="contained" startIcon={<CancelIcon />} color="error" size="medium"
                              onClick={handleRejectCancellation}
                              disabled={approveCancelMutation.isPending || rejectCancelMutation.isPending}
                              sx={{ flexGrow: 1 }}
                          >
                              {rejectCancelMutation.isPending ? <CircularProgress size={20} color="inherit"/> : "Từ chối Hủy"}
                          </Button>
                      </Box>
                  </Box>

                 {/* Snackbar */}
                  <Snackbar open={openSnackbar} autoHideDuration={3000} onClose={handleCloseSnackbar}>
                     <Alert onClose={handleCloseSnackbar} severity={snackbarSeverity} variant="filled" sx={{ width: "100%" }}>
                         {snackbarMessage}
                     </Alert>
                 </Snackbar>
             </Paper>
        </Slide>
    );
};

export default CancellationCard;