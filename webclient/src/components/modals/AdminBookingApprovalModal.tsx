// import { JSX, FC, useState } from "react";
// import { Button, Fade, Modal, Typography } from "@mui/material";
// import ReportProblemOutlinedIcon from "@mui/icons-material/ReportProblemOutlined";
// import { useMutation } from "@tanstack/react-query";
// import axios from "axios";

// import ErrorComponent from "../Error";

// const AdminBookingApprovalModal: FC<AdminBookingsModalProps> = ({
//   isOpen,
//   setIsOpen,
//   setOpenSnackbar,
//   slug,
// }): JSX.Element => {
//   const [error, setError] = useState<ErrorMessage>({
//       status: null,
//       message: "",
//   });

//   const mutation = useMutation({
//     mutationFn: (data: ApprovalType) =>
//       axios.post(
//         `${import.meta.env.VITE_APP_SERVER_URL}/admin/approval`,
//         data,
//         {
//           withCredentials: true,
//         }
//       ),
//     onSuccess: () => {
//       setIsOpen(false);
//       setOpenSnackbar(true);
//     },
//     onError: (error) => {
//       setError(error.response!.data as ErrorMessage);
//       console.log(error);
//     },
//   });

//   const handleSubmit = (): void => {
//     mutation.mutate({ slug: slug, approved: true });
//   };

//   const handleCancel = (): void => {
//     setIsOpen(false);
//   };

//   if (error.error.status) {
//     return (
//       <ErrorComponent
//         status={error.error.status!}
//         message={error.error.message}
//       />
//     );
//   }

//   return (
//     <Modal
//       open={isOpen}
//       onClose={() => {
//         setIsOpen(false);
//       }}
//     >
//       <Fade in={isOpen}>
//         <div className="bg-bgPrimary w-full max-w-[500px] px-10 py-10 absolute left-[50%] top-[50%] -translate-x-[50%] -translate-y-[50%] rounded-md flex flex-col gap-6 shadow-cardHover items-center justify-center">
//           <ReportProblemOutlinedIcon
//             color="error"
//             sx={{ width: "100px", height: "100px" }}
//           />
//           <Typography variant="h4" component="h2">
//             Are you sure?
//           </Typography>
//           <div className="w-full flex flex-col items-center justify-center">
//             <Typography variant="h6" component="h2">
//               Do you really want to approve this booking?
//             </Typography>
//             <Typography variant="h6" component="h2">
//               This process cannot be undone!
//             </Typography>
//           </div>
//           <div className="w-full flex gap-4 justify-center">
//             <Button
//               variant="contained"
//               color="success"
//               sx={{ paddingX: "2em", height: "45px" }}
//               size="large"
//               onClick={handleSubmit}
//             >
//               Approve
//             </Button>
//             <Button
//               variant="contained"
//               color="primary"
//               sx={{ paddingX: "2em", height: "45px" }}
//               size="large"
//               onClick={handleCancel}
//             >
//               Cancel
//             </Button>
//           </div>
//         </div>
//       </Fade>
//     </Modal>
//   );
// };

// export default AdminBookingApprovalModal;

import React, { JSX, FC, useState, useCallback } from "react";
import { Box, Button, CircularProgress, Fade, Modal, Typography, Alert, Icon } from "@mui/material";
import ReportProblemOutlinedIcon from "@mui/icons-material/ReportProblemOutlined"; 
import CheckCircleOutlineIcon from '@mui/icons-material/CheckCircleOutline'; 
import { useMutation, useQueryClient } from "@tanstack/react-query";
import axios, { AxiosError } from "axios";

import ErrorComponent from "../Error";

// --- Component ---
const AdminBookingApprovalModal: FC<AdminBookingApprovalModalProps> = ({
    isOpen,
    setIsOpen, // Có thể đổi thành onClose nếu muốn
    setOpenSnackbar, // Có thể đổi thành onApproveSuccess
    bookingId, // <<< Nhận bookingId
    bookingData, // <<< Nhận bookingData (optional)
    onSuccessCallback,
}): JSX.Element => {

    // Sửa: State lỗi chuẩn
    const [backendError, setBackendError] = useState<ErrorMessage | null>(null);
    const queryClient = useQueryClient();

    // --- Mutation (Sửa lại endpoint, method, payload) ---
    const mutation = useMutation<
        ApiResponse<BookingEntry> | void, // Kiểu trả về có thể là Booking đã update hoặc void
        AxiosError<ErrorMessage>,
        void // <<< Không cần biến đầu vào
    >({
        mutationFn: async () => { // <<< Không cần tham số data
            const token = localStorage.getItem("token");
            if (!token) throw new Error("No token found");
            if (!bookingId) throw new Error("Booking ID missing for approval"); // Kiểm tra bookingId

            console.log("Sending approval request for booking:", bookingId);
            // SỬA: Endpoint và method (thường là PUT hoặc POST)
            // Không cần gửi body dữ liệu cho hành động approve này
            await axios.put( // <<< Dùng PUT (hoặc POST tùy thiết kế API)
                `${import.meta.env.VITE_APP_SERVER_URL || 'http://localhost:8080'}/bookings/${bookingId}/approve`, // <<< URL đúng
                null, // <<< Không cần body
                { headers: { Authorization: `Bearer ${token}` } }
            );
             // Có thể không cần return gì nếu API trả về 204
        },
        onSuccess: () => {
            console.log("Booking approved successfully:", bookingId);
            setOpenSnackbar(true); // Hiện thông báo thành công ở component cha
            setIsOpen(false); // Đóng modal
            // Invalidate các query liên quan để cập nhật UI
            queryClient.invalidateQueries({ queryKey: ["adminBookings"] }); // Key của bảng admin
            queryClient.invalidateQueries({ queryKey: ["myBookings"] });    // Key của user (nếu cần)
            queryClient.invalidateQueries({ queryKey: ["roomBookings", bookingData?.roomName] }); // Key của calendar
            if (onSuccessCallback) onSuccessCallback(); // Gọi callback nếu có
            setBackendError(null);
        },
        onError: (error) => {
            console.error("Booking approval failed:", error);
            setBackendError(error.response?.data || { message: error.message || "Lỗi khi duyệt đặt phòng" });
        },
    });

    // --- Handlers ---
    const handleSubmit = () => { // Đổi tên hoặc giữ nguyên
        setBackendError(null); // Reset lỗi
        mutation.mutate(); // <<< SỬA: Gọi không cần tham số
    };

    const handleCancel = () => {
        setIsOpen(false);
        setBackendError(null);
    };

    // Sửa: Xử lý lỗi truy cập đúng thuộc tính
    // Hiển thị lỗi bên trong modal thay vì component riêng
    // if (backendError?.status) { ... }

    // --- Render JSX (Sửa lại UI và Text) ---
    return (
        <Modal open={isOpen} onClose={handleCancel}>
            <Fade in={isOpen}>
                {/* SỬA: Dùng Box và sx */}
                <Box sx={{
                    position: 'absolute', top: '50%', left: '50%', transform: 'translate(-50%, -50%)',
                    width: { xs: '90%', sm: 450 }, // Responsive width
                    bgcolor: 'background.paper', borderRadius: 2, boxShadow: 24, p: 4,
                    display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 2
                }}>
                    {/* Có thể dùng icon phù hợp hơn cho duyệt */}
                    {/* <CheckCircleOutlineIcon color="success" sx={{ fontSize: 60 }} /> */}
                    <ReportProblemOutlinedIcon color="warning" sx={{ fontSize: 60 }} /> {/* Hoặc giữ icon warning */}

                    <Typography variant="h5" component="h2" sx={{ fontWeight: 'bold' }}>
                        Xác nhận Duyệt
                    </Typography>
                    <Box sx={{ my: 1, textAlign: 'center' }}>
                        <Typography variant="body1">
                             {/* Hiển thị thông tin booking nếu có */}
                              Bạn có chắc chắn muốn duyệt yêu cầu đặt
                              {bookingData?.roomName ? ` phòng ${bookingData.roomName}` : ' thiết bị'}
                              {bookingData?.userName ? ` của ${bookingData.userName}` : ''}?
                         </Typography>
                        {/* Thông báo về hậu quả có thể không cần thiết cho việc duyệt */}
                        {/* <Typography variant="body1" sx={{ mt: 1, fontWeight:'medium' }}>
                            Hành động này không thể hoàn tác!
                        </Typography> */}
                    </Box>

                    {/* Hiển thị lỗi backend */}
                     {backendError && (<Alert severity="error" sx={{ width: '100%', mt: 1 }}>{backendError.message || 'Lỗi không xác định'}</Alert>)}

                    {/* Các nút bấm */}
                    <Box sx={{ width: '100%', display: 'flex', gap: 2, justifyContent: 'center', mt: 2 }}>
                         <Button
                            variant="outlined" // <<< Dùng outlined cho Cancel
                            color="secondary"
                            sx={{ flexGrow: 1, py: 1.2 }}
                            size="large"
                            onClick={handleCancel}
                            disabled={mutation.isPending} // <<< Dùng isPending
                        >
                            Hủy bỏ
                        </Button>
                         <Button
                            variant="contained"
                            color="success" // <<< Dùng màu success cho Approve
                            sx={{ flexGrow: 1, py: 1.2 }}
                            size="large"
                            onClick={handleSubmit}
                            disabled={mutation.isPending}
                        >
                            {mutation.isPending ? <CircularProgress size={24} color="inherit" /> : "Duyệt"}
                        </Button>
                    </Box>
                </Box>
            </Fade>
        </Modal>
    );
};

export default AdminBookingApprovalModal;