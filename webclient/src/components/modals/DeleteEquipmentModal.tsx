import { Button, Fade, Modal, Typography, CircularProgress, Alert, Box } from "@mui/material";
import { JSX, FC, useState } from "react";
import ReportProblemOutlinedIcon from "@mui/icons-material/ReportProblemOutlined";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import axios, { AxiosError } from "axios";

const DeleteEquipmentModal: FC<DeleteEquipmentModalProps> = ({
  isOpen,
  setIsOpen,
  setOpenSnackbar,
  equipmentData,
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

            const equipmentId = equipmentData?.id; 
            if (!equipmentId) {
                console.error("Delete attempt with no equipment ID.");
                throw new AppException(ErrorCode.INVALID_INPUT, "Không tìm thấy ID của đối tượng cần xóa."); 
            }

            console.log("Sending equipment delete request for ID:", equipmentId);
            await axios.delete(
                `${import.meta.env.VITE_APP_SERVER_URL}/equipments/${equipmentId}`,
                { headers: { Authorization: `Bearer ${token}` } } 
            );
        },
        onSuccess: () => {
            console.log("Equipment deleted successfully:", equipmentData?.id); // <<< Cập nhật log cho thiết bị
            setOpenSnackbar(true); // Hiển thị thông báo
            setIsOpen(false); // Đóng modal
            queryClient.invalidateQueries({ queryKey: ["managementEquipments"] }); // <<< Refresh lại bảng thiết bị
            if (onSuccessCallback) onSuccessCallback(); // Gọi callback nếu có
            setBackendError(null); // Xóa lỗi cũ nếu thành công
        },
        onError: (error) => {
            console.error("Equipment deletion failed:", error); // <<< Cập nhật log cho thiết bị
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
     if (!isOpen || !equipmentData) {
         return null;
     }

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
//               Do you really want to delete this facility?
//             </Typography>
//             <Typography variant="h6" component="h2">
//               This process cannot be undone!
//             </Typography>
//           </div>
//           <div className="w-full flex gap-4 justify-center">
//             <Button
//               variant="contained"
//               color="primary"
//               sx={{ paddingX: "2em", height: "45px" }}
//               size="large"
//               onClick={handleCancel}
//             >
//               Cancel
//             </Button>
//             <Button
//               variant="contained"
//               color="error"
//               sx={{ paddingX: "2em", height: "45px" }}
//               size="large"
//               onClick={handleSubmit}
//             >
//               Delete
//             </Button>
//           </div>
//         </div>
//       </Fade>
//     </Modal>
//   );
// };

return (
  <Modal open={isOpen} onClose={handleCancel}>
      <Fade in={isOpen}>
           <Box sx={{ position: 'absolute', top: '50%', left: '50%', transform: 'translate(-50%, -50%)', width: { xs: '90%', md: 450 }, bgcolor: 'background.paper', border: '1px solid #ccc', boxShadow: 24, p: 4, borderRadius: 2, textAlign: 'center' }}>
               <ReportProblemOutlinedIcon color="error" sx={{ fontSize: 60, mb: 2 }}/>
               <Typography variant="h5" component="h2" gutterBottom>
                   Xác nhận Xóa?
               </Typography>
               <Typography variant="body1" sx={{ mb: 3 }}>
                   Bạn có chắc chắn muốn xóa thiết bị: 
                    {/* Hiển thị tên thiết bị từ equipmentData */}
                   <strong> {equipmentData.modelName}</strong>  <br/>
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

export default DeleteEquipmentModal;