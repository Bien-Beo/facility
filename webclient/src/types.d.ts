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

type UserData = {
  id: string; // UUID khóa chính
  userId: string; // Mã nghiệp vụ (NV/SV)
  username: string; // Tên đăng nhập
  fullName: string | null;
  email: string;
  avatar: string | null;
  roleName: UserRoleType; // Tên vai trò
};

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

/**
 * Kiểu dữ liệu cho API trả về danh sách Room có phân trang (dùng cho AdminFacilitiesTable)
 */
type PaginatedRoomApiResponse = ApiResponse<PaginatedResult<RoomData>>;

type PaginatedUserApiResponse = ApiResponse<PaginatedResult<UserData>>;

/**
 * Kiểu dữ liệu cho API trả về chi tiết một Room
 */
type RoomDetailApiResponse = ApiResponse<RoomData>; // Result là một RoomData object


//Kiểu dữ liệu cho API trả về chi tiết User 
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

interface EventInfoProps {
  bookingId?: string;
  title: string;
  purpose: string;
  status: BookingStatusType;
  start: string; // Formatted time
  end: string;   // Formatted time
  date: string;  // Formatted date
  requestBy: string; // Username
  roomName: string | null;
  bookedEquipments: BookedEquipmentSummary[]; // Dùng kiểu tóm tắt
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

// Kiểu dữ liệu định nghĩa cột cho bảng Admin Room
interface AdminRoomsColumnData {
  id: keyof AdminRoomsRowData | 'actions'; // Key của row data hoặc 'actions'
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

interface AddFacilityModalProps {
  isOpen: boolean;
  setIsOpen: (isOpen: boolean) => void;
  setOpenSnackbar: (isOpen: boolean) => void;
  buildings: BuildingData[]; 
  roomTypes: RoomTypeData[];
  facilityManagers: UserData[];
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

interface DeleteFacilityModalProps {
  isOpen: boolean;
  setIsOpen: (isOpen: boolean) => void; // Hoặc onClose(): void;
  setOpenSnackbar: (isOpen: boolean) => void; // Hoặc onSuccess(): void;
  facilityData: Pick<RoomData, 'id' | 'name'> | null; // Dùng Pick và cho phép null
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
  id: string; // ID của người dùng hoặc context
  count: number; // Số lượng (ví dụ: số phòng quản lý)
  // Đổi tên các trường cho rõ ràng hơn
  // approvalCount: number;
  // cancellationCount: number;
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

// // --------INTERFACES--------

// interface RoomCardProps {
//   id: string;
//   name: string;
//   description: string;
//   capacity: number;
//   img: string;
//   status: "AVAILABLE" | "UNDER_MAINTENANCE";
//   buildingName: string;
//   roomTypeName: string;
//   nameFacilityManager: string;
//   location?: string;
//   createdAt: string;
//   updatedAt?: string;
//   deletedAt?: string;
//   defaultEquipments?: EquipmentItemData[] | null;
// }

// interface EquipmentCardProps {
//   id: string;
//   modelName: string;
//   typeName: string;
//   serialNumber: string | null;
//   assetTag?: string | null; 
//   status: string; 
//   purchaseDate: string | null;
//   warrantyExpiryDate: string | null;
//   defaultRoomName: string | null;
//   notes: string | null;
//   createdAt?: string | null;
//   updatedAt?: string | null; 
//   imgModel: string | null; 
// }

// interface DashboardPageProps {
//   type: FacilityType; 
// }

// interface FacilitiesProps {
//   type: FacilityType; 
// }

// // interface LoginData {
// //   username: string;
// //   password: string;
// // }

// // interface LoginResponse {
// //   code: number;
// //   result: {
// //     token: string;
// //     authenticated: boolean;
// //   };
// // }


// // --- Props Interface
// interface AddEventModalProps {
//   isOpen: boolean;
//   roomId: string;
//   roomName: string;
//   startTime: string; 
//   endTime: string; 
//   allDay: boolean;  
//   onSuccess: () => void;
//   onCancel: () => void;
// }

// // --- Kiểu dữ liệu cho state của form ---
// interface FormDataState {
//   purpose: string;
//   selectedDate: Dayjs | null;
//   startTimeString: string; // hh:mm A
//   endTimeString: string;   // hh:mm A
//   note: string;
//   additionalEquipmentItemIds: string[];
// }

// // interface EventModalProps {
// //   isOpen: boolean;
// //   setIsOpen: (isOpen: boolean) => void;
// //   eventInfo: EventInfoProps;
// // }

// interface EventContentProps {
//   event: {
//     extendedProps: {
//       slug: string;
//     };
//   };
// }

// // interface BookingDataProps {
// //   facility: {
// //     name: string;
// //   };
// //   bookings: BookingData[];
// // }

// // interface BookingData {
// //   id: string;
// //   title: string;
// //   slug: string;
// //   purpose: string;
// //   status: string;
// //   createdAt: string;
// //   facilityManager: string | null;
// //   statusUpdateAtGD: string | null;
// //   statusUpdateAtFM: string | null;
// //   statusUpdateAtAdmin: string | null;
// //   statusUpdateByGD: {
// //     user: {
// //       name: string;
// //       employeeId: number;
// //     };
// //   } | null;
// //   statusUpdateByFM: {
// //     user: {
// //       name: string;
// //       employeeId: number;
// //     };
// //   } | null;
// //   time: {
// //     start: string;
// //     end: string;
// //     date: string;
// //   };
// //   requestedBy: {
// //     name: string;
// //     employeeId: number;
// //   };
// //   facility: {
// //     name: string;
// //     slug: string;
// //   };
// // }

// interface BookingDataProps {
//   facility: {
//     name: string;
//   };
//   bookings: BookingData[];
// }

// interface BookingData {
//   id: string;
//   title: string;
//   slug: string;
//   purpose: string;
//   status: string;
//   createdAt: string;
//   facilityManager: string | null;
//   statusUpdateAtGD: string | null;
//   statusUpdateAtFM: string | null;
//   statusUpdateAtAdmin: string | null;
//   statusUpdateByGD: {
//     user: {
//       name: string;
//       employeeId: number;
//     };
//   } | null;
//   statusUpdateByFM: {
//     user: {
//       name: string;
//       employeeId: number;
//     };
//   } | null;
//   time: {
//     start: string;
//     end: string;
//     date: string;
//   };
//   requestedBy: {
//     name: string;
//     employeeId: number;
//   };
//   facility: {
//     name: string;
//     slug: string;
//   };
// }

// interface BookingNewDataProps {
//   id: string;
//   title: string;
//   slug: string;
//   purpose: string;
//   status: string;
//   createdAt: string;
//   facilityManager: string | null;
//   statusUpdateAtGD: string | null;
//   statusUpdateAtFM: string | null;
//   statusUpdateAtAdmin: string | null;
//   statusUpdateByGD: {
//     user: {
//       name: string;
//       employeeId: number;
//     };
//   } | null;
//   statusUpdateByFM: {
//     user: {
//       name: string;
//       employeeId: number;
//     };
//   } | null;
//   start: string;
//   end: string;
//   date: string;
//   requestedBy: {
//     name: string;
//     employeeId: number;
//   };
//   facility: {
//     name: string;
//     slug: string;
//   };
// }

// interface AddEventDataProps {
//   title: string;
//   purpose: string;
//   date: Dayjs | null;
//   start: string | null;
//   end: string | null;
//   userId: string | null;
//   slug: string;
// }

// interface Permission {
//   name: string;
//   description: string;
// }

// interface Role {
//   name: string;
//   description: string;
//   permissions: Permission[];
// }

// interface User {
//   id: string;
//   userId: string;
//   username: string;
//   email: string;
//   fullName: string;
//   avatar: string;
//   roleName: string;
// }

// interface AuthContextType {
//   user: User | null;
//   login: (authData: { token: string; authenticated: boolean }) => Promise<void>;
//   logout: () => void;
//   loadingUser: boolean;
// }

// interface AuthProviderProps {
//   children: ReactNode;
// }

// interface RequireAuthProps {
//   children: ReactNode;
//   Technician: boolean;
//   FacilityManager: boolean;
//   Admin?: boolean;
//   User?: boolean;
// }

// // interface EventInfoProps {
// //   title: string;
// //   purpose: string;
// //   status: string;
// //   start: string;
// //   end: string;
// //   date: string;
// //   requestBy: string;
// //   statusUpdateByGD: string | null;
// //   statusUpdateByFM: string | null;
// //   statusUpdateByAdmin: string | null;
// // }

// // interface ApprovalProps {
// //   title: string;
// //   purpose: string;
// //   cancellationRemark?: string;
// //   slug: string;
// //   createdAt: string;
// //   cancelledAt?: string | null;
// //   date: string;
// //   start: string;
// //   end: string;
// //   facility: string;
// //   requestedBy: string | null;
// //   approvedByGD: string | null;
// //   approvedAtGD?: string | null;
// // }

// // interface MyBookingCardProps {
// //   title: string;
// //   purpose: string;
// //   status: string;
// //   cancelStatus?: string;
// //   slug?: string;
// //   remark: string;
// //   createdAt?: string;
// //   date: string;
// //   start: string;
// //   end: string;
// //   facility: string;
// //   requestedBy: string | null;
// //   approvedByGD?: string | null;
// //   approvedByFM?: string | null;
// //   approvedAtGD?: string | null;
// //   approvedAtFM?: string | null;
// //   approvedAtAdmin?: string | null;
// //   cancellationRequestedAt?: string | null;
// //   cancellationRemark?: string | null;
// //   cancellationUpdateAtGD?: string | null;
// //   cancellationUpdateAtFM?: string | null;
// // }

// // interface ApprovalStatusProps {
// //   GD: boolean;
// //   FM: boolean;
// // }

// // interface FilterOptionProps {
// //   label: string;
// // }

// // interface AdminBookingsColumnData {
// //   id:
// //     | "title"
// //     | "purpose"
// //     | "date"
// //     | "time"
// //     | "createdAt"
// //     | "status"
// //     | "reqBy"
// //     | "actions"
// //     | "gd"
// //     | "fm"
// //     | "admin"
// //     | "cancellationremark"
// //     | "cancellationstatus"
// //     | "remark"
// //     | "actions";
// //   label: string;
// //   minWidth?: number;
// // }

// // interface AdminBookingsTableProps {
// //   bookingsData: ApprovalData[];
// //   forwardedRef?: React.RefObject<HTMLDivElement>;
// // }

// // interface FMBookingsTableProps {
// //   bookingsData: [{ bookings: ApprovalData[] }];
// //   forwardedRef?: React.RefObject<HTMLDivElement>;
// // }

// interface AdminRoomsTableProps {
//   roomsData?: RoomData[];
//   rooms: RoomData[];
//   buildings?: [
//     {
//       name: string;
//     }
//   ];
//   forwardedRef?: React.RefObject<HTMLDivElement>;
// }

// // interface AdminBookingsRowData {
// //   title: JSX.Element;
// //   purpose: string;
// //   date: string;
// //   time: string;
// //   createdAt: JSX.Element;
// //   reqBy: string;
// //   status: JSX.Element | string;
// //   cancellationstatus: JSX.Element | string;
// //   remark: string;
// //   cancellationremark: string;
// //   actions?: string | JSX.Element;
// //   gd: JSX.Element | null;
// //   fm: JSX.Element | null;
// //   admin: JSX.Element | null;
// // }

// interface AdminRoomsColumnData {
//   id:
//     | "name"
//     | "description"
//     | "status"
//     | "createdAt"
//     | "updatedAt"
//     | "deletedAt"
//     | "actions"
//     | "facilityManager";
//   label: string;
//   minWidth?: number;
// }

// interface AdminRoomsRowData {
//   name: string | JSX.Element;
//   description: string;
//   status: string;
//   createdAt: JSX.Element;
//   updatedAt: JSX.Element;
//   deletedAt: JSX.Element;
//   actions?: string | JSX.Element;
//   facilityManager: string | null;
// }

// interface AddRoomDataProps {
//   name: string;
//   description: string;
//   capacity: number;
//   buildingName: string | null;
//   img: string;
//   facilityManagerId: string | number | null;
//   roomTypeName: string;
//   location: string;
// }

// interface AddFacilityModalProps {
//   isOpen: boolean;
//   setIsOpen: (isOpen: boolean) => void;
//   setOpenSnackbar: (isOpen: boolean) => void;
//   buildings: [
//     {
//       name: string;
//     }
//   ];
// }

// // interface EditFacilityModalProps {
// //   isOpen: boolean;
// //   setIsOpen: (isOpen: boolean) => void;
// //   setOpenSnackbar: (isOpen: boolean) => void;
// //   facilityData: FacilityData;
// //   buildingData?: [
// //     {
// //       name: string;
// //     }
// //   ];
// // }

// // interface AdminBookingsModalProps {
// //   isOpen: boolean;
// //   setIsOpen: (isOpen: boolean) => void;
// //   setOpenSnackbar: (isOpen: boolean) => void;
// //   slug: string;
// // }

// interface ErrorMessage {
//     message: string;
//     status: number | null;
// }

// interface ErrorProps {
//   message: string;
//   status: number;
// }

// interface NavigationProps {
//   approvalCount: number;
//   cancellationCount: number;
// }

// interface RouteError {
//   status: number;
//   message: string;
// }

// // ----------TYPES-----------
// type FacilityType = "room" | "equipment";

// // --- Kiểu dữ liệu gửi lên backend 
// type BackendBookingPayload = {
//   roomId: string | null;
//   purpose: string;
//   plannedStartTime: string; // ISO string
//   plannedEndTime: string; // ISO string
//   additionalEquipmentItemIds: string[];
//   note: string;
// };

// type APIResponse<T> = {
//   result: T;
//   code: number;
// };

// type BuildingData = {
//   id: string;
//   name: string;
// };

// type RoomData = {
//   id: string;
//   name: string;
//   description: string;
//   capacity: number;
//   img: string;
//   //status: "AVAILABLE" | "UNDER_MAINTENANCE";
//   status: string;
//   buildingName: string;
//   roomTypeName: string;
//   nameFacilityManager: string;
//   location?: string;
//   createdAt: string;
//   updatedAt?: string;
//   deletedAt?: string;
//   defaultEquipments?: EquipmentItemData[] | null;
// };

// // type FacilityManager = {
// //   user: {
// //     name: string;
// //     employeeId: number | null;
// //   };
// // };

// type EquipmentTypeData = {
//   id: string;
//   name: string;
//   description?: string;
//   parentTypeId: string | null;
// };

// type EquipmentModelData = {
//   id: string;
//   typeId: string;
//   name: string;
//   manufacturer: string;
//   description: string;
//   createdAt: string;
//   updatedAt: string;
//   specifications: string;
//   imageUrl: string;
// };

// type EquipmentItemData = {
//   id: string;
//   modelName: string;
//   typeName: string;
//   serialNumber: string | null;
//   assetTag?: string | null; 
//   status: string; 
//   purchaseDate: string | null;
//   warrantyExpiryDate: string | null;
//   defaultRoomName: string | null;
//   notes: string | null;
//   createdAt?: string | null;
//   updatedAt?: string | null; 
//   imgModel: string | null; 
// };

// type DashboardData = {
//   rooms: RoomData[];
//   equipments: EquipmentItemData[];
//   type: string;
// };

// type LoginData = {
//   username: string | null;
//   password: string;
// };

// type ApprovalData = {
//   title: string;
//   purpose: string;
//   slug: string;
//   createdAt: string;
//   remark: string;
//   status: string;
//   facilityManagerName: string | null;
//   groupDirectorName: string | null;
//   cancellationStatus?: string;
//   cancellationRequestedAt?: string | null;
//   cancellationRemark?: string;
//   cancelledAt?: string | null;
//   cancellationUpdateAtGD?: string | null;
//   cancellationUpdateAtFM?: string | null;
//   cancellationUpdateAtAdmin?: string | null;
//   time: {
//     date: string;
//     start: string;
//     end: string;
//   };
//   facility: {
//     name: string;
//   };
//   facilityId: number;
//   statusUpdateAtGD: string | null;
//   statusUpdateAtFM: string | null;
//   statusUpdateAtAdmin: string | null;
//   statusUpdateByGD: null | {
//     user: {
//       name: string;
//     };
//   };
//   statusUpdateByFM: null | {
//     user: {
//       name: string;
//     };
//   };
//   requestedBy: {
//     name: string;
//     employeeId: number;
//   };
//   statusUpdateByFM: string | null;
//   statusUpdateByGD: string | null;
// };

// type FMApprovalData = {
//   count: number | null;
//   facilities: [{ bookings: ApprovalData[] }];
// };

// type BookingCardProps = {
//   bookingData: ApprovalData;
// };

// type ApprovalType = {
//   slug: string;
//   approved?: boolean;
//   remark?: string;
//   employeeId?: number;
// };

// type BookingCardData = {
//   facility: {
//     bookings: ApprovalData[];
//   };
// };

// type AdminBookingsData = {
//   facilities: FacilityData[];
//   bookings: ApprovalData[];
//   users?: User[];
// };

// type FMBookingsData = {
//   facilities: FacilityData[];
//   bookings: [{ bookings: ApprovalData[] }];
// };

// type AdminRoomsSubmitData = {
//   name: string;
//   description: string;
//   capacity: number;
//   buildingName: string | null;
//   img: string;
//   facilityManagerId: string | number | null;
//   roomTypeName: string;
//   location: string;
// };

// type AdminFacilitiesEditData = {
//   name: string;
//   description: string;
//   icon: string;
//   slug: string;
//   prevFacilityManagerId: number | null;
//   newFacilityManagerId: number | null;
// };

// //---------------------------------------
// type PageInfo = {
//   size: number;
//   number: number;
//   totalElements: number;
//   totalPages: number;
// };

// // Thông tin thiết bị được đặt KÈM trong một booking (từ API list booking)
// type BookedEquipmentSummary = {
//     itemId: string;
//     equipmentModelName: string;
//     notes: string | null;
//     isDefaultEquipment: boolean;
//     serialNumber: string | null;
//     assetTag?: string | null; // Thêm nếu API trả về
//     // Thiếu typeName trong JSON mẫu booking list?
// };

// // Thông tin một booking đơn lẻ (từ API list booking)
// type BookingEntry = {
//     id: string; // Booking ID
//     userName: string;
//     roomName: string | null;
//     purpose: string;
//     plannedStartTime: string; // ISO String
//     plannedEndTime: string; // ISO String
//     actualCheckInTime: string | null;
//     actualCheckOutTime: string | null;
//     status: string; // "PENDING_APPROVAL", "CONFIRMED", etc.
//     approvedByUserName: string | null;
//     cancellationReason: string | null;
//     cancelledByUserName: string | null;
//     createdAt: string;
//     updatedAt: string | null;
//     note: string | null;
//     bookedEquipments: BookedEquipmentSummary[];
// };

// // Cấu trúc response từ API lấy danh sách booking (phân trang)
// type PaginatedBookingApiResponse = {
//     code: number;
//     result: {
//         content: BookingEntry[];
//         page: PageInfo;
//     };
//     message?: string;
// };

// // Thông tin thiết bị MẶC ĐỊNH (từ API chi tiết phòng)
// type EquipmentItemData = {
//   id: string;
//   modelName: string;
//   typeName: string; // Có trong response chi tiết phòng
//   serialNumber: string | null;
//   assetTag?: string | null; // Có thể thiếu trong response chi tiết phòng?
//   status: string;
//   purchaseDate: string | null;
//   warrantyExpiryDate: string | null;
//   defaultRoomName: string | null;
//   notes: string | null;
//   createdAt?: string | null;
//   updatedAt?: string | null;
//   imgModel: string | null; // Có trong response chi tiết phòng
// };

// // Thông tin chi tiết phòng (từ API chi tiết phòng)
// type RoomDetailData = {
//     id: string;
//     name: string;
//     description: string | null;
//     capacity: number;
//     img: string | null;
//     status: "AVAILABLE" | "UNDER_MAINTENANCE";
//     buildingName: string | null;
//     roomTypeName: string | null;
//     nameFacilityManager: string | null;
//     location?: string | null;
//     createdAt?: string | null;
//     updatedAt?: string | null;
//     deletedAt?: string | null;
//     note?: string | null; // Thêm nếu có
//     defaultEquipments: EquipmentItemData[]; // Dùng kiểu EquipmentItemData ở trên
// };

// // Cấu trúc response từ API lấy chi tiết phòng
// type RoomDetailApiResponse = {
//     code: number;
//     result: RoomDetailData;
//     message?: string;
// };

// // --- Types cho State và Props của Calendar component ---

// // Kiểu dữ liệu cho state eventInfo (hiển thị trong modal khi click event)
// interface EventInfoProps {
//      bookingId?: string;
//      title: string;
//      purpose: string;
//      status: string;
//      start: string; // Chỉ giờ
//      end: string; // Chỉ giờ
//      date: string; // Chỉ ngày
//      requestBy: string; // Tên người yêu cầu
//      roomName: string | null;
//      bookedEquipments: BookedEquipmentSummary[]; // Thêm danh sách thiết bị
// }

// // Kiểu cho EventContentArg của FullCalendar (giữ nguyên hoặc import từ @fullcalendar/core/index.js)
// interface EventContentArg {
//   timeText: string;
//   event: {
//       id: string; // id của event trên lịch (chính là bookingId)
//       title: string;
//       start: Date | null; // FullCalendar dùng Date object
//       end: Date | null;   // FullCalendar dùng Date object
//       startStr: string; // ISO string
//       endStr: string;   // ISO string
//       extendedProps: BookingEntry; // Chứa dữ liệu gốc BookingEntry
//   };
// }

// // Kiểu cho đối tượng thiết bị bên trong mảng bookedEquipments của response tạo booking
// type CreatedBookingEquipmentInfo = {
//   itemId: string;
//   equipmentModelName: string;
//   notes: string | null;
//   isDefaultEquipment: boolean;
//   serialNumber: string | null;
//   assetTag: string | null; // Có trong JSON
// };

// // Kiểu cho đối tượng result bên trong response tạo booking
// // (Tương tự BookingResponse nhưng dùng CreatedBookingEquipmentInfo)
// type CreatedBookingResult = {
//   id: string;
//   userName: string;
//   roomName: string | null;
//   purpose: string;
//   plannedStartTime: string;
//   plannedEndTime: string;
//   actualCheckInTime: string | null;
//   actualCheckOutTime: string | null;
//   status: string; // Có thể dùng enum BookingStatus
//   approvedByUserName: string | null;
//   cancellationReason: string | null;
//   cancelledByUserName: string | null;
//   createdAt: string;
//   updatedAt: string | null;
//   note: string | null;
//   bookedEquipments: CreatedBookingEquipmentInfo[]; // Dùng kiểu vừa định nghĩa
// };

// // Kiểu cho toàn bộ API response khi tạo booking thành công
// type BookingCreationApiResponse = {
//   code: number;
//   result: CreatedBookingResult;
//   message?: string; // Optional
// };