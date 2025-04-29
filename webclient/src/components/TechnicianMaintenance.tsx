import React, { JSX, FC, useState, ChangeEvent } from "react";
import {
  Box,
  CircularProgress,
  Typography,
  Paper,
  FormControlLabel,
  Checkbox,
} from "@mui/material";
import { useQuery } from "@tanstack/react-query";
import axios, { AxiosError } from "axios";
import MaintenanceTicketTable from "../components/tables/MaintenanceTicketTable";
import ErrorComponent from "../components/Error";
import { useAuth } from "../hooks/useAuth";

const TechnicianMaintenance: FC = (): JSX.Element => {
    const [page, setPage] = useState<number>(0);
    const [rowsPerPage, setRowsPerPage] = useState<number>(10);
  
    const initialStatuses: MaintenanceStatusType[] = [
      "REPORTED",
      "IN_PROGRESS",
    ];
    const [statusFilter, setStatusFilter] = useState<MaintenanceStatusType[]>(initialStatuses);
  
    const auth = useAuth();
  
    const {
      data: apiResponse,
      isPending,
      isError,
      error,
    } = useQuery<PaginatedMaintenanceTicketApiResponse, AxiosError<ErrorMessage>>({
      queryKey: ["maintenanceTickets", page, rowsPerPage, statusFilter],
      queryFn: async () => {
        const token = localStorage.getItem("token");
        if (!token) throw new Error("No token found");
  
        const params = new URLSearchParams();
        params.append("page", String(page));
        params.append("size", String(rowsPerPage));
        statusFilter.forEach((status) => {
          params.append("status", status);
        });
  
        const url = `${import.meta.env.VITE_APP_SERVER_URL}/maintenance?${params.toString()}`;
        console.log("Fetching maintenance tickets with URL:", url);
  
        const response = await axios.get<PaginatedMaintenanceTicketApiResponse>(url, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
  
        if (response.data?.code !== 0 || !response.data?.result?.content || !response.data?.result?.page) {
          throw new Error(response.data?.message || "Invalid API response for maintenance tickets.");
        }
        return response.data;
      },
      keepPreviousData: true,
      enabled: !!auth.user,
    });
  
    const ticketsForCurrentPage: MaintenanceTicketData[] = apiResponse?.result?.content || [];
    const totalTicketCount: number = apiResponse?.result?.page?.totalElements || 0;
  
    const handlePageChange = (_: unknown, newPage: number) => {
      setPage(newPage - 1);
    };
  
    const handleChangeRowsPerPage = (event: ChangeEvent<HTMLInputElement>) => {
      setRowsPerPage(parseInt(event.target.value, 10));
      setPage(0);
    };
  
    const handleStatusFilterChange = (event: ChangeEvent<HTMLInputElement>) => {
      const { name, checked } = event.target;
      setStatusFilter((prev) => {
        if (checked) {
          return [...prev, name as MaintenanceStatusType];
        } else {
          return prev.filter((status) => status !== name);
        }
      });
      setPage(0);
    };
  
    if (!auth.user) {
      return (
        <Box sx={{ mt: 10, textAlign: "center" }}>
          <Typography variant="h6">Bạn chưa đăng nhập.</Typography>
        </Box>
      );
    }
  
    if (isError) {
      const errorData = error?.response?.data || {
        message: error.message,
        status: error.response?.status || 500,
      };
      return (
        <ErrorComponent
          status={errorData.status ?? 500}
          message={errorData.message}
        />
      );
    }
  
    return (
      <Box sx={{ width: "100%", display: "flex", flexDirection: "column", px: { xs: 2, md: 3 }, py: 4, mt: 8, gap: 3 }}>
        <Box sx={{ width: "100%", display: "flex", justifyContent: "space-between", alignItems: "center", flexWrap: "wrap", gap: 2 }}>
          <Typography variant="h4" component="h1">Công việc Bảo trì</Typography>
        </Box>
  
        <Paper sx={{ p: 2, display: "flex", gap: 2, flexWrap: "wrap" }}>
          <Typography variant="subtitle2" sx={{ width: "100%", mb: -1 }}>Lọc theo trạng thái:</Typography>
          {["REPORTED", "IN_PROGRESS", "COMPLETED", "CANNOT_REPAIR", "CANCELLED"].map((statusValue) => (
            <FormControlLabel
              key={statusValue}
              control={
                <Checkbox
                  checked={statusFilter.includes(statusValue)}
                  onChange={handleStatusFilterChange}
                  name={statusValue}
                  size="small"
                />
              }
              label={statusValue.replace(/_/g, " ")}
              sx={{ textTransform: "capitalize" }}
            />
          ))}
        </Paper>
  
        <Box sx={{ width: "100%" }}>
          {isPending && ticketsForCurrentPage.length === 0 ? (
            <Box sx={{ display: "flex", justifyContent: "center", p: 5 }}>
              <CircularProgress />
            </Box>
          ) : ticketsForCurrentPage.length === 0 ? (
            <Typography sx={{ textAlign: "center", p: 5, color: "text.secondary" }}>
              Không có công việc bảo trì nào khớp.
            </Typography>
          ) : (
            <MaintenanceTicketTable
              tickets={ticketsForCurrentPage}
              totalTicketCount={totalTicketCount}
              page={page}
              rowsPerPage={rowsPerPage}
              onPageChange={handlePageChange}
              onRowsPerPageChange={handleChangeRowsPerPage}
            />
          )}
        </Box>
      </Box>
    );
  };
  
  export default TechnicianMaintenance;
  