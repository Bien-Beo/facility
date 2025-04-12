// import {
// 	Button,
// 	Fade,
// 	FormControl,
// 	FormHelperText,
// 	InputLabel,
// 	MenuItem,
// 	Modal,
// 	Select,
// 	SelectChangeEvent,
// 	TextField,
// 	Typography,
// } from "@mui/material";
// import { useMutation } from "@tanstack/react-query";
// import axios from "axios";
// import { ChangeEvent, JSX, FC, FormEvent, useEffect, useState } from "react";

// import "dayjs/locale/en-gb";

// import ErrorComponent from "../Error";

// const AddFacilityModal: FC<AddFacilityModalProps> = ({
// 	isOpen,
// 	setIsOpen,
// 	setOpenSnackbar,
// 	buildings,
// }): JSX.Element => {
// 	const [formData, setFormData] = useState<RoomCreationRequest>({
// 		name: "",
// 		description: "",
// 		capacity: 0,
// 		location: "",
// 		img: "",
// 		facilityManagerId: "",
// 		roomTypeId: "",
// 		buildingId: "",
// 		note: ""
// 	});
// 	const [error, setError] = useState<ErrorMessage>({
// 			status: null,
// 			message: "",
// 	});
// 	const [errorMessage, setErrorMessage] = useState<string>("");

// 	const mutation = useMutation({
// 		mutationFn: (data: AdminRoomsSubmitData) =>
// 			axios.post(
// 				`${import.meta.env.VITE_APP_SERVER_URL}/admin/facility/add`,
// 				data,
// 				{
// 					withCredentials: true,
// 				}
// 			),
// 		onSuccess: () => {
// 			setIsOpen(false);
// 			setOpenSnackbar(true);
// 		},
// 		onError: (error) => {
// 			setError(error.response!.data as ErrorMessage);
// 			console.log(error);
// 		},
// 	});

// 	const handleSubmit = (e: FormEvent<HTMLFormElement>): void => {
// 		e.preventDefault();
// 		const submitData: AdminRoomsSubmitData = {
// 			name: formData.name,
// 			description: formData.description,
// 			buildingName: formData.buildingName!,
// 			img: formData.img,
// 			// slug: `${formData.name
// 			// 	.toLowerCase()
// 			// 	.replace(/\s/g, "-")}${new Date().getTime()}`,
// 			facilityManagerId: parseInt(formData.facilityManagerId! as string),
// 			capacity: formData.capacity,
// 			location: formData.location,
// 			roomTypeName: formData.roomTypeName
// 		};
// 		mutation.mutate(submitData);
// 	};

// 	const handleCancel = (): void => {
// 		setIsOpen(false);
// 		setFormData({
// 			name: "",
// 			description: "",
// 			buildingName: "",
// 			img: "",
// 			facilityManagerId: "",
// 			location: "",
// 			capacity: 0,
// 			roomTypeName: ""
// 		});
// 	};

// 	useEffect(() => {
// 		if (error.status === 404) {
// 			setErrorMessage("User with this ID is not available");
// 			setTimeout(() => {
// 				setErrorMessage("");
// 			}, 4000);
// 		}
// 	}, [error]);

// 	if (error.status) {
// 		if (!errorMessage && error.status !== 404) {
// 			return (
// 				<ErrorComponent
// 					status={error.status!}
// 					message={error.message}
// 				/>
// 			);
// 		}
// 	}

// 	return (
// 		<Modal
// 			open={isOpen}
// 			onClose={() => {
// 				setIsOpen(false);
// 			}}
// 		>
// 			<Fade in={isOpen}>
// 				<div className="bg-bgPrimary w-full max-w-[500px] px-10 py-10 absolute left-[50%] top-[50%] -translate-x-[50%] -translate-y-[50%] rounded-md flex flex-col gap-6 shadow-cardHover">
// 					<Typography
// 						id="modal-modal-title"
// 						variant="h4"
// 						component="h2"
// 					>
// 						Create new facility
// 					</Typography>
// 					<form
// 						autoComplete="off"
// 						className="flex flex-col gap-4"
// 						onSubmit={handleSubmit}
// 					>
// 						<FormControl className="flex gap-4" size="small">
// 							<TextField
// 								id="name"
// 								label="Name"
// 								variant="outlined"
// 								className="w-full"
// 								value={formData.name}
// 								onChange={(e: ChangeEvent<HTMLInputElement>) =>
// 									setFormData({
// 										...formData,
// 										name: e.target.value,
// 									})
// 								}
// 								required
// 								size="small"
// 							/>
// 							<TextField
// 								id="description"
// 								label="Description"
// 								variant="outlined"
// 								className="w-full"
// 								value={formData.description}
// 								onChange={(e: ChangeEvent<HTMLInputElement>) =>
// 									setFormData({
// 										...formData,
// 										description: e.target.value,
// 									})
// 								}
// 								required
// 								multiline
// 								size="small"
// 							/>
// 							<FormControl size="small" fullWidth>
// 								<InputLabel>Select Building</InputLabel>
// 								<Select
// 									label="Select a month"
// 									size="small"
// 									value={formData.buildingName}
// 									onChange={(
// 										e: SelectChangeEvent<string | null>
// 									) => {
// 										setFormData({
// 											...formData,
// 											buildingName: e.target.value,
// 										});
// 									}}
// 									required
// 								>
// 									{buildings.map((building) => (
// 										<MenuItem
// 											key={building.name}
// 											value={building.name}
// 										>
// 											{building.name}
// 										</MenuItem>
// 									))}
// 								</Select>
// 							</FormControl>
// 							<TextField
// 								id="icon"
// 								label="Icon"
// 								variant="outlined"
// 								className="w-full"
// 								value={formData.img}
// 								onChange={(e: ChangeEvent<HTMLInputElement>) =>
// 									setFormData({
// 										...formData,
// 										img: e.target.value,
// 									})
// 								}
// 								required
// 								size="small"
// 							/>
// 							<TextField
// 								id="FMId"
// 								label="Facility Manager Id"
// 								variant="outlined"
// 								className="w-full"
// 								value={formData.facilityManagerId}
// 								onChange={(e: ChangeEvent<HTMLInputElement>) =>
// 									setFormData({
// 										...formData,
// 										facilityManagerId: e.target.value,
// 									})
// 								}
// 								required
// 								size="small"
// 							/>
// 						</FormControl>
// 						{errorMessage && (
// 							<FormHelperText error={true}>
// 								{errorMessage}
// 							</FormHelperText>
// 						)}
// 						<div className="w-full flex items-center justify-between mt-2">
// 							<Button
// 								type="submit"
// 								variant="contained"
// 								color="success"
// 								sx={{ minWidth: "47%" }}
// 								size="large"
// 							>
// 								Add
// 							</Button>
// 							<Button
// 								variant="contained"
// 								color="error"
// 								sx={{ minWidth: "47%" }}
// 								size="large"
// 								onClick={handleCancel}
// 							>
// 								Cancel
// 							</Button>
// 						</div>
// 					</form>
// 				</div>
// 			</Fade>
// 		</Modal>
// 	);
// };

// export default AddFacilityModal;

import React, { JSX, ChangeEvent, FC, FormEvent, useState } from "react";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import axios, { AxiosError } from "axios";
import {
    Button, Fade, FormControl, InputLabel, MenuItem, Modal, Select, SelectChangeEvent, TextField, Typography, Box, CircularProgress, Alert
} from "@mui/material";
import "dayjs/locale/en-gb";

const AddFacilityModal: FC<AddFacilityModalProps> = ({
    isOpen,
    setIsOpen,
    setOpenSnackbar,
    buildings,
    roomTypes = [],      // Nhận prop, gán default []
    facilityManagers = [], // Nhận prop, gán default []
}): JSX.Element => {

    // SỬA: State khớp với RoomCreationRequest
    const [formData, setFormData] = useState<RoomCreationRequest>({
        name: "",
        description: "",
        capacity: 1, 
        location: "",
        img: "",
        facilityManagerId: null, 
        roomTypeId: "", 
        buildingId: ""
    });

    const [backendError, setBackendError] = useState<ErrorMessage | null>(null);
    const [validationError, setValidationError] = useState<string>(""); // Cho lỗi validation frontend

    const queryClient = useQueryClient(); // Để refresh lại danh sách sau khi thêm

    // --- Mutation ---
    const mutation = useMutation<
        ApiResponse<RoomData>, // Giả sử API trả về RoomResponse trong ApiResponse
        AxiosError<ErrorMessage>,
        RoomCreationRequest
    >({
        mutationFn: async (data: RoomCreationRequest): Promise<ApiResponse<RoomData>> => {
            const token = localStorage.getItem("token");
            console.log("Sending room creation request:", data);

            const response = await axios.post<ApiResponse<RoomData>>(
				// Sửa lại endpoint nếu cần, ví dụ: /admin/rooms
				`${import.meta.env.VITE_APP_SERVER_URL}/admin/rooms`,
				data,
				{ headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' } }
			);

			// Kiểm tra lại response trước khi trả về 
			if (response.data?.code !== 0 || !response.data?.result) {
				throw new Error(response.data?.message || "Invalid response data after creating room.");
			}
	
			// Trả về phần data của response, có kiểu là ApiResponse<RoomData>
			return response.data;
        },
        onSuccess: (responseData: ApiResponse<RoomData>) => { // responseData giờ có kiểu ApiResponse<RoomData>
			console.log("Room created successfully:", responseData.result); // Truy cập dữ liệu phòng qua .result
			setOpenSnackbar(true);
			setIsOpen(false);
			queryClient.invalidateQueries({ queryKey: ["adminRooms"] }); // Key khớp với query lấy danh sách phòng
			handleCancel(); // Reset form
		},
		onError: (error) => {
			console.error("Room creation failed:", error);
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

    const handleCapacityChange = (e: ChangeEvent<HTMLInputElement>) => {
        const value = e.target.value;
        const capacity = value === "" ? 0 : parseInt(value, 10); // Cho phép về 0 khi xóa input
         if (!isNaN(capacity) && capacity >= 0) {
            setFormData(prev => ({ ...prev, capacity }));
         }
    };

    // --- Submit ---
    const handleSubmit = (e: FormEvent<HTMLFormElement>): void => {
        e.preventDefault();
        setValidationError("");
        setBackendError(null);

        // --- Validation cơ bản ---
        if (!formData.name.trim()) { setValidationError("Tên phòng không được trống."); return; }
        if (formData.capacity < 1) { setValidationError("Sức chứa phải ít nhất là 1."); return; }
        if (!formData.buildingId) { setValidationError("Vui lòng chọn tòa nhà."); return; }
        if (!formData.roomTypeId) { setValidationError("Vui lòng chọn loại phòng."); return; }
        // facilityManagerId có thể null

        // Chuẩn bị dữ liệu gửi đi khớp với RoomCreationRequest
        const submitData: RoomCreationRequest = {
            ...formData,
            capacity: Number(formData.capacity), // Đảm bảo là number
            // Chuyển facilityManagerId rỗng thành null nếu backend yêu cầu null thay vì rỗng
            facilityManagerId: formData.facilityManagerId === "" ? null : formData.facilityManagerId,
            // Bỏ các trường không có trong DTO: buildingName, roomTypeName, slug...
        };

        console.log("Submitting data:", submitData);
        mutation.mutate(submitData);
    };

    // --- Cancel ---
    const handleCancel = (): void => {
        setIsOpen(false);
        // Reset form về trạng thái ban đầu
        setFormData({
            name: "", description: "", capacity: 1, location: "", img: "",
            facilityManagerId: null, roomTypeId: "", buildingId: ""
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
                        Thêm Phòng Mới
                    </Typography>
                    <Box component="form" onSubmit={handleSubmit} sx={{ display: 'flex', flexDirection: 'column', gap: 2.5 }}>

                         <TextField label="Tên phòng (*)" name="name" value={formData.name} onChange={handleInputChange} required fullWidth size="small" />
                         <TextField label="Mô tả" name="description" value={formData.description} onChange={handleInputChange} fullWidth multiline rows={2} size="small" />
                         {/* THÊM: Input Location */}
                         <TextField label="Vị trí chi tiết" name="location" value={formData.location} onChange={handleInputChange} fullWidth size="small" />

                         <Box sx={{ display: 'flex', gap: 2 }}>
                             {/* SỬA: Input Capacity */}
                             <TextField
                                label="Sức chứa (*)"
                                name="capacity"
                                type="number"
                                value={formData.capacity <= 0 ? '' : formData.capacity}
                                onChange={handleCapacityChange}
                                required
                                fullWidth
                                size="small"
                                InputProps={{ inputProps: { min: 1 } }}
                            />
                            {/* SỬA: Label cho Image */}
                            <TextField label="Ảnh URL/Path" name="img" value={formData.img} onChange={handleInputChange} fullWidth size="small" />
                        </Box>

                        <Box sx={{ display: 'flex', gap: 2 }}>
                            {/* SỬA: Select Building */}
                            <FormControl size="small" fullWidth required>
                                <InputLabel id="building-select-label">Tòa nhà (*)</InputLabel>
                                <Select
                                    labelId="building-select-label"
                                    label="Tòa nhà (*)"
                                    name="buildingId" // <<< Dùng buildingId
                                    value={formData.buildingId} // <<< Bind vào buildingId
                                    onChange={handleSelectChange}
                                >
                                    <MenuItem value=""><em>Chọn tòa nhà</em></MenuItem>
                                    {buildings?.map((building) => (
                                        <MenuItem key={building.id} value={building.id}> {/* <<< Value là ID */}
                                            {building.name}
                                        </MenuItem>
                                    ))}
                                </Select>
                            </FormControl>
                             {/* THÊM: Select Room Type */}
                             <FormControl size="small" fullWidth required>
                                <InputLabel id="roomtype-select-label">Loại phòng (*)</InputLabel>
                                <Select
                                    labelId="roomtype-select-label"
                                    label="Loại phòng (*)"
                                    name="roomTypeId" // <<< Dùng roomTypeId
                                    value={formData.roomTypeId} // <<< Bind vào roomTypeId
                                    onChange={handleSelectChange}
                                >
                                     <MenuItem value=""><em>Chọn loại phòng</em></MenuItem>
                                     {/* Giả định prop roomTypes được truyền vào */}
                                    {roomTypes?.map((type) => (
                                        <MenuItem key={type.id} value={type.id}> {/* <<< Value là ID */}
                                            {type.name}
                                        </MenuItem>
                                    ))}
                                </Select>
                            </FormControl>
                        </Box>

                         {/* SỬA: Select Facility Manager */}
                         <FormControl size="small" fullWidth>
                            <InputLabel id="fm-select-label">Người quản lý (Tùy chọn)</InputLabel>
                            <Select
                                labelId="fm-select-label"
                                label="Người quản lý (Tùy chọn)"
                                name="facilityManagerId" // <<< Dùng facilityManagerId
                                value={formData.facilityManagerId ?? ""} // <<< Bind vào facilityManagerId, dùng "" cho Select
                                onChange={handleSelectChange}
                            >
                                <MenuItem value=""><em>Không chọn</em></MenuItem> {/* Option để bỏ chọn */}
                                {/* Giả định prop facilityManagers được truyền vào */}
                                {facilityManagers?.map((manager) => (
                                    <MenuItem key={manager.id} value={manager.id}> {/* <<< Value là ID */}
                                        {manager.fullName || manager.username} ({manager.userId})
                                    </MenuItem>
                                ))}
                            </Select>
                        </FormControl>

                         {/*<TextField label="Ghi chú (Tùy chọn)" name="note" value={formData.note} onChange={handleInputChange} fullWidth multiline rows={2} size="small" />*/}

                        {/* Hiển thị lỗi */}
                         {validationError && (<Typography variant="body2" color="error" sx={{ mt: 1 }}>{validationError}</Typography>)}
                         {backendError && (<Alert severity="error" sx={{ mt: 1 }}>{backendError.message || 'Lỗi không xác định'}</Alert>)}

                        {/* Nút bấm */}
                        <Box sx={{ display: 'flex', justifyContent: 'flex-end', gap: 1, mt: 2 }}>
                            <Button variant="outlined" color="secondary" onClick={handleCancel} disabled={mutation.isPending}>Hủy bỏ</Button>
                            <Button type="submit" variant="contained" color="primary" disabled={mutation.isPending}>
                                {mutation.isPending ? <CircularProgress size={24} color="inherit" /> : "Thêm Phòng"}
                            </Button>
                        </Box>
                    </Box>
                </Box>
            </Fade>
        </Modal>
    );
};

export default AddFacilityModal;