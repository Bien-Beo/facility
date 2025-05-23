import React, { ChangeEvent, JSX, FC, FormEvent, useState, useMemo, useCallback } from "react";
import axios, { AxiosError } from "axios";
import {
    Alert, Box, Button, FormControl, FormLabel, Slide, Snackbar, TextField, Typography, CircularProgress, Badge
} from "@mui/material";
import CancelOutlinedIcon from "@mui/icons-material/CancelOutlined";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import dayjs from "dayjs";
import isSameOrAfter from 'dayjs/plugin/isSameOrAfter';
import isSameOrBefore from 'dayjs/plugin/isSameOrBefore';

import isoToDate from "../../utils/isoToDate";
import isoToTime from "../../utils/isoToTime";
import { useAuth } from "../../hooks/useAuth";

dayjs.extend(isSameOrAfter);
dayjs.extend(isSameOrBefore);

const MyBookingCard: FC<MyBookingCardProps> = ({
    booking, 
    onCancelSuccess
}): JSX.Element => {

    const [remarkValue, setRemarkValue] = useState<string>(booking.cancellationReason || "");
    const [openSnackbar, setOpenSnackbar] = useState<boolean>(false);
    const [backendError, setBackendError] = useState<ErrorMessage | null>(null); 
    const [validationError, setValidationError] = useState<string>("");

    const auth = useAuth();
    const queryClient = useQueryClient();

    // --- Cancel Mutation ---
    const cancelMutation = useMutation<
        ApiResponse<BookingEntry>, 
        AxiosError<ErrorMessage>,
        CancelBookingRequest      
    >({
        mutationFn: async (data: CancelBookingRequest) => {
            const token = localStorage.getItem("token");
            if (!token) throw new Error("No token found");
            console.log(`Sending cancellation request for booking ${booking.id} with reason: ${data.reason}`);
            const response = await axios.post<ApiResponse<BookingEntry>>(
                `${import.meta.env.VITE_APP_SERVER_URL}/booking/${booking.id}/cancel`,
                data, 
                { headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' } }
            );
            return response.data;
        },
        onSuccess: (response) => {
            console.log("Booking cancelled successfully:", response.data.result);
            setOpenSnackbar(true);
            setRemarkValue("");
            // Invalidate query để cập nhật lại danh sách booking cha
             queryClient.invalidateQueries({ queryKey: ["myBookings", auth.user?.id] }); // <<< Key cần khớp với query lấy list
            // queryClient.invalidateQueries({ queryKey: ["roomBookings", booking.roomName] }); // Có thể cần nếu đang xem calendar
            if (onCancelSuccess) onCancelSuccess();
            setBackendError(null);
        },
        onError: (error) => {
            console.error("Booking cancellation failed:", error);
            setBackendError(error.response?.data || { message: error.message || "Lỗi khi hủy đặt chỗ" });
        },
    });

    // --- Submit Cancellation ---
    const handleCancelSubmit = (e: FormEvent<HTMLFormElement>): void => {
        e.preventDefault();
        setValidationError("");
        setBackendError(null);
        if (!remarkValue.trim() || remarkValue.length < 5) {
            setValidationError("Vui lòng nhập lý do hủy (ít nhất 5 ký tự).");
            return;
        }
        // payload CancelBookingRequest
        const cancelRequestData: CancelBookingRequest = { reason: remarkValue.trim() };
        cancelMutation.mutate(cancelRequestData);
    };

    // --- Xác định có cho phép hủy không ---
    const canCancel = useMemo(() => {
        const status = booking.status as BookingStatusType;
        // Cho phép hủy khi PENDING_APPROVAL hoặc CONFIRMED 
         if (!["PENDING_APPROVAL", "CONFIRMED"].includes(status)) {
            return false;
         }
         // Kiểm tra thời gian nếu đã CONFIRMED
         const hoursBefore = 1; // phải hủy trước 1 tiếng
         if (status === "CONFIRMED" && dayjs().isAfter(dayjs(booking.plannedStartTime).subtract(hoursBefore, 'hour'))) {
              console.log(`Cancellation for booking ${booking.id} is too late.`);
              return false; // Quá gần giờ
         }
         return true;
    }, [booking.status, booking.plannedStartTime]);

    // --- Hàm định dạng Status ---
    const getDisplayStatus = (status: BookingStatusType | string): string => {
         switch(status) {
            case 'PENDING_APPROVAL': return "Chờ duyệt";
            case "CONFIRMED": return "Đã duyệt";
            case "REJECTED": return "Bị từ chối";
            case "CANCELLED": return "Đã hủy";
            case "COMPLETED": return "Đã hoàn thành";
            case "IN_PROGRESS": return "Đang sử dụng";
            case "OVERDUE": return "Quá hạn";
            default: return status?.replace(/_/g, ' ') || 'Không xác định'; // Fallback
         }
     };

    // --- Style dựa trên Status ---
    const cardStyleClasses = useMemo(() => {
        const status = booking.status as BookingStatusType;
        let baseClasses = "justify-between items-start px-6 sm:px-10 py-6 sm:py-8 w-full h-full flex flex-col sm:flex-row mt-6 rounded-lg shadow-md border-0 border-l-[10px] border-solid";

        switch (status) {
            case "PENDING_APPROVAL": return `${baseClasses} bg-blue-50 border-blue-500`;
            case "CONFIRMED":
            case "IN_PROGRESS": return `${baseClasses} bg-green-50 border-green-600`;
            case "REJECTED":
            case "CANCELLED": return `${baseClasses} bg-red-50 border-red-600`;
            case "COMPLETED": return `${baseClasses} bg-gray-100 border-gray-400`;
            case "OVERDUE": return `${baseClasses} bg-orange-50 border-orange-500`;
            default: return `${baseClasses} bg-gray-50 border-gray-300`;
        }
    }, [booking.status]);

    const handleCloseSnackbar = (event?: React.SyntheticEvent | Event, reason?: string) => {
        // Tham số event và reason được cung cấp bởi Snackbar/Alert
        // Có thể dùng reason để kiểm tra xem có phải người dùng click ra ngoài không
        if (reason === 'clickaway') {
            return; // Không đóng snackbar nếu click ra ngoài (tùy chọn)
        }
        setOpenSnackbar(false); // <<< Đặt state để đóng Snackbar
    };

    const showCheckInButton = booking.status === "CONFIRMED" && !booking.actualCheckInTime;
    const showCheckOutButton = (booking.status === "IN_PROGRESS" || booking.status === "OVERDUE") && booking.actualCheckInTime && !booking.actualCheckOutTime;

    // --- Xử lý Check-in/Check-out ---
    const handleCheckIn = useCallback(async () => {
        try {
          const token = localStorage.getItem("token");
          if (!token) return Promise.reject(new Error("No token found"));
      
          // Chuyển đổi dấu phẩy thành dấu chấm
          return axios.put<ApiResponse<BookingEntry>>(
            `${import.meta.env.VITE_APP_SERVER_URL}/booking/${booking.id}/checkin`, 
            {}, // Nếu có body thì điền vào đây
            {
              headers: {
                Authorization: `Bearer ${token}`,
                'Content-Type': 'application/json',
              },
            }
          );
        } catch (error) {
          const err = error as AxiosError<ErrorMessage>;
          setBackendError(err.response?.data ?? { message: err.message || "Lỗi khi check-in" });
        }
      }, [booking.id, auth.user?.id]);      

      const handleCheckOut = useCallback(async () => {
        try {
          const token = localStorage.getItem("token");
          if (!token) return Promise.reject(new Error("No token found"));
      
          return axios.put<ApiResponse<BookingEntry>>(
            `${import.meta.env.VITE_APP_SERVER_URL}/booking/${booking.id}/checkout`, 
            {},
            {
              headers: {
                Authorization: `Bearer ${token}`,
                'Content-Type': 'application/json',
              },
            }
          );
        } catch (error) {
          const err = error as AxiosError<ErrorMessage>;
          setBackendError(err.response?.data ?? { message: err.message || "Lỗi khi check-out" });
        }
      }, [booking.id, auth.user?.id]);      

    // Sửa: Xử lý lỗi truy cập đúng thuộc tính
    if (backendError?.status && backendError.status !== 400) { // Ví dụ: Chỉ hiển thị lỗi nghiêm trọng hơn 400
        // Có thể render lỗi nhỏ trong card thay vì component ErrorComponent lớn
         return <Alert severity="error" sx={{mt: 2, width: '100%', maxWidth: '800px'}}>Lỗi tải thông tin đặt phòng: {backendError.message}</Alert>;
    }

    return (
        <Slide direction="up" in={true} mountOnEnter unmountOnExit>
            <div className={cardStyleClasses}>
                {/* SỬA: Phần hiển thị thông tin lấy từ object booking */}
                <div className="flex flex-col justify-center flex-grow pr-4 mb-4 sm:mb-0">
                    <Typography variant="h6" component="p" sx={{ marginBottom: 1, fontWeight: 600 }}>
                        {/* Dùng roomName và purpose */}
                        Phòng: {booking.roomName || 'N/A'} | {booking.purpose}
                    </Typography>
                    <Typography variant="body1" component="p">
                        <span className="font-bold">Người đặt:</span> {booking.userName}
                    </Typography>
                    <Typography variant="body1" component="p">
                        <span className="font-bold">Ngày:</span> {booking.plannedStartTime ? isoToDate(booking.plannedStartTime) : 'N/A'}
                    </Typography>
                    <Typography variant="body1" component="p">
                        <span className="font-bold">Thời gian:</span> {booking.plannedStartTime && booking.plannedEndTime ? `${isoToTime(booking.plannedStartTime)} - ${isoToTime(booking.plannedEndTime)}` : 'N/A'}
                    </Typography>
                    <Typography variant="body1" component="p">
                        <span className="font-bold">Yêu cầu lúc:</span> {booking.createdAt ? `${isoToDate(booking.createdAt)}, ${isoToTime(booking.createdAt)}` : 'N/A'}
                    </Typography>
                    <Typography variant="body1" component="p">
                        <span className="font-bold">Trạng thái:</span>{' '}
                        <strong className={`status-${booking.status?.toLowerCase()}`}>{getDisplayStatus(booking.status as BookingStatusType)}</strong>
                    </Typography>
    
                    {/* Hiển thị thông tin duyệt/từ chối/hủy */}
                    {booking.status === "CONFIRMED" && booking.approvedByUserName && (<Typography variant="body2" sx={{ color: 'success.main', fontStyle: 'italic' }}>Đã duyệt bởi: {booking.approvedByUserName}</Typography>)}
                    {booking.status === "REJECTED" && booking.approvedByUserName && ( <Typography variant="body2" color="error" sx={{ fontStyle: 'italic' }}>Đã từ chối bởi: {booking.approvedByUserName}</Typography> )}
                    {booking.status === "CANCELLED" && booking.cancelledByUserName && ( <Typography variant="body2" color="error" sx={{ fontStyle: 'italic' }}>Đã hủy bởi: {booking.cancelledByUserName}</Typography> )}
                    {booking.cancellationReason && (<Typography variant="body2"><span className="font-bold">Lý do hủy/từ chối:</span> {booking.cancellationReason}</Typography>)}
    
                    {/* Checkin/out */}
                    {!canCancel && ( // Phần checkin/out hiển thị ngoài "Hủy Booking"
                        <>
                            {booking.actualCheckOutTime && (<Typography variant="body2"><strong>Check-out:</strong> {`${isoToDate(booking.actualCheckOutTime)}, ${isoToTime(booking.actualCheckOutTime)}`}</Typography>)}
                            {booking.actualCheckInTime && (<Typography variant="body2"><strong>Check-in:</strong> {`${isoToDate(booking.actualCheckInTime)}, ${isoToTime(booking.actualCheckInTime)}`}</Typography>)}
                        </>
                    )}
    
                    {/* Note */}
                    {booking.note && booking.status !== "REJECTED" && ( <Typography variant="body1"><span className="font-bold">Ghi chú:</span> {booking.note}</Typography> )}
    
                    {/* Thiết bị đính kèm */}
                    {booking.bookedEquipments && booking.bookedEquipments.length > 0 && (
                        <Typography variant="body1" component="p" sx={{mt: 1}}>
                            <span className="font-bold">Thiết bị kèm theo:</span>{' '}
                            {booking.bookedEquipments.map(eq => `${eq.equipmentModelName}${eq.isDefaultEquipment ? '(Mặc định)' : '(Mượn thêm)'}`).join(', ')}
                        </Typography>
                    )}
                </div>
    
                {/* Phần Hủy Booking */}
                {canCancel && (
                    <Box component="form" onSubmit={handleCancelSubmit}
                         sx={{ width: { xs: '100%', sm: '35%', md: '30%' }, pl: { xs: 0, sm: 3 }, mt: { xs: 3, sm: 0 }, borderLeft: { sm: `1px solid ${'divider'}` }, display: 'flex', flexDirection: 'column', gap: 1.5 }}
                    >
                        <Typography variant="subtitle2" sx={{fontWeight: 'bold'}}>Yêu cầu hủy đặt chỗ:</Typography>
                        <TextField id="remark" label="Lý do hủy (*)" variant="outlined" value={remarkValue} onChange={(e: ChangeEvent<HTMLInputElement>) => setRemarkValue(e.target.value)} required fullWidth size="small" multiline rows={3} error={!!validationError || !!backendError} helperText={validationError} />
                        {backendError && (<Alert severity="error" size="small" sx={{mt: -0.5}}>{backendError.message}</Alert>)}
                        <Button
                            variant="contained" startIcon={<CancelOutlinedIcon />} color="error" size="medium"
                            sx={{ width: "100%", mt: 1 }} type="submit" disabled={cancelMutation.isPending}
                        >
                            {cancelMutation.isPending ? <CircularProgress size={20} color="inherit"/> : "Gửi Yêu cầu Hủy"}
                        </Button>
    
                        {/* Kiểm tra trạng thái để hiển thị nút nhận/trả phòng */}
                        {showCheckInButton && (
                            <Button variant="contained" color="primary" onClick={handleCheckIn}>
                                Nhận phòng
                            </Button>
                        )}
    
                        {showCheckOutButton && (
                            <Button variant="contained" color="secondary" onClick={handleCheckOut}>
                                Trả phòng
                            </Button>
                        )}
                    </Box>
                )}
    
                {/* Nếu không hiển thị phần hủy booking thì check-in/check-out sẽ ở ngoài */}
                {!canCancel && (
                    <>
                        {showCheckInButton && (
                            <Button variant="contained" color="primary" onClick={handleCheckIn}>
                                Nhận phòng
                            </Button>
                        )}
    
                        {showCheckOutButton && (
                            <Button variant="contained" color="secondary" onClick={handleCheckOut}>
                                Trả phòng
                            </Button>
                        )}
                    </>
                )}
    
                {/* Snackbar */}
                <Snackbar open={openSnackbar} autoHideDuration={3000} onClose={handleCloseSnackbar} anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}>
                    <Alert onClose={handleCloseSnackbar} severity="success" variant="filled" sx={{ width: "100%" }}>
                        Yêu cầu hủy đã được gửi thành công!
                    </Alert>
                </Snackbar>
            </div>
        </Slide>
    );    
};

export default MyBookingCard;