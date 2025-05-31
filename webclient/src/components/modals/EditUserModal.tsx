import React, { JSX, ChangeEvent, FC, FormEvent, useEffect, useState } from "react";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import axios, { AxiosError } from "axios";
import {
    Button, Fade, FormControl, FormHelperText, InputLabel, MenuItem, Modal, Select, SelectChangeEvent, TextField, Typography, Box, CircularProgress, Alert
} from "@mui/material";
import "dayjs/locale/en-gb";

import ErrorComponent from "../Error";

// --- Component EditUserModal ---
const EditUserModal: FC<EditUserModalProps> = ({
    isOpen,
    setIsOpen,
    setOpenSnackbar,
    userData, // Dữ liệu người dùng hiện tại để edit
    onSuccessCallback,
}): JSX.Element => {

    // SỬA: State dùng kiểu gần với UserUpdateRequest và khởi tạo từ userData
    // Dùng Partial để chỉ lưu những gì thay đổi? Hoặc khởi tạo đầy đủ để dễ quản lý form hơn.
    // Chọn khởi tạo đầy đủ để dễ bind value cho form.
    const [formData, setFormData] = useState<Partial<UserUpdateRequest>>({});

    // Khởi tạo/Reset formData khi modal mở hoặc dữ liệu người dùng thay đổi
    useEffect(() => {
        if (isOpen && userData) {
            console.log("Initializing edit form with user data:", userData);
            setFormData({
                username: userData.username ?? "", // Dùng ?? để xử lý null/undefined
                fullName: userData.fullName ?? "",
                roleName: userData.roleName ?? "USER",
            });
        }
         setBackendError(null); // Reset lỗi khi mở
         setValidationError("");
    }, [userData, isOpen]); // Phụ thuộc


    // SỬA: Cấu trúc state lỗi chuẩn
    const [backendError, setBackendError] = useState<ErrorMessage | null>(null);
    const [validationError, setValidationError] = useState<string>(""); // Vẫn giữ validationError cho FE
    const queryClient = useQueryClient();

    // --- Mutation ---
    const mutation = useMutation<
        ApiResponse<UserData>,      // Kiểu response thành công
        AxiosError<ErrorMessage>, // Kiểu lỗi
        UserUpdateRequest        
    >({
        mutationFn: (updateData: UserUpdateRequest) => { 
            const token = localStorage.getItem("token");
            if (!token) throw new Error("No token found");
            console.log(`Sending user update request for ID ${userData.userId}:`, updateData);
            return axios.patch<ApiResponse<UserData>>( 
                `${import.meta.env.VITE_APP_SERVER_URL}/users/${userData.userId}`, 
                updateData, // Gửi đi payload đúng DTO Update
                { headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' } }
            );
        },
        onSuccess: (response) => {
            console.log("User updated successfully:", response.data.result);
            setOpenSnackbar(true);
            setIsOpen(false);
            queryClient.invalidateQueries({ queryKey: ["adminUsers"] }); // Refresh lại bảng
            if (onSuccessCallback) onSuccessCallback();
        },
        onError: (error) => {
            console.error("User update failed:", error);
             // SỬA: Gán lỗi đúng cấu trúc ErrorMessage
            setBackendError(error.response?.data || { message: error.message || "Lỗi không xác định" });
        },
    });

    // --- Handlers ---
    const handleInputChange = (e: ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
    };

    const handleSelectChange = (e: SelectChangeEvent<string | null>) => {
        const { name, value } = e.target;
        const finalValue = (name === 'facilityManagerId' && value === "") ? null : value;
        setFormData(prev => ({ ...prev, [name]: finalValue }));
    };

    // --- Submit ---
    const handleSubmit = (e: FormEvent<HTMLFormElement>): void => {
        e.preventDefault();
        setValidationError("");
        setBackendError(null);

        // --- Validation cơ bản ---
        if (!formData.fullName?.trim()) { setValidationError("Họ tên không được trống."); return; }
        if (!formData.roleName) { setValidationError("Vui lòng chọn vai trò."); return; }

        // SỬA: Chuẩn bị payload đúng kiểu UserUpdateRequest
        // Gửi các giá trị hiện tại trong state formData
        const submitData: UserUpdateRequest = {
            userId: userData.id,
            ...formData,
        };

        console.log("Submitting update data:", submitData);
        mutation.mutate(submitData);
    };

    // --- Cancel ---
    const handleCancel = (): void => {
        setIsOpen(false);
        // Reset lỗi khi hủy
        setBackendError(null);
        setValidationError("");
        // Không cần reset formData ở đây vì useEffect sẽ làm khi mở lại
    };

    // --- useEffect xử lý lỗi (Sửa cách truy cập thuộc tính lỗi) ---
     useEffect(() => {
         if (backendError?.status === 404) { // <<< Sửa cách truy cập
              // Kiểm tra message để biết lỗi 404 là của User hay không
             if (backendError.message?.includes("User")) { // Ví dụ kiểm tra message
                 setValidationError("Người quản lý với ID này không tồn tại."); // Thông báo lỗi cụ thể hơn
             } else {
                  setValidationError(backendError.message); // Hiển thị lỗi khác nếu có
             }
             // setTimeout(() => { setValidationError(""); }, 4000); // Có thể không cần tự xóa lỗi
         }
     }, [backendError]);

    // --- Render JSX (Cập nhật các input/select) ---
    return (
        <Modal open={isOpen} onClose={handleCancel}>
            <Fade in={isOpen}>
                <Box sx={{ position: 'absolute', top: '50%', left: '50%', transform: 'translate(-50%, -50%)', width: { xs: '90%', md: 600 }, bgcolor: 'background.paper', border: '1px solid #ccc', boxShadow: 24, p: 4, borderRadius: 2 }}>
                    <Typography variant="h5" component="h2" gutterBottom sx={{ textAlign: 'center', mb: 1 }}>
                        Chỉnh sửa thông tin tài khoản
                    </Typography>
                     <Typography variant="subtitle1" gutterBottom sx={{ textAlign: 'center', mb: 3 }}>
                        ID: {userData.userId} - {userData.fullName}
                     </Typography>
                    <Box component="form" onSubmit={handleSubmit} sx={{ display: 'flex', flexDirection: 'column', gap: 2.5 }}>

                         <TextField label="Tên tài khoản" name="username" value={formData.username ?? ''} onChange={handleInputChange} fullWidth size="small" />
                         <TextField label="Họ tên" name="fullName" value={formData.fullName ?? ''} onChange={handleInputChange} required fullWidth size="small" />
                         <FormControl fullWidth size="small" required>
                             <InputLabel id="role-select-label">Vai trò</InputLabel>
                             <Select
                                 labelId="role-select-label"
                                 label="Vai trò"
                                 name="roleName"
                                 value={formData.roleName}
                                 onChange={handleSelectChange}
                             >
                                 <MenuItem value="USER">Người dùng</MenuItem>
                                 <MenuItem value="ADMIN">Quản trị viên</MenuItem>
                                 <MenuItem value="TECHNICIAN">Kỹ thuật viên</MenuItem>
                                <MenuItem value="FACILITY_MANAGER">Quản lý cơ sở vật chất</MenuItem>
                             </Select>
                         </FormControl>

                        {/* Hiển thị lỗi */}
                         {validationError && (<Typography variant="body2" color="error" sx={{ mt: 1 }}>{validationError}</Typography>)}
                         {backendError && (<Alert severity="error" sx={{ mt: 1 }}>{backendError.message || 'Lỗi không xác định'}</Alert>)}

                        {/* Nút bấm */}
                        <Box sx={{ display: 'flex', justifyContent: 'flex-end', gap: 1, mt: 2 }}>
                            <Button variant="outlined" color="secondary" onClick={handleCancel} disabled={mutation.isPending}>Hủy bỏ</Button>
                            <Button type="submit" variant="contained" color="primary" disabled={mutation.isPending}>
                                {mutation.isPending ? <CircularProgress size={24} color="inherit" /> : "Cập nhật tài khoản"}
                            </Button>
                        </Box>
                    </Box>
                </Box>
            </Fade>
        </Modal>
    );
};

export default EditUserModal;