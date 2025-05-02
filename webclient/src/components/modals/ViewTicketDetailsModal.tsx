import React from 'react';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  Typography,
  Grid
} from '@mui/material';

import isoToDate from '../../utils/isoToDate';
import isoToTime from '../../utils/isoToTime';

type ViewTicketDetailsModalProps = {
  open: boolean;
  onClose: () => void;
  ticketData: MaintenanceTicketData;
};

const ViewTicketDetailsModal: React.FC<ViewTicketDetailsModalProps> = ({ open, onClose, ticketData }) => {
  const renderDateTime = (isoDate?: string | null) => (
    isoDate ? `${isoToDate(isoDate)} ${isoToTime(isoDate)}` : 'N/A'
  );

  return (
    <Dialog open={open} onClose={onClose} maxWidth="md" fullWidth>
      <DialogTitle>Chi tiết Ticket #{ticketData.id}</DialogTitle>
      <DialogContent dividers>
        <Grid container spacing={2}>
          {/* Thông tin cơ bản */}
          <Grid item xs={6}>
            <Typography variant="subtitle2">Phòng:</Typography>
            <Typography variant="body1">{ticketData.roomName || 'N/A'}</Typography>
          </Grid>
          <Grid item xs={6}>
            <Typography variant="subtitle2">Thiết bị:</Typography>
            <Typography variant="body1">{ticketData.modelName || 'N/A'}</Typography>
          </Grid>
          <Grid item xs={12}>
            <Typography variant="subtitle2">Mô tả sự cố:</Typography>
            <Typography variant="body1" sx={{ whiteSpace: 'pre-line' }}>{ticketData.description || 'N/A'}</Typography>
          </Grid>
          <Grid item xs={6}>
            <Typography variant="subtitle2">Người báo cáo:</Typography>
            <Typography variant="body1">{ticketData.reportByUser || 'N/A'}</Typography>
          </Grid>
          <Grid item xs={6}>
            <Typography variant="subtitle2">Thời gian báo cáo:</Typography>
            <Typography variant="body1">{renderDateTime(ticketData.reportDate)}</Typography>
          </Grid>
          <Grid item xs={6}>
            <Typography variant="subtitle2">Trạng thái:</Typography>
            <Typography variant="body1">{ticketData.status || 'N/A'}</Typography>
          </Grid>
          <Grid item xs={6}>
            <Typography variant="subtitle2">KTV được giao:</Typography>
            <Typography variant="body1">{ticketData.technicianName || 'Chưa giao'}</Typography>
          </Grid>

          {/* Thông tin mở rộng */}
          <Grid item xs={6}>
            <Typography variant="subtitle2">Ngày bắt đầu sửa:</Typography>
            <Typography variant="body1">{renderDateTime(ticketData.startDate)}</Typography>
          </Grid>
          <Grid item xs={6}>
            <Typography variant="subtitle2">Ngày hoàn thành:</Typography>
            <Typography variant="body1">{renderDateTime(ticketData.completionDate)}</Typography>
          </Grid>
          <Grid item xs={6}>
            <Typography variant="subtitle2">Thời gian cập nhật cuối:</Typography>
            <Typography variant="body1">{renderDateTime(ticketData.updatedAt)}</Typography>
          </Grid>
          <Grid item xs={6}>
            <Typography variant="subtitle2">Chi phí sửa chữa:</Typography>
            <Typography variant="body1">{ticketData.cost != null ? `${ticketData.cost.toLocaleString()} ₫` : 'N/A'}</Typography>
          </Grid>
          <Grid item xs={12}>
            <Typography variant="subtitle2">Hành động đã thực hiện:</Typography>
            <Typography variant="body1" sx={{ whiteSpace: 'pre-line' }}>{ticketData.actionTaken || 'N/A'}</Typography>
          </Grid>
          <Grid item xs={12}>
            <Typography variant="subtitle2">Ghi chú thêm:</Typography>
            <Typography variant="body1" sx={{ whiteSpace: 'pre-line' }}>{ticketData.notes || 'N/A'}</Typography>
          </Grid>
        </Grid>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose} variant="contained" color="primary">
          Đóng
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default ViewTicketDetailsModal;
