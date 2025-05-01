// import {
//     Dialog,
//     DialogTitle,
//     DialogContent,
//     DialogActions,
//     Button,
//     TextField,
//     MenuItem,
//     FormControl,
//     InputLabel,
//     Select,
//     SelectChangeEvent,
//   } from '@mui/material';
//   import { useEffect, useState } from 'react';
//   import { useMutation, useQueryClient } from '@tanstack/react-query';
//   import dayjs, { Dayjs } from 'dayjs';
//   import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
//   import { LocalizationProvider, DateTimePicker } from '@mui/x-date-pickers';
//   import axios from 'axios'; 
  
//   const updateMaintenance = async (data: UpdateMaintenanceData) => {
//     try {
//       const token = localStorage.getItem('token');
//       if (!token) throw new Error('No token found');
  
//       const response = await axios.put(
//         `/maintenance/${data.ticketId}`,
//         {
//           status: data.newStatus,
//           note: data.note,
//           scheduledTime: data.scheduledTime,
//         },
//         {
//           headers: {
//             Authorization: `Bearer ${token}`,
//           },
//         }
//       );
  
//       if (response.data?.code !== 0 || !response.data?.result) {
//         throw new Error('Failed to update maintenance status');
//       }
  
//       return response.data.result;
//     } catch (err) {
//       console.error(err);
//       throw err;
//     }
//   };
  

// type MaintenanceStatusType = 'ASSIGNED' | 'IN_PROGRESS' | 'COMPLETED' | 'CANNOT_REPAIR' | 'CANCELLED';

// const UpdateMaintenanceModal = ({
//     open,
//     onClose,
//     ticketData,
// }: UpdateMaintenanceModalProps) => {
//     const [newStatus, setNewStatus] = useState<MaintenanceStatusType>('ASSIGNED');
//     const [note, setNote] = useState<string>('');
//     const [scheduledTime, setScheduledTime] = useState<Dayjs | null>(null);
//     const queryClient = useQueryClient();

//     const mutation = useMutation({
//         mutationFn: updateMaintenance,
//         onSuccess: () => {
//             alert('Cập nhật trạng thái thành công');
//             queryClient.invalidateQueries({ queryKey: ['maintenance-requests'] });
//             onClose();
//         },
//         onError: () => {
//             alert('Cập nhật trạng thái thất bại');
//         },
//     });

//     useEffect(() => {
//         if (ticketData) {
//             setNewStatus(ticketData.status as MaintenanceStatusType);
//             setNote('');
//             setScheduledTime(
//                 ticketData.startDate ? dayjs(ticketData.startDate) : null
//             );
//         }
//     }, [ticketData]);

//     const handleStatusChange = (event: SelectChangeEvent<MaintenanceStatusType>) => {
//         setNewStatus(event.target.value as MaintenanceStatusType);
//     };

//     const handleUpdate = () => {
//         if (!ticketData) return;

//         if (
//             (newStatus === 'COMPLETED' || newStatus === 'CANNOT_REPAIR') &&
//             note.trim() === ''
//         ) {
//             alert('Vui lòng nhập ghi chú hoàn thành hoặc lý do');
//             return;
//         }

//         mutation.mutate({
//             ticketId: ticketData.id,
//             newStatus,
//             note,
//             scheduledTime: scheduledTime ? scheduledTime.toISOString() : undefined,
//         });
//     };

//     const possibleNextStatuses: MaintenanceStatusType[] = (() => {
//         switch (ticketData?.status) {
//             case 'ASSIGNED':
//                 return ['IN_PROGRESS', 'CANCELLED'];
//             case 'IN_PROGRESS':
//                 return ['COMPLETED', 'CANNOT_REPAIR', 'CANCELLED'];
//             default:
//                 return [];
//         }
//     })();

//     return (
//         <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
//             <DialogTitle>Cập nhật trạng thái bảo trì</DialogTitle>
//             <DialogContent
//                 sx={{
//                     display: 'flex',
//                     flexDirection: 'column',
//                     gap: 2,
//                     maxHeight: '70vh',
//                     overflowY: 'auto',
//                 }}
//             >
//             {/* Thông tin hiển thị */}
//             <TextField
//                 label="Phòng"
//                 value={ticketData.roomName || 'N/A'}
//                 fullWidth
//                 size="small"
//                 InputProps={{ readOnly: true }}
//             />
//             <TextField
//                 label="Thiết bị"
//                 value={ticketData.modelName || 'N/A'}
//                 fullWidth
//                 size="small"
//                 InputProps={{ readOnly: true }}
//             />
//             <TextField
//                 label="Người báo cáo"
//                 value={ticketData.reportByUser || 'N/A'}
//                 fullWidth
//                 size="small"
//                 InputProps={{ readOnly: true }}
//             />
//             <TextField
//                 label="KTV được giao"
//                 value={ticketData.technicianName || 'Chưa giao'}
//                 fullWidth
//                 size="small"
//                 InputProps={{ readOnly: true }}
//             />
//             <TextField
//                 label="Mô tả sự cố"
//                 value={ticketData.description || 'N/A'}
//                 fullWidth
//                 multiline
//                 rows={3}
//                 size="small"
//                 InputProps={{ readOnly: true }}
//             />
//             <TextField
//                 label="Ngày báo cáo"
//                 value={ticketData.reportDate ? dayjs(ticketData.reportDate).format('DD/MM/YYYY HH:mm') : 'N/A'}
//                 fullWidth
//                 size="small"
//                 InputProps={{ readOnly: true }}
//             />
//             {ticketData.startDate && (
//                 <TextField
//                 label="Ngày bắt đầu sửa"
//                 value={dayjs(ticketData.startDate).format('DD/MM/YYYY HH:mm')}
//                 fullWidth
//                 size="small"
//                 InputProps={{ readOnly: true }}
//                 />
//             )}
//             {ticketData.completionDate && (
//                 <TextField
//                 label="Ngày hoàn thành"
//                 value={dayjs(ticketData.completionDate).format('DD/MM/YYYY HH:mm')}
//                 fullWidth
//                 size="small"
//                 InputProps={{ readOnly: true }}
//                 />
//             )}
//             {ticketData.cost !== null && (
//                 <TextField
//                 label="Chi phí"
//                 value={`${ticketData.cost} đ`}
//                 fullWidth
//                 size="small"
//                 InputProps={{ readOnly: true }}
//                 />
//             )}
//             {ticketData.actionTaken && (
//                 <TextField
//                 label="Hành động đã thực hiện"
//                 value={ticketData.actionTaken}
//                 fullWidth
//                 multiline
//                 rows={2}
//                 size="small"
//                 InputProps={{ readOnly: true }}
//                 />
//             )}
//             {ticketData.notes && (
//                 <TextField
//                 label="Ghi chú"
//                 value={ticketData.notes}
//                 fullWidth
//                 multiline
//                 rows={2}
//                 size="small"
//                 InputProps={{ readOnly: true }}
//                 />
//             )}

//             {/* Phần cập nhật trạng thái như hiện tại */}
//             <FormControl fullWidth size="small">
//                 <InputLabel>Trạng thái mới</InputLabel>
//                 <Select
//                 value={newStatus}
//                 label="Trạng thái mới"
//                 onChange={handleStatusChange}
//                 disabled={mutation.isPending}
//                 >
//                 {possibleNextStatuses.map((status) => (
//                     <MenuItem key={status} value={status}>
//                     {status}
//                     </MenuItem>
//                 ))}
//                 </Select>
//             </FormControl>

//             {['ASSIGNED', 'IN_PROGRESS'].includes(newStatus) && (
//                 <LocalizationProvider dateAdapter={AdapterDayjs}>
//                 <DateTimePicker
//                     label="Thời gian hẹn sửa (nếu có)"
//                     value={scheduledTime}
//                     onChange={(newValue) => setScheduledTime(newValue)}
//                     slotProps={{
//                     textField: {
//                         fullWidth: true,
//                         size: 'small',
//                     },
//                     }}
//                     disabled={mutation.isPending}
//                 />
//                 </LocalizationProvider>
//             )}

//             {(newStatus === 'COMPLETED' || newStatus === 'CANNOT_REPAIR') && (
//                 <TextField
//                 label={`Ghi chú hoàn thành / Lý do ${newStatus === 'CANNOT_REPAIR' ? 'không sửa được' : ''} (*)`}
//                 multiline
//                 rows={3}
//                 fullWidth
//                 value={note}
//                 onChange={(e) => setNote(e.target.value)}
//                 size="small"
//                 disabled={mutation.isPending}
//                 />
//             )}
//             </DialogContent>

//             <DialogActions>
//                 <Button onClick={onClose} disabled={mutation.isPending}>
//                     Hủy
//                 </Button>
//                 <Button
//                     onClick={handleUpdate}
//                     variant="contained"
//                     disabled={mutation.isPending}
//                 >
//                     Cập nhật
//                 </Button>
//             </DialogActions>
//         </Dialog>
//     );
// };
  
//   export default UpdateMaintenanceModal;
  

import React, { JSX, FC, FormEvent, useState, useEffect, useCallback, useMemo, ChangeEvent } from "react";
import {
    Box, Button, CircularProgress, Fade, Modal, Typography, Alert, TextField, FormControl, InputLabel, MenuItem, Select, SelectChangeEvent, IconButton, FormHelperText, Dialog, DialogTitle, DialogContent, DialogActions // Dùng Dialog chuẩn của MUI
} from "@mui/material";
import CloseIcon from '@mui/icons-material/Close';
import { LocalizationProvider, DateTimePicker, AdapterDayjs } from '@mui/x-date-pickers';
import dayjs, { Dayjs } from 'dayjs';
import { useMutation, useQueryClient } from "@tanstack/react-query";
import axios, { AxiosError } from "axios";
// --- Helper lấy Status Text ---
const getMaintenanceStatusText = (status: MaintenanceStatusType | string): string => {
     switch(status) {
        case "REPORTED": return "Mới báo cáo"; case "ASSIGNED": return "Đã giao KTV";
        case "IN_PROGRESS": return "Đang xử lý"; case "COMPLETED": return "Đã hoàn thành";
        case "CANNOT_REPAIR": return "Không thể sửa"; case "CANCELLED": return "Đã hủy";
        default: return status?.replace(/_/g, ' ') || 'N/A';
     }
 };

// --- API Call Function (Đã sửa) ---
const updateMaintenanceApi = async (payload: MaintenanceTicketUpdatePayload & { ticketId: string }): Promise<ApiResponse<MaintenanceTicketData> | void> => {
    const token = localStorage.getItem('token');
    if (!token) throw new Error('No token found');
    if (!payload.ticketId) throw new Error('Ticket ID is missing');
    const apiPayload: MaintenanceTicketUpdatePayload = { status: payload.status, completionNotes: payload.completionNotes };
    console.log(`Sending maintenance update for ticket ${payload.ticketId}:`, apiPayload);
    const response = await axios.patch<ApiResponse<MaintenanceTicketData>>(
        `${import.meta.env.VITE_APP_SERVER_URL || 'http://localhost:8080'}/maintenance-tickets/${payload.ticketId}`, // Endpoint chuẩn
        apiPayload,
        { headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' } } // Thêm Auth Header
    );
    if (response.data?.code !== 0 && response.data?.message) { throw new Error(response.data.message); }
    return response.data;
};


// --- Component ---
const UpdateMaintenanceModal = ({
    open,
    onClose,
    ticketData,
}: UpdateMaintenanceModalProps) => {

    // --- State ---
    const [newStatus, setNewStatus] = useState<MaintenanceStatusType | string>("");
    const [note, setNote] = useState<string>(""); // Đổi tên note cho khớp API payload
    const [backendError, setBackendError] = useState<ErrorMessage | null>(null);
    const [validationError, setValidationError] = useState<string>("");
    const queryClient = useQueryClient();

    // --- useEffect Init/Reset ---
    useEffect(() => {
        if (open && ticketData) {
            setNewStatus(ticketData.status);
            setNote(ticketData.notes || ""); // <<< Khởi tạo note từ ticketData.notes
            setValidationError(""); setBackendError(null);
        }
    }, [open, ticketData]);

    // --- Logic Possible Statuses (Giữ nguyên) ---
     const possibleNextStatuses = useMemo((): MaintenanceStatusType[] => {
          if (!ticketData) return [];
          const currentStatus = ticketData.status as MaintenanceStatusType;
            switch (currentStatus) {
                 case "REPORTED": return ["ASSIGNED", "CANCELLED"];
                 case "ASSIGNED": return ["IN_PROGRESS", "CANCELLED"];
                 case "IN_PROGRESS": return ["COMPLETED", "CANNOT_REPAIR", "CANCELLED"];
                 case "COMPLETED": return []; // Không thể chuyển từ COMPLETED
                 case "CANNOT_REPAIR": return []; // Không thể chuyển từ CANNOT_REPAIR
                 case "CANCELLED": return []; // Không thể chuyển từ CANCELLED
                 default: return []; // Trạng thái không hợp lệ
            }
          return []; // Thêm dòng này để đảm bảo luôn trả về array
     }, [ticketData?.status]);

    // --- Mutation (Sửa lại) ---
    const mutation = useMutation< ApiResponse<MaintenanceTicketData> | void, AxiosError<ErrorMessage>, MaintenanceTicketUpdatePayload & { ticketId: string } >({
        mutationFn: updateMaintenanceApi, // <<< Dùng hàm API riêng
        onSuccess: () => {
            console.log("Maintenance ticket updated:", ticketData?.id);
            queryClient.invalidateQueries({ queryKey: ["maintenanceTickets"] });
            if (onSuccessCallback) onSuccessCallback(); // Gọi callback của cha (cha sẽ show snackbar)
            onClose(); // Đóng modal
        },
        onError: (error) => {
            console.error("Update failed:", error);
            setBackendError(error.response?.data || { message: error.message || "Lỗi cập nhật" });
        },
    });

    // --- Handlers ---
    const handleStatusChange = (event: SelectChangeEvent<string>) => { setNewStatus(event.target.value as MaintenanceStatusType); setValidationError(""); setBackendError(null); };
    const handleNoteChange = (event: ChangeEvent<HTMLInputElement>) => { setNote(event.target.value); };
    const handleClose = () => { onClose(); }; // Gọi prop onClose

    const handleUpdate = () => { // <<< Đổi thành handleUpdate
        if (!ticketData) return;
        setValidationError(""); setBackendError(null);
        const selectedStatus = newStatus as MaintenanceStatusType;

        // Validation
        if (!selectedStatus || selectedStatus === ticketData.status) { setValidationError("Vui lòng chọn trạng thái mới."); return; }
        if ( (selectedStatus === 'COMPLETED' || selectedStatus === 'CANNOT_REPAIR') && note.trim() === '' ) { setValidationError("Vui lòng nhập ghi chú/lý do."); return; }

        // Payload
        const payload: MaintenanceTicketUpdatePayload & { ticketId: string } = {
            ticketId: ticketData.id,
            status: selectedStatus,
            // Sửa tên trường thành completionNotes nếu DTO yêu cầu
            completionNotes: (selectedStatus === 'COMPLETED' || selectedStatus === 'CANNOT_REPAIR') ? note.trim() : null,
        };
        mutation.mutate(payload);
    };

    if (!open || !ticketData) return null;

    return (
        <Dialog open={open} onClose={handleClose} maxWidth="sm" fullWidth>
            <DialogTitle sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                Cập nhật Trạng thái (Ticket: {ticketData.id})
                <IconButton onClick={handleClose} size="small"><CloseIcon /></IconButton>
            </DialogTitle>
            <DialogContent dividers sx={{ display: 'flex', flexDirection: 'column', gap: 2.5 }}> 
                {/* <TextField
                    label="Hành động đã thực hiện"
                    value={actionTaken}
                    onChange={(e) => setActionTaken(e.target.value)}
                    fullWidth
                />

                <LocalizationProvider dateAdapter={AdapterDayjs}>
                    <DateTimePicker
                        label="Ngày bắt đầu"
                        value={startDate}
                        onChange={(newValue) => setStartDate(newValue)}
                        slotProps={{ textField: { fullWidth: true } }}
                    />
                    <DateTimePicker
                        label="Ngày hoàn thành"
                        value={completionDate}
                        onChange={(newValue) => setCompletionDate(newValue)}
                        slotProps={{ textField: { fullWidth: true } }}
                    />
                </LocalizationProvider>

                <TextField
                    label="Chi phí"
                    type="number"
                    value={cost ?? ''}
                    onChange={(e) => setCost(parseFloat(e.target.value))}
                    fullWidth
                />

                <TextField
                    label="Ghi chú thêm"
                    value={note}
                    onChange={handleNoteChange}
                    multiline
                    rows={2}
                    fullWidth
                /> */}

                 {/* Phần cập nhật */}
                <FormControl fullWidth size="small" required sx={{ mt: 1 }} error={!!validationError && validationError.includes('trạng thái')}>
                    <InputLabel id="update-status-select-label">Trạng thái mới (*)</InputLabel>
                    <Select labelId="update-status-select-label" value={newStatus} label="Trạng thái mới (*)" onChange={handleStatusChange} disabled={possibleNextStatuses.length === 0 || mutation.isPending}>
                        <MenuItem value={ticketData.status} disabled><em>-- {getMaintenanceStatusText(ticketData.status)} --</em></MenuItem>
                        {possibleNextStatuses.map((statusOption) => ( <MenuItem key={statusOption} value={statusOption}> {getMaintenanceStatusText(statusOption)} </MenuItem> ))}
                        {possibleNextStatuses.length === 0 && <MenuItem disabled><em>Không thể đổi trạng thái</em></MenuItem>}
                    </Select>
                     {validationError && validationError.includes('trạng thái') && <FormHelperText>{validationError}</FormHelperText>}
                </FormControl>

                {/* Ghi chú */}
                 {(newStatus === 'COMPLETED' || newStatus === 'CANNOT_REPAIR') && (
                     <TextField id="completionNotes" name="completionNotes" label={`Ghi chú/Lý do ${newStatus === 'CANNOT_REPAIR' ? 'không sửa được ' : 'hoàn thành '}(*)`} variant="outlined" value={note} onChange={handleNoteChange} fullWidth size="small" multiline rows={3} required={true} error={!!validationError && validationError.includes('ghi chú')} helperText={validationError && validationError.includes('ghi chú') ? validationError : ""} disabled={mutation.isPending} />
                  )}

                  {/* Lỗi */}
                  {backendError && (<Alert severity="error" sx={{ mt: 1 }}>{backendError.message}</Alert>)}
                  {validationError && !validationError.includes('trạng thái') && !validationError.includes('ghi chú') && ( <Typography variant="caption" color="error">{validationError}</Typography> )}

            </DialogContent>
            <DialogActions sx={{p: 2}}> {/* Thêm padding cho actions */}
                <Button onClick={handleClose} disabled={mutation.isPending}> Hủy </Button>
                <Button onClick={handleUpdate} variant="contained" disabled={mutation.isPending || newStatus === ticketData.status || possibleNextStatuses.length === 0}>
                    {mutation.isPending ? <CircularProgress size={24} /> : "Cập nhật"}
                </Button>
            </DialogActions>
        </Dialog>
    );
};

export default UpdateMaintenanceModal;