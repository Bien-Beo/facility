import React, { JSX, FC, useEffect, useRef, useState, ChangeEvent } from "react"; 
import DownloadIcon from "@mui/icons-material/Download";
import InsertInvitationIcon from "@mui/icons-material/InsertInvitation";
import {
    Alert, Box, Button, CircularProgress, Snackbar, Typography, Paper, Grid, FormControl, InputLabel, Select, MenuItem, TextField
} from "@mui/material";
import { useQuery, useQueryClient } from "@tanstack/react-query";
import axios, { AxiosError } from "axios";
import { Options } from "react-to-pdf";

import FacilitiesReport from "../reports/FacilitiesReport"; 
import ErrorComponent from "./Error";                    
import AddFacilityModal from "./modals/AddFacilityModal";   
import AdminFacilitiesTable from "./tables/AdminFacilitiesTable"; 

import html2canvas from 'html2canvas-pro';
import jsPDF from 'jspdf';
const ManagementRooms: FC = (): JSX.Element => {
      // --- State cho Filters ---
      const [selectedBuildingId, setSelectedBuildingId] = useState<string>(""); 
      const [selectedRoomTypeId, setSelectedRoomTypeId] = useState<string>(""); 
      const [selectedYear, setSelectedYear] = useState<string>(
        String(new Date().getFullYear())
      ); 
      const [selectedUserId, setSelectedUserId] = useState<string>("");

    // State cho modal, snackbar, print
    const [isAddFacilityModalOpen, setIsAddFacilityModalOpen] = useState<boolean>(false);
    const [isPrint, setIsPrint] = useState<boolean>(false);
    const [openSnackbar, setOpenSnackbar] = useState<boolean>(false);

    // === STATE CHO PHÂN TRANG ===
    const [page, setPage] = useState<number>(0); 
    const [rowsPerPage, setRowsPerPage] = useState<number>(10); 

    const targetRef = useRef<HTMLDivElement>(null);
    const queryClient = useQueryClient();

    const {
        data: apiResponse,
        isPending,
        isError,
        error,
      } = useQuery<PaginatedRoomApiResponse, AxiosError<ErrorMessage>>({
        queryKey: [
          "adminRooms",
          page,
          rowsPerPage,
          selectedBuildingId,
          selectedRoomTypeId,
          selectedYear,
          selectedUserId,
        ],
        queryFn: async () => {
          const token = localStorage.getItem("token");
          if (!token) throw new Error("No token found");
      
          const params = new URLSearchParams();
          params.append("page", String(page));
          params.append("size", String(rowsPerPage));
          params.append("sort", "createdAt,desc");
          
          if (selectedBuildingId) params.append("buildingId", selectedBuildingId);
          if (selectedRoomTypeId) params.append("roomTypeId", selectedRoomTypeId);
          if (selectedYear) params.append("year", selectedYear);
          if (selectedUserId) params.append("userId", selectedUserId);
      
          const response = await axios.get<PaginatedRoomApiResponse>(
            `${import.meta.env.VITE_APP_SERVER_URL}/rooms?${params.toString()}`,
            {
              headers: {
                Authorization: `Bearer ${token}`,
              },
            }
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
        keepPreviousData: true,
        retry: 1,
      });

    // === Fetch Buildings riêng cho Modal ===
     const { data: buildingsApiResponse } = useQuery<ApiResponse<BuildingData[]>, AxiosError>({
          queryKey: ["allBuildingsList"],
          queryFn: async () => {
              const token = localStorage.getItem("token");
              if (!token) throw new Error("No token found");
               const response = await axios.get<ApiResponse<BuildingData[]>>(
                  `${import.meta.env.VITE_APP_SERVER_URL}/buildings`, 
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

	// === Fetch RoomTypes riêng cho Modal ===
	const { data: roomTypesApiResponse } = useQuery<ApiResponse<RoomTypeData[]>, AxiosError>({
		queryKey: ["allRoomTypesList"],
		queryFn: async () => {
			const token = localStorage.getItem("token");
			if (!token) throw new Error("No token found");
			const response = await axios.get<ApiResponse<RoomTypeData[]>>(
				`${import.meta.env.VITE_APP_SERVER_URL}/roomtypes`, 
				{ headers: { Authorization: `Bearer ${token}` } }
			);
			if (response.data?.code !== 0 || !response.data?.result) {
				throw new Error(response.data?.message || "Invalid response for room types");
			}
			return response.data;
		},
		staleTime: Infinity, 
	});
	const roomTypesList: RoomTypeData[] = roomTypesApiResponse?.result || [];

	// === Fetch FacilityManagers riêng cho Modal ===
	const { data: facilityManagersApiResponse } = useQuery<ApiResponse<UserData[]>, AxiosError>({
		queryKey: ["allFacilityManagersList"],
		queryFn: async () => {
			const token = localStorage.getItem("token");
			if (!token) throw new Error("No token found");
			const response = await axios.get<ApiResponse<UserData[]>>(
				`${import.meta.env.VITE_APP_SERVER_URL}/users/fm`, 
				{ headers: { Authorization: `Bearer ${token}` } }
			);
			if (response.data?.code !== 0 || !response.data?.result) {
				throw new Error(response.data?.message || "Invalid response for facility managers");
			}
			return response.data;
		},
		staleTime: Infinity, 
	});
	const facilityManagersList: UserData[] = facilityManagersApiResponse?.result || [];

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

    const handleExportPdfClick = () => {
        // Chỉ set isPrint thành true để trigger useEffect bên dưới
        console.log("Export button clicked, setting isPrint to true");
        setIsPrint(true);
    };

    // --- useEffect để xử lý việc tạo PDF SAU KHI report đã render ---
    useEffect(() => {
        // Chỉ chạy khi isPrint là true VÀ targetRef đã được gắn vào DOM element
        if (isPrint && targetRef.current) {
            const elementToCapture = targetRef.current;
            console.log("Starting html2canvas capture because isPrint is true and ref is set.");

            const canvasOptions = { scale: 2, useCORS: true };
            const pdfOptions = { orientation: 'landscape', unit: 'pt', format: 'a4' };
            const filename = "facilities-report.pdf";

            html2canvas(elementToCapture, canvasOptions).then((canvas) => {
                console.log("Canvas generated by html2canvas");
                try {
                    const imgData = canvas.toDataURL('image/png');
                    const pdf = new jsPDF(pdfOptions.orientation, pdfOptions.unit, pdfOptions.format);
                    const imgProps = pdf.getImageProperties(imgData);
                    const pdfWidth = pdf.internal.pageSize.getWidth();
                    const pdfHeight = pdf.internal.pageSize.getHeight();
                    const ratio = Math.min(pdfWidth / imgProps.width, pdfHeight / imgProps.height);
                    const imgX = (pdfWidth - imgProps.width * ratio) / 2;
                    const imgY = 10;
                    pdf.addImage(imgData, 'PNG', imgX, imgY, imgProps.width * ratio, imgProps.height * ratio);
                    pdf.save(filename);
                    console.log("PDF saved");
                } catch (pdfError) {
                    console.error("Error generating PDF from canvas:", pdfError);
                } finally {
                    // Tự động tắt trạng thái print sau khi xử lý xong
                    console.log("Resetting isPrint to false");
                    setIsPrint(false);
                }
            }).catch(canvasError => {
                 console.error("html2canvas failed:", canvasError);
                 setIsPrint(false); // Cũng reset nếu canvas lỗi
            });
        }
        // Thêm điều kiện này để xử lý trường hợp isPrint là true nhưng ref chưa sẵn sàng (ít xảy ra)
        else if (isPrint && !targetRef.current) {
             console.error("PDF Target Ref not available yet after setting isPrint to true. Resetting.");
             setIsPrint(false);
        }
        // Nếu isPrint là false thì không làm gì cả
    }, [isPrint]); // Hook này chạy lại mỗi khi isPrint thay đổi

    // --- Render Logic ---
    if (isError) {
         const errorData = error?.response?.data || { message: error?.message || "Unknown error", status: error?.response?.status || 500 };
         return (<ErrorComponent status={errorData.status!} message={errorData.message}/> );
    }

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
            setSelectedBuildingId("");
            setSelectedRoomTypeId("");
            setSelectedYear(String(new Date().getFullYear()));
            setSelectedUserId("");
            setPage(0); 
          };          

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
                    onSuccessCallback={() => { 
                        queryClient.invalidateQueries({ queryKey: ["adminRooms"] });
                    }}
                />
            )}

            <Typography variant="h3" component="h1">Quản lý phòng</Typography>

             {/* Các nút Add, Export */}
             <Box className="w-full flex justify-between items-center">
                 <Button variant="contained" color="primary" startIcon={<InsertInvitationIcon />} sx={{ paddingX: "2em", height: "45px" }} size="large" onClick={() => setIsAddFacilityModalOpen(true)}>
                     Thêm phòng
                 </Button>
                 <Button variant="contained" color="secondary" endIcon={<DownloadIcon />} sx={{ paddingX: "2em", height: "45px" }} size="large" onClick={handleExportPdfClick}>
                    Xuất PDF
                </Button>
             </Box>
             
             <Paper sx={{ p: 2, mb: 2 }}>
                     {" "}
                     {/* Bọc filter trong Paper */}
                     <Grid container spacing={2} alignItems="center">
                       <Grid item xs={12} sm={6} md={2.5}>
                         <FormControl size="small" fullWidth>
                           <InputLabel id="building-filter-label">Lọc theo tòa nhà</InputLabel>
                           <Select
                             labelId="building-filter-label"
                             label="Lọc theo tòa nhà"
                             name="selectedBuildingId"
                             value={selectedBuildingId}
                             onChange={handleFilterChange(setSelectedBuildingId)}
                           >
                             <MenuItem value="">
                               <em>Tất cả tòa nhà</em>
                             </MenuItem>
                             {buildingsList.map((building) => (
                               <MenuItem key={building.id} value={building.id}>
                                 {building.name}
                               </MenuItem>
                             ))}
                           </Select>
                         </FormControl>
                       </Grid>
                       <Grid item xs={12} sm={6} md={2.5}>
                         <FormControl size="small" fullWidth>
                           <InputLabel id="roomTypes-filter-label">Lọc theo loại phòng</InputLabel>
                           <Select
                             labelId="roomTypes-filter-label"
                             label="Lọc theo loại phòng"
                             value={selectedRoomTypeId}
                             onChange={handleFilterChange(setSelectedRoomTypeId)}
                           >
                             <MenuItem value="">
                               <em>Tất cả loại phòng</em>
                             </MenuItem>
                             {roomTypesList.map((roomType) => (
                               <MenuItem key={roomType.id} value={roomType.id}>
                                 {roomType.name}
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
                           label="Năm xây dựng"
                           name="selectedYear"
                           value={selectedYear}
                           onChange={handleFilterChange(setSelectedYear)}
                         />
                       </Grid>
                       <Grid item xs={12} sm={6} md={3}>
                         <FormControl size="small" fullWidth>
                           <InputLabel id="user-filter-label">Lọc theo người quản lý</InputLabel>
                           <Select
                             labelId="user-filter-label"
                             label="Lọc theo người quản lý"
                             name="selectedUserId"
                             value={selectedUserId}
                             onChange={handleFilterChange(setSelectedUserId)}
                           >
                             <MenuItem value="">
                               <em>Tất cả người quản lý</em>
                             </MenuItem>
                             {facilityManagersList.map((user) => (
                               <MenuItem key={user.id} value={user.id}>
                                 {user.fullName || user.username} 
                               </MenuItem>
                             ))}
                           </Select>
                         </FormControl>
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
                 ) : roomsForCurrentPage.length === 0 && page === 0 ? (
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
                          buildings={buildingsList} 
					                roomTypes={roomTypesList}
					                facilityManagers={facilityManagersList}
                     />
                 )}
            </Box>

            {/* PDF Target */}
            {/* Chỉ nên render report component khi isPrint=true và có dữ liệu */}
            {isPrint && roomsForCurrentPage.length > 0 && (
                 <div ref={targetRef} className="absolute -left-[9999px] top-0">
                     <FacilitiesReport rooms={roomsForCurrentPage} forwardedRef={targetRef} />
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

export default ManagementRooms;