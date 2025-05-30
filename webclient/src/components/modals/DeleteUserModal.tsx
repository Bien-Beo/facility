import { Button, Fade, Modal, Typography, CircularProgress, Alert, Box } from "@mui/material";
import { JSX, FC, useState } from "react";
import ReportProblemOutlinedIcon from "@mui/icons-material/ReportProblemOutlined";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import axios, { AxiosError } from "axios";

const DeleteUserModal: FC<DeleteUserModalProps> = ({
  isOpen,
  setIsOpen,
  setOpenSnackbar,
  userData,
  onSuccessCallback
}): JSX.Element => {
  const [backendError, setBackendError] = useState<ErrorMessage | null>(null); // Sửa cấu trúc state lỗi
    const queryClient = useQueryClient();

    // --- Mutation ---
    const mutation = useMutation<
        void,
        AxiosError<ErrorMessage>,
        void 
    >({
        mutationFn: async () => { 
            const token = localStorage.getItem("token");
            if (!token) throw new Error("No token found");

            const userId = userData?.userId; 
            if (!userId) {
                console.error("Delete attempt with no user ID.");
                throw new AppException(ErrorCode.INVALID_INPUT, "Không tìm thấy ID của đối tượng cần xóa."); 
            }

            console.log("Sending user delete request for ID:", userId);
            await axios.delete(
                `${import.meta.env.VITE_APP_SERVER_URL}/users/${userId}`,
                { headers: { Authorization: `Bearer ${token}` } } 
            );
        },
        onSuccess: () => {
            console.log("User deleted successfully:", userData?.id);
            setOpenSnackbar(true); // Hiển thị thông báo
            setIsOpen(false); // Đóng modal
            queryClient.invalidateQueries({ queryKey: ["adminUsers"] }); // <<< Refresh lại bảng người dùng
            if (onSuccessCallback) onSuccessCallback(); // Gọi callback nếu có
            setBackendError(null); // Xóa lỗi cũ nếu thành công
        },
        onError: (error) => {
            console.error("User deletion failed:", error);
             // SỬA: Gán lỗi đúng cấu trúc
            setBackendError(error.response?.data || { message: error.message || "Lỗi không xác định khi xóa" });
        },
    });

    // --- Handlers ---
    const handleDeleteConfirm = () => { 
        setBackendError(null); // Reset lỗi trước khi thử lại
        mutation.mutate(); 
    };

    const handleCancel = () => {
        setIsOpen(false);
        setBackendError(null); // Reset lỗi khi đóng
    };

    // Không render nếu không mở hoặc không có dữ liệu
     if (!isOpen || !userData) {
         return null;
     }

return (
  <Modal open={isOpen} onClose={handleCancel}>
      <Fade in={isOpen}>
           <Box sx={{ position: 'absolute', top: '50%', left: '50%', transform: 'translate(-50%, -50%)', width: { xs: '90%', md: 450 }, bgcolor: 'background.paper', border: '1px solid #ccc', boxShadow: 24, p: 4, borderRadius: 2, textAlign: 'center' }}>
               <ReportProblemOutlinedIcon color="error" sx={{ fontSize: 60, mb: 2 }}/>
               <Typography variant="h5" component="h2" gutterBottom>
                   Xác nhận Xóa?
               </Typography>
               <Typography variant="body1" sx={{ mb: 3 }}>
                   Bạn có chắc chắn muốn xóa người dùng: 
                   <strong> {userData.username}</strong>  <br/>
                   Hành động này không thể hoàn tác.
               </Typography>

               {/* Hiển thị lỗi backend nếu có */}
               {backendError && (<Alert severity="error" sx={{ mt: 1, mb: 2, textAlign: 'left' }}>{backendError.message || 'Lỗi không xác định'}</Alert>)}

               <Box sx={{ display: 'flex', justifyContent: 'center', gap: 2, mt: 2 }}>
                    <Button variant="outlined" color="secondary" onClick={handleCancel} disabled={mutation.isPending}>
                        Hủy bỏ
                    </Button>
                    <Button
                        variant="contained"
                        color="error" // Nút xóa dùng màu error
                        onClick={handleDeleteConfirm} // Gọi đúng handler
                        disabled={mutation.isPending} // Disable khi đang xóa
                    >
                        {mutation.isPending ? <CircularProgress size={24} color="inherit" /> : "Xác nhận Xóa"}
                    </Button>
               </Box>
          </Box>
      </Fade>
  </Modal>
);
};

export default DeleteUserModal;