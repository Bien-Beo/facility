import React, { JSX, ChangeEvent, FC, FormEvent, useEffect, useState, useMemo } from "react";
import { useMutation } from "@tanstack/react-query";
import dayjs, { Dayjs } from "dayjs";
import isSameOrBefore from 'dayjs/plugin/isSameOrBefore'; 
import isSameOrAfter from 'dayjs/plugin/isSameOrAfter';   
import axios, { AxiosError } from "axios";
import {
    Button, Fade, FormControl, InputLabel, MenuItem, Modal, Select, SelectChangeEvent, TextField, Typography, Box, CircularProgress, Alert
} from "@mui/material";
import { LocalizationProvider } from "@mui/x-date-pickers/LocalizationProvider";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import { DatePicker } from "@mui/x-date-pickers/DatePicker";
import "dayjs/locale/en-gb";

// Extend dayjs plugins
dayjs.extend(isSameOrBefore);
dayjs.extend(isSameOrAfter);

// --- Component ---
const AddEventModal: FC<AddEventModalProps> = ({
    isOpen,
    roomId,
    roomName,
    startTime,
    endTime,
    onSuccess,
    onCancel,
}): JSX.Element => {

    const [formData, setFormData] = useState<AddBookingFormDataState>({
        purpose: "",
        selectedDate: null,
        startTimeString: "",
        endTimeString: "",
        note: "",
        additionalEquipmentItemIds: []
    });
    const [availableEndTimes, setAvailableEndTimes] = useState<string[]>([]);
    const [backendError, setBackendError] = useState<ErrorMessage | null>(null);
    const [validationError, setValidationError] = useState<string>("");

    // --- Danh sách giờ cố định ---
    const possibleTimeSlots = useMemo(() => {
        const slots: string[] = [];
        const minTime = dayjs().set("hour", 6).set("minute", 0);
        const maxTime = dayjs().set("hour", 22).set("minute", 0);
        let currentTime = minTime;
        while (currentTime.isSameOrBefore(maxTime)) {
            slots.push(currentTime.format("hh:mm A"));
            currentTime = currentTime.add(30, "minute");
        }
        console.log("Generated possibleTimeSlots:", slots); // Debug log
        return slots;
    }, []);

    // --- useEffect để khởi tạo/reset form khi props thay đổi hoặc modal mở ---
     useEffect(() => {
         if (isOpen && startTime && endTime && possibleTimeSlots.length > 0) {
             console.log("AddEventModal received props: ", { startTime, endTime });
             const startDt = dayjs(startTime);
             const endDt = dayjs(endTime);

             if (startDt.isValid() && endDt.isValid() && endDt.isAfter(startDt)) {
                 const initialStartTimeString = startDt.format("hh:mm A");
                 let initialEndTimeString = "";

                 // Logic tìm End Time Slot phù hợp hơn:
                 // Tìm slot cuối cùng mà startTime của slot đó <= endTime thực tế từ calendar selection
                 const startIndex = possibleTimeSlots.findIndex(time => time === initialStartTimeString);
                 const potentialEndTimes = startIndex !== -1 ? possibleTimeSlots.slice(startIndex + 1) : possibleTimeSlots;
                 setAvailableEndTimes(potentialEndTimes); // Set các lựa chọn cho End Time Select

                 // Tìm slot phù hợp nhất cho End Time dựa trên endDt
                 let matchedEndSlot = "";
                 for (const slot of potentialEndTimes) {
                      const slotDt = dayjs(`${startDt.format("YYYY-MM-DD")} ${slot}`, "YYYY-MM-DD hh:mm A");
                      if (slotDt.isValid() && slotDt.isSameOrBefore(endDt, 'minute')) {
                            matchedEndSlot = slot; // Slot này vẫn nằm trong khoảng chọn
                      } else if (slotDt.isAfter(endDt, 'minute')) {
                           break; // Đã vượt qua, không cần tìm nữa
                      }
                 }
                 // Nếu không tìm thấy slot nào phù hợp (ví dụ endDt quá sớm), giữ trống hoặc xử lý khác
                 initialEndTimeString = matchedEndSlot;


                 console.log("Formatted initial times: ", { initialStartTimeString, initialEndTimeString });

                 // Kiểm tra (chỉ để log)
                  if (!possibleTimeSlots.includes(initialStartTimeString)) console.warn(`Start time ${initialStartTimeString} not found in possible slots.`);
                  if (initialEndTimeString && !possibleTimeSlots.includes(initialEndTimeString)) console.warn(`End time ${initialEndTimeString} not found in possible slots.`);

                 setFormData({
                     purpose: "",
                     note: "",
                     additionalEquipmentItemIds: [],
                     selectedDate: startDt,
                     startTimeString: initialStartTimeString,
                     endTimeString: initialEndTimeString,
                 });
                 setValidationError("");
                 setBackendError(null);

             } else {
                  console.error("Invalid time props received or end time is not after start time", { startTime, endTime });
                  setFormData({ purpose: "", note: "", additionalEquipmentItemIds: [], selectedDate: null, startTimeString: "", endTimeString: "" });
                  setAvailableEndTimes([]);
                  setValidationError("Thời gian được chọn không hợp lệ.");
                  setBackendError(null);
             }
         }
     }, [isOpen, startTime, endTime, possibleTimeSlots]);

    // --- Mutation ---
    const mutation = useMutation<
        BookingCreationApiResponse, 
        AxiosError<ErrorMessage>,
        BookingCreationRequest
    >({
        mutationFn: async (newBookingData): Promise<BookingCreationApiResponse> => {
            const token = localStorage.getItem("token");
            console.log("Sending booking creation request:", newBookingData);
            // SỬA: Định kiểu cho axios.post là kiểu wrapper
            const response = await axios.post<BookingCreationApiResponse>(
                `${import.meta.env.VITE_APP_SERVER_URL || 'http://localhost:8080'}/booking`,
                newBookingData,
                { headers: { Authorization: `Bearer ${token}` } }
            );
            return response.data; 
        },
        onSuccess: (data: BookingCreationApiResponse) => { 
            console.log("Booking created successfully, API Response:", data);
            console.log("Created Booking Details:", data.result);
            onSuccess(); 
        },
        onError: (error) => {
            console.error("Booking creation failed:", error);
            setBackendError(error.response?.data || { message: error.message || "Lỗi không xác định", status: null });
        },
    });

    // --- Handlers ---
    const handleInputChange = (e: ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
    };

    const handleDateChange = (newValue: Dayjs | null) => {
        setFormData(prev => ({
            ...prev,
            selectedDate: newValue,
            startTimeString: "", // Reset time khi đổi ngày
            endTimeString: ""
        }));
        setAvailableEndTimes([]);
        setValidationError("");
    };

    const handleStartTimeChange = (e: SelectChangeEvent<string>) => {
         const newStartTimeString = e.target.value;
         setFormData(prev => ({ ...prev, startTimeString: newStartTimeString, endTimeString: "" })); // Reset end time

         // Cập nhật availableEndTimes ngay lập tức
         const startIndex = possibleTimeSlots.findIndex(time => time === newStartTimeString);
         if (startIndex !== -1) {
             setAvailableEndTimes(possibleTimeSlots.slice(startIndex + 1));
         } else {
             setAvailableEndTimes([]);
         }
         setValidationError("");
    };

    const handleEndTimeChange = (e: SelectChangeEvent<string>) => {
         setFormData(prev => ({ ...prev, endTimeString: e.target.value }));
    };

    // --- Submit ---
    const handleSubmit = (e: FormEvent<HTMLFormElement>): void => {
        e.preventDefault();
        setValidationError("");
        setBackendError(null);

        // Validation Client cơ bản
        if (!formData.purpose.trim()) { setValidationError("Mục đích không được để trống."); return; }
        if (!formData.selectedDate) { setValidationError("Vui lòng chọn ngày."); return; }
        if (!formData.startTimeString) { setValidationError("Vui lòng chọn thời gian bắt đầu."); return; }
        if (!formData.endTimeString) { setValidationError("Vui lòng chọn thời gian kết thúc."); return; }

        const format = "YYYY-MM-DD hh:mm A";
        const datePrefix = formData.selectedDate.format("YYYY-MM-DD");
        const startDateTime = dayjs(`${datePrefix} ${formData.startTimeString}`, format);
        const endDateTime = dayjs(`${datePrefix} ${formData.endTimeString}`, format);

        if (!startDateTime.isValid() || !endDateTime.isValid() || !endDateTime.isAfter(startDateTime)) {
            setValidationError("Thời gian không hợp lệ hoặc thời gian kết thúc không sau thời gian bắt đầu.");
            return;
        }

        const dataToSend: BookingCreationRequest = {
            roomId: roomId,
            purpose: formData.purpose.trim(),
            plannedStartTime: startDateTime.toISOString(),
            plannedEndTime: endDateTime.toISOString(),
            additionalEquipmentItemIds: formData.additionalEquipmentItemIds,
            note: formData.note.trim(),
        };

        mutation.mutate(dataToSend);
    };

    // --- Cancel ---
    const handleCancel = () => {
        onCancel(); // Gọi callback từ Calendar
    };


    // --- Render JSX ---
    return (
        <Modal open={isOpen} onClose={handleCancel}>
            <Fade in={isOpen}>
                <Box sx={{ position: 'absolute', top: '50%', left: '50%', transform: 'translate(-50%, -50%)', width: { xs: '90%', sm: '80%', md: 600 }, bgcolor: 'background.paper', border: '1px solid #ccc', boxShadow: 24, p: { xs: 2, md: 4 }, borderRadius: 2 }}>
                    <Typography variant="h5" component="h2" gutterBottom sx={{ textAlign: 'center', mb: 3 }}>
                        Tạo Yêu Cầu Đặt Phòng
                    </Typography>
                    <Typography variant="subtitle1" gutterBottom>
                        Phòng: <strong>{roomName}</strong> 
                    </Typography>
                    <Box component="form" onSubmit={handleSubmit} sx={{ display: 'flex', flexDirection: 'column', gap: 2.5 }}> {/* Tăng gap */}
                        <TextField
                            label="Mục đích (*)"
                            name="purpose"
                            value={formData.purpose}
                            onChange={handleInputChange}
                            required
                            fullWidth
                            multiline
                            rows={2}
                            size="small"
                        />
                         <LocalizationProvider dateAdapter={AdapterDayjs} adapterLocale="en-gb">
                             <DatePicker
                                 label="Ngày đặt (*)"
                                 value={formData.selectedDate}
                                 onChange={handleDateChange}
                                 disablePast={true}
                                 format="DD/MM/YYYY"
                                 slotProps={{ textField: { required: true, fullWidth: true, size: 'small' } }}
                             />
                         </LocalizationProvider>

                         <Box sx={{ display: 'flex', gap: 2 }}>
                            <FormControl fullWidth size="small" required>
                                <InputLabel id="start-time-label">Giờ bắt đầu (*)</InputLabel>
                                <Select
                                    labelId="start-time-label"
                                    value={formData.startTimeString}
                                    onChange={handleStartTimeChange}
                                    label="Giờ bắt đầu (*)"
                                    disabled={!formData.selectedDate}
                                >
                                    <MenuItem value=""><em>Chọn giờ</em></MenuItem>
                                    {possibleTimeSlots.map((time) => (
                                        <MenuItem key={"start-" + time} value={time}>{time}</MenuItem>
                                    ))}
                                </Select>
                            </FormControl>
                            <FormControl fullWidth size="small" required>
                                <InputLabel id="end-time-label">Giờ kết thúc (*)</InputLabel>
                                <Select
                                    labelId="end-time-label"
                                    value={formData.endTimeString}
                                    onChange={handleEndTimeChange}
                                    label="Giờ kết thúc (*)"
                                    disabled={!formData.startTimeString}
                                >
                                     <MenuItem value=""><em>Chọn giờ</em></MenuItem>
                                    {availableEndTimes.map((time) => (
                                        <MenuItem key={"end-" + time} value={time}>{time}</MenuItem>
                                    ))}
                                     {formData.startTimeString && availableEndTimes.length === 0 && (
                                         <MenuItem value="" disabled><em>Không có giờ kết thúc</em></MenuItem>
                                     )}
                                </Select>
                            </FormControl>
                        </Box>

                        {/* TODO: Input chọn additionalEquipmentItemIds */}
                        <Typography variant="body2" sx={{mt: 1}}>Thiết bị mượn thêm (Optional):</Typography>
                        {/* Placeholder - Cần component MultiSelect hoặc Checkbox Group */}
                        <Box sx={{border: '1px dashed grey', p: 1, minHeight: '50px', color: 'grey'}}>
                             (Khu vực chọn thiết bị thêm) <br/>
                             Comming Soon...
                        </Box>

                        <TextField
                            label="Ghi chú (Tùy chọn)"
                            name="note"
                            value={formData.note}
                            onChange={handleInputChange}
                            fullWidth
                            multiline
                            rows={2}
                            size="small"
                        />

                        {validationError && (<Typography variant="body2" color="error" sx={{ mt: 1 }}>{validationError}</Typography>)}
                        {backendError && (<Alert severity="error" sx={{ mt: 1 }}>{backendError.message || 'Lỗi không xác định'}</Alert>)}

                         <Box sx={{ display: 'flex', justifyContent: 'flex-end', gap: 1, mt: 2 }}>
                             <Button variant="outlined" color="secondary" onClick={handleCancel} disabled={mutation.isPending}>Hủy bỏ</Button>
                             <Button type="submit" variant="contained" color="primary" disabled={mutation.isPending}>
                                 {mutation.isPending ? <CircularProgress size={24} color="inherit" /> : "Gửi Yêu cầu"}
                             </Button>
                         </Box>
                    </Box>
                </Box>
            </Fade>
        </Modal>
    );
};

export default AddEventModal;