// --------INTERFACES--------

interface RoomCardProps {
  name: string;
  description: string;
  img: string;
  manager: string;
  capacity: number;
  building: string;
  updatedAt: string;
  nameFacilityManager: string;
  equipments: EquipmentData[];
  status: "AVAILABLE" | "BOOKED" | "UNDER_MAINTENANCE";
}

interface EquipmentCardProps {
  name: string;
  description: string;
  img: string;
  manager: string;
  status: "OPERATIONAL" | "BROKEN" | "UNDER_MAINTENANCE";
  type: string;
  updatedAt: string;
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

interface AddEventModalProps {
  isOpen: boolean;
  setIsOpen: (isOpen: boolean) => void;
  setOpenSnackbar: (isOpen: boolean) => void;
  setDefaultDate: (message: string | null) => void;
  bookingsData: BookingNewDataProps[];
  defaultDate: string | null;
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
  FacilityManagement: boolean;
  Admin?: boolean;
  User?: boolean;
}

interface EventInfoProps {
  title: string;
  purpose: string;
  status: string;
  start: string;
  end: string;
  date: string;
  requestBy: string;
  statusUpdateByGD: string | null;
  statusUpdateByFM: string | null;
  statusUpdateByAdmin: string | null;
}

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
  building?: BuildingData;
  status?: "AVAILABLE" | "BOOKED" | "UNDER_MAINTENANCE";
  img: string;
  slug: string;
  isActive?: boolean;
  createdAt?: string;
  updatedAt?: string;
  deletedAt?: string;
  nameFacilityManager?: string;
  equipments: EquipmentData[];
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
};

type EquipmentData = {
  id: string;
  name: string;
  description?: string;
  img?: string;
  slug: string;
  status: "OPERATIONAL" | "BROKEN" | "UNDER_MAINTENANCE";
  isActive?: boolean;
  createdAt?: string;
  updatedAt?: string;
  deletedAt?: string;
  room?: {
    id: string;
    name: string;
  };
  equipmentManager?: {
    id: string;
    name: string;
  };
  equipmentType?: EquipmentTypeData;
};

type DashboardData = {
  rooms: RoomData[];
  equipments: EquipmentData[];
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