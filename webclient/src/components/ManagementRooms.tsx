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

import html2canvas from 'html2canvas-pro';
import jsPDF from 'jspdf';

// --- Component AdminFacilities ---
const ManagementRooms: FC = (): JSX.Element => {

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

            <Typography variant="h3" component="h1">Quản lý phòng</Typography>

             {/* Các nút Add, Export */}
             <Box className="w-full flex justify-between items-center">
                 <Button variant="contained" color="primary" startIcon={<InsertInvitationIcon />} sx={{ paddingX: "2em", height: "45px" }} size="large" onClick={() => setIsAddFacilityModalOpen(true)}>
                     Thêm phòng
                 </Button>
                 {/* <Button variant="contained" color="secondary" endIcon={<DownloadIcon />} sx={{ paddingX: "2em", height: "45px" }} size="large" onClick={handleExportPdf}>
                     Export PDF
                 </Button> */}
                 <Button variant="contained" color="secondary" endIcon={<DownloadIcon />} sx={{ paddingX: "2em", height: "45px" }} size="large" onClick={handleExportPdfClick}>
                    Xuất PDF
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