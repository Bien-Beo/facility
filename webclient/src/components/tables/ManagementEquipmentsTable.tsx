import React, { ChangeEvent, JSX, FC, useState, useMemo } from "react"; 
import Paper from "@mui/material/Paper";
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell from "@mui/material/TableCell";
import TableContainer from "@mui/material/TableContainer";
import TableHead from "@mui/material/TableHead";
import TablePagination from "@mui/material/TablePagination"; 
import TableRow from "@mui/material/TableRow";
import { Alert, Box, IconButton, Snackbar, Typography } from "@mui/material"; 
import DeleteIcon from "@mui/icons-material/Delete";
import EditIcon from "@mui/icons-material/Edit";

import isoToDate from "../../utils/isoToDate";
import isoToTime from "../../utils/isoToTime";
import EditFacilityModal from "../modals/EditFacilityModal";
import DeleteFacilityModal from "../modals/DeleteFacilityModal";

// Định nghĩa cột 
const columns: readonly EquipmentColumnData[] = [
    { id: "name", label: "Tên/Tòa", minWidth: 145 },
    { id: "description", label: "Mô tả", minWidth: 140 },
    { id: "status", label: "Trạng thái", minWidth: 100 },
    { id: "createdAt", label: "Thời gian tạo", minWidth: 150 },
    { id: "updatedAt", label: "Thời gian cập nhật", minWidth: 150 },
    { id: "facilityManager", label: "Quản lý", minWidth: 170 },
    { id: "actions", label: "Sửa/Xóa", minWidth: 130, align: 'center' },
];

// --- Component AdminFacilitiesTable ---
const ManagementEquipmentsTable: FC<AdminRoomsTableProps> = ({
    rooms,              // Mảng dữ liệu RoomData cho trang hiện tại
    totalRoomCount,     // Tổng số lượng item trên tất cả các trang
    page,               // Index trang hiện tại (từ 0)
    rowsPerPage,        // Số dòng trên mỗi trang
    onPageChange,       // Hàm callback từ cha khi đổi trang
    onRowsPerPageChange,// Hàm callback từ cha khi đổi số dòng/trang
    buildings,       // Vẫn nhận buildings nếu Modal Edit cần
    roomTypes,
    facilityManagers
}): JSX.Element => {

    // Giữ lại state cho modal và snackbar
    const [isEditFacilityModalOpen, setIsEditFacilityModalOpen] = useState<boolean>(false);
    const [isDeleteFacilityModalOpen, setIsDeleteFacilityModalOpen] = useState<boolean>(false);
    const [modalData, setModalData] = useState<RoomData | null>(null); 
    const [openEditSnackbar, setOpenEditSnackbar] = useState<boolean>(false);
    const [openDeleteSnackbar, setOpenDeleteSnackbar] = useState<boolean>(false);

    const handleCloseSnackbar = (): void => {
        setOpenEditSnackbar(false);
        setOpenDeleteSnackbar(false);
    };

    // Mapping dữ liệu sang định dạng hàng (Giữ nguyên logic mapping đã sửa)
    const rows: AdminRoomsRowData[] = useMemo(() =>
        rooms?.map((room) => ({
            id: room.id,
            name: (<>{room.name}<br /><Typography variant="caption" color="text.secondary">({room.buildingName ?? 'N/A'})</Typography></>),
            description: room.description,
            status: room.status,
            createdAt: room.createdAt ? (<>{isoToTime(room.createdAt)}<br />{isoToDate(room.createdAt)}</>) : 'N/A',
            updatedAt: room.updatedAt ? (<>{isoToTime(room.updatedAt)}<br />{isoToDate(room.updatedAt)}</>) : 'N/A',
            deletedAt: room.deletedAt ? (<>{isoToTime(room.deletedAt)}<br />{isoToDate(room.deletedAt)}</>) : 'N/A',
            facilityManager: room.nameFacilityManager ?? 'N/A',
            actions: (
                <Box sx={{ display: 'flex', justifyContent: 'center', gap: 0.5 }}>
                    <IconButton size="small" color="primary" onClick={() => { setModalData(room); setIsEditFacilityModalOpen(true); }} aria-label="edit">
                        <EditIcon fontSize="small"/>
                    </IconButton>
                    <IconButton size="small" color="error" onClick={() => { setModalData(room); setIsDeleteFacilityModalOpen(true); }} aria-label="delete">
                        <DeleteIcon fontSize="small"/>
                    </IconButton>
                </Box>
            ),
        })) || [],
    [rooms]);

    return (
        <Paper sx={{ width: "100%", overflow: "hidden" }}> 
             {isEditFacilityModalOpen && modalData && (
                 <EditFacilityModal
                     isOpen={isEditFacilityModalOpen}
                     setIsOpen={setIsEditFacilityModalOpen}
                     setOpenSnackbar={setOpenEditSnackbar}
                     facilityData={modalData} // Truyền dữ liệu phòng cần sửa
                    buildings={buildings || []} // Truyền danh sách buildings
                    roomTypes={roomTypes || []}
                    facilityManagers={facilityManagers || []}
                 />
             )}
             {isDeleteFacilityModalOpen && modalData && (
                 <DeleteFacilityModal
                     isOpen={isDeleteFacilityModalOpen}
                     setIsOpen={setIsDeleteFacilityModalOpen}
                     setOpenSnackbar={setOpenDeleteSnackbar}
                     facilityData={modalData} // Truyền dữ liệu phòng cần xóa (ít nhất ID)
                 />
             )}
             <Snackbar open={openEditSnackbar}  >
                 <Alert severity="success" sx={{ width: "100%" }}>Chỉnh sửa phòng thành công !</Alert>
             </Snackbar>
             <Snackbar open={openDeleteSnackbar}  >
                  <Alert severity="success" sx={{ width: "100%" }}>Xóa phòng thành công !</Alert>
             </Snackbar>

             {/* Sửa: maxHeight để table không bị cố định chiều cao */}
            <TableContainer sx={{ maxHeight: 600 }}> {/* Ví dụ maxHeight */}
                <Table stickyHeader size="small">
                     <TableHead>
                         <TableRow>
                             {columns.map((column) => (
                                 <TableCell key={column.id} align={column.align || "center"} style={{ minWidth: column.minWidth }} sx={{ backgroundColor: "action.hover", fontWeight: 'bold' }}>
                                     {column.label}
                                 </TableCell>
                             ))}
                         </TableRow>
                     </TableHead>
                    <TableBody>
                        {/* BỎ .slice() - Map trực tiếp 'rows' đã là dữ liệu của trang hiện tại */}
                        {rows.map((row) => {
                            return (
                                <TableRow hover role="checkbox" tabIndex={-1} key={row.id}> {/* <<< Dùng row.id làm key */}
                                    {columns.map((column) => {
                                        const columnId = column.id as keyof AdminRoomsRowData;
                                        const value = columnId === 'actions' ? row.actions : row[columnId];
                                        return (
                                            <TableCell key={column.id} align={column.align || "left"}>
                                                 {/* Sửa: Render value trực tiếp, fallback */}
                                                {value ?? 'N/A'}
                                            </TableCell>
                                        );
                                    })}
                                </TableRow>
                            );
                        })}
                    </TableBody>
                </Table>
            </TableContainer>

            {/* SỬA: Sử dụng props từ component cha cho TablePagination */}
            <TablePagination
                rowsPerPageOptions={[10, 25, 100]}
                component="div"
                count={totalRoomCount} // <<< Dùng tổng số lượng từ props
                rowsPerPage={rowsPerPage} // <<< Dùng giá trị từ props
                page={page} // <<< Dùng giá trị từ props
                onPageChange={onPageChange} // <<< Dùng handler từ props
                onRowsPerPageChange={onRowsPerPageChange} // <<< Dùng handler từ props
            />
        </Paper>
    );
};

export default ManagementEquipmentsTable;