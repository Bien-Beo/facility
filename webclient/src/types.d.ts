// --------INTERFACES--------

interface RoomCardProps {
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

interface EquipmentCardProps {
  id: string;
  modelName: string;
  typeName: string;
  serialNumber: string | null;
  assetTag?: string | null; 
  status: string; 
  purchaseDate: string | null;
  warrantyExpiryDate: string | null;
  defaultRoomName: string | null;
  notes: string | null;
  createdAt?: string | null;
  updatedAt?: string | null; 
  imgModel: string | null; 
}

interface DashboardPageProps {
  type: FacilityType; 
}

interface FacilitiesProps {
  type: FacilityType; 
}

// interface LoginData {
//   username: string;
//   password: string;
// }

// interface LoginResponse {
//   code: number;
//   result: {
//     token: string;
//     authenticated: boolean;
//   };
// }


// --- Props Interface
interface AddEventModalProps {
  isOpen: boolean;
  roomId: string;
  roomName: string;
  startTime: string; 
  endTime: string; 
  allDay: boolean;  
  onSuccess: () => void;
  onCancel: () => void;
}

// --- Kiểu dữ liệu cho state của form ---
interface FormDataState {
  purpose: string;
  selectedDate: Dayjs | null;
  startTimeString: string; // hh:mm A
  endTimeString: string;   // hh:mm A
  note: string;
  additionalEquipmentItemIds: string[];
}

// interface EventModalProps {
//   isOpen: boolean;
//   setIsOpen: (isOpen: boolean) => void;
//   eventInfo: EventInfoProps;
// }

interface EventContentProps {
  event: {
    extendedProps: {
      slug: string;
    };
  };
}

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

interface BookingDataProps {
  facility: {
    name: string;
  };
  bookings: BookingData[];
}

interface BookingData {
  id: string;
  title: string;
  slug: string;
  purpose: string;
  status: string;
  createdAt: string;
  facilityManager: string | null;
  statusUpdateAtGD: string | null;
  statusUpdateAtFM: string | null;
  statusUpdateAtAdmin: string | null;
  statusUpdateByGD: {
    user: {
      name: string;
      employeeId: number;
    };
  } | null;
  statusUpdateByFM: {
    user: {
      name: string;
      employeeId: number;
    };
  } | null;
  time: {
    start: string;
    end: string;
    date: string;
  };
  requestedBy: {
    name: string;
    employeeId: number;
  };
  facility: {
    name: string;
    slug: string;
  };
}

interface BookingNewDataProps {
  id: string;
  title: string;
  slug: string;
  purpose: string;
  status: string;
  createdAt: string;
  facilityManager: string | null;
  statusUpdateAtGD: string | null;
  statusUpdateAtFM: string | null;
  statusUpdateAtAdmin: string | null;
  statusUpdateByGD: {
    user: {
      name: string;
      employeeId: number;
    };
  } | null;
  statusUpdateByFM: {
    user: {
      name: string;
      employeeId: number;
    };
  } | null;
  start: string;
  end: string;
  date: string;
  requestedBy: {
    name: string;
    employeeId: number;
  };
  facility: {
    name: string;
    slug: string;
  };
}

interface AddEventDataProps {
  title: string;
  purpose: string;
  date: Dayjs | null;
  start: string | null;
  end: string | null;
  userId: string | null;
  slug: string;
}

interface Permission {
  name: string;
  description: string;
}

interface Role {
  name: string;
  description: string;
  permissions: Permission[];
}

interface User {
  id: string;
  userId: string;
  username: string;
  email: string;
  avatar: string;
  role: string;
}

interface AuthContextType {
  user: User | null;
  login: (authData: { token: string; authenticated: boolean }) => Promise<void>;
  logout: () => void;
  loadingUser: boolean;
}

interface AuthProviderProps {
  children: ReactNode;
}

interface RequireAuthProps {
  children: ReactNode;
  Technician: boolean;
  FacilityManager: boolean;
  Admin?: boolean;
  User?: boolean;
}

// interface EventInfoProps {
//   title: string;
//   purpose: string;
//   status: string;
//   start: string;
//   end: string;
//   date: string;
//   requestBy: string;
//   statusUpdateByGD: string | null;
//   statusUpdateByFM: string | null;
//   statusUpdateByAdmin: string | null;
// }

// interface ApprovalProps {
//   title: string;
//   purpose: string;
//   cancellationRemark?: string;
//   slug: string;
//   createdAt: string;
//   cancelledAt?: string | null;
//   date: string;
//   start: string;
//   end: string;
//   facility: string;
//   requestedBy: string | null;
//   approvedByGD: string | null;
//   approvedAtGD?: string | null;
// }

// interface MyBookingCardProps {
//   title: string;
//   purpose: string;
//   status: string;
//   cancelStatus?: string;
//   slug?: string;
//   remark: string;
//   createdAt?: string;
//   date: string;
//   start: string;
//   end: string;
//   facility: string;
//   requestedBy: string | null;
//   approvedByGD?: string | null;
//   approvedByFM?: string | null;
//   approvedAtGD?: string | null;
//   approvedAtFM?: string | null;
//   approvedAtAdmin?: string | null;
//   cancellationRequestedAt?: string | null;
//   cancellationRemark?: string | null;
//   cancellationUpdateAtGD?: string | null;
//   cancellationUpdateAtFM?: string | null;
// }

// interface ApprovalStatusProps {
//   GD: boolean;
//   FM: boolean;
// }

// interface FilterOptionProps {
//   label: string;
// }

// interface AdminBookingsColumnData {
//   id:
//     | "title"
//     | "purpose"
//     | "date"
//     | "time"
//     | "createdAt"
//     | "status"
//     | "reqBy"
//     | "actions"
//     | "gd"
//     | "fm"
//     | "admin"
//     | "cancellationremark"
//     | "cancellationstatus"
//     | "remark"
//     | "actions";
//   label: string;
//   minWidth?: number;
// }

// interface AdminBookingsTableProps {
//   bookingsData: ApprovalData[];
//   forwardedRef?: React.RefObject<HTMLDivElement>;
// }

// interface FMBookingsTableProps {
//   bookingsData: [{ bookings: ApprovalData[] }];
//   forwardedRef?: React.RefObject<HTMLDivElement>;
// }

// interface AdminFacilitiesTableProps {
//   facilitiesData?: FacilityData[];
//   facilities: FacilityData[];
//   buildings?: [
//     {
//       name: string;
//     }
//   ];
//   forwardedRef?: React.RefObject<HTMLDivElement>;
// }

// interface AdminBookingsRowData {
//   title: JSX.Element;
//   purpose: string;
//   date: string;
//   time: string;
//   createdAt: JSX.Element;
//   reqBy: string;
//   status: JSX.Element | string;
//   cancellationstatus: JSX.Element | string;
//   remark: string;
//   cancellationremark: string;
//   actions?: string | JSX.Element;
//   gd: JSX.Element | null;
//   fm: JSX.Element | null;
//   admin: JSX.Element | null;
// }

// interface AdminFacilitiesColumnData {
//   id:
//     | "name"
//     | "description"
//     | "status"
//     | "createdAt"
//     | "updatedAt"
//     | "deletedAt"
//     | "actions"
//     | "fm";
//   label: string;
//   minWidth?: number;
// }

// interface AdminFacilitiesRowData {
//   name: string | JSX.Element;
//   description: string;
//   status: string;
//   createdAt: JSX.Element;
//   updatedAt: JSX.Element;
//   deletedAt: JSX.Element;
//   actions?: string | JSX.Element;
//   fm: JSX.Element | null;
// }

interface AddFacilityDataProps {
  name: string;
  description: string;
  building: string | null;
  icon: string;
  FMId: string | number | null;
}

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

// interface EditFacilityModalProps {
//   isOpen: boolean;
//   setIsOpen: (isOpen: boolean) => void;
//   setOpenSnackbar: (isOpen: boolean) => void;
//   facilityData: FacilityData;
//   buildingData?: [
//     {
//       name: string;
//     }
//   ];
// }

// interface AdminBookingsModalProps {
//   isOpen: boolean;
//   setIsOpen: (isOpen: boolean) => void;
//   setOpenSnackbar: (isOpen: boolean) => void;
//   slug: string;
// }

interface ErrorMessage {
    message: string;
    status: number | null;
}

interface ErrorProps {
  message: string;
  status: number;
}

interface NavigationProps {
  approvalCount: number;
  cancellationCount: number;
}

interface RouteError {
  status: number;
  message: string;
}

// ----------TYPES-----------
type FacilityType = "room" | "equipment";

// --- Kiểu dữ liệu gửi lên backend 
type BackendBookingPayload = {
  roomId: string | null;
  purpose: string;
  plannedStartTime: string; // ISO string
  plannedEndTime: string; // ISO string
  additionalEquipmentItemIds: string[];
  note: string;
};

type APIResponse<T> = {
  result: T;
  code: number;
};

type BuildingData = {
  id: string;
  name: string;
};

type RoomData = {
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
};

// type FacilityManager = {
//   user: {
//     name: string;
//     employeeId: number | null;
//   };
// };

type EquipmentTypeData = {
  id: string;
  name: string;
  description?: string;
  parentTypeId: string | null;
};

type EquipmentModelData = {
  id: string;
  typeId: string;
  name: string;
  manufacturer: string;
  description: string;
  createdAt: string;
  updatedAt: string;
  specifications: string;
  imageUrl: string;
};

type EquipmentItemData = {
  id: string;
  modelName: string;
  typeName: string;
  serialNumber: string | null;
  assetTag?: string | null; 
  status: string; 
  purchaseDate: string | null;
  warrantyExpiryDate: string | null;
  defaultRoomName: string | null;
  notes: string | null;
  createdAt?: string | null;
  updatedAt?: string | null; 
  imgModel: string | null; 
};

type DashboardData = {
  rooms: RoomData[];
  equipments: EquipmentItemData[];
  type: string;
};

type LoginData = {
  username: string | null;
  password: string;
};

type ApprovalData = {
  title: string;
  purpose: string;
  slug: string;
  createdAt: string;
  remark: string;
  status: string;
  facilityManagerName: string | null;
  groupDirectorName: string | null;
  cancellationStatus?: string;
  cancellationRequestedAt?: string | null;
  cancellationRemark?: string;
  cancelledAt?: string | null;
  cancellationUpdateAtGD?: string | null;
  cancellationUpdateAtFM?: string | null;
  cancellationUpdateAtAdmin?: string | null;
  time: {
    date: string;
    start: string;
    end: string;
  };
  facility: {
    name: string;
  };
  facilityId: number;
  statusUpdateAtGD: string | null;
  statusUpdateAtFM: string | null;
  statusUpdateAtAdmin: string | null;
  statusUpdateByGD: null | {
    user: {
      name: string;
    };
  };
  statusUpdateByFM: null | {
    user: {
      name: string;
    };
  };
  requestedBy: {
    name: string;
    employeeId: number;
  };
  statusUpdateByFM: string | null;
  statusUpdateByGD: string | null;
};

type FMApprovalData = {
  count: number | null;
  facilities: [{ bookings: ApprovalData[] }];
};

type BookingCardProps = {
  bookingData: ApprovalData;
};

type ApprovalType = {
  slug: string;
  approved?: boolean;
  remark?: string;
  employeeId?: number;
};

type BookingCardData = {
  facility: {
    bookings: ApprovalData[];
  };
};

type AdminBookingsData = {
  facilities: FacilityData[];
  bookings: ApprovalData[];
  users?: User[];
};

type FMBookingsData = {
  facilities: FacilityData[];
  bookings: [{ bookings: ApprovalData[] }];
};

type AdminFacilitiesSubmitData = {
  name: string;
  description: string;
  building: string;
  icon: string;
  slug: string;
  facilityManagerId: number | null;
};

type AdminFacilitiesEditData = {
  name: string;
  description: string;
  icon: string;
  slug: string;
  prevFacilityManagerId: number | null;
  newFacilityManagerId: number | null;
};

//---------------------------------------
type PageInfo = {
  size: number;
  number: number;
  totalElements: number;
  totalPages: number;
};

// Thông tin thiết bị được đặt KÈM trong một booking (từ API list booking)
type BookedEquipmentSummary = {
    itemId: string;
    equipmentModelName: string;
    notes: string | null;
    isDefaultEquipment: boolean;
    serialNumber: string | null;
    assetTag?: string | null; // Thêm nếu API trả về
    // Thiếu typeName trong JSON mẫu booking list?
};

// Thông tin một booking đơn lẻ (từ API list booking)
type BookingEntry = {
    id: string; // Booking ID
    userName: string;
    roomName: string | null;
    purpose: string;
    plannedStartTime: string; // ISO String
    plannedEndTime: string; // ISO String
    actualCheckInTime: string | null;
    actualCheckOutTime: string | null;
    status: string; // "PENDING_APPROVAL", "CONFIRMED", etc.
    approvedByUserName: string | null;
    cancellationReason: string | null;
    cancelledByUserName: string | null;
    createdAt: string;
    updatedAt: string | null;
    note: string | null;
    bookedEquipments: BookedEquipmentSummary[];
};

// Cấu trúc response từ API lấy danh sách booking (phân trang)
type PaginatedBookingApiResponse = {
    code: number;
    result: {
        content: BookingEntry[];
        page: PageInfo;
    };
    message?: string;
};

// Thông tin thiết bị MẶC ĐỊNH (từ API chi tiết phòng)
type EquipmentItemData = {
  id: string;
  modelName: string;
  typeName: string; // Có trong response chi tiết phòng
  serialNumber: string | null;
  assetTag?: string | null; // Có thể thiếu trong response chi tiết phòng?
  status: string;
  purchaseDate: string | null;
  warrantyExpiryDate: string | null;
  defaultRoomName: string | null;
  notes: string | null;
  createdAt?: string | null;
  updatedAt?: string | null;
  imgModel: string | null; // Có trong response chi tiết phòng
};

// Thông tin chi tiết phòng (từ API chi tiết phòng)
type RoomDetailData = {
    id: string;
    name: string;
    description: string | null;
    capacity: number;
    img: string | null;
    status: "AVAILABLE" | "UNDER_MAINTENANCE";
    buildingName: string | null;
    roomTypeName: string | null;
    nameFacilityManager: string | null;
    location?: string | null;
    createdAt?: string | null;
    updatedAt?: string | null;
    deletedAt?: string | null;
    note?: string | null; // Thêm nếu có
    defaultEquipments: EquipmentItemData[]; // Dùng kiểu EquipmentItemData ở trên
};

// Cấu trúc response từ API lấy chi tiết phòng
type RoomDetailApiResponse = {
    code: number;
    result: RoomDetailData;
    message?: string;
};

// --- Types cho State và Props của Calendar component ---

// Kiểu dữ liệu cho state eventInfo (hiển thị trong modal khi click event)
interface EventInfoProps {
     bookingId?: string;
     title: string;
     purpose: string;
     status: string;
     start: string; // Chỉ giờ
     end: string; // Chỉ giờ
     date: string; // Chỉ ngày
     requestBy: string; // Tên người yêu cầu
     roomName: string | null;
     bookedEquipments: BookedEquipmentSummary[]; // Thêm danh sách thiết bị
}

// Kiểu cho EventContentArg của FullCalendar (giữ nguyên hoặc import từ @fullcalendar/core/index.js)
interface EventContentArg {
  timeText: string;
  event: {
      id: string; // id của event trên lịch (chính là bookingId)
      title: string;
      start: Date | null; // FullCalendar dùng Date object
      end: Date | null;   // FullCalendar dùng Date object
      startStr: string; // ISO string
      endStr: string;   // ISO string
      extendedProps: BookingEntry; // Chứa dữ liệu gốc BookingEntry
  };
}

// Kiểu cho đối tượng thiết bị bên trong mảng bookedEquipments của response tạo booking
type CreatedBookingEquipmentInfo = {
  itemId: string;
  equipmentModelName: string;
  notes: string | null;
  isDefaultEquipment: boolean;
  serialNumber: string | null;
  assetTag: string | null; // Có trong JSON
};

// Kiểu cho đối tượng result bên trong response tạo booking
// (Tương tự BookingResponse nhưng dùng CreatedBookingEquipmentInfo)
type CreatedBookingResult = {
  id: string;
  userName: string;
  roomName: string | null;
  purpose: string;
  plannedStartTime: string;
  plannedEndTime: string;
  actualCheckInTime: string | null;
  actualCheckOutTime: string | null;
  status: string; // Có thể dùng enum BookingStatus
  approvedByUserName: string | null;
  cancellationReason: string | null;
  cancelledByUserName: string | null;
  createdAt: string;
  updatedAt: string | null;
  note: string | null;
  bookedEquipments: CreatedBookingEquipmentInfo[]; // Dùng kiểu vừa định nghĩa
};

// Kiểu cho toàn bộ API response khi tạo booking thành công
type BookingCreationApiResponse = {
  code: number;
  result: CreatedBookingResult;
  message?: string; // Optional
};