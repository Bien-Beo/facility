import DownloadIcon from "@mui/icons-material/Download";
import InsertInvitationIcon from "@mui/icons-material/InsertInvitation";
import {
	Alert,
	Button,
	CircularProgress,
	Snackbar,
	Typography,
	Box,
	Paper,
	Grid,
	Select,
	FormControl,
	InputLabel,
	MenuItem,
	TextField,
	SelectChangeEvent
} from "@mui/material";
import { useQuery, useQueryClient } from "@tanstack/react-query";
import axios, { AxiosError } from "axios";
import { JSX, FC, useEffect, useRef, useState, ChangeEvent } from "react";
import generatePDF, { Options } from "react-to-pdf";

import EquipmentsReport from "../reports/EquipmentsReport";
import ErrorComponent from "./Error";
import AddEquipmentModal from "./modals/AddEquipmentModal";
import ManagementEquipmentsTable from "./tables/ManagementEquipmentsTable";

const ManagementEquipments: FC = (): JSX.Element => {
	// --- State cho Filters ---
	const [selectedRoomId, setSelectedRoomId] = useState<string>(""); 
	const [selectedEquipmentModelId, setSelectedEquipmentModelId] = useState<string>(""); 
	const [selectedYear, setSelectedYear] = useState<string>(
	String(new Date().getFullYear())
	); 

	// State cho modal, snackbar, print
	const [isAddEquipmentModalOpen, setIsAddEquipmentModalOpen] = useState<boolean>(false);
	const [isPrint, setIsPrint] = useState<boolean>(false);
	const [openSnackbar, setOpenSnackbar] = useState<boolean>(false);

    // === STATE CHO PHÂN TRANG ===
    const [page, setPage] = useState<number>(0); 
    const [rowsPerPage, setRowsPerPage] = useState<number>(10); 

    const targetRef = useRef<HTMLDivElement>(null);
    const queryClient = useQueryClient();

	const { data: apiResponse, isPending, isError, error } = useQuery({
		queryKey: [
			"managementequipments",
			page,
			rowsPerPage,
			selectedEquipmentModelId,
			selectedRoomId,
			selectedYear,
		],
		queryFn: async () => {
            const token = localStorage.getItem("token");
            if (!token) throw new Error("No token found");

			const params = new URLSearchParams();
			params.append("page", String(page));
			params.append("size", String(rowsPerPage));
			params.append("sort", "createdAt,desc");

			if (selectedEquipmentModelId) params.append("equipmentModelId", selectedEquipmentModelId);
			if (selectedRoomId) params.append("roomId", selectedRoomId);
			if (selectedYear) params.append("year", selectedYear);

			const response = await axios.get<PaginatedRoomApiResponse>(
				`${import.meta.env.VITE_APP_SERVER_URL}/equipments?${params.toString()}`,
				{ headers: { Authorization: `Bearer ${token}` } }
			);

			if (
				response.data?.code !== 0 ||
				!response.data?.result?.content ||
				!response.data?.result?.page
			  ) {
				console.error("Invalid API response structure:", response.data);
				throw new Error("Cấu trúc API response không hợp lệ.");
			  }

			return response.data;
		},
		refetchInterval: 5 * 1000,
		retry: 1,
		gcTime: 0,
	});

	// === Trích xuất dữ liệu cần thiết từ API Response ===
	const equipmentsForCurrentPage: EquipmentItemData[] = apiResponse?.result?.content || [];
	const totalEquipmentCount: number = apiResponse?.result?.page?.totalElements || 0;

	// === Fetch Rooms riêng cho Modal ===
	const { data: roomsApiResponse } = useQuery<ApiResponse<RoomData[]>, AxiosError>({
		queryKey: ["allRoomsList"],
		queryFn: async () => {
			const token = localStorage.getItem("token");
			if (!token) throw new Error("No token found");
				const response = await axios.get<ApiResponse<RoomData[]>>(
				`${import.meta.env.VITE_APP_SERVER_URL}/rooms`, 
				{ headers: { Authorization: `Bearer ${token}` } }
			);
			if (response.data?.code !== 0 || !response.data?.result) {
				throw new Error(response.data?.message || "Invalid response for rooms");
			}
			return response.data;
		},
		staleTime: Infinity, 
	});
	const roomsList: RoomData[] = roomsApiResponse?.result?.content || [];

	// === Fetch EquipmentModels riêng cho Modal ===
	const { data: equipmentModelsApiResponse } = useQuery<ApiResponse<EquipmentModelData[]>, AxiosError>({
		queryKey: ["allEquipmentModelsList"],
		queryFn: async () => {
			const token = localStorage.getItem("token");
			if (!token) throw new Error("No token found");
			const response = await axios.get<ApiResponse<EquipmentModelData[]>>(
				`${import.meta.env.VITE_APP_SERVER_URL}/models`, 
				{ headers: { Authorization: `Bearer ${token}` } }
			);
			if (response.data?.code !== 0 || !response.data?.result) {
				throw new Error(response.data?.message || "Invalid response for equipment models");
			}
			return response.data;
		},
		staleTime: Infinity, 
	});
	const equipmentModelsList: EquipmentModelData[] = equipmentModelsApiResponse?.result?.content || [];

	// === Handlers cho Pagination (Để truyền xuống Table) ===
	const handleChangePage = (event: unknown, newPage: number) => {
		console.log("Changing page to:", newPage);
		setPage(newPage); // Cập nhật state -> trigger useQuery fetch lại
	};

	const handleChangeRowsPerPage = (event: ChangeEvent<HTMLInputElement>) => {
		const newSize = parseInt(event.target.value, 10);
		console.log("Changing rows per page to:", newSize);
		setRowsPerPage(newSize); // Cập nhật state số dòng/trang
		setPage(0); // Reset về trang đầu tiên
	};

	const handleCloseSnackbar = (): void => {
		setOpenSnackbar(false);
	};

	// === Handlers Filter ===
		const handleFilterChange =
		(setter: React.Dispatch<React.SetStateAction<string>>) =>
		(
			e:
			| SelectChangeEvent<string>
			| ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
		) => {
			setter(e.target.value);
			setPage(0); // Reset về trang đầu khi filter
		};
	
		const handleResetFilters = () => {
			setSelectedEquipmentModelId("");
			setSelectedRoomId("");
			setSelectedYear(String(new Date().getFullYear()));
			setPage(0); 
		};  
    

	useEffect(() => {
		if (isPrint) {
			setTimeout(() => {
				setIsPrint(false);
			}, 3000);
		}

		if (isPrint) {
			document.body.style.overflowY = "hidden";
		} else {
			document.body.style.overflowY = "auto";
		}
	}, [isPrint]);

	if (isError) {
		const errorData = error.response!.data as ErrorMessage;
		return (
			<ErrorComponent
				status={errorData.status!}
				message={errorData.message}
			/>
		);
	}

	if (isPending)
		return (
			<div className="w-[74vw] min-h-screen h-full flex flex-col items-center justify-center">
				<CircularProgress />
			</div>
		);

	const options: Options = {
		filename: "admin-facilities-bookings-report.pdf",
		page: {
			orientation: "landscape",
		},
	};

	return (
		<div className="w-full flex flex-col px-6 pt-8 gap-6">
			{isAddEquipmentModalOpen && (
				<AddEquipmentModal
					isOpen={isAddEquipmentModalOpen}
					setIsOpen={setIsAddEquipmentModalOpen}
					setOpenSnackbar={setOpenSnackbar}
                    defaultRoom={roomsList}
					models={equipmentModelsList}
					onSuccessCallback={() => { 
                        queryClient.invalidateQueries({ queryKey: ["managementequipments"] });
                    }}
				/>
			)}

			<Typography variant="h3" component="h1">
				Quản lý thiết bị
			</Typography>
			<div className="w-full flex justify-between items-center">
				<Button
					variant="contained"
					color="primary"
					endIcon={
						<InsertInvitationIcon
							sx={{ height: "20px", width: "20px" }}
						/>
					}
					sx={{ paddingX: "2em", height: "45px" }}
					size="large"
					onClick={() => {
						setIsAddEquipmentModalOpen(true);
					}}
				>
					Thêm thiết bị
				</Button>

				<Button
					variant="contained"
					color="primary"
					endIcon={
						<DownloadIcon sx={{ height: "20px", width: "20px" }} />
					}
					sx={{ paddingX: "2em", height: "45px" }}
					size="large"
					onClick={() => {
						setIsPrint(true);
						setTimeout(() => {
							generatePDF(targetRef, options);
						}, 1000);
					}}
				>
					Xuất báo cáo
				</Button>
			</div>

			<Paper sx={{ p: 2, mb: 2 }}>
				{" "}
				{/* Bọc filter trong Paper */}
				<Grid container spacing={2} alignItems="center">
				<Grid item xs={12} sm={6} md={2.5}>
					<FormControl size="small" fullWidth>
					<InputLabel id="room-filter-label">Lọc theo phòng</InputLabel>
					<Select
						labelId="room-filter-label"
						label="Lọc theo phòng"
						name="selectedRoomId"
						value={selectedRoomId}
						onChange={handleFilterChange(setSelectedRoomId)}
					>
						<MenuItem value="">
						<em>Tất cả phòng</em>
						</MenuItem>
						{roomsList.map((room) => (
						<MenuItem key={room.id} value={room.id}>
							{room.name}
						</MenuItem>
						))}
					</Select>
					</FormControl>
				</Grid>
				<Grid item xs={12} sm={6} md={2.5}>
					<FormControl size="small" fullWidth>
					<InputLabel id="equipmentModels-filter-label">Lọc theo loại model</InputLabel>
					<Select
						labelId="equipmentModels-filter-label"
						label="Lọc theo loại model"
						value={selectedEquipmentModelId}
						onChange={handleFilterChange(setSelectedEquipmentModelId)}
					>
						<MenuItem value="">
						<em>Tất cả loại model</em>
						</MenuItem>
						{equipmentModelsList.map((model) => (
						<MenuItem key={model.id} value={model.id}>
							{model.name}
						</MenuItem>
						))}
					</Select>
					</FormControl>
				</Grid>
				<Grid item xs={6} sm={3} md={2}>
					<TextField
					fullWidth
					size="small"
					type="number"
					label="Thời gian mua"
					name="selectedYear"
					value={selectedYear}
					onChange={handleFilterChange(setSelectedYear)}
					/>
				</Grid>
				<Grid
					item
					xs={12}
					sm={6}
					md={2}
					sx={{ display: "flex", gap: 1, alignItems: "center" }}
				>
					<Button
					variant="outlined"
					onClick={handleResetFilters}
					size="medium"
					sx={{ height: "40px" }}
					>
					Reset
					</Button>
				</Grid>
				</Grid>
			</Paper>	

			{/* Bảng dữ liệu */}
            <Box sx={{ width: '100%' }}>
                 {isPending ? (
                     <Box sx={{ display: 'flex', justifyContent: 'center', p: 5 }}><CircularProgress /></Box>
                 ) : equipmentsForCurrentPage.length === 0 && page === 0 ? (
                      <Typography sx={{ textAlign: 'center', p: 5 }}>No equipments found.</Typography>
                 ) : (
                     // === TRUYỀN PROPS PHÂN TRANG XUỐNG TABLE ===
                    <ManagementEquipmentsTable
						equipments={equipmentsForCurrentPage}     
						totalEquipmentCount={totalEquipmentCount} 
						page={page}                 
						rowsPerPage={rowsPerPage}     
						onPageChange={handleChangePage}
						onRowsPerPageChange={handleChangeRowsPerPage} 
						defaultRoom={roomsList}
                    />
                 )}
            </Box>
			{isPrint && (
				<div className="mt-[100dvh]">
					<EquipmentsReport
						equipments={equipmentsData.equipments}
						forwardedRef={targetRef}
					/>
				</div>
			)}
			<Snackbar
				open={openSnackbar}
				autoHideDuration={3000}
				onClose={handleCloseSnackbar}
			>
				<Alert
					onClose={handleCloseSnackbar}
					severity="success"
					sx={{ width: "100%" }}
				>
					Thiết bị đã được thêm thành công!
				</Alert>
			</Snackbar>
		</div>
	);
};

export default ManagementEquipments;