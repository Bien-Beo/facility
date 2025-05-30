import React, { JSX, ChangeEvent, FC, FormEvent, useState } from "react";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import axios, { AxiosError } from "axios";
import {
    Button, Fade, FormControl, InputLabel, MenuItem, Modal, Select, SelectChangeEvent, TextField, Typography, Box, CircularProgress, Alert
} from "@mui/material";
import "dayjs/locale/en-gb";

const AddAccountModal: FC<AddAccountModalProps> = ({
    isOpen,
    setIsOpen,
    setOpenSnackbar
}): JSX.Element => {

    const [formData, setFormData] = useState<UserCreationRequest>({
        userId: "",
        username: "",
        fullName: null,
        email: "",
        roleName: "USER"
    });

    const [backendError, setBackendError] = useState<ErrorMessage | null>(null);
    const [validationError, setValidationError] = useState<string>(""); // Cho lỗi validation frontend

    const queryClient = useQueryClient(); // Để refresh lại danh sách sau khi thêm

    // --- Mutation ---
    const mutation = useMutation<
        ApiResponse<UserData>, 
        AxiosError<ErrorMessage>,
        UserCreationRequest
    >({
        mutationFn: async (data: UserCreationRequest): Promise<ApiResponse<UserData>> => {
            const token = localStorage.getItem("token");
            console.log("Sending user creation request:", data);

            const response = await axios.post<ApiResponse<UserData>>(
				`${import.meta.env.VITE_APP_SERVER_URL}/users`,
				data,
				{ headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' } }
			);

			// Kiểm tra lại response trước khi trả về 
			if (response.data?.code !== 0 || !response.data?.result) {
				throw new Error(response.data?.message || "Invalid response data after creating user.");
			}

			// Trả về phần data của response, có kiểu là ApiResponse<UserData>
			return response.data;
        },
        onSuccess: (responseData: ApiResponse<UserData>) => { // responseData giờ có kiểu ApiResponse<UserData>
			console.log("User created successfully:", responseData.result); // Truy cập dữ liệu người dùng qua .result
			setOpenSnackbar(true);
			setIsOpen(false);
			queryClient.invalidateQueries({ queryKey: ["adminUsers"] }); // Key khớp với query lấy danh sách người dùng
			handleCancel(); // Reset form
		},
		onError: (error) => {
			console.error("User creation failed:", error);
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
        // Giá trị rỗng ("") từ Select sẽ được dùng cho building/type,
        // còn "" cho manager sẽ được chuyển thành null khi submit nếu cần
        setFormData(prev => ({ ...prev, [name]: value }));
    };

    // --- Submit ---
    const handleSubmit = (e: FormEvent<HTMLFormElement>): void => {
        e.preventDefault();
        setValidationError("");
        setBackendError(null);

        // --- Validation cơ bản ---
        if (!(formData.fullName?.trim())) { setValidationError("Họ tên không được trống."); return; }
        if (!formData.email.trim()) { setValidationError("Email không được trống."); return; }
        if (!formData.roleName) { setValidationError("Vui lòng chọn vai trò."); return; }

        // Chuẩn bị dữ liệu gửi đi khớp với UserCreationRequest
        const submitData: UserCreationRequest = {
            ...formData,
            // Không cần chuyển đổi gì thêm vì đã khớp với DTO
        };
        console.log("Submitting data:", submitData);
        mutation.mutate(submitData);
    };

    // --- Cancel ---
    const handleCancel = (): void => {
        setIsOpen(false);
        // Reset form về trạng thái ban đầu
        setFormData({
            userId: "", username: "", fullName: null, email: "", roleName: "USER"
        });
        setValidationError("");
        setBackendError(null);
    };

    // --- Render JSX ---
    return (
        <Modal open={isOpen} onClose={handleCancel} >
            <Fade in={isOpen}>
                 <Box sx={{ position: 'absolute', top: '50%', left: '50%', transform: 'translate(-50%, -50%)', width: { xs: '90%', md: 600 }, bgcolor: 'background.paper', border: '1px solid #ccc', boxShadow: 24, p: 4, borderRadius: 2 }}>
                    <Typography variant="h5" component="h2" gutterBottom sx={{ textAlign: 'center', mb: 3 }}>
                        Thêm Tài Khoản Mới
                    </Typography>
                    <Box component="form" onSubmit={handleSubmit} sx={{ display: 'flex', flexDirection: 'column', gap: 2.5 }}>

                        <TextField label="Mã số" name="userId" value={formData.userId} onChange={handleInputChange} required fullWidth size="small" />
                         <TextField label="Tên tài khoản" name="username" value={formData.username} onChange={handleInputChange} required fullWidth size="small" />
                         <TextField label="Họ tên" name="fullName" value={formData.fullName} onChange={handleInputChange} fullWidth size="small" />
                         <TextField label="Email" name="email" value={formData.email} onChange={handleInputChange} required fullWidth size="small" />
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
                                {mutation.isPending ? <CircularProgress size={24} color="inherit" /> : "Thêm Tài Khoản"}
                            </Button>
                        </Box>
                    </Box>
                </Box>
            </Fade>
        </Modal>
    );
};

export default AddAccountModal;