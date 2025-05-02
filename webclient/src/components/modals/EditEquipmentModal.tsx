import React, { JSX, ChangeEvent, FC, FormEvent, useEffect, useState } from "react";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import axios, { AxiosError } from "axios";
import {
    Button, Fade, FormControl, FormHelperText, InputLabel, MenuItem, Modal, Select, SelectChangeEvent, TextField, Typography, Box, CircularProgress, Alert
} from "@mui/material";
import "dayjs/locale/en-gb";

import ErrorComponent from "../Error";

const EditEquipmentModal: FC<EditEquipmentModalProps> = ({
    isOpen,
    setIsOpen,
    setOpenSnackbar,
    equipmentData, 
    defaultRoom,
    onSuccessCallback,
}): JSX.Element => {

    const [formData, setFormData] = useState<Partial<EquipmentItemUpdateRequest>>({});

    // Khởi tạo/Reset formData khi modal mở hoặc dữ liệu phòng thay đổi
    useEffect(() => {
        if (isOpen && equipmentData) {
            console.log("Initializing edit form with equipment data:", equipmentData);
            setFormData({
                assetTag: equipmentData.assetTag ?? "",
                defaultRoomId: equipmentData.defaultRoomId ?? "",   
                notes: equipmentData.notes ?? "",
                status: equipmentData.status as EquipmentStatusType, // <<< Lấy status
            });
        }
         setBackendError(null); // Reset lỗi khi mở
         setValidationError("");
    }, [isOpen, equipmentData]); // Phụ thuộc


    // SỬA: Cấu trúc state lỗi chuẩn
    const [backendError, setBackendError] = useState<ErrorMessage | null>(null);
    const [validationError, setValidationError] = useState<string>(""); // Vẫn giữ validationError cho FE
    const queryClient = useQueryClient();

    // --- Mutation ---
    const mutation = useMutation<
        ApiResponse<EquipmentItemData>,      // Kiểu response thành công
        AxiosError<ErrorMessage>, // Kiểu lỗi
        EquipmentItemUpdateRequest         // <<< SỬA: Kiểu biến là DTO Update
    >({
        mutationFn: (updateData: EquipmentItemUpdateRequest) => { // <<< SỬA: Kiểu biến
            const token = localStorage.getItem("token");
            if (!token) throw new Error("No token found");
            console.log(`Sending equipment update request for ID ${equipmentData.id}:`, updateData);
            // SỬA: Endpoint là PUT hoặc PATCH đến /admin/equipment/{id}
            return axios.patch<ApiResponse<EquipmentItemData>>( 
                `${import.meta.env.VITE_APP_SERVER_URL}/equipments/${equipmentData.id}`, // <<< Thêm ID vào URL
                updateData, // Gửi đi payload đúng DTO Update
                { headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' } }
            );
        },
        onSuccess: (response) => {
            console.log("Equipment updated successfully:", response.data.result); // <<< Cập nhật log cho thiết bị
            setOpenSnackbar(true);
            setIsOpen(false);
            queryClient.invalidateQueries({ queryKey: ["managementEquipment"] }); // Refresh lại bảng
            if (onSuccessCallback) onSuccessCallback();
        },
        onError: (error) => {
            console.error("Equipment update failed:", error); // <<< Cập nhật log cho lỗi thiết bị
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

    const handleStatusChange = (e: SelectChangeEvent<string>) => {
        setFormData(prev => ({ ...prev, status: e.target.value as EquipmentStatusType })); // <<< Sửa kiểu dữ liệu cho status
    };

    // --- Submit ---
    const handleSubmit = (e: FormEvent<HTMLFormElement>): void => {
        e.preventDefault();
        setValidationError("");
        setBackendError(null);

        // --- Validation cơ bản ---
        if (!formData.assetTag?.trim()) { setValidationError("Mã tài sản không được trống."); return; }
        if (!formData.notes?.trim()) { setValidationError("Ghi chú không được trống."); return; } 
        if (!formData.status?.trim()) { setValidationError("Trạng thái không được trống."); return; } 

        // SỬA: Chuẩn bị payload đúng kiểu EquipmentUpdateRequest
        // Gửi các giá trị hiện tại trong state formData
        const submitData: EquipmentItemUpdateRequest = { 
             assetTag: formData.assetTag,
             defaultRoomId: formData.defaultRoomId || null, // Nếu không có thì gửi null
             notes: formData.notes || "", // Nếu không có thì gửi chuỗi rỗng
             status: formData.status as EquipmentStatusType, // Gửi status từ formData             
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
                        Chỉnh sửa thông tin thiết bị
                    </Typography>
                     <Typography variant="subtitle1" gutterBottom sx={{ textAlign: 'center', mb: 3 }}>
                        {/* ID: {equipmentData.id} */}
                     </Typography>
                    <Box component="form" onSubmit={handleSubmit} sx={{ display: 'flex', flexDirection: 'column', gap: 2.5 }}>

                         <TextField label="Mã tài sản (*)" name="assetTag" value={formData.assetTag ?? ''} onChange={handleInputChange} required fullWidth size="small" />

                        <Box sx={{ display: 'flex', gap: 2 }}>
                             <FormControl size="small" fullWidth required>
                                <InputLabel id="edit-building-select-label">Phòng quản lý</InputLabel>
                                <Select
                                    labelId="edit-building-select-label" label="Tòa nhà (*)"
                                    name="buildingId" value={formData.defaultRoomId ?? ''} onChange={handleSelectChange}
                                >
                                     <MenuItem value=""><em>Chọn phòng</em></MenuItem>
                                    {defaultRoom?.map((room) => (
                                        <MenuItem key={room.id} value={room.id}>{room.name}</MenuItem>
                                    ))}
                                </Select>
                            </FormControl>
                         </Box>

                         {/* THÊM: Select Status */}
                          <FormControl size="small" fullWidth>
                              <InputLabel id="edit-status-label">Trạng thái</InputLabel>
                              <Select
                                  labelId="edit-status-label" label="Trạng thái"
                                  name="status" value={formData.status ?? ""} onChange={handleStatusChange}
                              >
                                   {/* Chỉ cho phép chọn các trạng thái hợp lệ */}
                                   <MenuItem value={"AVAILABLE"}>AVAILABLE</MenuItem>
                                   <MenuItem value={"UNDER_MAINTENANCE"}>UNDER_MAINTENANCE</MenuItem>
                              </Select>
                          </FormControl>

                         <TextField label="Ghi chú" name="note" value={formData.notes ?? ''} onChange={handleInputChange} fullWidth multiline rows={2} size="small" />


                        {/* Hiển thị lỗi */}
                         {validationError && (<Typography variant="body2" color="error" sx={{ mt: 1 }}>{validationError}</Typography>)}
                         {/* SỬA: Hiển thị lỗi backend */}
                         {backendError && (<Alert severity="error" sx={{ mt: 1 }}>{backendError.message || 'Lỗi không xác định'}</Alert>)}

                        {/* Nút bấm */}
                        <Box sx={{ display: 'flex', justifyContent: 'flex-end', gap: 1, mt: 2 }}>
                            <Button variant="outlined" color="secondary" onClick={handleCancel} disabled={mutation.isPending}>Hủy bỏ</Button>
                            <Button type="submit" variant="contained" color="primary" disabled={mutation.isPending}>
                                {mutation.isPending ? <CircularProgress size={24} color="inherit" /> : "Lưu Thay Đổi"}
                            </Button>
                        </Box>
                    </Box>
                </Box>
            </Fade>
        </Modal>
    );
};

export default EditEquipmentModal;