import React, { JSX, FC, useState, useCallback, useMemo, ChangeEvent } from "react";
import { Box, Button, CircularProgress, FormControl, FormLabel, Paper, Slide, Snackbar, TextField, Typography, IconButton, Alert, Chip, Tooltip } from "@mui/material";
import HighlightOffIcon from "@mui/icons-material/HighlightOff";
import TaskAltIcon from "@mui/icons-material/TaskAlt"; 
import { useMutation, useQueryClient } from "@tanstack/react-query";
import axios, { AxiosError } from "axios";

import isoToDate from "../../utils/isoToDate";
import isoToTime from "../../utils/isoToTime";

// --- Helper Status ---
const getDisplayStatus = (status: BookingStatusType | string): { text: string; color: "warning" | "success" | "error" | "info" | "default" } => {
    switch(status) {
        case "OVERDUE": return { text: "Quá hạn", color: "error" };
       default: return { text: status?.replace(/_/g, ' ') || 'N/A', color: "default" };
    }
};

// --- Component ---
const OverdueCard: FC<OverdueCardProps> = ({
    booking,
    onActionSuccess
}): JSX.Element => {

    const [recallValue, setRecallValue] = useState<string>("");
    const [openSnackbar, setOpenSnackbar] = useState<boolean>(false);
    const [snackbarMessage, setSnackbarMessage] = useState<string>("");
    const [snackbarSeverity, setSnackbarSeverity] = useState<"success" | "error" | "warning">("success");
    const [backendError, setBackendError] = useState<ErrorMessage | null>(null); 

    const queryClient = useQueryClient();

    const reminderMutation = useMutation<void, AxiosError<ErrorMessage>, void>({
            mutationFn: async () => {
                const token = localStorage.getItem("token");
                if (!token) throw new Error("No token found");
                console.log(`Sending reminder request for booking ${booking.id}`);
                const param = { bookingId: booking.id };
                await axios.get(
                    `${import.meta.env.VITE_APP_SERVER_URL}/api/notifications/overdue-reminder`, 
                    { 
                        params: param, 
                        headers: { Authorization: `Bearer ${token}` } 
                    }
                );
            },
            onSuccess: () => {
                console.log("Reminder sent for booking:", booking.id);
                setSnackbarMessage("Nhắc nhở đã được gửi thành công!");
                setSnackbarSeverity("success");
                setOpenSnackbar(true);
                queryClient.invalidateQueries({ queryKey: ["reminderNotification"] }); 
                if (onActionSuccess) onActionSuccess();
                setBackendError(null);
            },
            onError: (error) => {
                console.error("Reminder failed:", error);
                const errMsg = error.response?.data?.message || error.message || "Lỗi khi gửi nhắc nhở";
                setBackendError({ message: errMsg });
                setSnackbarMessage(`Gửi nhắc nhở thất bại: ${errMsg}`);
                setSnackbarSeverity("error");
                setOpenSnackbar(true);
            },
        });

        const recallMutation = useMutation<void, AxiosError<ErrorMessage>, RecallBookingRequest>({
            mutationFn: async (data: RecallBookingRequest) => {
                const token = localStorage.getItem("token");
                if (!token) throw new Error("No token found");
                console.log(`Sending recall request for booking ${booking.id} with reason: ${data.reason}`);
                await axios.put(
                    `${import.meta.env.VITE_APP_SERVER_URL}/booking/revoke`,
                    null,
                    {
                        headers: {
                            Authorization: `Bearer ${token}`,
                            'Content-Type': 'application/json'
                        },
                        params: {
                            bookingId: booking.id,
                            reason: data.reason
                        }
                    }
                );                
            },
             onSuccess: () => {
                console.log("Booking recalled:", booking.id);
                setSnackbarMessage("Yêu cầu đã được thu hồi!");
                setSnackbarSeverity("warning");
                setOpenSnackbar(true);
                setRecallValue("");
                queryClient.invalidateQueries({ queryKey: ["recallBooking"] }); 
                if (onActionSuccess) onActionSuccess();
                setBackendError(null);
            },
            onError: (error) => {
                console.error("Reject failed:", error);
                const errMsg = error.response?.data?.message || error.message || "Lỗi khi từ chối";
                setBackendError({ message: errMsg });
                setSnackbarMessage(`Từ chối thất bại: ${errMsg}`);
                setSnackbarSeverity("error");
                setOpenSnackbar(true);
            },
        });
    
        // --- Handlers ---
        const handleReminder = () => {
            setBackendError(null);
            reminderMutation.mutate();
        };
    
        const handleRecall = () => {
            setBackendError(null);
            if (!recallValue.trim() || recallValue.length < 5) {
                setBackendError({ message: "Vui lòng nhập lý do thu hồi (ít nhất 5 ký tự)." });
                return;
            }
            const recallData: RecallBookingRequest = { reason: recallValue.trim() };
            recallMutation.mutate(recallData);
        };

    const handleCloseSnackbar = (): void => { setOpenSnackbar(false); };

    if (booking.status !== "OVERDUE") { 
       return null;
    }

    return (
        <Slide direction="up" in={true} mountOnEnter unmountOnExit>
             <Paper elevation={2} sx={{ display: 'flex', flexDirection: { xs: 'column', md: 'row' }, alignItems: 'flex-start', justifyContent: 'space-between', p: { xs: 2, sm: 2.5 }, my: 2, width: '100%', maxWidth: '950px', borderLeft: 5, borderColor: 'info.main' }}> {/* Ví dụ màu khác cho chờ hủy */}
                 <Box sx={{ flexGrow: 1, pr: { md: 3 }, mb: { xs: 2, md: 0 } }}>
                      <Typography variant="h6" component="div" sx={{ fontWeight: 600, mb: 1 }}>
                           Phòng: {booking.roomName || 'N/A'} - {booking.purpose}
                      </Typography>
                      <Typography variant="body2" sx={{mt: 0.5}}>Người đặt: <strong>{booking.userName}</strong></Typography>
                      <Typography variant="body2" sx={{mt: 0.5}}>Thời gian đặt: <strong>{`${isoToDate(booking.plannedStartTime)} (${isoToTime(booking.plannedStartTime)} - ${isoToTime(booking.plannedEndTime)})`}</strong></Typography>
                      <Typography variant="body2" sx={{mt: 0.5, fontWeight: 'bold', color: 'error.main'}}>Yêu cầu hủy bởi: {booking.cancelledByUserName || 'N/A'}</Typography>
                      <Typography variant="body2" sx={{mt: 0.5, fontStyle: 'italic'}}>Lý do: "{booking.cancellationReason || 'Không có lý do'}"</Typography>
                      <Typography variant="body2" sx={{mt: 0.5}}>Trạng thái hiện tại: <Chip label={getDisplayStatus(booking.status as BookingStatusType).text} color={getDisplayStatus(booking.status as BookingStatusType).color} size="small" /></Typography>
                 </Box>

                 <Box sx={{ width: { xs: '100%', md: '40%' }, pl: { md: 3 }, borderLeft: { md: `1px solid` }, display: 'flex', flexDirection: 'column', gap: 1.5 }}>
                    <FormControl fullWidth >
                        <TextField
                            id={`recall-reason-${booking.id}`}
                            label="Thông báo thu hồi"
                            variant="outlined"
                            value={recallValue}
                            onChange={(e: ChangeEvent<HTMLInputElement>) => setRecallValue(e.target.value)}
                            size="small"
                            multiline
                            rows={2}
                            error={!!backendError && backendError.message?.toLowerCase().includes('reason')}
                            helperText={backendError && backendError.message?.toLowerCase().includes('reason') ? backendError.message : ""}
                        />
                    </FormControl>

                     {/* Hiển thị lỗi chung nếu có */}
                      {backendError && !backendError.message?.toLowerCase().includes('reason') && (
                          <Alert severity="error" size="small" sx={{ mt: 1 }}>{backendError.message}</Alert>
                      )}

                     <Box sx={{ display: 'flex', gap: 1.5, justifyContent: 'space-between', mt: 1 }}>
                          <Button
                             variant="contained" startIcon={<TaskAltIcon />} color="success" size="medium"
                             onClick={handleReminder}
                             disabled={reminderMutation.isPending || recallMutation.isPending}
                             sx={{ flexGrow: 1 }} 
                         >
                              {reminderMutation.isPending ? <CircularProgress size={20} color="inherit"/> : "Nhắc nhở"}
                         </Button>
                         <Button
                             variant="contained" startIcon={<HighlightOffIcon />} color="error" size="medium"
                             onClick={handleRecall}
                             disabled={reminderMutation.isPending || recallMutation.isPending || !recallValue.trim()}
                             sx={{ flexGrow: 1 }}
                         >
                             {recallMutation.isPending ? <CircularProgress size={20} color="inherit"/> : "Thu hồi"}
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

export default OverdueCard;