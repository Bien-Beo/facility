import {
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    Button,
    TextField,
    MenuItem,
    FormControl,
    InputLabel,
    Select,
    SelectChangeEvent,
  } from '@mui/material';
  import { useEffect, useState } from 'react';
  import { useMutation, useQueryClient } from '@tanstack/react-query';
  import dayjs, { Dayjs } from 'dayjs';
  import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
  import { LocalizationProvider, DateTimePicker } from '@mui/x-date-pickers';
  import axios from 'axios'; 
  
  const updateMaintenance = async (data: UpdateMaintenanceData) => {
    try {
      const token = localStorage.getItem('token');
      if (!token) throw new Error('No token found');
  
      const response = await axios.put(
        `/maintenance/${data.ticketId}`,
        {
          status: data.newStatus,
          note: data.note,
          scheduledTime: data.scheduledTime,
        },
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
  
      if (response.data?.code !== 0 || !response.data?.result) {
        throw new Error('Failed to update maintenance status');
      }
  
      return response.data.result;
    } catch (err) {
      console.error(err);
      throw err;
    }
  };
  

type MaintenanceStatusType = 'ASSIGNED' | 'IN_PROGRESS' | 'COMPLETED' | 'CANNOT_REPAIR' | 'CANCELLED';

const UpdateMaintenanceModal = ({
    open,
    onClose,
    ticketData,
}: UpdateMaintenanceModalProps) => {
    const [newStatus, setNewStatus] = useState<MaintenanceStatusType>('ASSIGNED');
    const [note, setNote] = useState<string>('');
    const [scheduledTime, setScheduledTime] = useState<Dayjs | null>(null);
    const queryClient = useQueryClient();

    const mutation = useMutation({
        mutationFn: updateMaintenance,
        onSuccess: () => {
            alert('Cập nhật trạng thái thành công');
            queryClient.invalidateQueries({ queryKey: ['maintenance-requests'] });
            onClose();
        },
        onError: () => {
            alert('Cập nhật trạng thái thất bại');
        },
    });

    useEffect(() => {
        if (ticketData) {
            setNewStatus(ticketData.status as MaintenanceStatusType);
            setNote('');
            setScheduledTime(
                ticketData.startDate ? dayjs(ticketData.startDate) : null
            );
        }
    }, [ticketData]);

    const handleStatusChange = (event: SelectChangeEvent<MaintenanceStatusType>) => {
        setNewStatus(event.target.value as MaintenanceStatusType);
    };

    const handleUpdate = () => {
        if (!ticketData) return;

        if (
            (newStatus === 'COMPLETED' || newStatus === 'CANNOT_REPAIR') &&
            note.trim() === ''
        ) {
            alert('Vui lòng nhập ghi chú hoàn thành hoặc lý do');
            return;
        }

        mutation.mutate({
            ticketId: ticketData.id,
            newStatus,
            note,
            scheduledTime: scheduledTime ? scheduledTime.toISOString() : undefined,
        });
    };

    const possibleNextStatuses: MaintenanceStatusType[] = (() => {
        switch (ticketData?.status) {
            case 'ASSIGNED':
                return ['IN_PROGRESS', 'CANCELLED'];
            case 'IN_PROGRESS':
                return ['COMPLETED', 'CANNOT_REPAIR', 'CANCELLED'];
            default:
                return [];
        }
    })();

    return (
        <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
            <DialogTitle>Cập nhật trạng thái bảo trì</DialogTitle>
            <DialogContent sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
                <FormControl fullWidth size="small">
                    <InputLabel>Trạng thái mới</InputLabel>
                    <Select
                        value={newStatus}
                        label="Trạng thái mới"
                        onChange={handleStatusChange}
                        disabled={mutation.isPending}
                    >
                        {possibleNextStatuses.map((status) => (
                            <MenuItem key={status} value={status}>
                                {status}
                            </MenuItem>
                        ))}
                    </Select>
                </FormControl>

                {['ASSIGNED', 'IN_PROGRESS'].includes(newStatus) && (
                    <LocalizationProvider dateAdapter={AdapterDayjs}>
                        <DateTimePicker
                            label="Thời gian hẹn sửa (nếu có)"
                            value={scheduledTime}
                            onChange={(newValue) => setScheduledTime(newValue)}
                            slotProps={{
                                textField: {
                                    fullWidth: true,
                                    size: 'small',
                                },
                            }}
                            disabled={mutation.isPending}
                        />
                    </LocalizationProvider>
                )}

                {(newStatus === 'COMPLETED' || newStatus === 'CANNOT_REPAIR') && (
                    <TextField
                        label={`Ghi chú hoàn thành / Lý do ${
                            newStatus === 'CANNOT_REPAIR' ? 'không sửa được' : ''
                        } (*)`}
                        multiline
                        rows={3}
                        fullWidth
                        value={note}
                        onChange={(e) => setNote(e.target.value)}
                        size="small"
                        disabled={mutation.isPending}
                    />
                )}
            </DialogContent>
            <DialogActions>
                <Button onClick={onClose} disabled={mutation.isPending}>
                    Hủy
                </Button>
                <Button
                    onClick={handleUpdate}
                    variant="contained"
                    disabled={mutation.isPending}
                >
                    Cập nhật
                </Button>
            </DialogActions>
        </Dialog>
    );
};
  
  export default UpdateMaintenanceModal;
  