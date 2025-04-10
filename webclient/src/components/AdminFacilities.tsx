// import DownloadIcon from "@mui/icons-material/Download";
// import InsertInvitationIcon from "@mui/icons-material/InsertInvitation";
// import {
// 	Alert,
// 	Button,
// 	CircularProgress,
// 	Snackbar,
// 	Typography,
// } from "@mui/material";
// import { useQuery } from "@tanstack/react-query";
// import axios from "axios";
// import { JSX, FC, useEffect, useRef, useState } from "react";
// import generatePDF, { Options } from "react-to-pdf";

// import FacilitiesReport from "../reports/FacilitiesReport";
// import ErrorComponent from "./Error";
// import AddFacilityModal from "./modals/AddFacilityModal";
// import AdminFacilitiesTable from "./tables/AdminFacilitiesTable";

// const AdminFacilities: FC = (): JSX.Element => {
// 	const [facilitiesData, setFacilitiesData] =
// 		useState<AdminRoomsTableProps>({
// 			rooms: [],
// 			totalRoomCount: 0,
// 			page: 0,
// 			rowsPerPage: 0,
// 			onPageChange: () => {},
// 			onRowsPerPageChange: () => {},
// 		});
// 	const [isAddFacilityModalOpen, setIsAddFacilityModalOpen] =
// 		useState<boolean>(false);
// 	const [isPrint, setIsPrint] = useState<boolean>(false);
// 	const [openSnackbar, setOpenSnackbar] = useState<boolean>(false);

// 	const targetRef = useRef<HTMLDivElement>(null);

// 	const { data, isPending, isError, error } = useQuery({
// 		queryKey: ["adminfacilities"],
// 		queryFn: async () => {
//             const token = localStorage.getItem("token");
//             if (!token) throw new Error("No token found");
// 			const response = await axios.get(
// 				`${import.meta.env.VITE_APP_SERVER_URL}/admin/rooms`,
// 				{ headers: { Authorization: `Bearer ${token}` } }
// 			);
// 			return response.data;
// 		},
// 		refetchInterval: 5 * 1000,
// 		retry: 1,
// 		gcTime: 0,
// 	});

// 	const handleCloseSnackbar = (): void => {
// 		setOpenSnackbar(false);
// 	};

// 	useEffect(() => {
// 		if (!isPending) {
// 			setFacilitiesData(data);
// 		}
// 	}, [data, isPending]);

// 	useEffect(() => {
// 		if (isPrint) {
// 			setTimeout(() => {
// 				setIsPrint(false);
// 			}, 3000);
// 		}

// 		if (isPrint) {
// 			document.body.style.overflowY = "hidden";
// 		} else {
// 			document.body.style.overflowY = "auto";
// 		}
// 	}, [isPrint]);

// 	if (isError) {
// 		const errorData = error.response!.data as ErrorMessage;
// 		return (
// 			<ErrorComponent
// 				status={errorData.status!}
// 				message={errorData.message}
// 			/>
// 		);
// 	}

// 	if (isPending)
// 		return (
// 			<div className="w-[74vw] min-h-screen h-full flex flex-col items-center justify-center">
// 				<CircularProgress />
// 			</div>
// 		);

// 	const options: Options = {
// 		filename: "admin-facilities-bookings-report.pdf",
// 		page: {
// 			orientation: "landscape",
// 		},
// 	};

// 	return (
// 		<div className="w-full flex flex-col px-6 pt-8 gap-6">
// 			{isAddFacilityModalOpen && (
// 				<AddFacilityModal
// 					isOpen={isAddFacilityModalOpen}
// 					setIsOpen={setIsAddFacilityModalOpen}
// 					setOpenSnackbar={setOpenSnackbar}
// 					//buildings={facilitiesData.buildings!}
// 				/>
// 			)}

// 			<Typography variant="h3" component="h1">
// 				Manage facilities
// 			</Typography>
// 			<div className="w-full flex justify-between items-center">
// 				<Button
// 					variant="contained"
// 					color="primary"
// 					endIcon={
// 						<InsertInvitationIcon
// 							sx={{ height: "20px", width: "20px" }}
// 						/>
// 					}
// 					sx={{ paddingX: "2em", height: "45px" }}
// 					size="large"
// 					onClick={() => {
// 						setIsAddFacilityModalOpen(true);
// 					}}
// 				>
// 					Add facility
// 				</Button>

// 				<Button
// 					variant="contained"
// 					color="primary"
// 					endIcon={
// 						<DownloadIcon sx={{ height: "20px", width: "20px" }} />
// 					}
// 					sx={{ paddingX: "2em", height: "45px" }}
// 					size="large"
// 					onClick={() => {
// 						setIsPrint(true);
// 						setTimeout(() => {
// 							generatePDF(targetRef, options);
// 						}, 1000);
// 					}}
// 				>
// 					Export
// 				</Button>
// 			</div>
// 			{!isPending && (
// 				<AdminFacilitiesTable
// 					rooms={facilitiesData.rooms}
// 					buildings={facilitiesData.buildings}
// 				/>
// 			)}
// 			{isPrint && (
// 				<div className="mt-[100dvh]">
// 					<FacilitiesReport
// 						facilities={facilitiesData.rooms}
// 						forwardedRef={targetRef}
// 					/>
// 				</div>
// 			)}
// 			<Snackbar
// 				open={openSnackbar}
// 				autoHideDuration={3000}
// 				onClose={handleCloseSnackbar}
// 			>
// 				<Alert
// 					onClose={handleCloseSnackbar}
// 					severity="success"
// 					sx={{ width: "100%" }}
// 				>
// 					Facility added successfully!
// 				</Alert>
// 			</Snackbar>
// 		</div>
// 	);
// };

// export default AdminFacilities;

import React, { JSX, FC, useEffect, useRef, useState, ChangeEvent } from "react"; 
import DownloadIcon from "@mui/icons-material/Download";
import InsertInvitationIcon from "@mui/icons-material/InsertInvitation";
import {
    Alert, Box, Button, CircularProgress, Snackbar, Typography
} from "@mui/material";
import { useQuery, useQueryClient } from "@tanstack/react-query";
import axios, { AxiosError } from "axios";
import generatePDF, { Options } from "react-to-pdf";

import FacilitiesReport from "../reports/FacilitiesReport"; 
import ErrorComponent from "./Error";                    
import AddFacilityModal from "./modals/AddFacilityModal";   
import AdminFacilitiesTable from "./tables/AdminFacilitiesTable"; 

// --- Component AdminFacilities ---
const AdminFacilities: FC = (): JSX.Element => {

    // State cho modal, snackbar, print
    const [isAddFacilityModalOpen, setIsAddFacilityModalOpen] = useState<boolean>(false);
    const [isPrint, setIsPrint] = useState<boolean>(false);
    const [openSnackbar, setOpenSnackbar] = useState<boolean>(false);

    // === STATE CHO PHÂN TRANG ===
    const [page, setPage] = useState<number>(0); // Trang hiện tại (bắt đầu từ 0)
    const [rowsPerPage, setRowsPerPage] = useState<number>(10); // Số dòng/trang

    const targetRef = useRef<HTMLDivElement>(null);
    const queryClient = useQueryClient();

    // === SỬA useQuery ĐỂ LẤY DỮ LIỆU PHÂN TRANG ===
    const { data: apiResponse, isPending, isLoading, isError, error } = useQuery<PaginatedRoomApiResponse, AxiosError<ErrorMessage>>({
        // Query key giờ bao gồm cả page và rowsPerPage để React Query tự fetch lại khi chúng thay đổi
        queryKey: ["adminRooms", page, rowsPerPage], // Đổi key nếu cần
        queryFn: async () => {
            const token = localStorage.getItem("token");
            if (!token) throw new Error("No token found");
            console.log(`Workspaceing admin rooms - Page: ${page}, Size: ${rowsPerPage}`);
            // Gọi API với tham số phân trang
            const response = await axios.get<PaginatedRoomApiResponse>(
                `${import.meta.env.VITE_APP_SERVER_URL}/admin/rooms?page=${page}&size=${rowsPerPage}&sort=createdAt,desc`, // Thêm tham số page/size
                { headers: { Authorization: `Bearer ${token}` } }
            );
             // Kiểm tra response cơ bản
             if (response.data?.code !== 0 || !response.data?.result?.content || !response.data?.result?.page) {
                  console.error("Invalid API response structure:", response.data);
                  throw new Error("Cấu trúc API response không hợp lệ.");
             }
            return response.data;
        },
        keepPreviousData: true, 
        retry: 1,
    });

    // === Fetch Buildings riêng cho Modal ===
    // Bạn cần đảm bảo có API endpoint /buildings hoặc cách khác để lấy dữ liệu này
     const { data: buildingsApiResponse } = useQuery<ApiResponse<BuildingData[]>, AxiosError>({
          queryKey: ["allBuildingsList"],
          queryFn: async () => {
              const token = localStorage.getItem("token");
              if (!token) throw new Error("No token found");
               const response = await axios.get<ApiResponse<BuildingData[]>>(
                  `${import.meta.env.VITE_APP_SERVER_URL}/buildings`, // Endpoint ví dụ
                  { headers: { Authorization: `Bearer ${token}` } }
              );
              if (response.data?.code !== 0 || !response.data?.result) {
                  throw new Error(response.data?.message || "Invalid response for buildings");
              }
              return response.data;
          },
          staleTime: Infinity, 
     });
     const buildingsList: BuildingData[] = buildingsApiResponse?.result?.content || [];

    // === Trích xuất dữ liệu cần thiết từ API Response ===
    const roomsForCurrentPage: RoomData[] = apiResponse?.result?.content || [];
    const totalRoomCount: number = apiResponse?.result?.page?.totalElements || 0;

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

    // Logic PDF và Snackbar giữ nguyên
    const handleCloseSnackbar = (): void => { setOpenSnackbar(false); };
    useEffect(() => { /* ... isPrint logic ... */ }, [isPrint]);
    const options: Options = { filename: "admin-facilities-report.pdf", page: { orientation: "landscape" } };

    // --- Render Logic ---
    if (isError) {
         const errorData = error?.response?.data || { message: error?.message || "Unknown error", status: error?.response?.status || 500 };
         return (<ErrorComponent status={errorData.status!} message={errorData.message}/> );
    }

    // isPending được dùng trực tiếp bên dưới

    return (
        <Box className="w-full flex flex-col px-6 pt-8 gap-6">
            {/* Modal Add Facility */}
            {isAddFacilityModalOpen && (
                <AddFacilityModal
                    isOpen={isAddFacilityModalOpen}
                    setIsOpen={setIsAddFacilityModalOpen}
                    setOpenSnackbar={setOpenSnackbar}
                    buildings={buildingsList} 
					roomTypes={roomTypesList}
					facilityManagers={facilityManagersList}
                    onSuccessCallback={() => { // Thêm callback để refresh bảng
                        queryClient.invalidateQueries({ queryKey: ["adminRooms"] });
                    }}
                />
            )}

            <Typography variant="h3" component="h1">Manage Rooms</Typography>

             {/* Các nút Add, Export */}
             <Box className="w-full flex justify-between items-center">
                 <Button variant="contained" color="primary" startIcon={<InsertInvitationIcon />} sx={{ paddingX: "2em", height: "45px" }} size="large" onClick={() => setIsAddFacilityModalOpen(true)}>
                     Add Room
                 </Button>
                 <Button variant="contained" color="secondary" endIcon={<DownloadIcon />} sx={{ paddingX: "2em", height: "45px" }} size="large" onClick={() => { setIsPrint(true); setTimeout(() => { generatePDF(targetRef, options); }, 1000); }}>
                     Export PDF
                 </Button>
             </Box>

            {/* Bảng dữ liệu */}
            <Box sx={{ width: '100%' }}>
                 {/* Dùng isPending từ useQuery để hiển thị loading */}
                 {isPending ? (
                     <Box sx={{ display: 'flex', justifyContent: 'center', p: 5 }}><CircularProgress /></Box>
                 ) : roomsForCurrentPage.length === 0 && page === 0 ? ( // Chỉ báo không có nếu ở trang đầu tiên
                      <Typography sx={{ textAlign: 'center', p: 5 }}>No rooms found.</Typography>
                 ) : (
                     // === TRUYỀN PROPS PHÂN TRANG XUỐNG TABLE ===
                     <AdminFacilitiesTable
                          rooms={roomsForCurrentPage}      // <<< Chỉ dữ liệu trang này
                          totalRoomCount={totalRoomCount} // <<< Tổng số lượng
                          page={page}                  // <<< Trang hiện tại
                          rowsPerPage={rowsPerPage}      // <<< Số dòng/trang
                          onPageChange={handleChangePage} // <<< Handler đổi trang
                          onRowsPerPageChange={handleChangeRowsPerPage} // <<< Handler đổi số dòng
                     />
                 )}
            </Box>

            {/* PDF Target */}
            {/* Chỉ nên render report component khi isPrint=true và có dữ liệu */}
            {isPrint && roomsForCurrentPage.length > 0 && (
                 <div ref={targetRef} className="absolute -left-[9999px] top-0">
                     {/* Cân nhắc: facilitiesReport nên nhận toàn bộ data hay chỉ trang hiện tại? */}
                     <FacilitiesReport facilities={roomsForCurrentPage} />
                 </div>
             )}

            {/* Snackbar */}
            <Snackbar open={openSnackbar} autoHideDuration={3000} onClose={handleCloseSnackbar} anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}>
                <Alert onClose={handleCloseSnackbar} severity="success" variant="filled" sx={{ width: "100%" }}>
                    Facility action successful! {/* Thông báo chung chung hơn */}
                </Alert>
            </Snackbar>
        </Box>
    );
};

export default AdminFacilities;