import { JSX, FC } from "react";
import ManagementRooms from "../components/ManagementRooms";

const ManagementRoomsPage: FC = (): JSX.Element => {
  return (
    <div className="w-full h-full flex">
      <ManagementRooms />
    </div>
  );
};

export default ManagementRoomsPage;