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
// import { ChangeEvent, FC, FormEvent, useEffect, useState } from "react";

// import "dayjs/locale/en-gb";

// import ErrorComponent from "../Error";

// const EditFacilityModal: FC<EditFacilityModalProps> = ({
// 	isOpen,
// 	setIsOpen,
// 	setOpenSnackbar,
// 	facilityData,
// 	buildingData,
// }): JSX.Element => {
// 	const [formData, setFormData] = useState<AddFacilityDataProps>({
// 		name: facilityData ? facilityData.name : "",
// 		description: facilityData ? facilityData.description : "",
// 		building: facilityData ? facilityData.building!.name : "",
// 		icon: facilityData ? facilityData.icon : "",
// 		FMId: facilityData ? facilityData.facilityManager.user.employeeId : "",
// 	});
// 	const [error, setError] = useState<ErrorMessage>({
// 		error: {
// 			status: null,
// 			message: "",
// 		},
// 	});
// 	const [errorMessage, setErrorMessage] = useState<string>("");

// 	const mutation = useMutation({
// 		mutationFn: (data: AdminFacilitiesEditData) =>
// 			axios.put(
// 				`${import.meta.env.VITE_APP_SERVER_URL}/admin/facility`,
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
// 		const submitData: AdminFacilitiesEditData = {
// 			name: formData.name,
// 			description: formData.description,
// 			icon: formData.icon,
// 			slug: facilityData.slug,
// 			prevFacilityManagerId: facilityData.facilityManager.user.employeeId,
// 			newFacilityManagerId: parseInt(formData.FMId! as string),
// 		};
// 		mutation.mutate(submitData);
// 	};

// 	const handleCancel = (): void => {
// 		setIsOpen(false);
// 		setFormData({
// 			name: "",
// 			description: "",
// 			building: "",
// 			icon: "",
// 			FMId: "",
// 		});
// 	};

// 	useEffect(() => {
// 		if (error.error.status === 404) {
// 			setErrorMessage("User with this ID is not available");
// 			setTimeout(() => {
// 				setErrorMessage("");
// 			}, 4000);
// 		}
// 	}, [error]);

// 	if (error.error.status) {
// 		if (!errorMessage && error.error.status !== 404) {
// 			return (
// 				<ErrorComponent
// 					status={error.error.status!}
// 					message={error.error.message}
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
// 						Edit facility
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
// 								size="small"
// 							/>
// 							<FormControl size="small" fullWidth>
// 								<InputLabel>Select Building</InputLabel>
// 								<Select
// 									label="Select a month"
// 									size="small"
// 									value={formData.building}
// 									onChange={(
// 										e: SelectChangeEvent<string | null>
// 									) => {
// 										setFormData({
// 											...formData,
// 											building: e.target.value,
// 										});
// 									}}
// 									required
// 								>
// 									{buildingData!.map((building) => (
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
// 								value={formData.icon}
// 								onChange={(e: ChangeEvent<HTMLInputElement>) =>
// 									setFormData({
// 										...formData,
// 										icon: e.target.value,
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
// 								value={formData.FMId}
// 								onChange={(e: ChangeEvent<HTMLInputElement>) =>
// 									setFormData({
// 										...formData,
// 										FMId: e.target.value,
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
// 								Edit
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

// export default EditFacilityModal;

import React, { JSX, ChangeEvent, FC, FormEvent, useEffect, useState } from "react";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import axios, { AxiosError } from "axios";
import {
    Button, Fade, FormControl, FormHelperText, InputLabel, MenuItem, Modal, Select, SelectChangeEvent, TextField, Typography, Box, CircularProgress, Alert
} from "@mui/material";
import "dayjs/locale/en-gb";

import ErrorComponent from "../Error";

// --- Component EditFacilityModal ---


const EditFacilityModal: FC<EditFacilityModalProps> = ({
    isOpen,
    setIsOpen,
    setOpenSnackbar,
    facilityData, // Dữ liệu phòng hiện tại để edit
    buildings,
    roomTypes = [],
    facilityManagers = [],
    onSuccessCallback,
}): JSX.Element => {

    // SỬA: State dùng kiểu gần với RoomUpdateRequest và khởi tạo từ facilityData
    // Dùng Partial để chỉ lưu những gì thay đổi? Hoặc khởi tạo đầy đủ để dễ quản lý form hơn.
    // Chọn khởi tạo đầy đủ để dễ bind value cho form.
    const [formData, setFormData] = useState<Partial<RoomUpdateRequest>>({});

    // Khởi tạo/Reset formData khi modal mở hoặc dữ liệu phòng thay đổi
    useEffect(() => {
        if (isOpen && facilityData) {
            console.log("Initializing edit form with facility data:", facilityData);
            setFormData({
                name: facilityData.name ?? "", // Dùng ?? để xử lý null/undefined
                description: facilityData.description ?? "",
                capacity: facilityData.capacity ?? 1,
                location: facilityData.location ?? "",
                img: facilityData.img ?? "",
                buildingId: facilityData.buildingId ?? "", // <<< Lấy ID
                roomTypeId: facilityData.roomTypeId ?? "",   // <<< Lấy ID
                facilityManagerId: facilityData.facilityManagerId ?? null, // <<< Lấy ID
                note: facilityData.note ?? "",
                status: facilityData.status as RoomStatusType, // <<< Lấy status
            });
        }
         setBackendError(null); // Reset lỗi khi mở
         setValidationError("");
    }, [facilityData, isOpen]); // Phụ thuộc


    // SỬA: Cấu trúc state lỗi chuẩn
    const [backendError, setBackendError] = useState<ErrorMessage | null>(null);
    const [validationError, setValidationError] = useState<string>(""); // Vẫn giữ validationError cho FE
    const queryClient = useQueryClient();

    // --- Mutation ---
    const mutation = useMutation<
        ApiResponse<RoomData>,      // Kiểu response thành công
        AxiosError<ErrorMessage>, // Kiểu lỗi
        RoomUpdateRequest         // <<< SỬA: Kiểu biến là DTO Update
    >({
        mutationFn: (updateData: RoomUpdateRequest) => { // <<< SỬA: Kiểu biến
            const token = localStorage.getItem("token");
            if (!token) throw new Error("No token found");
            console.log(`Sending room update request for ID ${facilityData.id}:`, updateData);
            // SỬA: Endpoint là PUT hoặc PATCH đến /admin/rooms/{id}
            return axios.patch<ApiResponse<RoomData>>( 
                `${import.meta.env.VITE_APP_SERVER_URL}/rooms/${facilityData.id}`, // <<< Thêm ID vào URL
                updateData, // Gửi đi payload đúng DTO Update
                { headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' } }
            );
        },
        onSuccess: (response) => {
            console.log("Room updated successfully:", response.data.result);
            setOpenSnackbar(true);
            setIsOpen(false);
            queryClient.invalidateQueries({ queryKey: ["adminRooms"] }); // Refresh lại bảng
            if (onSuccessCallback) onSuccessCallback();
        },
        onError: (error) => {
            console.error("Room update failed:", error);
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

    const handleCapacityChange = (e: ChangeEvent<HTMLInputElement>) => {
        const value = e.target.value;
        const capacity = value === "" ? 0 : parseInt(value, 10);
        if (!isNaN(capacity) && capacity >= 0) { // Cho phép capacity = 0 nếu logic cho phép
            setFormData(prev => ({ ...prev, capacity }));
        } else if (value === "") {
            setFormData(prev => ({ ...prev, capacity: undefined })); // Set undefined để mapper bỏ qua nếu không nhập gì? Hoặc 0
        }
    };

    const handleStatusChange = (e: SelectChangeEvent<string>) => {
        setFormData(prev => ({ ...prev, status: e.target.value as RoomStatusType }));
    };

    // --- Submit ---
    const handleSubmit = (e: FormEvent<HTMLFormElement>): void => {
        e.preventDefault();
        setValidationError("");
        setBackendError(null);

        // --- Validation cơ bản ---
        if (!formData.name?.trim()) { setValidationError("Tên phòng không được trống."); return; }
        if (formData.capacity === undefined || formData.capacity === null || formData.capacity < 1) { setValidationError("Sức chứa phải ít nhất là 1."); return; }
        if (!formData.buildingId) { setValidationError("Vui lòng chọn tòa nhà."); return; }
        if (!formData.roomTypeId) { setValidationError("Vui lòng chọn loại phòng."); return; }

        // SỬA: Chuẩn bị payload đúng kiểu RoomUpdateRequest
        // Gửi các giá trị hiện tại trong state formData
        const submitData: RoomUpdateRequest = {
             name: formData.name,
             description: formData.description,
             capacity: formData.capacity ? Number(formData.capacity) : undefined, // Chuyển thành number hoặc undefined
             location: formData.location,
             img: formData.img,
             buildingId: formData.buildingId,
             roomTypeId: formData.roomTypeId,
             facilityManagerId: formData.facilityManagerId, // Giữ null nếu không chọn
             note: formData.note,
             status: formData.status,
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
                        Chỉnh sửa thông tin phòng
                    </Typography>
                     <Typography variant="subtitle1" gutterBottom sx={{ textAlign: 'center', mb: 3 }}>
                        ID: {facilityData.id}
                     </Typography>
                    <Box component="form" onSubmit={handleSubmit} sx={{ display: 'flex', flexDirection: 'column', gap: 2.5 }}>

                         <TextField label="Tên phòng (*)" name="name" value={formData.name ?? ''} onChange={handleInputChange} required fullWidth size="small" />
                         <TextField label="Mô tả" name="description" value={formData.description ?? ''} onChange={handleInputChange} fullWidth multiline rows={2} size="small" />
                         {/* THÊM: Input Location */}
                         <TextField label="Vị trí chi tiết" name="location" value={formData.location ?? ''} onChange={handleInputChange} fullWidth size="small" />


                         <Box sx={{ display: 'flex', gap: 2 }}>
                              {/* THÊM: Input Capacity */}
                             <TextField
                                label="Sức chứa (*)" name="capacity" type="number"
                                value={formData.capacity === undefined || formData.capacity === null || formData.capacity <= 0 ? '' : formData.capacity}
                                onChange={handleCapacityChange} required fullWidth size="small"
                                InputProps={{ inputProps: { min: 1 } }}
                             />
                            {/* SỬA: Label và name cho Image */}
                            <TextField label="Ảnh URL/Path" name="img" value={formData.img ?? ''} onChange={handleInputChange} fullWidth size="small" />
                        </Box>

                        <Box sx={{ display: 'flex', gap: 2 }}>
                             {/* SỬA: Select Building dùng ID */}
                             <FormControl size="small" fullWidth required>
                                <InputLabel id="edit-building-select-label">Tòa nhà (*)</InputLabel>
                                <Select
                                    labelId="edit-building-select-label" label="Tòa nhà (*)"
                                    name="buildingId" value={formData.buildingId ?? ''} onChange={handleSelectChange}
                                >
                                     <MenuItem value=""><em>Chọn tòa nhà</em></MenuItem>
                                     {/* buildings giờ là BuildingData[] từ props */}
                                    {buildings?.map((building) => (
                                        <MenuItem key={building.id} value={building.id}>{building.name}</MenuItem>
                                    ))}
                                </Select>
                            </FormControl>
                             {/* THÊM: Select Room Type dùng ID */}
                             <FormControl size="small" fullWidth required>
                                <InputLabel id="edit-roomtype-select-label">Loại phòng (*)</InputLabel>
                                <Select
                                     labelId="edit-roomtype-select-label" label="Loại phòng (*)"
                                     name="roomTypeId" value={formData.roomTypeId ?? ''} onChange={handleSelectChange}
                                 >
                                     <MenuItem value=""><em>Chọn loại phòng</em></MenuItem>
                                     {/* Dùng prop roomTypes */}
                                     {roomTypes?.map((type) => (
                                         <MenuItem key={type.id} value={type.id}>{type.name}</MenuItem>
                                     ))}
                                 </Select>
                             </FormControl>
                         </Box>

                         {/* SỬA: Select Facility Manager dùng ID */}
                         <FormControl size="small" fullWidth>
                            <InputLabel id="edit-fm-select-label">Người quản lý (Tùy chọn)</InputLabel>
                            <Select
                                labelId="edit-fm-select-label" label="Người quản lý (Tùy chọn)"
                                name="facilityManagerId" value={formData.facilityManagerId ?? ""} // Dùng ?? "" cho Select
                                onChange={handleSelectChange}
                             >
                                <MenuItem value=""><em>Không chọn</em></MenuItem>
                                 {/* Dùng prop facilityManagers */}
                                {facilityManagers?.map((manager) => (
                                     <MenuItem key={manager.id} value={manager.id}>
                                         {manager.fullName || manager.username} ({manager.userId})
                                     </MenuItem>
                                ))}
                            </Select>
                        </FormControl>

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

                         <TextField label="Ghi chú" name="note" value={formData.note ?? ''} onChange={handleInputChange} fullWidth multiline rows={2} size="small" />


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

export default EditFacilityModal;