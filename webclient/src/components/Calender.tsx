import React, { JSX, FC, useMemo, useState } from "react"; 
import { useParams } from "react-router-dom"; 
import { useQuery, useQueryClient } from "@tanstack/react-query";
import {
    DateSelectArg,
    EventClickArg,
    EventInput,
    EventContentArg 
} from "@fullcalendar/core/index.js";
import {
    Alert, Box, Button, CircularProgress, Snackbar, Typography
} from "@mui/material";
import InsertInvitationIcon from "@mui/icons-material/InsertInvitation";
import axios, { AxiosError } from "axios"; 
import FullCalendar from "@fullcalendar/react";
import dayGridPlugin from "@fullcalendar/daygrid";
import timeGridPlugin from "@fullcalendar/timegrid";
import interactionPlugin from "@fullcalendar/interaction";

import AddEventModal from "./modals/AddEventModal";
import EventModal from "./modals/EventModal";
import isoToTime from "../utils/isoToTime";
import isoToDate from "../utils/isoToDate";
import ErrorComponent from "./Error";

// ---- Component Calendar ----

const Calendar: FC = (): JSX.Element => {
    const [isAddOpen, setIsAddOpen] = useState<boolean>(false);
    const [isOpenInfo, setIsOpenInfo] = useState<boolean>(false);
    const [openSnackbar, setOpenSnackbar] = useState<boolean>(false);
    const [selectionInfo, setSelectionInfo] = useState<DateSelectArg | null>(null);
    const [eventInfo, setEventInfo] = useState<EventInfoProps | null>(null);

    const { id: roomId } = useParams<{ id: string }>(); 
    const queryClient = useQueryClient();

    // --- Query 1: Lấy chi tiết phòng (để lấy tên phòng) ---
    const { data: roomDetailData, isLoading: isLoadingRoom, isError: isErrorRoom, error: errorRoom } =
        useQuery<RoomDetailApiResponse, AxiosError>({
            queryKey: ["roomDetail", roomId],
            queryFn: async () => {
                if (!roomId) throw new Error("Room ID is missing.");
                const token = localStorage.getItem("token");
                if (!token) throw new Error("No token found");
                const response = await axios.get<RoomDetailApiResponse>(
                    `${import.meta.env.VITE_APP_SERVER_URL}/rooms/${roomId}`,
                    { headers: { Authorization: `Bearer ${token}` } }
                );
                return response.data;
            },
            enabled: !!roomId, // Chỉ chạy khi có roomId
            staleTime: 5 * 60 * 1000, // Cache thông tin phòng trong 5 phút
        });

    // --- Query 2: Lấy danh sách booking của phòng (để hiển thị lịch) ---
    const { data: bookingListData, isLoading: isLoadingBookings, isError: isErrorBookings, error: errorBookings } =
        useQuery<PaginatedBookingApiResponse, AxiosError>({
            queryKey: ["roomBookings", roomId], // Key riêng cho booking
            queryFn: async () => {
                if (!roomId) throw new Error("Room ID is missing.");
                const token = localStorage.getItem("token");
                if (!token) throw new Error("No token found");
                // Gọi API lấy booking cho phòng 
                const response = await axios.get<PaginatedBookingApiResponse>(
                    `${import.meta.env.VITE_APP_SERVER_URL}/booking/room/${roomId}`, 
                    { headers: { Authorization: `Bearer ${token}` } }
                );
                return response.data;
            },
            enabled: !!roomId, // Chỉ chạy khi có roomId
            // gcTime: 0, // Có thể điều chỉnh
            // retry: 1,
        });

    // --- Chuyển đổi booking data sang events cho FullCalendar ---
    const calendarEvents = useMemo((): EventInput[] => {
        const bookings = bookingListData?.result?.content;
        if (!bookings) return [];
        console.debug("Mapping {} bookings to calendar events", bookings.length);
        return bookings.map((booking): EventInput => ({
            id: booking.id, // Dùng booking ID làm event ID
            title: booking.purpose || booking.userName, // Ưu tiên purpose, nếu không có dùng userName
            start: booking.plannedStartTime, 
            end: booking.plannedEndTime, 
            classNames: booking.status === "CONFIRMED" ? ['event-confirmed'] :
                        booking.status === "PENDING_APPROVAL" ? ['event-pending'] :
                        ['event-other'], // Thêm class cho các status khác nếu cần
            extendedProps: { // Lưu dữ liệu gốc quan trọng
                bookingData: booking // Lưu cả object booking gốc
            }
        }));
    }, [bookingListData]);

    // --- Handlers ---
    const handleCloseSnackbar = (): void => setOpenSnackbar(false);

    const handleEventClick = (clickInfo: EventClickArg): void => {
        const bookingData = clickInfo.event.extendedProps.bookingData as BookingEntry; // Lấy dữ liệu gốc
        if (!bookingData) return;

        console.debug("Event clicked, booking data:", bookingData);
        setEventInfo({
            bookingId: bookingData.id,
            title: clickInfo.event.title,
            purpose: bookingData.purpose,
            status: bookingData.status,
            start: isoToTime(bookingData.plannedStartTime), // Format giờ
            end: isoToTime(bookingData.plannedEndTime),
            date: isoToDate(bookingData.plannedStartTime), // Format ngày
            requestBy: bookingData.userName,
            roomName: bookingData.roomName,
            bookedEquipments: bookingData.bookedEquipments || [], // Lấy danh sách thiết bị
            actualCheckInTime: bookingData.actualCheckInTime,
            actualCheckOutTime: bookingData.actualCheckOutTime,
            cancellationReason: bookingData.cancellationReason,
            cancelledByUserName: bookingData.cancelledByUserName,
            createdAt: isoToDate(bookingData.createdAt),
            updatedAt: bookingData.updatedAt ? isoToDate(bookingData.updatedAt) : "N/A",
            note: bookingData.note,
        });
        setIsOpenInfo(true); // Mở modal thông tin
    };

    const handleSelect = (selectInfo: DateSelectArg): void => {
        // TODO: Có thể thêm kiểm tra xem thời gian chọn có hợp lệ không (ví dụ: không chọn quá khứ)
        console.debug("Date range selected:", selectInfo);
        setSelectionInfo(selectInfo);
        setIsAddOpen(true); // Mở modal thêm mới
    };

    const renderEventContent = (eventContent: EventContentArg) => {
        // Lấy status từ extendedProps để quyết định màu sắc/style
        const status = eventContent.event.extendedProps.bookingData?.status;
        let className = 'event-other'; // Mặc định
        if (status === 'CONFIRMED') {
            className = 'event-confirmed';
        } else if (status === 'PENDING_APPROVAL') {
            className = 'event-pending';
        }
    
        return (
            <div className={`fc-event-main-frame ${className}`}> 
                <div className="fc-event-time">{eventContent.timeText}</div>
                <div className="fc-event-title-container">
                    <div className="fc-event-title fc-sticky">{eventContent.event.title}</div>
                </div>
            </div>
        );
    };

    // --- Xử lý Loading / Error ---
    if (!roomId) {
        return <ErrorComponent status={400} message="Không có ID phòng được cung cấp trong URL." />;
    }
    if (isLoadingRoom || isLoadingBookings) {
         // Hiển thị loading nếu một trong hai query đang chạy
        return (
            <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '80vh' }}>
                <CircularProgress />
            </Box>
        );
    }
    if (isErrorRoom || isErrorBookings) {
        const errorToShow = errorRoom || errorBookings; // Ưu tiên hiển thị lỗi nào đó
        console.error("Error loading data:", errorToShow);
        const status = errorToShow?.response?.status || 500;
        const message = (errorToShow?.response?.data as { message?: string })?.message || errorToShow?.message || "Lỗi tải dữ liệu.";
        return <ErrorComponent status={status} message={message} />;
    }

    // --- Render ---
    const roomName = roomDetailData?.result?.name || `Phòng ${roomId}`;

    return (
        <Box sx={{ width: '100%', height: '100%', padding: { xs: 1, sm: 2, md: 3 }, paddingTop: 8 }}>
             {/* Modals */}
             {isAddOpen && selectionInfo && (
                 <AddEventModal
                     isOpen={isAddOpen}
                     roomId={roomId}
                    roomName={roomName}
                     startTime={selectionInfo.startStr}
                     endTime={selectionInfo.endStr}
                     allDay={selectionInfo.allDay}
                     onSuccess={() => {
                         setIsAddOpen(false);
                         setSelectionInfo(null);
                         setOpenSnackbar(true);
                         queryClient.invalidateQueries({ queryKey: ["roomBookings", roomId] }); // Invalidate query booking
                     }}
                     onCancel={() => {
                         setIsAddOpen(false);
                         setSelectionInfo(null);
                     }}
                 />
             )}
             {isOpenInfo && eventInfo && (
                 <EventModal
                     isOpen={isOpenInfo}
                     setIsOpen={setIsOpenInfo}
                     eventInfo={eventInfo} // Truyền EventInfoProps
                 />
             )}

             {/* Header */}
             <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 2, flexWrap: 'wrap', gap: 1 }}>
                 <Typography variant="h4" component="h1" gutterBottom>
                     Lịch {roomName}
                 </Typography>
                 <Button
                     variant="contained"
                     color="primary"
                     startIcon={<InsertInvitationIcon />}
                     onClick={() => alert("Vui lòng chọn một ngày hoặc khoảng thời gian trên lịch để thêm booking.")}
                 >
                     Thêm Booking
                 </Button>
             </Box>

             {/* Calendar */}
             <Box sx={{ height: 'calc(100vh - 150px)', position: 'relative' }}> {/* Thêm position relative nếu cần cho tooltip */}
                  <FullCalendar
                      key={roomId} // Quan trọng để reset khi đổi phòng
                      plugins={[dayGridPlugin, timeGridPlugin, interactionPlugin]}
                      initialView="timeGridWeek"
                      headerToolbar={{
                          left: "prev,next today",
                          center: "title",
                          right: "dayGridMonth,timeGridWeek,timeGridDay",
                      }}
                      events={calendarEvents}
                      eventContent={renderEventContent}
                      eventClick={handleEventClick}
                      selectable={true}
                      select={handleSelect}
                      editable={false}
                      droppable={false}
                      nowIndicator={true}
                      slotMinTime="06:00:00"
                      slotMaxTime="22:00:00"
                      allDaySlot={false} // Ẩn dòng All-day?
                      // height="100%" // Để Box ngoài kiểm soát
                      contentHeight="auto" // Thử nghiệm với auto
                      aspectRatio={1.8} // Điều chỉnh tỉ lệ
                      // Thêm timezone nếu cần
                      //timeZone='Asia/Ho_Chi_Minh'
                      //locale='vi' // Nếu muốn ngôn ngữ tiếng Việt (cần import locale)
                  />
             </Box>

            {/* Snackbar */}
             <Snackbar open={openSnackbar} autoHideDuration={4000} onClose={handleCloseSnackbar} anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}>
                <Alert onClose={handleCloseSnackbar} severity="success" variant="filled" sx={{ width: '100%' }}>
                    Yêu cầu đặt phòng đã được gửi!
                </Alert>
            </Snackbar>
        </Box>
    );
};

export default Calendar;