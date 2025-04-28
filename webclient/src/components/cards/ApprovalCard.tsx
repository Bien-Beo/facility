import React, { JSX, ChangeEvent, FC, FormEvent, useState, useMemo, useCallback } from "react";
import { Box, Button, CircularProgress, FormControl, FormLabel, Paper, Slide, Snackbar, TextField, Typography, IconButton, Alert, Chip, Tooltip } from "@mui/material";
import TaskAltIcon from "@mui/icons-material/TaskAlt"; 
import HighlightOffIcon from "@mui/icons-material/HighlightOff"; 
import InfoOutlinedIcon from '@mui/icons-material/InfoOutlined'; 
import { useMutation, useQueryClient } from "@tanstack/react-query";
import axios, { AxiosError } from "axios";
import dayjs from "dayjs";

import isoToDate from "../../utils/isoToDate";
import isoToTime from "../../utils/isoToTime";

// --- Helper định dạng Status ---
const getDisplayStatus = (status: BookingStatusType | string): { text: string; color: "warning" | "success" | "error" | "info" | "default" } => {
     switch(status) {
        case "PENDING_APPROVAL": return { text: "Chờ duyệt", color: "warning" };
        default: return { text: status?.replace(/_/g, ' ') || 'N/A', color: "default" };
     }
 };

// --- Component ---
const ApprovalCard: FC<ApprovalCardProps> = ({
    booking,
    onActionSuccess
}): JSX.Element => {

    const [remarkValue, setRemarkValue] = useState<string>(""); // State cho lý do từ chối
    const [openSnackbar, setOpenSnackbar] = useState<boolean>(false);
    const [snackbarMessage, setSnackbarMessage] = useState<string>("");
    const [snackbarSeverity, setSnackbarSeverity] = useState<"success" | "error" | "warning">("success");
    const [backendError, setBackendError] = useState<ErrorMessage | null>(null);

    const queryClient = useQueryClient();

    // === Mutation cho Approve ===
    const approveMutation = useMutation<ApiResponse<BookingEntry> | void, AxiosError<ErrorMessage>, void>({
        mutationFn: async () => {
            const token = localStorage.getItem("token");
            if (!token) throw new Error("No token found");
            console.log(`Sending approval request for booking ${booking.id}`);
            await axios.put(
                `${import.meta.env.VITE_APP_SERVER_URL || 'http://localhost:8080'}/booking/${booking.id}/approve`, null,
                { headers: { Authorization: `Bearer ${token}` } }
            );
        },
        onSuccess: () => {
            console.log("Booking approved:", booking.id);
            setSnackbarMessage("Yêu cầu đã được duyệt thành công!");
            setSnackbarSeverity("success");
            setOpenSnackbar(true);
            queryClient.invalidateQueries({ queryKey: ["adminBookings"] }); 
            if (onActionSuccess) onActionSuccess();
            setBackendError(null);
        },
        onError: (error) => {
            console.error("Approve failed:", error);
            const errMsg = error.response?.data?.message || error.message || "Lỗi khi duyệt";
            setBackendError({ message: errMsg });
            setSnackbarMessage(`Duyệt thất bại: ${errMsg}`);
            setSnackbarSeverity("error");
            setOpenSnackbar(true);
        },
    });

    // === Mutation cho Reject ===
    const rejectMutation = useMutation<ApiResponse<BookingEntry> | void, AxiosError<ErrorMessage>, RejectBookingRequest>({
        mutationFn: async (data: RejectBookingRequest) => {
            const token = localStorage.getItem("token");
            if (!token) throw new Error("No token found");
             console.log(`Sending rejection request for booking ${booking.id} with reason: ${data.reason}`);
            await axios.put(
                `${import.meta.env.VITE_APP_SERVER_URL || 'http://localhost:8080'}/booking/${booking.id}/reject`,
                data, 
                { headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' } }
            );
        },
         onSuccess: () => {
            console.log("Booking rejected:", booking.id);
            setSnackbarMessage("Yêu cầu đã được từ chối!");
            setSnackbarSeverity("warning");
            setOpenSnackbar(true);
            setRemarkValue("");
            queryClient.invalidateQueries({ queryKey: ["adminBookings"] }); 
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
    const handleApprove = () => {
        setBackendError(null);
        approveMutation.mutate();
    };

    const handleReject = () => {
        setBackendError(null);
        if (!remarkValue.trim() || remarkValue.length < 5) {
            setBackendError({ message: "Vui lòng nhập lý do từ chối (ít nhất 5 ký tự)." });
            return;
        }
        const rejectData: RejectBookingRequest = { reason: remarkValue.trim() };
        rejectMutation.mutate(rejectData);
    };

    const handleCloseSnackbar = (): void => { setOpenSnackbar(false); };

    // --- JSX ---
    // Chỉ hiển thị card nếu booking đang ở trạng thái chờ duyệt
     if (!booking || booking.status !== "PENDING_APPROVAL") {
         return null;
     }

    const displayStatus = getDisplayStatus(booking.status);

    return (
        <Slide direction="up" in={true} mountOnEnter unmountOnExit>
            <Paper elevation={3} sx={{ display: 'flex', flexDirection: { xs: 'column', md: 'row' }, alignItems: 'flex-start', justifyContent: 'space-between', p: { xs: 2, sm: 2.5 }, my: 2, width: '100%', maxWidth: '950px', borderLeft: 5, borderColor: 'warning.main' }}>
                 {/* Phần thông tin booking */}
                 <Box sx={{ flexGrow: 1, pr: { md: 3 }, mb: { xs: 2, md: 0 } }}>
                     <Typography variant="h6" component="div" sx={{ fontWeight: 600, mb: 1 }}>
                         Phòng: {booking.roomName || 'N/A'} - <Typography component="span" variant="body1">{booking.purpose}</Typography>
                     </Typography>
                     <Typography variant="body2" >Người đặt: <strong>{booking.userName}</strong></Typography>
                     <Typography variant="body2" >Ngày: <strong>{isoToDate(booking.plannedStartTime)}</strong></Typography>
                     <Typography variant="body2" >Thời gian: <strong>{`${isoToTime(booking.plannedStartTime)} - ${isoToTime(booking.plannedEndTime)}`}</strong></Typography>
                     <Typography variant="body2" >Yêu cầu lúc: {booking.createdAt ? `${isoToDate(booking.createdAt)}, ${isoToTime(booking.createdAt)}` : 'N/A'}</Typography>
                     <Typography variant="body2" >Trạng thái: <Chip label={displayStatus.text} color={displayStatus.color} size="small" /></Typography>
                     {booking.note && (<Typography variant="body2" sx={{mt: 1}}>Ghi chú: {booking.note}</Typography> )}
                     {booking.bookedEquipments && booking.bookedEquipments.length > 0 && (
                          <Typography variant="body2" component="div" sx={{mt: 1}}>
                              <strong>Thiết bị kèm theo:</strong> {booking.bookedEquipments.map(eq => <Chip size="small" sx={{ml: 0.5, mb: 0.5}} key={eq.itemId} label={`${eq.equipmentModelName}${eq.isDefaultEquipment ? ' (MĐ)' : ' (Thêm)'}`}/> )}
                          </Typography>
                      )}
                 </Box>

                 {/* Phần Actions: Approve/Reject */}
                 <Box sx={{ width: { xs: '100%', md: '40%' }, pl: { md: 3 }, borderLeft: { md: `1px solid` }, display: 'flex', flexDirection: 'column', gap: 1.5 }}>
                    <FormControl fullWidth >
                        <TextField
                            id={`reject-reason-${booking.id}`} // ID duy nhất
                            label="Lý do từ chối (Bắt buộc nếu từ chối)"
                            variant="outlined"
                            value={remarkValue}
                            onChange={(e: ChangeEvent<HTMLInputElement>) => setRemarkValue(e.target.value)}
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

                     {/* Nút Approve và Reject */}
                     <Box sx={{ display: 'flex', gap: 1.5, justifyContent: 'space-between', mt: 1 }}>
                          <Button
                             variant="contained" startIcon={<TaskAltIcon />} color="success" size="medium"
                             onClick={handleApprove}
                             disabled={approveMutation.isPending || rejectMutation.isPending}
                             sx={{ flexGrow: 1 }} // Chia đều không gian
                         >
                              {approveMutation.isPending ? <CircularProgress size={20} color="inherit"/> : "Duyệt"}
                         </Button>
                         <Button
                             variant="contained" startIcon={<HighlightOffIcon />} color="error" size="medium"
                             onClick={handleReject} // Gọi handler riêng
                             disabled={approveMutation.isPending || rejectMutation.isPending || !remarkValue.trim()} // Disable nếu đang xử lý hoặc chưa nhập lý do
                             sx={{ flexGrow: 1 }}
                         >
                             {rejectMutation.isPending ? <CircularProgress size={20} color="inherit"/> : "Từ chối"}
                         </Button>
                     </Box>
                 </Box>

                {/* Snackbar */}
                <Snackbar open={openSnackbar} autoHideDuration={3000} onClose={handleCloseSnackbar} anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}>
                     <Alert onClose={handleCloseSnackbar} severity={snackbarSeverity} variant="filled" sx={{ width: "100%" }}>
                         {snackbarMessage}
                     </Alert>
                </Snackbar>
            </Paper>
        </Slide>
    );
};

export default ApprovalCard;