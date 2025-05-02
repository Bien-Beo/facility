import React, { JSX, ChangeEvent, FC, FormEvent, useState } from "react";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import axios, { AxiosError } from "axios";
import {
    Button, Fade, FormControl, InputLabel, MenuItem, Modal, Select, SelectChangeEvent, TextField, Typography, Box, CircularProgress, Alert
} from "@mui/material";
import "dayjs/locale/en-gb";

const AddEquipmentModal: FC<AddEquipmentModalProps> = ({
    isOpen,
    setIsOpen,
    setOpenSnackbar,
    defaultRoom,
}): JSX.Element => {

    const [formData, setFormData] = useState<EquipmentItemCreationRequest>({
        serialNumber: "",
        assetTag: "",
        purchaseDate: "", 
        warrantyExpiryDate: "",
        notes: "",
        defaultRoomId: defaultRoom || null,
    });

    const [backendError, setBackendError] = useState<ErrorMessage | null>(null);
    const [validationError, setValidationError] = useState<string>("");

    const queryClient = useQueryClient(); 

    // --- Mutation ---
    const mutation = useMutation<
        ApiResponse<EquipmentItemData>, 
        AxiosError<ErrorMessage>,
        EquipmentItemCreationRequest
    >({
        mutationFn: async (data: EquipmentItemCreationRequest): Promise<ApiResponse<EquipmentItemData>> => {
            const token = localStorage.getItem("token");
            console.log("Sending room creation request:", data);

            const response = await axios.post<ApiResponse<EquipmentItemData>>(
				`${import.meta.env.VITE_APP_SERVER_URL}/equipments`,
				data,
				{ headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' } }
			);

			if (response.data?.code !== 0 || !response.data?.result) {
				throw new Error(response.data?.message || "Invalid response data after creating equipment.");
			}
	
			return response.data;
        },
        onSuccess: (responseData: ApiResponse<EquipmentItemData>) => {
			console.log("Equipment created successfully:", responseData.result);
			setOpenSnackbar(true);
			setIsOpen(false);
			queryClient.invalidateQueries({ queryKey: ["adminEquipments"] }); 
			handleCancel(); 
		},
		onError: (error) => {
			console.error("Equipment creation failed:", error);
			setBackendError(error.response?.data || { message: error.message || "Lỗi không xác định" });
		},
	});

    // --- Handlers ---
    const handleInputChange = (e: ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
    };

    // --- Submit ---
    const handleSubmit = (e: FormEvent<HTMLFormElement>): void => {
        e.preventDefault();
        setValidationError("");
        setBackendError(null);

        // --- Validation cơ bản ---
        if (!formData.assetTag?.trim()) { setValidationError("Mã tài sản không được trống."); return; }
        if (!formData.serialNumber?.trim()) { setValidationError("Số hiệu thiết bị không được trống."); return; }
        if (!formData.purchaseDate) { setValidationError("Vui lòng chọn ngày mua."); return; }
        if (!formData.warrantyExpiryDate) { setValidationError("Vui lòng chọn ngày hết bảo hành."); return; }
        if (formData.warrantyExpiryDate < formData.purchaseDate) { setValidationError("Ngày hết bảo hành phải lớn hơn ngày mua."); return; }
        if (formData.warrantyExpiryDate < new Date().toISOString()) { setValidationError("Ngày hết bảo hành không được nhỏ hơn ngày hiện tại."); return; }


        const submitData: EquipmentItemCreationRequest = {
            ...formData,
            defaultRoomId: formData.defaultRoomId || null,
            purchaseDate: new Date(formData.purchaseDate).toISOString(),
            warrantyExpiryDate: new Date(formData.warrantyExpiryDate).toISOString(),
            notes: formData.notes || "",
        };

        console.log("Submitting data:", submitData);
        mutation.mutate(submitData);
    };

    // --- Cancel ---
    const handleCancel = (): void => {
        setIsOpen(false);
        setFormData({
            serialNumber: "",
            assetTag: "",
            purchaseDate: "", 
            warrantyExpiryDate: "",
            notes: "",
            defaultRoomId: null,
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
                        Thêm Thiết Bị
                    </Typography>
                    <Box component="form" onSubmit={handleSubmit} sx={{ display: 'flex', flexDirection: 'column', gap: 2.5 }}>
                         <TextField label="Phòng mặc định" name="defaultRoom" value={defaultRoom} onChange={handleInputChange} fullWidth size="small" />
                            <TextField label="Số hiệu thiết bị (*)" name="serialNumber" value={formData.serialNumber} onChange={handleInputChange} required fullWidth size="small" />
                            <TextField label="Mã tài sản (*)" name="assetTag" value={formData.assetTag} onChange={handleInputChange} required fullWidth size="small" />
                            <TextField label="Ngày mua (*)" name="purchaseDate" type="date" value={formData.purchaseDate} onChange={handleInputChange} required fullWidth size="small" InputLabelProps={{ shrink: true }} />
                            <TextField label="Ngày hết bảo hành (*)" name="warrantyExpiryDate" type="date" value={formData.warrantyExpiryDate} onChange={handleInputChange} required fullWidth size="small" InputLabelProps={{ shrink: true }} />
                            <TextField label="Ghi chú (Tùy chọn)" name="notes" value={formData.notes} onChange={handleInputChange} fullWidth multiline rows={2} size="small" />
                        </Box>

                        {/* Hiển thị lỗi */}
                         {validationError && (<Typography variant="body2" color="error" sx={{ mt: 1 }}>{validationError}</Typography>)}
                         {backendError && (<Alert severity="error" sx={{ mt: 1 }}>{backendError.message || 'Lỗi không xác định'}</Alert>)}

                        {/* Nút bấm */}
                        <Box sx={{ display: 'flex', justifyContent: 'flex-end', gap: 1, mt: 2 }}>
                            <Button variant="outlined" color="secondary" onClick={handleCancel} disabled={mutation.isPending}>Hủy bỏ</Button>
                            <Button type="submit" variant="contained" color="primary" disabled={mutation.isPending}>
                                {mutation.isPending ? <CircularProgress size={24} color="inherit" /> : "Thêm Thiết Bị"}
                            </Button>
                        </Box>
                    </Box>
            </Fade>
        </Modal>
    );
};

export default AddEquipmentModal;