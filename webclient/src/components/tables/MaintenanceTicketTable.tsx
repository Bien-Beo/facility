import React, { JSX, FC, useState, useMemo } from "react";
import {
  Paper, Table, TableBody, TableCell, TableContainer, TableHead,
  TablePagination, TableRow, Chip, Tooltip, IconButton, Box, Typography, Button
} from "@mui/material";
import EditIcon from '@mui/icons-material/Edit'; 
import VisibilityIcon from '@mui/icons-material/Visibility'; 
import * as XLSX from 'xlsx';
import { saveAs } from 'file-saver';

import isoToDate from "../../utils/isoToDate";
import isoToTime from "../../utils/isoToTime";
import UpdateMaintenanceModal from '../modals/UpdateMaintenanceModal';
import ViewTicketDetailsModal from '../modals/ViewTicketDetailsModal';

// --- Helper: Trạng thái bảo trì thành Chip ---
const getMaintenanceStatusChip = (status: MaintenanceStatusType | string): JSX.Element => {
  const statusMap: Record<string, { label: string, color: "error" | "warning" | "info" | "success" | "default" }> = {
    REPORTED: { label: "Mới báo cáo", color: "error" },
    ASSIGNED: { label: "Đã giao KTV", color: "warning" },
    IN_PROGRESS: { label: "Đang xử lý", color: "info" },
    COMPLETED: { label: "Đã hoàn thành", color: "success" },
    CANNOT_REPAIR: { label: "Không sửa được", color: "default" },
    CANCELLED: { label: "Đã hủy", color: "default" }
  };

  const entry = statusMap[status] || { label: status?.replace(/_/g, ' ') || 'N/A', color: 'default' };

  return (
    <Chip
      label={entry.label}
      color={entry.color}
      size="small"
      sx={{ textTransform: 'capitalize' }}
    />
  );
};

// --- Cấu hình cột ---
const columns: readonly MaintenanceTicketColumnData[] = [
  { id: "roomName", label: "Phòng", minWidth: 170 },
    { id: "modelName", label: "Thiết bị", minWidth: 170 },
  { id: "description", label: "Mô tả sự cố", minWidth: 200 },
  { id: "status", label: "Trạng thái", minWidth: 120, align: 'center' },
  { id: "reporter", label: "Người báo cáo", minWidth: 130 },
  { id: "reportedAt", label: "Thời gian báo cáo", minWidth: 150 },
  { id: "technician", label: "KTV được giao", minWidth: 130 },
  { id: "actions", label: "Hành động", minWidth: 100, align: 'center' },
];

// --- Component chính ---
const MaintenanceTicketTable: FC<MaintenanceTicketTableProps> = ({
  tickets,
  totalTicketCount,
  page,
  rowsPerPage,
  onPageChange,
  onRowsPerPageChange,
}): JSX.Element => {

  const [isUpdateModalOpen, setIsUpdateModalOpen] = useState(false);
  const [isViewModalOpen, setIsViewModalOpen] = useState(false);
  const [selectedTicket, setSelectedTicket] = useState<MaintenanceTicketData | null>(null);

  const rows: MaintenanceTicketRowData[] = useMemo(() => (
    tickets?.map((ticket) => ({
      id: ticket.id,
      roomName: ticket.roomName || 'N/A',
      modelName: ticket.modelName || 'N/A',
      description: (
        <Tooltip title={ticket.description || ''} placement="top-start">
          <Typography variant="body2" noWrap sx={{ maxWidth: 250, overflow: 'hidden', textOverflow: 'ellipsis' }}>
            {ticket.description || 'N/A'}
          </Typography>
        </Tooltip>
      ),
      status: getMaintenanceStatusChip(ticket.status as MaintenanceStatusType),
      reporter: ticket.reportByUser || 'N/A',
      reportedAt: ticket.reportDate ? (
        <>
          {isoToDate(ticket.reportDate)}<br />{isoToTime(ticket.reportDate)}
        </>
      ) : 'N/A',
      technician: ticket.technicianName  || 'Chưa giao',
      actions: (
        <Box sx={{ display: 'flex', justifyContent: 'center', gap: 0.5 }}>
          <Tooltip title="Cập nhật trạng thái / Lên lịch">
            <IconButton
              size="small"
              color="primary"
              onClick={() => {
                  setSelectedTicket(ticket);
                  setIsUpdateModalOpen(true);   
              }}
              aria-label={`update-ticket-${ticket.id}`}
            >
              <EditIcon fontSize="small" />
            </IconButton>
          </Tooltip>
          <Tooltip title="Xem chi tiết Ticket">
            <IconButton
              size="small"
              color="info"
              onClick={() => {
                setSelectedTicket(ticket);
                setIsViewModalOpen(true);
              }}              
              aria-label={`view-details-ticket-${ticket.id}`}
            >
              <VisibilityIcon fontSize="small" />
            </IconButton>
          </Tooltip>
        </Box>
      ),
    })) || []
  ), [tickets]);

  const handleExportExcel = () => {
    const exportData = tickets.map((ticket) => ({
      "Phòng": ticket.roomName || 'N/A',
      "Thiết bị": ticket.modelName || 'N/A',
      "Mô tả sự cố": ticket.description || 'N/A',
      "Trạng thái": statusLabelMap[ticket.status as MaintenanceStatusType] || ticket.status,
      "Người báo cáo": ticket.reportByUser || 'N/A',
      "Ngày báo cáo": isoToDate(ticket.reportDate),
      "Giờ báo cáo": isoToTime(ticket.reportDate),
      "KTV được giao": ticket.technicianName || 'Chưa giao',
    }));
  
    const worksheet = XLSX.utils.json_to_sheet(exportData);
    const workbook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(workbook, worksheet, "Maintenance Tickets");
  
    const excelBuffer = XLSX.write(workbook, { bookType: "xlsx", type: "array" });
    const data = new Blob([excelBuffer], { type: "application/octet-stream" });
    saveAs(data, `danh-sach-bao-tri_${new Date().toISOString().slice(0, 10)}.xlsx`);
  };
  
  // Bản đồ trạng thái thuần text để đưa vào Excel
  const statusLabelMap: Record<string, string> = {
    REPORTED: "Mới báo cáo",
    ASSIGNED: "Đã giao KTV",
    IN_PROGRESS: "Đang xử lý",
    COMPLETED: "Đã hoàn thành",
    CANNOT_REPAIR: "Không sửa được",
    CANCELLED: "Đã hủy",
  };

  return (
    <Paper sx={{ width: "100%", overflow: "hidden" }}>
      {isUpdateModalOpen && selectedTicket && (
        <UpdateMaintenanceModal
          open={isUpdateModalOpen}
          onClose={() => setIsUpdateModalOpen(false)}
          ticketData={selectedTicket}
        />
      )}
      
      {isViewModalOpen && selectedTicket && (
        <ViewTicketDetailsModal
          open={isViewModalOpen}
          onClose={() => setIsViewModalOpen(false)}
          ticketData={selectedTicket}
        />
      )}

      <Box sx={{ p: 2, display: 'flex', justifyContent: 'flex-end' }}>
        <Button variant="contained" color="success" onClick={handleExportExcel}>
          Xuất Excel
        </Button>
      </Box>

      <TableContainer sx={{ maxHeight: 650 }}>
        <Table stickyHeader size="small">
          <TableHead>
            <TableRow>
              {columns.map((column) => (
                <TableCell
                  key={column.id}
                  align={column.align || "left"}
                  sx={{ backgroundColor: "grey.200", fontWeight: 'bold', py: 1 }}
                >
                  {column.label}
                </TableCell>
              ))}
            </TableRow>
          </TableHead>

          <TableBody>
            {rows.map((row) => (
              <TableRow hover tabIndex={-1} key={row.id}>
                {columns.map((column) => {
                  const value = row[column.id as keyof MaintenanceTicketRowData];
                  return (
                    <TableCell key={column.id} align={column.align || "left"} sx={{ py: 0.8 }}>
                      {value ?? 'N/A'}
                    </TableCell>
                  );
                })}
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
      <TablePagination
        rowsPerPageOptions={[10, 25, 50]}
        component="div"
        count={totalTicketCount}
        rowsPerPage={rowsPerPage}
        page={page}
        onPageChange={onPageChange}
        onRowsPerPageChange={onRowsPerPageChange}
      />
    </Paper>
  );
};

export default MaintenanceTicketTable;
