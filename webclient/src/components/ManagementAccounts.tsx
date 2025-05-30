import React, { JSX, FC, useState, ChangeEvent } from "react"; 
import InsertInvitationIcon from "@mui/icons-material/InsertInvitation";
import {
    Alert, Box, Button, CircularProgress, Snackbar, Typography, Paper, Grid, FormControl, InputLabel, Select, MenuItem, TextField
} from "@mui/material";
import { SelectChangeEvent } from "@mui/material/Select";
import { useQuery, useQueryClient } from "@tanstack/react-query";
import axios, { AxiosError } from "axios";

import ErrorComponent from "./Error";                    
import AddAccountModal from "./modals/AddAccountModal";   
import AdminAccountsTable from "./tables/AdminAccountsTable"; 

const ManagementAccounts: FC = (): JSX.Element => {
      // --- State cho Filters ---
      const [selectedYear, setSelectedYear] = useState<string>(
        String(new Date().getFullYear())
      ); 

    // State cho modal, snackbar, print
    const [isAddAccountModalOpen, setIsAddAccountModalOpen] = useState<boolean>(false);
    const [openSnackbar, setOpenSnackbar] = useState<boolean>(false);

    // === STATE CHO PHÂN TRANG ===
    const [page, setPage] = useState<number>(0); 
    const [rowsPerPage, setRowsPerPage] = useState<number>(10); 

    const queryClient = useQueryClient();

    const {
        data: apiResponse,
        isPending,
        isError,
        error,
      } = useQuery<PaginatedUserApiResponse, AxiosError<ErrorMessage>>({
        queryKey: [
          "adminAccounts",
          page,
          rowsPerPage,
          selectedYear
        ],
        queryFn: async () => {
          const token = localStorage.getItem("token");
          if (!token) throw new Error("No token found");
      
          const params = new URLSearchParams();
          params.append("page", String(page));
          params.append("size", String(rowsPerPage));
          params.append("sort", "createdAt,desc");
          if (selectedYear) params.append("year", selectedYear);

          const response = await axios.get<PaginatedUserApiResponse>(
            `${import.meta.env.VITE_APP_SERVER_URL}/users?${params.toString()}`,
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

    // === Trích xuất dữ liệu cần thiết từ API Response ===
    const accountsForCurrentPage: UserData[] = apiResponse?.result?.content || [];
    const totalAccountCount: number = apiResponse?.result?.page?.totalElements || 0;

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
            setSelectedYear(String(new Date().getFullYear()));
            setPage(0); 
          };
          
        const handleCloseSnackbar = (): void => { setOpenSnackbar(false); };

    return (
        <Box className="w-full flex flex-col px-6 pt-8 gap-6">
            {/* Modal Add Account */}
            {isAddAccountModalOpen && (
                <AddAccountModal
                    isOpen={isAddAccountModalOpen}
                    setIsOpen={setIsAddAccountModalOpen}
                    setOpenSnackbar={setOpenSnackbar}
                    onSuccessCallback={() => {
                        queryClient.invalidateQueries({ queryKey: ["adminAccounts"] });
                    }}
                />
            )}

            <Typography variant="h3" component="h1">Quản lý tài khoản</Typography>

             {/* Các nút Add, Export */}
             <Box className="w-full flex justify-between items-center">
                 <Button variant="contained" color="primary" startIcon={<InsertInvitationIcon />} sx={{ paddingX: "2em", height: "45px" }} size="large" onClick={() => setIsAddAccountModalOpen(true)}>
                     Thêm tài khoản
                 </Button>
             </Box>
             
             <Paper sx={{ p: 2, mb: 2 }}>
                     {" "}
                     {/* Bọc filter trong Paper */}
                     <Grid container spacing={2} alignItems="center">
                       <Grid item xs={6} sm={3} md={2}>
                         <TextField
                           fullWidth
                           size="small"
                           type="number"
                           label="Thời gian tạo (năm)"
                           InputLabelProps={{ shrink: true }}
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
                 ) : accountsForCurrentPage.length === 0 && page === 0 ? (
                      <Typography sx={{ textAlign: 'center', p: 5 }}>No accounts found.</Typography>
                 ) : (
                     // === TRUYỀN PROPS PHÂN TRANG XUỐNG TABLE ===
                     <AdminAccountsTable
                          users={accountsForCurrentPage}      // <<< Chỉ dữ liệu trang này
                          totalUserCount={totalAccountCount} // <<< Tổng số lượng
                          page={page}                  // <<< Trang hiện tại
                          rowsPerPage={rowsPerPage}      // <<< Số dòng/trang
                          onPageChange={handleChangePage} // <<< Handler đổi trang
                          onRowsPerPageChange={handleChangeRowsPerPage} // <<< Handler đổi số dòng
                     />
                 )}
            </Box>

            {/* Snackbar */}
            <Snackbar open={openSnackbar} autoHideDuration={3000} onClose={handleCloseSnackbar} anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}>
                <Alert onClose={handleCloseSnackbar} severity="success" variant="filled" sx={{ width: "100%" }}>
                    Account action successful! {/* Thông báo chung chung hơn */}
                </Alert>
            </Snackbar>
        </Box>
    );
};

export default ManagementAccounts;