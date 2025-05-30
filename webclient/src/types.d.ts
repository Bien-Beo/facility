// ==================================
// ==      ENUMS / UNION TYPES     ==
// ==================================
type RoomStatusType = "AVAILABLE" | "UNDER_MAINTENANCE";
type EquipmentStatusType = "AVAILABLE" | "IN_MAINTENANCE" | "BROKEN" | "DISPOSED";
type BookingStatusType = "PENDING_APPROVAL" | "CONFIRMED" | "REJECTED" | "CANCELLED" | "COMPLETED" | "OVERDUE" | "IN_PROGRESS";
type MaintenanceStatusType = "REPORTED" | "ASSIGNED" | "IN_PROGRESS" | "COMPLETED" | "CANNOT_REPAIR" | "CANCELLED";
type UserRoleType = "ADMIN" | "USER" | "TECHNICIAN" | "FACILITY_MANAGER";

// ==================================
// ==    CORE DATA ENTITY TYPES    ==
// ==================================
type EquipmentItemData = {
  id: string; // ID của Item (vd: i_epson1)
  modelName: string; // Tên Model (vd: Epson EB-S41)
  typeName: string; // Tên Loại (vd: Máy chiếu)
  serialNumber: string | null;
  assetTag?: string | null;
  status: EquipmentStatusType; // Dùng Union Type
  purchaseDate: string | null; // ISO Date string
  warrantyExpiryDate: string | null; // ISO Date string
  defaultRoomName: string | null; // Tên phòng mặc định
  notes: string | null;
  createdAt?: string | null; // ISO DateTime string
  updatedAt?: string | null; // ISO DateTime string
  imgModel: string | null; // Ảnh của Model
};

type RoomData = {
  id: string;
  name: string;
  description: string | null;
  capacity: number;
  img: string | null;
  status: RoomStatusType; // Dùng Union Type
  buildingName: string | null; // Tên tòa nhà
  roomTypeName: string | null; // Tên loại phòng
  nameFacilityManager: string | null; // Tên người quản lý
  location?: string | null; // Vị trí chi tiết
  createdAt?: string | null; // ISO DateTime string
  updatedAt?: string | null; // ISO DateTime string
  deletedAt?: string | null; // ISO DateTime string (cho soft delete)
  note?: string | null;
  defaultEquipments: EquipmentItemData[]; // Danh sách thiết bị mặc định đi kèm
};

interface RoomDataWithIds extends RoomData {
  buildingId: string | null;
  roomTypeId: string | null;
  facilityManagerId: string | null;
}

type Notification = {
  id: string; // ID của thông báo
  message: string; // Nội dung thông báo
  createdAt: string; // Thời gian tạo thông báo (ISO DateTime string)
  isRead: boolean; // Trạng thái đã đọc hay chưa
  type: string; // Loại thông báo (vd: "booking", "maintenance", ...)
  bookingId?: string | null; // ID của booking liên quan (nếu có)
  userId?: string | null; // ID của người dùng liên quan (nếu có)
  roomId?: string | null; // ID của phòng liên quan (nếu có)
}

type UserData = {
  id: string; // UUID khóa chính
  userId: string; // Mã nghiệp vụ (NV/SV)
  username: string; // Tên đăng nhập
  fullName: string | null;
  email: string;
  avatar: string | null;
  roleName: UserRoleType; // Tên vai trò
  createdAt: string; // ISO DateTime string
  updatedAt: string | null; // ISO DateTime string
};

type RoleData = {
  name: UserRoleType; // Tên vai trò
  description: string | null; // Mô tả vai trò
}

type BuildingData = {
  id: string;
  name: string;
  roomList: RoomData[];
};

type RoomTypeData = {
  id: string;
  name: string;
  description?: string | null;
};

type EquipmentModelData = {
  id: string;
  name: string;
  description?: string | null;
};

type BookedEquipmentSummary = {
  itemId: string;
  equipmentModelName: string;
  notes: string | null;
  isDefaultEquipment: boolean;
  serialNumber: string | null;
  assetTag: string | null; 
};

type BookingEntry = {
  id: string; // Booking ID
  userName: string;
  roomName: string | null;
  purpose: string;
  plannedStartTime: string; // ISO String
  plannedEndTime: string; // ISO String
  actualCheckInTime: string | null;
  actualCheckOutTime: string | null;
  status: BookingStatusType; // Dùng Union Type
  approvedByUserName: string | null;
  cancellationReason: string | null;
  cancelledByUserName: string | null;
  createdAt: string; // ISO String
  updatedAt: string | null; // ISO String
  note: string | null;
  bookedEquipments: BookedEquipmentSummary[];
};

interface MaintenanceTicketData {
  id: string;
  roomName: string | null; // Tên phòng
  modelName: string | null; // Tên model của thiết bị (nếu có)
  reportByUser: string; // Tên người báo cáo
  technicianName: string | null; // Tên KTV được giao
  description: string; // Mô tả sự cố
  notes: string | null; // Ghi chú khi hoàn thành/xử lý
  cost?: number | null; // Chi phí (mới)
  actionTaken?: string | null; // Hành động đã thực hiện (mới)
  status: MaintenanceStatusType; // Trạng thái ticket
  updatedAt: string | null; // Thời gian cập nhật cuối (ISO string)
  startDate?: string | null; // Ngày bắt đầu sửa? (mới, ISO string)
  completionDate?: string | null; // Ngày hoàn thành? (mới, ISO string)
  reportDate: string; // Ngày báo cáo (trước đó dùng createdAt?) (ISO string)
}

// ==================================
// ==     API RESPONSE TYPES       ==
// ==================================
type ApiResponse<T> = {
  code: number;
  result: T; // Phần dữ liệu chính
  message?: string;
};

type PageInfo = {
  size: number;
  number: number; // Trang hiện tại (0-based)
  totalElements: number;
  totalPages: number;
  // Thêm first, last, empty... nếu API trả về
};

type PaginatedResult<T> = {
  content: T[]; // Dữ liệu của trang hiện tại
  page: PageInfo;
};

/**
 * Kiểu dữ liệu cho API trả về danh sách Booking có phân trang
 */
type PaginatedBookingApiResponse = ApiResponse<PaginatedResult<BookingEntry>>;

type PaginatedNotificationApiResponse = ApiResponse<PaginatedResult<Notification>>;


// Kiểu dữ liệu cho API trả về danh sách Room có phân trang (dùng cho AdminFacilitiesTable)
type PaginatedRoomApiResponse = ApiResponse<PaginatedResult<RoomData>>;

type PaginatedUserApiResponse = ApiResponse<PaginatedResult<UserData>>;

type PaginatedMaintenanceTicketApiResponse = ApiResponse<PaginatedResult<MaintenanceTicketData>>;


// Kiểu dữ liệu cho API trả về chi tiết một Room
type RoomDetailApiResponse = ApiResponse<RoomData>; // Result là một RoomData object

// Kiểu dữ liệu cho API trả về chi tiết User 
type UserDetailApiResponse = ApiResponse<UserData>;

type CreatedBookingEquipmentInfo = {
  itemId: string;
  equipmentModelName: string;
  notes: string | null;
  isDefaultEquipment: boolean;
  serialNumber: string | null;
  assetTag: string | null;
};

type CreatedBookingResult = {
  id: string;
  userName: string;
  roomName: string | null;
  purpose: string;
  plannedStartTime: string;
  plannedEndTime: string;
  actualCheckInTime: string | null;
  actualCheckOutTime: string | null;
  status: BookingStatusType;
  approvedByUserName: string | null;
  cancellationReason: string | null;
  cancelledByUserName: string | null;
  createdAt: string;
  updatedAt: string | null;
  note: string | null;
  bookedEquipments: CreatedBookingEquipmentInfo[];
};
type BookingCreationApiResponse = ApiResponse<CreatedBookingResult>;

type DashboardRoomGroup = {
  type: string; // Tên loại phòng
  rooms: RoomData[]; // Mảng các RoomData
};

// Kiểu cho toàn bộ response Dashboard Room
type DashboardRoomResponse = ApiResponse<DashboardRoomGroup[]>; // Result là mảng các group

// Kiểu cho một nhóm trong response Dashboard Equipment
type DashboardEquipmentGroup = {
  type: string; // Tên loại thiết bị
  equipments: EquipmentItemData[]; // Mảng các EquipmentItemData
};
// Kiểu cho toàn bộ response Dashboard Equipment
type DashboardEquipmentResponse = ApiResponse<DashboardEquipmentGroup[]>;

// ==================================
// ==     API REQUEST DTO TYPES    ==
// ==================================

// --- Authentication ---
interface AuthenticationRequest {
  username?: string | null; 
  password?: string | null;
}
interface IntrospectRequest { token: string; }
interface LogoutRequest { token: string; }
interface RefreshRequest { token: string; } 
interface ResetPasswordRequest {
  oldPassword: string;
  newPassword: string;
}

// --- Booking ---
type BookingCreationRequest = {
  roomId: string | null;
  purpose: string;
  plannedStartTime: string; // ISO string
  plannedEndTime: string; // ISO string
  additionalEquipmentItemIds: string[];
  note: string;
};
type BookingUpdateRequest = {
  purpose?: string;
  plannedStartTime?: string; // ISO string
  plannedEndTime?: string; // ISO string
  additionalEquipmentItemIds?: string[] | null; // null = không đổi, [] = xóa hết
  note?: string;
};
type CancelBookingRequest = { 
  reason: string;
};
interface RejectBookingRequest {
  reason: string;
}

interface RecallBookingRequest {
  reason: string;
}

// --- User ---
type UserCreationRequest = {
  userId: string; // Mã nghiệp vụ (NV/SV)
  username: string;
  fullName?: string | null;
  email: string;
  roleName: UserRoleType; // Dùng Union Type
};

// --- Room ---
type RoomCreationRequest = {
  name: string;
  description?: string | null;
  capacity: number;
  location?: string | null;
  buildingId: string; 
  roomTypeId: string; 
  facilityManagerId?: string | null; 
  img?: string | null;
};

type RoomUpdateRequest = {
  name?: string;
  description?: string | null;
  capacity?: number | null; // Dùng number|null để phân biệt không cập nhật
  location?: string | null;
  buildingId?: string; // ID
  roomTypeId?: string; // ID
  facilityManagerId?: string | null; // ID (null/rỗng để xóa)
  status?: RoomStatusType; // Chỉ AVAILABLE/UNDER_MAINTENANCE
  img?: string | null;
  note?: string | null;
};

// --- User Update ---
type UserUpdateRequest = {
  userId?: string; // Mã nghiệp vụ (NV/SV)
  username?: string;
  fullName?: string | null;
  email?: string;
  roleName?: UserRoleType; // Dùng Union Type
  avatar?: string | null; // URL ảnh đại diện
};

// --- Equipment Item ---
type EquipmentItemCreationRequest = {
  modelId: string; // ID
  serialNumber?: string | null;
  assetTag?: string | null;
  purchaseDate?: string | null; // ISO Date string
  warrantyExpiryDate?: string | null; // ISO Date string
  defaultRoomId?: string | null; // ID
  notes?: string | null;
};

type EquipmentItemUpdateRequest = {
  assetTag?: string | null;
  status?: EquipmentStatusType; // Cẩn thận khi cho update qua đây
  defaultRoomId?: string | null; // ID (null/rỗng để xóa)
  notes?: string | null;
};


type MaintenanceTicketUpdatePayload = {
  status: MaintenanceStatusType; 
  completionNotes?: string | null;
  scheduledRepairTime?: string | null; 
  // assignedTechnicianId?: string | null; // Backend tự gán KTV đang đăng nhập?
};

interface MaintenanceTicketCreationRequest {
  itemId?: string | null; // ID của EquipmentItem (nếu có)
  roomId: string; // ID của phòng (luôn có)
  description: string;
}

type MaintenanceRequest = {
  id: number;
  status: MaintenanceStatus;
  scheduledTime?: string;
};

type UpdateMaintenanceData = {
  ticketId: string;
    newStatus: string;
    note?: string;
    scheduledTime?: string;
};

// Có thể thêm các DTO Request khác nếu cần (UserUpdate, RoleUpdate, ...)

// ==================================
// ==  COMPONENT PROPS INTERFACES  ==
// ==================================
type RoomCardProps = RoomData;

type EquipmentCardProps = EquipmentItemData;

interface DashboardPageProps {
  type: "room" | "equipment"; 
}

interface AddEventModalProps {
  isOpen: boolean;
  roomId: string;
  roomName: string;
  startTime: string; // ISO String
  endTime: string;   // ISO String
  allDay: boolean;
  onSuccess: () => void;
  onCancel: () => void;
}

interface RoomDetailProps {
  open: boolean;
  onClose: () => void;
  id: string;
  name: string;
  description: string;
  capacity: number;
  img: string;
  status: "AVAILABLE" | "UNDER_MAINTENANCE";
  buildingName: string;
  roomTypeName: string;
  nameFacilityManager: string;
  location?: string;
  createdAt: string;
  updatedAt?: string;
  deletedAt?: string;
  defaultEquipments?: EquipmentItemData[] | null;
}

// interface UpdateMaintenanceStatusModalProps {
//   isOpen: boolean;
//   setIsOpen: (isOpen: boolean) => void;
//   onClose: () => void; 
//   ticketData: MaintenanceTicketData | null;
//   onSuccessCallback?: () => void; 
// }

type UpdateMaintenanceModalProps = {
  open: boolean;
  onClose: () => void;
  ticketData: MaintenanceTicketData; 
};

interface ReportIssueProps {
  roomId: string;
}

interface EventInfoProps {
  bookingId?: string;
  purpose: string;
  status: BookingStatusType;
  start: string; // Formatted time
  end: string;   // Formatted time
  date: string;  // Formatted date
  requestBy: string; // Username
  roomName: string | null;
  bookedEquipments: BookedEquipmentSummary[]; // Dùng kiểu tóm tắt

  actualCheckInTime: string | null;
  actualCheckOutTime: string | null;
  approvedByUserName: string | null;
  cancellationReason: string | null;
  cancelledByUserName: string | null;
  createdAt: string; // ISO String
  updatedAt: string | null; // ISO String
  note: string | null;
}

interface EventModalProps {
  isOpen: boolean;
  setIsOpen: (isOpen: boolean) => void; // Hoặc dùng onCancel
  eventInfo: EventInfoProps | null; // Cho phép null
}

// Kiểu dữ liệu cho một dòng trong bảng Admin Room (đã xử lý)
interface AdminRoomsRowData {
  id: string; // Thêm id để làm key
  name: string | JSX.Element;
  description: string | null;
  status: string;
  createdAt: JSX.Element | string;
  updatedAt: JSX.Element | string;
  deletedAt: JSX.Element | string; // Có thể là 'N/A'
  facilityManager: string | null;
  actions?: JSX.Element;
}

// Kiểu dữ liệu cho một dòng trong bảng Admin Users
interface AdminUsersRowData {
  id: string; // Thêm id để làm key
  userId: string;
  username: string;
  fullName: string | null;
  email: string;
  avatar: string | null;
  roleName: UserRoleType; // Dùng Union Type
  createdAt: JSX.Element | string;
  updatedAt: JSX.Element | string;
  actions?: JSX.Element;
}

interface EquipmentsRowData {
  id: string;
  modelName: string | JSX.Element;
  typeName: string | null;
  serialNumber: string;
  status: string;
  notes: string;
  purchaseDate: JSX.Element | string;
  warrantyExpiryDate: JSX.Element | string;
  createdAt: JSX.Element | string;
  updatedAt: JSX.Element | string;
  defaultRoomName: string | null;
  actions?: JSX.Element;
}

// Kiểu dữ liệu định nghĩa cột cho bảng Admin Room
interface AdminRoomsColumnData {
  id: keyof AdminRoomsRowData | 'actions'; // Key của row data hoặc 'actions'
  label: string;
  minWidth?: number;
  align?: 'left' | 'right' | 'center'; // Thêm align nếu cần
}

interface AdminUsersColumnData {
  id: keyof AdminUsersRowData | 'actions'; // Key của row data hoặc 'actions'
  label: string;
  minWidth?: number;
  align?: 'left' | 'right' | 'center'; // Thêm align nếu cần
}

interface EquipmentColumnData {
  id: keyof EquipmentRowData | 'actions'; 
  label: string;
  minWidth?: number;
  align?: 'left' | 'right' | 'center'; // Thêm align nếu cần
}

// Props cho component bảng Admin Room
interface AdminRoomsTableProps {
  rooms: RoomData[]; // Mảng RoomData cho trang hiện tại
  totalRoomCount: number; // Tổng số lượng
  page: number; // Index trang hiện tại (0-based)
  rowsPerPage: number; // Số dòng/trang
  onPageChange: (event: unknown, newPage: number) => void;
  onRowsPerPageChange: (event: ChangeEvent<HTMLInputElement>) => void;
  buildings?: BuildingData[]; // Có thể cần cho modal edit/add
  roomTypes: RoomTypeData[];
  facilityManagers: UserData[];
}

interface AdminUsersTableProps {
  users: UserData[]; // Mảng UserData cho trang hiện tại
  totalUserCount: number; // Tổng số lượng user
  page: number; // Index trang hiện tại (0-based)
  rowsPerPage: number; // Số dòng/trang
  onPageChange: (event: unknown, newPage: number) => void;
  onRowsPerPageChange: (event: ChangeEvent<HTMLInputElement>) => void;
}

interface EquipmentsTableProps {
  equipments: EquipmentItemData[]; 
  totalEquipmentCount: number; 
  page: number;
  rowsPerPage: number; 
  onPageChange: (event: unknown, newPage: number) => void;
  onRowsPerPageChange: (event: ChangeEvent<HTMLInputElement>) => void;
  defaultRoom: RoomData[];
}

interface AddFacilityModalProps {
  isOpen: boolean;
  setIsOpen: (isOpen: boolean) => void;
  setOpenSnackbar: (isOpen: boolean) => void;
  buildings: BuildingData[]; 
  roomTypes: RoomTypeData[];
  facilityManagers: UserData[];
  onSuccessCallback?: () => void;
}

interface AddAccountModalProps {
  isOpen: boolean;
  setIsOpen: (isOpen: boolean) => void;
  setOpenSnackbar: (isOpen: boolean) => void;
  onSuccessCallback?: () => void; // Callback khi thêm thành công
}

interface AddEquipmentModalProps {
  isOpen: boolean;
  setIsOpen: (isOpen: boolean) => void;
  setOpenSnackbar: (isOpen: boolean) => void;
  defaultRoom: RoomData[] | null;
  models: EquipmentModelData[];
  onSuccessCallback?: () => void;
}

interface MaintenanceTicketTableProps {
  tickets: MaintenanceTicketData[]; 
  totalTicketCount: number; 
  page: number;
  rowsPerPage: number;
  onPageChange: (event: unknown, newPage: number) => void;
  onRowsPerPageChange: (event: ChangeEvent<HTMLInputElement>) => void;
  //onUpdateStatusClick?: (ticket: MaintenanceTicketData) => void;
}

interface EditFacilityModalProps {
  isOpen: boolean;
  setIsOpen: (isOpen: boolean) => void;
  setOpenSnackbar: (isOpen: boolean) => void;
  facilityData: RoomDataWithIds;
  buildings: BuildingData[];
  roomTypes?: RoomTypeData[];
  facilityManagers?: UserData[];
  onSuccessCallback?: () => void;
}

interface EditUserModalProps {
  isOpen: boolean;
  setIsOpen: (isOpen: boolean) => void;
  setOpenSnackbar: (isOpen: boolean) => void;
  userData: UserData; // Dữ liệu người dùng cần sửa
  onSuccessCallback?: () => void; // Callback khi sửa thành công
}

interface EditEquipmentModalProps {
  isOpen: boolean;
  setIsOpen: (isOpen: boolean) => void;
  setOpenSnackbar: (isOpen: boolean) => void;
  equipmentData: EquipmentDataWithIds; 
  defaultRoom?: RoomData[] | null;  
  onSuccessCallback?: () => void;
}

interface DeleteFacilityModalProps {
  isOpen: boolean;
  setIsOpen: (isOpen: boolean) => void; // Hoặc onClose(): void;
  setOpenSnackbar: (isOpen: boolean) => void; // Hoặc onSuccess(): void;
  facilityData: Pick<RoomData, 'id' | 'name'> | null; // Dùng Pick và cho phép null
  onSuccessCallback?: () => void; // Optional callback khi xóa thành công
}

interface DeleteUserModalProps {
  isOpen: boolean;
  setIsOpen: (isOpen: boolean) => void; // Hoặc onClose(): void;
  setOpenSnackbar: (isOpen: boolean) => void; // Hoặc onSuccess(): void;
  userData: Pick<UserData, 'userId' | 'username'> | null; // Dùng Pick và cho phép null
  onSuccessCallback?: () => void; // Optional callback khi xóa thành công
}

interface DeleteEquipmentModalProps {
  isOpen: boolean;
  setIsOpen: (isOpen: boolean) => void; // Hoặc onClose(): void;
  setOpenSnackbar: (isOpen: boolean) => void; // Hoặc onSuccess(): void;
  equipmentData: Pick<EquipmentItemData, 'id' | 'modelName'> | null; // Dùng Pick và cho phép null
  onSuccessCallback?: () => void; // Optional callback khi xóa thành công
}

interface MyBookingCardProps {
  booking: BookingEntry; // Chỉ cần nhận đối tượng booking
  onCancelSuccess?: () => void; // Optional callback
}

interface AdminBookingsTableProps {
  bookings: BookingEntry[]; // Mảng BookingEntry cho trang hiện tại
  totalBookingCount: number; // Tổng số lượng item
  page: number; // Index trang hiện tại (0-based)
  rowsPerPage: number; // Số dòng/trang
  onPageChange: (event: unknown, newPage: number) => void;
  onRowsPerPageChange: (event: ChangeEvent<HTMLInputElement>) => void;
}

interface AdminBookingApprovalModalProps {
  isOpen: boolean;
  setIsOpen: (isOpen: boolean) => void;
  setOpenSnackbar: (isOpen: boolean) => void;
  bookingId: string | null; // ID của booking cần duyệt
  bookingData?: BookingEntry | null; // Optional: Dữ liệu để hiển thị trong modal
  onSuccessCallback?: () => void; // Optional: Callback khi duyệt thành công
}

interface AdminBookingRejectModalProps {
  isOpen: boolean;
  setIsOpen: (isOpen: boolean) => void;
  setOpenSnackbar: (isOpen: boolean) => void;
  bookingId: string | null;
  bookingData?: BookingEntry | null; // Optional: để hiển thị thông tin xác nhận
  onSuccessCallback?: () => void;
}

interface AdminBookingRowData {
  id: string;
  requesterInfo: JSX.Element | string;
  userName: string;
  roomName: string | null;
  purpose: string | null;
  plannedTime: JSX.Element | string;
  equipmentInfo: JSX.Element | string;
  requestedAt: JSX.Element | string;
  status: JSX.Element | string; // Có thể là Chip JSX hoặc string đã format
  processedBy: string | null;
  reasonOrNote: string | null;
  actions?: JSX.Element;
}

interface AdminBookingColumnData {
  id: keyof Omit<AdminBookingRowData, 'id'>;
  label: string;
  minWidth?: number;
  align?: 'left' | 'right' | 'center';
}
interface MaintenanceTicketRowData {
  id: string;
  roomName: JSX.Element | string; 
  modelName: JSX.Element | string; 
  description: JSX.Element | string;
  status: JSX.Element; 
  reporter: string;
  reportedAt: JSX.Element | string; 
  technician: string | null;
  actions?: JSX.Element;
}

interface MaintenanceTicketColumnData {
  id: keyof Omit<MaintenanceTicketRowData, 'id'>; // Key của RowData
  label: string;
  minWidth?: number;
  align?: 'left' | 'right' | 'center';
}

// Dữ liệu một dòng trong bảng báo cáo Admin Bookings 
interface ReportBookingRowData {
  id: string;
  userName: string;
  roomName: string | null;
  purpose: string | null;
  plannedTime: JSX.Element | string;
  requestedAt: JSX.Element | string;
  status: BookingStatusType | string; // Giữ nguyên để style hoặc format
  processedBy: string | null;
  reasonOrNote: string | null;
}

// Định nghĩa cột cho bảng báo cáo Admin Bookings
interface ReportBookingColumnData {
    id: keyof Omit<ReportBookingRowData, 'id'>; 
    label: string;
    minWidth?: number;
    align?: 'left' | 'right' | 'center';
}

// Props cho component
interface AdminBookingsReportProps {
  bookings: BookingEntry[]; // <<< Sửa: Nhận mảng BookingEntry
  forwardedRef: React.Ref<HTMLDivElement>;
}

// Kiểu dữ liệu cho Context mà Provider sẽ cung cấp
interface AuthContextType {
  user: UserData | null; // Thông tin user hoặc null nếu chưa đăng nhập
  login: (authData: { token: string; authenticated: boolean }) => Promise<void>;
  logout: () => Promise<void>; // Chuyển logout thành async để đợi API backend
  loadingUser: boolean; // Cờ báo trạng thái đang tải thông tin user ban đầu
}

// Kiểu dữ liệu cho Request API /auth/logout
interface LogoutRequest {
  token: string;
}

// Kiểu dữ liệu cho Props của AuthProvider
interface AuthProviderProps {
  children: React.ReactNode;
}
interface RequireAuthProps {
  children: React.ReactNode;
  // Dùng UserRoleType Union thay vì boolean riêng lẻ sẽ tốt hơn
  allowedRoles?: UserRoleType[];
  // Bỏ các boolean riêng: Technician?: boolean; FacilityManager?: boolean; Admin?: boolean; User?: boolean;
}

interface ApprovalCardProps {
  booking: BookingEntry;
  onActionSuccess?: () => void; // Callback khi duyệt/từ chối thành công
}

interface OverdueCardProps {
  booking: BookingEntry;
  onActionSuccess?: () => void;
}

/**
 * Các kiểu dữ liệu khác (Error, Navigation...)
 */
interface ErrorMessage {
  message: string;
  status?: number | null; // Status code từ backend (nếu có)
  code?: number | string; // Mã lỗi nội bộ (nếu có)
}
interface ErrorProps {
  message: string;
  status: number;
}
interface NavigationProps {
  id: string;
  count: number; 
}

// ==================================
// ==     INTERNAL COMPONENT STATE ==
// ==================================
/**
 * State nội bộ cho form trong AddEventModal
 */
interface AddBookingFormDataState {
  purpose: string;
  selectedDate: Dayjs | null;
  startTimeString: string; // hh:mm A
  endTimeString: string;   // hh:mm A
  note: string;
  additionalEquipmentItemIds: string[];
}