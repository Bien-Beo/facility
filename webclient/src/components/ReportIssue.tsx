import React, { JSX, FC, FormEvent, useState, ChangeEvent } from "react";
import {
    Box, Button, CircularProgress, FormControl, InputLabel, MenuItem, Paper, Select, SelectChangeEvent, Snackbar, TextField, Typography, Alert 
} from "@mui/material";
import ReportProblemIcon from '@mui/icons-material/ReportProblem'; 
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import axios, { AxiosError } from "axios";
import { useNavigate } from "react-router-dom";
import ErrorComponent from "./Error";

// --- Component ---
const ReportIssue: FC<ReportIssueProps> = ({ roomId }): JSX.Element => {
    // --- State cho Form ---
    const [selectedItemId, setSelectedItemId] = useState<string>(""); 
    const [description, setDescription] = useState<string>("");
    const [backendError, setBackendError] = useState<ErrorMessage | null>(null);
    const [validationError, setValidationError] = useState<string>("");
    const [openSnackbar, setOpenSnackbar] = useState<boolean>(false);

    const queryClient = useQueryClient();
    const navigate = useNavigate();

    // --- Fetch chi tiết phòng để lấy tên và thiết bị mặc định ---
    const { data: roomApiResponse, isLoading: isLoadingRoom, isError: isRoomError, error: roomError } =
        useQuery<RoomDetailApiResponse, AxiosError<ErrorMessage>>({
            queryKey: ["roomDetailForReport", roomId], 
            queryFn: async () => {
                if (!roomId) throw new Error("Room ID is missing.");
                const token = localStorage.getItem("token"); 
                if (!token) throw new Error("No token found");
                console.log(`Workspaceing room details for report form: ${roomId}`);
                const response = await axios.get<RoomDetailApiResponse>(
                    `${import.meta.env.VITE_APP_SERVER_URL || 'http://localhost:8080'}/rooms/${roomId}`,
                    { headers: { Authorization: `Bearer ${token}` } }
                );
                if (response.data?.code !== 0 || !response.data?.result) {
                    throw new Error(response.data?.message || "Invalid room detail response");
                }
                return response.data;
            },
            enabled: !!roomId, 
            staleTime: 10 * 60 * 1000, 
        });

    const roomData: RoomData | null = roomApiResponse?.result || null;
    const defaultEquipments: EquipmentItemData[] = roomData?.defaultEquipments || [];

    // --- Mutation để gửi báo cáo ---
    const mutation = useMutation<
        ApiResponse<MaintenanceTicketData>, 
        AxiosError<ErrorMessage>,
        MaintenanceTicketCreationRequest
    >({
        mutationFn: async (payload: MaintenanceTicketCreationRequest) => {
            const token = localStorage.getItem("token");
            if (!token) throw new Error("No token found");
            console.log("Submitting maintenance ticket:", payload);
            const response = await axios.post<ApiResponse<MaintenanceTicketData>>(
                `${import.meta.env.VITE_APP_SERVER_URL || 'http://localhost:8080'}/maintenance`, 
                payload,
                { headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' } }
            );
            if (response.data?.code !== 0) { 
                 throw new Error(response.data?.message || "Failed to submit report");
            }
            return response.data;
        },
        onSuccess: () => {
            console.log("Maintenance ticket submitted successfully.");
            setOpenSnackbar(true);
            setSelectedItemId("");
            setDescription("");
            setValidationError("");
            setBackendError(null);
            setTimeout(() => navigate(`/rooms/${roomId}`), 2000); 
        },
        onError: (error) => {
            console.error("Maintenance ticket submission failed:", error);
            setBackendError(error.response?.data || { message: error.message || "Lỗi khi gửi báo cáo" });
        },
    });

    // --- Handlers ---
    const handleItemChange = (event: SelectChangeEvent<string>) => {
        setSelectedItemId(event.target.value);
        setValidationError("");
    };

    const handleDescriptionChange = (event: ChangeEvent<HTMLTextAreaElement>) => {
        setDescription(event.target.value);
         if (event.target.value.trim()) setValidationError("");
    };

    const handleSubmit = (event: FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        setValidationError("");
        setBackendError(null);

        // --- Validation ---
        if (!selectedItemId) {
            setValidationError("Vui lòng chọn thiết bị hoặc 'Vấn đề chung của phòng'."); return;
        }
        if (!description.trim() || description.trim().length < 10) { 
            setValidationError("Vui lòng mô tả sự cố chi tiết (ít nhất 10 ký tự)."); return;
        }

        // --- Chuẩn bị Payload ---
        const payload: MaintenanceTicketCreationRequest = {
            itemId: selectedItemId === "GENERAL_ROOM_ISSUE" ? null : selectedItemId,
            roomId: roomId, 
            description: description.trim(),
        };

        mutation.mutate(payload);
    };

     const handleCloseSnackbar = (event?: React.SyntheticEvent | Event, reason?: string) => {
         if (reason === 'clickaway') { return; }
         setOpenSnackbar(false);
     };

    // --- Render ---
     if (isLoadingRoom) {
         return ( <Box sx={{ display: 'flex', justifyContent: 'center', p: 5 }}><CircularProgress /></Box> );
     }

     if (isRoomError) {
         const errorData = roomError?.response?.data || { message: roomError.message, status: roomError?.response?.status || 500 };
         return (<ErrorComponent status={errorData.status ?? 500} message={`Lỗi tải thông tin phòng: ${errorData.message}`} />);
     }

    return (
        <Paper elevation={3} sx={{ p: { xs: 2, sm: 3, md: 4 }, mt: {xs: 2, md: 4}, maxWidth: '700px', width: '100%', mx: 'auto' }}>
            <Typography variant="h5" component="h2" gutterBottom sx={{ textAlign: 'center', mb: 3 }}>
                Báo cáo Sự cố cho Phòng: {roomData?.name ?? roomId}
            </Typography>
            <Box component="form" onSubmit={handleSubmit} sx={{ display: 'flex', flexDirection: 'column', gap: 3 }}>
                {/* Chọn Thiết bị/Vấn đề */}
                <FormControl fullWidth required size="small" error={!!validationError && validationError.includes('thiết bị')}>
                    <InputLabel id="item-select-label">Thiết bị hoặc Vấn đề (*)</InputLabel>
                    <Select
                        labelId="item-select-label"
                        id="selectedItemId"
                        name="selectedItemId"
                        value={selectedItemId}
                        label="Thiết bị hoặc Vấn đề (*)"
                        onChange={handleItemChange}
                    >
                        <MenuItem value="" disabled><em>-- Chọn --</em></MenuItem>
                        {/* Lựa chọn cho vấn đề chung */}
                        <MenuItem value="GENERAL_ROOM_ISSUE">Vấn đề chung của phòng</MenuItem>
                        <MenuItem disabled sx={{ fontStyle: 'italic', fontWeight: 'bold', mt: 1 }}>--- Thiết bị mặc định ---</MenuItem>
                        {/* Danh sách thiết bị mặc định */}
                        {defaultEquipments.length === 0 && <MenuItem disabled><em>(Phòng không có thiết bị mặc định)</em></MenuItem>}
                        {defaultEquipments.map((item) => (
                             // Hiển thị thêm trạng thái của thiết bị để người dùng biết
                            <MenuItem key={item.id} value={item.id} disabled={item.status !== 'AVAILABLE'}>
                                {item.modelName} ({item.assetTag || item.id}) - [{item.status}] {item.status !== 'AVAILABLE' ? '(Không thể chọn)' : ''}
                            </MenuItem>
                        ))}
                        {/* TODO: Có thể thêm Autocomplete để tìm thiết bị khác nếu cần */}
                    </Select>
                    {validationError && validationError.includes('thiết bị') && <FormHelperText>{validationError}</FormHelperText>}
                </FormControl>

                {/* Mô tả sự cố */}
                <TextField
                    id="description" name="description"
                    label="Mô tả chi tiết sự cố (*)"
                    placeholder="Ví dụ: Máy chiếu chập chờn, không lên nguồn; Điều hòa chảy nước; Chuột máy tính hỏng nút bấm..."
                    required fullWidth multiline rows={4} value={description}
                    onChange={handleDescriptionChange}
                    error={!!validationError && validationError.includes('mô tả')}
                    helperText={validationError && validationError.includes('mô tả') ? validationError : "Cung cấp càng nhiều chi tiết càng tốt."}
                />

                 {/* Hiển thị Lỗi Backend */}
                 {backendError && ( <Alert severity="error" sx={{ mt: 1 }}>{backendError.message || 'Đã xảy ra lỗi không xác định.'}</Alert> )}
                 {/* Hiển thị Lỗi Validation Chung */}
                 {validationError && !validationError.includes('thiết bị') && !validationError.includes('mô tả') && ( <Typography variant="caption" color="error">{validationError}</Typography> )}

                {/* Nút Gửi */}
                <Button
                    type="submit" variant="contained" color="error" 
                    disabled={mutation.isPending}
                    startIcon={mutation.isPending ? <CircularProgress size={20} color="inherit"/> : <ReportProblemIcon />}
                    sx={{ mt: 2, py: 1.5, fontSize: '1rem', fontWeight: 'bold' }}
                >
                     Gửi Báo cáo Sự cố
                </Button>
            </Box>

             {/* Snackbar Thông báo Thành công */}
            <Snackbar open={openSnackbar} autoHideDuration={5000} onClose={handleCloseSnackbar} anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}>
                <Alert onClose={handleCloseSnackbar} severity="success" variant="filled" sx={{ width: '100%' }}>
                     Đã gửi báo cáo sự cố thành công! Bộ phận kỹ thuật sẽ xem xét sớm.
                 </Alert>
            </Snackbar>
        </Paper>
    );
};

export default ReportIssue;