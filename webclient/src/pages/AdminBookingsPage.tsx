import { JSX, FC } from "react";
import AdminBookings from "../components/AdminBookings";

const AdminBookingsPage: FC = (): JSX.Element => {
  return (
    <div className="w-full h-full flex">
      {/* <AdminBookings /> */}
      <div className="flex-1 max-w-[1200px] mx-auto">
        <AdminBookings />
      </div>
    </div>
  );
};

export default AdminBookingsPage;