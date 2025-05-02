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
import EditEquipmentModal from "../modals/EditEquipmentModal";
import DeleteEquipmentModal from "../modals/DeleteEquipmentModal";

// Định nghĩa cột 
const columns: readonly EquipmentColumnData[] = [
    { id: "modelName", label: "Tên model", minWidth: 145 },
    { id: "notes", label: "Mô tả", minWidth: 140 },
    { id: "status", label: "Trạng thái", minWidth: 100 },
    { id: "purchaseDate", label: "Thời gian mua", minWidth: 150 },
    { id: "warrantyExpiryDate", label: "Thời gian bảo hành", minWidth: 150 },
    { id: "updatedAt", label: "Thời gian cập nhật", minWidth: 150 },
    { id: "defaultRoomName", label: "Phòng sở hữu", minWidth: 170 },
    { id: "actions", label: "Sửa/Xóa", minWidth: 130, align: 'center' },
];

const ManagementEquipmentsTable: FC<EquipmentsTableProps> = ({
    equipments,             
    totalEquipmentCount,   
    page,             
    rowsPerPage,        
    onPageChange,     
    onRowsPerPageChange,
    defaultRoom
}): JSX.Element => {

    const [isEditEquipmentModalOpen, setIsEditEquipmentModalOpen] = useState<boolean>(false);
    const [isDeleteEquipmentModalOpen, setIsDeleteEquipmentModalOpen] = useState<boolean>(false);
    const [modalData, setModalData] = useState<EquipmentItemData | null>(null); 
    const [openEditSnackbar, setOpenEditSnackbar] = useState<boolean>(false);
    const [openDeleteSnackbar, setOpenDeleteSnackbar] = useState<boolean>(false);

    const handleCloseSnackbar = (): void => {
        setOpenEditSnackbar(false);
        setOpenDeleteSnackbar(false);
    };

    // Mapping dữ liệu sang định dạng hàng (Giữ nguyên logic mapping đã sửa)
    const rows: EquipmentsRowData[] = useMemo(() =>
        equipments?.map((equipment) => ({
            id: equipment.id,
            modelName: (<>{equipment.modelName}<br /><Typography variant="caption" color="text.secondary">({equipment.typeName ?? 'N/A'})</Typography></>),
            notes: equipment.notes,
            status: equipment.status,
            purchaseDate: equipment.purchaseDate ? (<>{isoToTime(equipment.purchaseDate)}<br />{isoToDate(equipment.purchaseDate)}</>) : 'N/A',
            warrantyExpiryDate: equipment.warrantyExpiryDate ? (<>{isoToTime(equipment.warrantyExpiryDate)}<br />{isoToDate(equipment.warrantyExpiryDate)}</>) : 'N/A',
            updatedAt: equipment.updatedAt ? (<>{isoToTime(equipment.updatedAt)}<br />{isoToDate(equipment.updatedAt)}</>) : 'N/A',
            defaultRoomName: equipment.defaultRoomName ?? 'N/A',
            actions: (
                <Box sx={{ display: 'flex', justifyContent: 'center', gap: 0.5 }}>
                    <IconButton size="small" color="primary" onClick={() => { setModalData(equipment); setIsEditEquipmentModalOpen(true); }} aria-label="edit">
                        <EditIcon fontSize="small"/>
                    </IconButton>
                    <IconButton size="small" color="error" onClick={() => { setModalData(equipment); setIsDeleteEquipmentModalOpen(true); }} aria-label="delete">
                        <DeleteIcon fontSize="small"/>
                    </IconButton>
                </Box>
            ),
        })) || [],
    [equipments]);

    return (
        <Paper sx={{ width: "100%", overflow: "hidden" }}> 
             {isEditEquipmentModalOpen && modalData && (
                 <EditEquipmentModal
                     isOpen={isEditEquipmentModalOpen}
                     setIsOpen={setIsEditEquipmentModalOpen}
                     setOpenSnackbar={setOpenEditSnackbar}
                     equipmentData={modalData} 
                    defaultRoom={defaultRoom}
                 />
             )}
             {isDeleteEquipmentModalOpen && modalData && (
                 <DeleteEquipmentModal
                     isOpen={isDeleteEquipmentModalOpen}
                     setIsOpen={setIsDeleteEquipmentModalOpen}
                     setOpenSnackbar={setOpenDeleteSnackbar}
                     equipmentData={modalData} 
                 />
             )}
             <Snackbar open={openEditSnackbar}  >
                 <Alert severity="success" sx={{ width: "100%" }}>Chỉnh sửa phòng thành công !</Alert>
             </Snackbar>
             <Snackbar open={openDeleteSnackbar}  >
                  <Alert severity="success" sx={{ width: "100%" }}>Xóa phòng thành công !</Alert>
             </Snackbar>

            <TableContainer sx={{ maxHeight: 600 }}> 
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
                        {rows.map((row) => {
                            return (
                                <TableRow hover role="checkbox" tabIndex={-1} key={row.id}> {/* <<< Dùng row.id làm key */}
                                    {columns.map((column) => {
                                        const columnId = column.id as keyof EquipmentsRowData;
                                        const value = columnId === 'actions' ? row.actions : row[columnId];
                                        return (
                                            <TableCell key={column.id} align={column.align || "left"}>
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
                count={totalEquipmentCount} // <<< Dùng tổng số lượng từ props
                rowsPerPage={rowsPerPage} // <<< Dùng giá trị từ props
                page={page} // <<< Dùng giá trị từ props
                onPageChange={onPageChange} // <<< Dùng handler từ props
                onRowsPerPageChange={onRowsPerPageChange} // <<< Dùng handler từ props
            />
        </Paper>
    );
};

export default ManagementEquipmentsTable;