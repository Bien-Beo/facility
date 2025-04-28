// import { JSX, ChangeEvent, FC, FormEvent, useState } from "react";
// import {
//   Button,
//   Fade,
//   FormControl,
//   FormLabel,
//   Modal,
//   TextField,
//   Typography,
// } from "@mui/material";
// import ReportProblemOutlinedIcon from "@mui/icons-material/ReportProblemOutlined";
// import { useMutation } from "@tanstack/react-query";
// import axios from "axios";

// import ErrorComponent from "../Error";

// const AdminBookingRejectModal: FC<AdminBookingsModalProps> = ({
//   isOpen,
//   setIsOpen,
//   setOpenSnackbar,
//   slug,
// }): JSX.Element => {
//   const [remarkValue, setRemarkValue] = useState<string>("");
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

//   const handleSubmit = (e: FormEvent<HTMLFormElement>): void => {
//     e.preventDefault();
//     mutation.mutate({ slug: slug, approved: false, remark: remarkValue });
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
//               Do you really want to reject this booking?
//             </Typography>
//             <Typography variant="h6" component="h2">
//               This process cannot be undone!
//             </Typography>
//           </div>
//           <form
//             autoComplete="off"
//             className="w-full"
//             onSubmit={(e) => handleSubmit(e)}
//           >
//             <FormControl className="w-full flex flex-col gap-4">
//               <div className="w-full flex flex-col gap-2">
//                 <FormLabel htmlFor="remark">State the reason:</FormLabel>
//                 <TextField
//                   id="remark"
//                   label="Remark"
//                   variant="outlined"
//                   className="w-full"
//                   value={remarkValue}
//                   onChange={(e: ChangeEvent<HTMLInputElement>) =>
//                     setRemarkValue(e.target.value)
//                   }
//                   required
//                   size="small"
//                   multiline
//                 />
//               </div>

//               <div className="w-full flex gap-4 justify-center">
//                 <Button
//                   variant="contained"
//                   color="primary"
//                   sx={{ paddingX: "2em", height: "45px" }}
//                   size="large"
//                   onClick={handleCancel}
//                 >
//                   Cancel
//                 </Button>
//                 <Button
//                   variant="contained"
//                   color="error"
//                   sx={{ paddingX: "2em", height: "45px" }}
//                   size="large"
//                   type="submit"
//                 >
//                   Reject
//                 </Button>
//               </div>
//             </FormControl>
//           </form>
//         </div>
//       </Fade>
//     </Modal>
//   );
// };

// export default AdminBookingRejectModal;

import React, { JSX, FC, FormEvent, useState, useCallback, useEffect } from "react"; 
import { Box, Button, CircularProgress, Fade, Modal, Typography, Alert, TextField, FormControl, FormLabel } from "@mui/material";
import ReportProblemOutlinedIcon from "@mui/icons-material/ReportProblemOutlined";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import axios, { AxiosError } from "axios";

// --- Component ---
const AdminBookingRejectModal: FC<AdminBookingRejectModalProps> = ({
    isOpen,
    setIsOpen,
    setOpenSnackbar, // Có thể đổi tên thành onRejectSuccess
    bookingId,
    bookingData,
    onSuccessCallback,
}): JSX.Element => {

    const [remarkValue, setRemarkValue] = useState<string>(""); // State cho lý do từ chối
    const [backendError, setBackendError] = useState<ErrorMessage | null>(null); // Sửa state lỗi
    const [validationError, setValidationError] = useState<string>(""); // Lỗi validation frontend
    const queryClient = useQueryClient();

     // Reset remark khi modal mở
     useEffect(() => {
         if (isOpen) {
             setRemarkValue("");
             setValidationError("");
             setBackendError(null);
         }
     }, [isOpen]);

    // --- Mutation (Sửa lại endpoint, method, payload) ---
    const mutation = useMutation<
        ApiResponse<BookingEntry> | void, // Kiểu trả về có thể là Booking đã update hoặc void
        AxiosError<ErrorMessage>,
        RejectBookingRequest // <<< SỬA: Input type là DTO Reject { reason: string }
    >({
        mutationFn: async (data: RejectBookingRequest) => { // <<< SỬA: Input type
            const token = localStorage.getItem("token");
            if (!token) throw new Error("No token found");
            if (!bookingId) throw new Error("Booking ID missing for rejection");

            console.log(`Sending rejection request for booking ${bookingId} with reason: ${data.reason}`);
            // SỬA: Endpoint và method (thường là PUT hoặc POST), gửi reason trong body
            await axios.put( // <<< Dùng PUT (hoặc POST tùy backend API)
                `${import.meta.env.VITE_APP_SERVER_URL || 'http://localhost:8080'}/booking/${bookingId}/reject`, // <<< URL đúng
                data, // <<< Gửi payload { reason: "..." }
                { headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' } }
            );
             // API reject có thể không trả về body
        },
        onSuccess: () => {
            console.log("Booking rejected successfully:", bookingId);
            setOpenSnackbar(true);
            setIsOpen(false);
            // Invalidate các query liên quan
            queryClient.invalidateQueries({ queryKey: ["adminBookings"] });
            queryClient.invalidateQueries({ queryKey: ["myBookings"] });
            queryClient.invalidateQueries({ queryKey: ["roomBookings", bookingData?.roomName] });
            if (onSuccessCallback) onSuccessCallback();
            // Không cần reset remark ở đây vì handleCancel sẽ làm khi đóng
        },
        onError: (error) => {
            console.error("Booking rejection failed:", error);
            setBackendError(error.response?.data || { message: error.message || "Lỗi khi từ chối đặt phòng" });
        },
    });

    // --- Handlers ---
    const handleSubmit = (e: FormEvent<HTMLFormElement>): void => {
        e.preventDefault();
        setValidationError("");
        setBackendError(null);

        // Bắt buộc nhập lý do từ chối
        if (!remarkValue.trim() || remarkValue.length < 5) {
            setValidationError("Vui lòng nhập lý do từ chối (ít nhất 5 ký tự).");
            return;
        }

        // SỬA: Tạo đúng payload RejectBookingRequest
        const submitData: RejectBookingRequest = {
            reason: remarkValue.trim(),
        };

        mutation.mutate(submitData);
    };

    const handleCancel = (): void => {
        setIsOpen(false);
        setRemarkValue(""); // Reset lý do khi hủy
        setBackendError(null);
        setValidationError("");
    };


    // --- Render JSX (Sửa lại UI và Text) ---
    return (
        <Modal open={isOpen} onClose={handleCancel}>
            <Fade in={isOpen}>
                 {/* SỬA: Dùng Box và sx */}
                 <Box sx={{
                     position: 'absolute', top: '50%', left: '50%', transform: 'translate(-50%, -50%)',
                     width: { xs: '90%', sm: 500 },
                     bgcolor: 'background.paper', borderRadius: 2, boxShadow: 24, p: 4,
                     display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 2
                 }}>
                    <ReportProblemOutlinedIcon color="error" sx={{ fontSize: 60 }} />
                    <Typography variant="h5" component="h2" sx={{ fontWeight: 'bold' }}>
                        Xác nhận Từ chối?
                    </Typography>
                    <Box sx={{ my: 1, textAlign: 'center', width: '100%' }}>
                        <Typography variant="body1">
                             Bạn có chắc chắn muốn từ chối yêu cầu đặt
                             {bookingData?.roomName ? ` phòng ${bookingData.roomName}` : ' thiết bị'}
                             {bookingData?.userName ? ` của ${bookingData.userName}` : ''}?
                         </Typography>
                         {/* Bỏ dòng "không thể hoàn tác" cho từ chối? */}
                    </Box>

                    {/* Form nhập lý do */}
                     <Box component="form" onSubmit={handleSubmit} sx={{ width: '100%', mt: 1 }}>
                        <FormControl fullWidth>
                             {/* Sửa Label */}
                            <FormLabel htmlFor="remark" sx={{ mb: 1, fontWeight: 'medium' }}>Lý do từ chối (*):</FormLabel>
                            <TextField
                                id="remark"
                                placeholder="Nhập lý do từ chối..." // Dùng placeholder
                                variant="outlined"
                                value={remarkValue}
                                onChange={(e: ChangeEvent<HTMLInputElement>) => setRemarkValue(e.target.value)}
                                required
                                fullWidth
                                size="small"
                                multiline
                                rows={3}
                                error={!!validationError || !!backendError} // Highlight khi có lỗi
                                helperText={validationError} // Hiển thị lỗi validation frontend
                            />
                        </FormControl>

                         {/* Hiển thị lỗi backend */}
                         {backendError && (<Alert severity="error" sx={{ width: '100%', mt: 2 }}>{backendError.message}</Alert>)}

                        {/* Nút bấm */}
                        <Box sx={{ width: '100%', display: 'flex', gap: 2, justifyContent: 'center', mt: 3 }}>
                            <Button
                                variant="outlined" color="secondary"
                                sx={{ flexGrow: 1, py: 1.2 }} size="large"
                                onClick={handleCancel} disabled={mutation.isPending}
                            >
                                Hủy bỏ
                            </Button>
                            <Button
                                variant="contained" color="error" // <<< Dùng màu Error cho Reject
                                sx={{ flexGrow: 1, py: 1.2 }} size="large"
                                type="submit" disabled={mutation.isPending}
                            >
                                {mutation.isPending ? <CircularProgress size={24} color="inherit" /> : "Từ chối Yêu cầu"}
                            </Button>
                        </Box>
                     </Box>

                </Box>
            </Fade>
        </Modal>
    );
};

export default AdminBookingRejectModal;