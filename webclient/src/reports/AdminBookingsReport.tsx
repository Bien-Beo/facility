import React, { JSX, FC, useMemo } from "react";
import Paper from "@mui/material/Paper";
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell from "@mui/material/TableCell";
import TableContainer from "@mui/material/TableContainer";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import { Typography, Box } from "@mui/material"; 

import isoToTime from "../utils/isoToTime";
import isoToDate from "../utils/isoToDate"; 

const columns: readonly ReportBookingColumnData[] = [
    { id: "userName", label: "Người đặt", minWidth: 130 },
    { id: "roomName", label: "Phòng", minWidth: 80 },
    { id: "purpose", label: "Mục đích", minWidth: 150 },
    { id: "plannedTime", label: "Thời gian dự kiến", minWidth: 150 },
    { id: "requestedAt", label: "Yêu cầu lúc", minWidth: 150 },
    { id: "status", label: "Trạng thái", minWidth: 120 },
    { id: "processedBy", label: "Người xử lý", minWidth: 130 },
    { id: "reasonOrNote", label: "Lý do/Ghi chú", minWidth: 170 },
];

// --- Hàm Helper định dạng Status ---
const getDisplayStatus = (status: BookingStatusType | string): string => {
    switch(status) {
       case "PENDING_APPROVAL": return "Chờ duyệt";
       case "CONFIRMED": return "Đã duyệt";
       case "REJECTED": return "Bị từ chối";
       case "CANCELLED": return "Đã hủy";
       case "COMPLETED": return "Đã hoàn thành";
       case "IN_PROGRESS": return "Đang sử dụng";
       case "OVERDUE": return "Quá hạn";
       default: return status?.replace(/_/g, ' ') || 'N/A';
    }
};

// --- Component ---
const AdminBookingsReport: FC<AdminBookingsReportProps> = ({
    bookings, 
    forwardedRef,
}): JSX.Element => {

    // Map BookingEntry sang ReportBookingRowData
    const rows: ReportBookingRowData[] = useMemo(() =>
        bookings?.map((booking) => {
            let processedByInfo: string | null = null;
            // Ưu tiên hiển thị người hủy hoặc người duyệt/từ chối cuối cùng
            if (booking.status === "CANCELLED" && booking.cancelledByUserName) {
                processedByInfo = `Hủy bởi: ${booking.cancelledByUserName}`;
            } else if (booking.approvedByUserName) {
                 processedByInfo = `${booking.status === "REJECTED" ? 'Từ chối' : 'Duyệt'} bởi: ${booking.approvedByUserName}`;
            }

            let displayReasonOrNote: string | null = null;
             if (booking.status === "CANCELLED" || booking.status === "REJECTED") {
                 displayReasonOrNote = booking.cancellationReason || booking.note || null;
             } else {
                  displayReasonOrNote = booking.note;
             }

            return {
                id: booking.id,
                userName: booking.userName,
                roomName: booking.roomName || 'N/A',
                purpose: booking.purpose,
                plannedTime: booking.plannedStartTime && booking.plannedEndTime ? (
                    <>{isoToDate(booking.plannedStartTime)}<br />{isoToTime(booking.plannedStartTime)} - {isoToTime(booking.plannedEndTime)}</>
                ) : 'N/A',
                requestedAt: booking.createdAt ? (
                    <>{isoToTime(booking.createdAt)}<br />{isoToDate(booking.createdAt)}</>
                ) : 'N/A',
                status: booking.status as BookingStatusType, 
                processedBy: processedByInfo,
                reasonOrNote: displayReasonOrNote,
            };
        }) || [],
    [bookings]);

    return (
        <div className="w-full flex flex-col gap-4 px-1" ref={forwardedRef}>
            {/* Header */}
            <div className="w-full flex justify-between items-center">
                <Typography variant="h6" component="h1">
                    Báo cáo Đặt phòng/Thiết bị
                </Typography>
                <Typography variant="caption" component="p">
                    Tạo lúc: {new Date().toLocaleString('vi-VN')}
                </Typography>
            </div>
            {/* Bảng */}
            <Paper sx={{ overflow: "hidden", width: '100%' }}>
                <TableContainer>
                    <Table stickyHeader size="small">
                        <TableHead>
                            <TableRow>
                                {columns.map((column) => (
                                    <TableCell key={column.id} align={column.align || "left"}
                                        sx={{ minWidth: column.minWidth, backgroundColor: "#f0f0f0", color: "#333", fontWeight: 'bold', fontSize: "10px", padding: "4px 8px", border: '1px solid #ccc' }}
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
                                        const columnId = column.id as keyof ReportBookingRowData;
                                        let value: React.ReactNode | string | null = row[columnId]; // Kiểu có thể là JSX

                                        // Format lại status nếu cần
                                        if (columnId === 'status' && typeof value === 'string') {
                                             value = getDisplayStatus(value as BookingStatusType);
                                        }

                                        return (
                                            <TableCell key={column.id} align={column.align || "left"}
                                                sx={{ fontSize: "10px", padding: "4px 8px", border: '1px solid #eee' }}
                                            >
                                                {value ?? 'N/A'} {/* Dùng fallback */}
                                            </TableCell>
                                        );
                                    })}
                                </TableRow>
                            ))}
                        </TableBody>
                    </Table>
                </TableContainer>
            </Paper>
        </div>
    );
};

export default AdminBookingsReport;