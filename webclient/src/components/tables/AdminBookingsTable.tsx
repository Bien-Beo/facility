// import { ChangeEvent, FC, useState } from "react";
// import Paper from "@mui/material/Paper";
// import Table from "@mui/material/Table";
// import TableBody from "@mui/material/TableBody";
// import TableCell from "@mui/material/TableCell";
// import TableContainer from "@mui/material/TableContainer";
// import TableHead from "@mui/material/TableHead";
// import TablePagination from "@mui/material/TablePagination";
// import TableRow from "@mui/material/TableRow";
// import { Alert, IconButton, Snackbar } from "@mui/material";
// import TaskAltIcon from "@mui/icons-material/TaskAlt";
// import HighlightOffIcon from "@mui/icons-material/HighlightOff";

// import AdminBookingApprovalModal from "../modals/AdminBookingApprovalModal";
// import AdminBookingRejectModal from "../modals/AdminBookingRejectModal";
// import isoToDate from "../../utils/isoToDate";
// import isoToTime from "../../utils/isoToTime";

// const columns: readonly AdminBookingsColumnData[] = [
//   { id: "title", label: "Title/Facility", minWidth: 140 },
//   { id: "reqBy", label: "Requested By", minWidth: 140 },
//   { id: "purpose", label: "Purpose", minWidth: 140 },
//   { id: "date", label: "Date", minWidth: 140 },
//   { id: "time", label: "Time slot", minWidth: 170 },
//   { id: "createdAt", label: "Requested At", minWidth: 150 },
//   { id: "gd", label: "Group Director", minWidth: 170 },
//   { id: "fm", label: "Facility Manager", minWidth: 170 },
//   { id: "admin", label: "Admin", minWidth: 170 },
//   { id: "remark", label: "Rejection Remark", minWidth: 170 },
//   { id: "cancellationremark", label: "Cancellation Remark", minWidth: 170 },
//   { id: "status", label: "Approval Status", minWidth: 170 },
//   { id: "cancellationstatus", label: "Cancellation Status", minWidth: 170 },
//   { id: "actions", label: "Operations", minWidth: 130 },
// ];

// const AdminBookingsTable: FC<AdminBookingsTableProps> = (
//   bookingsData
// ): JSX.Element => {
//   const [page, setPage] = useState<number>(0);
//   const [rowsPerPage, setRowsPerPage] = useState<number>(10);
//   const [isApproveModalOpen, setIsApproveModalOpen] = useState<boolean>(false);
//   const [isOpenApproveSnackbar, setIsOpenApproveSnackbar] =
//     useState<boolean>(false);
//   const [isRejectModalOpen, setIsRejectModalOpen] = useState<boolean>(false);
//   const [isOpenRejectSnackbar, setIsOpenRejectSnackbar] =
//     useState<boolean>(false);
//   const [selectedSlug, setSelectedSlug] = useState<string>("");

//   const rows: AdminBookingsRowData[] =
//     bookingsData &&
//     bookingsData.bookingsData.map((booking) => ({
//       title: (
//         <>
//           {booking.title}
//           <br />
//           {booking.facility.name}
//         </>
//       ),
//       reqBy: booking.requestedBy.name,
//       purpose: booking.purpose,
//       date: isoToDate(booking.time.date).toString(),
//       time: isoToTime(booking.time.start) + " - " + isoToTime(booking.time.end),
//       createdAt: (
//         <>
//           {isoToTime(booking.createdAt)}
//           <br />
//           {isoToDate(booking.createdAt).toString()}
//         </>
//       ),
//       gd: booking.groupDirectorName ? (
//         <p
//           className={
//             booking.status === "REJECTED_BY_GD"
//               ? "text-red-600"
//               : booking.status === "APPROVED_BY_GD" ||
//                 booking.status === "APPROVED_BY_FM" ||
//                 booking.status === "REJECTED_BY_FM" ||
//                 booking.status === "CANCELLED"
//               ? "text-green-600"
//               : ""
//           }
//         >
//           {booking.groupDirectorName || null}
//           <br />
//           {booking.statusUpdateAtGD
//             ? isoToTime(booking.statusUpdateAtGD!)
//             : null}
//           <br />
//           {booking.statusUpdateAtGD
//             ? isoToDate(booking.statusUpdateAtGD!)
//             : null}
//         </p>
//       ) : null,
//       fm: booking.facilityManagerName ? (
//         <p
//           className={
//             booking.status === "REJECTED_BY_FM"
//               ? "text-red-600"
//               : booking.status === "APPROVED_BY_FM" ||
//                 booking.status === "CANCELLED"
//               ? "text-green-600"
//               : ""
//           }
//         >
//           {booking.facilityManagerName || null}
//           <br />
//           {booking.statusUpdateAtFM
//             ? isoToTime(booking.statusUpdateAtFM!)
//             : null}
//           <br />
//           {booking.statusUpdateAtFM
//             ? isoToDate(booking.statusUpdateAtFM!)
//             : null}
//         </p>
//       ) : null,
//       admin: (
//         <p
//           className={
//             booking.status === "REJECTED_BY_ADMIN"
//               ? "text-red-600"
//               : booking.status === "APPROVED_BY_ADMIN"
//               ? "text-green-600"
//               : ""
//           }
//         >
//           {booking.statusUpdateAtAdmin
//             ? isoToTime(booking.statusUpdateAtAdmin!)
//             : "N/A"}
//           <br />
//           {booking.statusUpdateAtAdmin &&
//             isoToDate(booking.statusUpdateAtAdmin!)}
//         </p>
//       ),
//       remark: booking.remark ? booking.remark : "N/A",
//       cancellationremark: booking.cancellationRemark
//         ? booking.cancellationRemark
//         : "N/A",
//       status: (
//         <p
//           className={
//             booking.status.startsWith("APPROVED")
//               ? "text-green-600"
//               : booking.status.startsWith("REJECTED") ||
//                 booking.status.startsWith("CANCELLED")
//               ? "text-red-600"
//               : "text-blue-600"
//           }
//         >
//           {booking.status.toLowerCase().replace(/_/g, " ")}
//         </p>
//       ),
//       cancellationstatus: (
//         <p
//           className={
//             booking.cancellationStatus!.startsWith("APPROVED")
//               ? "text-green-600"
//               : booking.cancellationStatus!.startsWith("REJECTED") ||
//                 booking.cancellationStatus!.startsWith("CANCELLED")
//               ? "text-red-600"
//               : "text-blue-600"
//           }
//         >
//           {booking.cancellationStatus!.toLowerCase().replace(/_/g, " ")}
//         </p>
//       ),
//       actions:
//         booking.status === "PENDING" || booking.status === "APPROVED_BY_GD" ? (
//           <div className="flex gap-1">
//             <IconButton
//               color="success"
//               onClick={() => {
//                 setSelectedSlug(booking.slug);
//                 setIsApproveModalOpen(true);
//               }}
//             >
//               <TaskAltIcon />
//             </IconButton>
//             <IconButton
//               color="error"
//               onClick={() => {
//                 setSelectedSlug(booking.slug);
//                 setIsRejectModalOpen(true);
//               }}
//             >
//               <HighlightOffIcon />
//             </IconButton>
//           </div>
//         ) : (
//           "No actions"
//         ),
//     }));

//   const handleCloseSnackbar = (): void => {
//     setIsOpenApproveSnackbar(false);
//     setIsOpenRejectSnackbar(false);
//   };

//   const handleChangePage = (_event: unknown, newPage: number) => {
//     setPage(newPage);
//   };

//   const handleChangeRowsPerPage = (event: ChangeEvent<HTMLInputElement>) => {
//     setRowsPerPage(+event.target.value);
//     setPage(0);
//   };

//   return (
//     <Paper sx={{ width: "75vw", height: "75dvh", overflow: "hidden" }}>
//       {isApproveModalOpen && (
//         <AdminBookingApprovalModal
//           isOpen={isApproveModalOpen}
//           setIsOpen={setIsApproveModalOpen}
//           setOpenSnackbar={setIsOpenApproveSnackbar}
//           slug={selectedSlug}
//         />
//       )}
//       {isRejectModalOpen && (
//         <AdminBookingRejectModal
//           isOpen={isRejectModalOpen}
//           setIsOpen={setIsRejectModalOpen}
//           setOpenSnackbar={setIsOpenRejectSnackbar}
//           slug={selectedSlug}
//         />
//       )}
//       <TableContainer sx={{ height: "90%", overflow: "auto" }}>
//         <Table stickyHeader>
//           <TableHead>
//             <TableRow>
//               {columns.map((column) => (
//                 <TableCell
//                   key={column.id}
//                   align="left"
//                   style={{ minWidth: column.minWidth }}
//                   sx={{ backgroundColor: "#646464", color: "#fff" }}
//                 >
//                   {column.label}
//                 </TableCell>
//               ))}
//             </TableRow>
//           </TableHead>
//           <TableBody>
//             {rows
//               .slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
//               .map((row, index) => {
//                 return (
//                   <TableRow hover role="checkbox" tabIndex={-1} key={index}>
//                     {columns.map((column) => {
//                       const value = row[column.id];
//                       return (
//                         <TableCell key={column.id} align={"left"}>
//                           {value ? (
//                             value
//                           ) : (
//                             <p className="text-blue-600">Not approved</p>
//                           )}
//                         </TableCell>
//                       );
//                     })}
//                   </TableRow>
//                 );
//               })}
//           </TableBody>
//         </Table>
//       </TableContainer>
//       <TablePagination
//         rowsPerPageOptions={[10, 25, 100]}
//         component="div"
//         count={rows.length}
//         rowsPerPage={rowsPerPage}
//         page={page}
//         onPageChange={handleChangePage}
//         onRowsPerPageChange={handleChangeRowsPerPage}
//       />
//       <Snackbar
//         open={isOpenApproveSnackbar}
//         autoHideDuration={3000}
//         onClose={handleCloseSnackbar}
//       >
//         <Alert
//           onClose={handleCloseSnackbar}
//           severity="success"
//           sx={{ width: "100%" }}
//         >
//           Booking approved successfully!
//         </Alert>
//       </Snackbar>
//       <Snackbar
//         open={isOpenRejectSnackbar}
//         autoHideDuration={3000}
//         onClose={handleCloseSnackbar}
//       >
//         <Alert
//           onClose={handleCloseSnackbar}
//           severity="success"
//           sx={{ width: "100%" }}
//         >
//           Booking rejected successfully!
//         </Alert>
//       </Snackbar>
//     </Paper>
//   );
// };

// export default AdminBookingsTable;

import React, { ChangeEvent, JSX, FC, useState, useMemo, useCallback } from "react";
import Paper from "@mui/material/Paper";
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell from "@mui/material/TableCell";
import TableContainer from "@mui/material/TableContainer";
import TableHead from "@mui/material/TableHead";
import TablePagination from "@mui/material/TablePagination";
import TableRow from "@mui/material/TableRow";
import { Alert, Box, IconButton, Snackbar, Chip, Tooltip, Typography } from "@mui/material"
import TaskAltIcon from "@mui/icons-material/TaskAlt"; 
import HighlightOffIcon from "@mui/icons-material/HighlightOff"; 
import InfoOutlinedIcon from '@mui/icons-material/InfoOutlined'; 

import isoToDate from "../../utils/isoToDate";
import isoToTime from "../../utils/isoToTime";

import AdminBookingApprovalModal from "../modals/AdminBookingApprovalModal";
import AdminBookingRejectModal from "../modals/AdminBookingRejectModal";
import EventModal from "../modals/EventModal";


// --- Định nghĩa cột cho Bảng Admin Bookings (Sửa lại) ---
const columns: readonly AdminBookingColumnData[] = [
    // { id: "id", label: "Booking ID", minWidth: 150 },
    { id: "requesterInfo", label: "Người đặt/Phòng", minWidth: 170 }, // Gộp thông tin
    { id: "purpose", label: "Mục đích", minWidth: 150 },
    { id: "plannedTime", label: "Thời gian dự kiến", minWidth: 150 },
    { id: "equipmentInfo", label: "Thiết bị", minWidth: 150 },
    { id: "requestedAt", label: "Yêu cầu lúc", minWidth: 150 },
    { id: "status", label: "Trạng thái", minWidth: 120, align: 'center' },
    { id: "processedBy", label: "Người xử lý", minWidth: 130 }, // Người duyệt/từ chối/hủy
    { id: "reasonOrNote", label: "Lý do/Ghi chú", minWidth: 170 },
    { id: "actions", label: "Hành động", minWidth: 100, align: 'center' },
];

// --- Hàm Helper định dạng Status ---
const getDisplayStatus = (status: BookingStatusType | string): { text: string; color: "warning" | "success" | "error" | "info" | "default" } => { // Thêm kiểu màu MUI
    switch (status) {
        case "PENDING_APPROVAL": return { text: "Chờ duyệt", color: "warning" };
        case "CONFIRMED": return { text: "Đã duyệt", color: "success" };
        case "REJECTED": return { text: "Bị từ chối", color: "error" };
        case "CANCELLED": return { text: "Đã hủy", color: "error" };
        case "COMPLETED": return { text: "Đã hoàn thành", color: "default" };
        case "IN_PROGRESS": return { text: "Đang sử dụng", color: "info" };
        case "OVERDUE": return { text: "Quá hạn", color: "warning" };
        default: return { text: status?.replace(/_/g, ' ') || 'N/A', color: "default" };
    }
};

// --- Component AdminBookingsTable ---
// SỬA: Nhận props phân trang từ cha
const AdminBookingsTable: FC<AdminBookingsTableProps> = ({
    bookings,           // Mảng BookingEntry cho trang hiện tại
    totalBookingCount,  // Tổng số booking
    page,
    rowsPerPage,
    onPageChange,
    onRowsPerPageChange,
}): JSX.Element => {

    // BỎ: State phân trang cục bộ
    // const [page, setPage] = useState<number>(0);
    // const [rowsPerPage, setRowsPerPage] = useState<number>(10);

    // Giữ lại state cho modal và snackbar
    const [isApproveModalOpen, setIsApproveModalOpen] = useState<boolean>(false);
    const [isOpenApproveSnackbar, setIsOpenApproveSnackbar] = useState<boolean>(false);
    const [isRejectModalOpen, setIsRejectModalOpen] = useState<boolean>(false);
    const [isOpenRejectSnackbar, setIsOpenRejectSnackbar] = useState<boolean>(false);
    const [isDetailModalOpen, setIsDetailModalOpen] = useState<boolean>(false); // State cho modal chi tiết

    // SỬA: Lưu trữ booking đang được chọn để mở modal
    const [selectedBooking, setSelectedBooking] = useState<BookingEntry | null>(null);

    const handleCloseSnackbar = (): void => {
        setIsOpenApproveSnackbar(false);
        setIsOpenRejectSnackbar(false);
    };

    // SỬA: Map BookingEntry sang định dạng hàng cho bảng
    const rows: AdminBookingRowData[] = useMemo(() =>
        bookings?.map((booking) => {
            const displayStatus = getDisplayStatus(booking.status as BookingStatusType);
            let processedByInfo: string | null = null;
            if (booking.status === "CONFIRMED" && booking.approvedByUserName) { processedByInfo = `Duyệt: ${booking.approvedByUserName}`; }
            else if (booking.status === "REJECTED" && booking.approvedByUserName) { processedByInfo = `Từ chối: ${booking.approvedByUserName}`; }
            else if (booking.status === "CANCELLED" && booking.cancelledByUserName) { processedByInfo = `Hủy: ${booking.cancelledByUserName}`; }

             let displayReasonOrNote: string | null = null;
             if (booking.status === "CANCELLED" || booking.status === "REJECTED") {
                 displayReasonOrNote = booking.cancellationReason || booking.note || null;
             } else {
                  displayReasonOrNote = booking.note;
             }

             // Hiển thị tóm tắt thiết bị
              const equipmentSummary = booking.bookedEquipments && booking.bookedEquipments.length > 0
                    ? booking.bookedEquipments.map(eq => eq.equipmentModelName).join(', ')
                    : 'Không có';

            return {
                id: booking.id, // <<< Thêm ID
                requesterInfo: ( // Gộp thông tin người đặt và phòng
                    <>
                        {booking.userName}
                        <br />
                        <Typography variant="caption" color="text.secondary">Phòng: {booking.roomName ?? 'N/A'}</Typography>
                    </>
                ),
                 userName: booking.userName, // Giữ lại nếu dùng cột riêng
                 roomName: booking.roomName, // Giữ lại nếu dùng cột riêng
                purpose: booking.purpose,
                plannedTime: booking.plannedStartTime && booking.plannedEndTime ? (
                    <>{isoToDate(booking.plannedStartTime)}<br />{isoToTime(booking.plannedStartTime)} - {isoToTime(booking.plannedEndTime)}</>
                ) : 'N/A',
                 equipmentInfo: ( // Hiển thị tóm tắt, có thể thêm Tooltip
                     <Tooltip title={booking.bookedEquipments?.map(eq => `${eq.equipmentModelName} (${eq.itemId})${eq.isDefaultEquipment ? ' [MĐ]' : ''}`).join('\n') || 'Không có'}>
                         <Typography variant="body2" noWrap sx={{maxWidth: 150, overflow: 'hidden', textOverflow: 'ellipsis'}}>{equipmentSummary}</Typography>
                     </Tooltip>
                 ),
                requestedAt: booking.createdAt ? (<>{isoToTime(booking.createdAt)}<br />{isoToDate(booking.createdAt)}</>) : 'N/A',
                status: ( // Dùng Chip MUI
                     <Chip label={displayStatus.text} color={displayStatus.color} size="small" />
                ),
                processedBy: processedByInfo,
                reasonOrNote: displayReasonOrNote,
                actions: ( // Hiển thị nút dựa trên trạng thái
                    <Box sx={{ display: 'flex', justifyContent: 'center', gap: 0.5 }}>
                        {booking.status === "PENDING_APPROVAL" ? (
                            <>
                                <Tooltip title="Duyệt">
                                    <IconButton size="small" color="success" onClick={() => { setSelectedBooking(booking); setIsApproveModalOpen(true); }} aria-label="approve">
                                        <TaskAltIcon fontSize="small"/>
                                    </IconButton>
                                </Tooltip>
                                <Tooltip title="Từ chối">
                                    <IconButton size="small" color="error" onClick={() => { setSelectedBooking(booking); setIsRejectModalOpen(true); }} aria-label="reject">
                                        <HighlightOffIcon fontSize="small"/>
                                    </IconButton>
                                </Tooltip>
                            </>
                        ) : ( // Hiển thị nút xem chi tiết cho các trạng thái khác
                             <Tooltip title="Xem chi tiết">
                                  <IconButton size="small" color="info" onClick={() => { setSelectedBooking(booking); setIsDetailModalOpen(true); }} aria-label="details">
                                       <InfoOutlinedIcon fontSize="small"/>
                                  </IconButton>
                              </Tooltip>
                        )}
                    </Box>
                ),
            };
        }) || [],
    [bookings]); // Chỉ phụ thuộc vào bookings prop

    // BỎ: Handlers phân trang cục bộ

    // Helper map dữ liệu sang EventInfoProps nếu dùng lại EventModal
     const mapBookingToEventInfo = (bookingData: BookingEntry | null): EventInfoProps | null => {
         if (!bookingData) return null;
         return {
             bookingId: bookingData.id,
             title: bookingData.purpose || bookingData.userName,
             purpose: bookingData.purpose,
             status: bookingData.status as BookingStatusType,
             start: isoToTime(bookingData.plannedStartTime),
             end: isoToTime(bookingData.plannedEndTime),
             date: isoToDate(bookingData.plannedStartTime),
             requestBy: bookingData.userName,
             roomName: bookingData.roomName,
             bookedEquipments: bookingData.bookedEquipments || []
         };
     };


    return (
        <Paper sx={{ width: "100%", overflow: "hidden" }}>
            {/* Modals: Truyền ID hoặc cả object booking nếu cần */}
             {isApproveModalOpen && selectedBooking && (
                 <AdminBookingApprovalModal
                     isOpen={isApproveModalOpen}
                     setIsOpen={setIsApproveModalOpen}
                     setOpenSnackbar={setIsOpenApproveSnackbar}
                     bookingId={selectedBooking.id} // <<< Truyền ID
                     bookingData={selectedBooking} // <<< Truyền cả data nếu modal cần
                     // onSuccessCallback={() => ...}
                 />
             )}
              {isRejectModalOpen && selectedBooking && (
                  <AdminBookingRejectModal
                      isOpen={isRejectModalOpen}
                      setIsOpen={setIsRejectModalOpen}
                      setOpenSnackbar={setIsOpenRejectSnackbar}
                      bookingId={selectedBooking.id} // <<< Truyền ID
                      bookingData={selectedBooking} // <<< Truyền cả data nếu modal cần
                  />
              )}
              {/* Modal xem chi tiết */}
               {isDetailModalOpen && selectedBooking && (
                   <EventModal
                       isOpen={isDetailModalOpen}
                       setIsOpen={setIsDetailModalOpen}
                       eventInfo={mapBookingToEventInfo(selectedBooking)} // Map sang kiểu EventInfoProps
                   />
               )}


             {/* Snackbars */}
             <Snackbar open={isOpenApproveSnackbar} autoHideDuration={3000} onClose={handleCloseSnackbar}>
                 <Alert severity="success" variant="filled" sx={{ width: "100%" }}>Booking approved!</Alert>
             </Snackbar>
             <Snackbar open={isOpenRejectSnackbar} autoHideDuration={3000} onClose={handleCloseSnackbar}>
                  <Alert severity="warning" variant="filled" sx={{ width: "100%" }}>Booking rejected.</Alert>
             </Snackbar>

            {/* Table */}
            <TableContainer sx={{ maxHeight: 650 }}>
                <Table stickyHeader size="small">
                     <TableHead>
                         <TableRow>
                             {columns.map((column) => (
                                 <TableCell key={column.id} align={column.align || "left"} style={{ minWidth: column.minWidth }} sx={{ backgroundColor: "grey.200", fontWeight: 'bold' }}>
                                     {column.label}
                                 </TableCell>
                             ))}
                         </TableRow>
                     </TableHead>
                    <TableBody>
                        {/* BỎ .slice() */}
                        {rows.map((row) => (
                            <TableRow hover tabIndex={-1} key={row.id}> {/* <<< Dùng row.id */}
                                {columns.map((column) => {
                                    const columnId = column.id as keyof AdminBookingRowData;
                                    const value = columnId === 'actions' ? row.actions : row[columnId];
                                    return (
                                        <TableCell key={column.id} align={column.align || "left"}>
                                            {/* Sửa: Render value + fallback */}
                                            {value ?? 'N/A'}
                                        </TableCell>
                                    );
                                })}
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>

            {/* SỬA: Dùng props từ component cha */}
            <TablePagination
                rowsPerPageOptions={[10, 25, 100]}
                component="div"
                count={totalBookingCount} // <<< Dùng tổng số lượng
                rowsPerPage={rowsPerPage}   // <<< Dùng prop
                page={page}               // <<< Dùng prop
                onPageChange={onPageChange}           // <<< Dùng handler prop
                onRowsPerPageChange={onRowsPerPageChange} // <<< Dùng handler prop
            />
        </Paper>
    );
};

export default AdminBookingsTable;

// --- Cần định nghĩa các kiểu mới trong types.ts ---
/*

// Kiểu dữ liệu định nghĩa cột cho bảng Admin Bookings
export interface AdminBookingColumnData {
    id: keyof Omit<AdminBookingRowData, 'id'>;
    label: string;
    minWidth?: number;
    align?: 'left' | 'right' | 'center';
}

// Props cho component bảng Admin Bookings (nhận từ cha)
export interface AdminBookingsTableProps {
    bookings: BookingEntry[]; // Mảng BookingEntry cho trang hiện tại
    totalBookingCount: number; // Tổng số lượng item
    page: number; // Index trang hiện tại (0-based)
    rowsPerPage: number; // Số dòng/trang
    onPageChange: (event: unknown, newPage: number) => void;
    onRowsPerPageChange: (event: ChangeEvent<HTMLInputElement>) => void;
}

// Props cho Modals (Ví dụ)
export interface AdminBookingActionModalProps {
    isOpen: boolean;
    setIsOpen: (isOpen: boolean) => void;
    setOpenSnackbar: (isOpen: boolean) => void;
    bookingId: string;
    bookingData?: BookingEntry; // Pass data nếu modal cần hiển thị gì đó
    onSuccessCallback?: () => void;
}

// Cần các kiểu EventInfoProps, BookingEntry, BookedEquipmentSummary... đã định nghĩa
*/