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
import EditUserModal from "../modals/EditUserModal";
import DeleteUserModal from "../modals/DeleteUserModal";

// Định nghĩa cột 
const columns: readonly AdminUsersColumnData[] = [
    { id: "userId", label: "Mã số", minWidth: 100 },
    { id: "fullName", label: "Họ và tên", minWidth: 170 },
    { id: "email", label: "Email", minWidth: 145 },
    { id: "roleName", label: "Quyền", minWidth: 100 },
    { id: "createdAt", label: "Thời gian tạo", minWidth: 150 },
    { id: "updatedAt", label: "Thời gian cập nhật", minWidth: 150 },
    { id: "actions", label: "Sửa/Xóa", minWidth: 130, align: 'center' },
];

// --- Component AdminAccountsTable ---
const AdminAccountsTable: FC<AdminUsersTableProps> = ({
    users,              // Mảng dữ liệu UserData cho trang hiện tại
    totalUserCount,     // Tổng số lượng item trên tất cả các trang
    page,               // Index trang hiện tại (từ 0)
    rowsPerPage,        // Số dòng trên mỗi trang
    onPageChange,       // Hàm callback từ cha khi đổi trang
    onRowsPerPageChange,// Hàm callback từ cha khi đổi số dòng/trang
}): JSX.Element => {

    // Giữ lại state cho modal và snackbar
    const [isEditUserModalOpen, setIsEditUserModalOpen] = useState<boolean>(false);
    const [isDeleteUserModalOpen, setIsDeleteUserModalOpen] = useState<boolean>(false);
    const [modalData, setModalData] = useState<UserData | null>(null);
    const [openEditSnackbar, setOpenEditSnackbar] = useState<boolean>(false);
    const [openDeleteSnackbar, setOpenDeleteSnackbar] = useState<boolean>(false);

    const handleCloseSnackbar = (): void => {
        setOpenEditSnackbar(false);
        setOpenDeleteSnackbar(false);
    };

    // Mapping dữ liệu sang định dạng hàng (Giữ nguyên logic mapping đã sửa)
    const rows: AdminUsersRowData[] = useMemo(() =>
        users?.map((user) => ({
            id: user.id,
            userId: user.userId,
            fullName: user.fullName,
            email: user.email,
            roleName: user.roleName,
            createdAt: user.createdAt ? (<>{isoToTime(user.createdAt)}<br />{isoToDate(user.createdAt)}</>) : 'N/A',
            updatedAt: user.updatedAt ? (<>{isoToTime(user.updatedAt)}<br />{isoToDate(user.updatedAt)}</>) : 'N/A',
            actions: (
                <Box sx={{ display: 'flex', justifyContent: 'center', gap: 0.5 }}>
                    <IconButton size="small" color="primary" onClick={() => { setModalData(user); setIsEditUserModalOpen(true); }} aria-label="edit">
                        <EditIcon fontSize="small"/>
                    </IconButton>
                    <IconButton size="small" color="error" onClick={() => { setModalData(user); setIsDeleteUserModalOpen(true); }} aria-label="delete">
                        <DeleteIcon fontSize="small"/>
                    </IconButton>
                </Box>
            ),
        })) || [],
    [users]);

    return (
        <Paper sx={{ width: "100%", overflow: "hidden" }}> 
             {isEditUserModalOpen && modalData && (
                 <EditUserModal
                     isOpen={isEditUserModalOpen}
                     setIsOpen={setIsEditUserModalOpen}
                     setOpenSnackbar={setOpenEditSnackbar}
                     userData={modalData} // Truyền dữ liệu user cần sửa
                 />
             )}
             {isDeleteUserModalOpen && modalData && (
                 <DeleteUserModal
                     isOpen={isDeleteUserModalOpen}
                     setIsOpen={setIsDeleteUserModalOpen}
                     setOpenSnackbar={setOpenDeleteSnackbar}
                     userData={modalData} // Truyền dữ liệu user cần xóa (ít nhất ID)
                 />
             )}
             <Snackbar open={openEditSnackbar}  >
                 <Alert severity="success" sx={{ width: "100%" }}>Chỉnh sửa người dùng thành công !</Alert>
             </Snackbar>
             <Snackbar open={openDeleteSnackbar}  >
                  <Alert severity="success" sx={{ width: "100%" }}>Xóa người dùng thành công !</Alert>
             </Snackbar>

            <TableContainer sx={{ maxHeight: 600 }}> 
                <Table stickyHeader size="small">
                     <TableHead>
                         <TableRow>
                             {columns.map((column) => (
                                 <TableCell 
                                    key={column.id} 
                                    align={column.align || "center"} 
                                    style={{ minWidth: column.minWidth }} 
                                    sx={{ backgroundColor: "action.hover", fontWeight: 'bold' }}
                                    >
                                    {column.label}
                                 </TableCell>
                             ))}
                         </TableRow>
                     </TableHead>
                    <TableBody>
                        {rows.map((row) => {
                            return (
                                <TableRow hover role="checkbox" tabIndex={-1} key={row.id}> 
                                    {columns.map((column) => {
                                        const columnId = column.id as keyof AdminRoomsRowData;
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

            <TablePagination
                rowsPerPageOptions={[10, 25, 100]}
                component="div"
                count={totalUserCount} 
                rowsPerPage={rowsPerPage} 
                page={page} 
                onPageChange={onPageChange} 
                onRowsPerPageChange={onRowsPerPageChange} 
            />
        </Paper>
    );
};

export default AdminAccountsTable;