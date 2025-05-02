import { JSX, FC } from "react";
import ManagementEquipments from "../components/ManagementEquipments";

const ManagementEquipmentsPage: FC = (): JSX.Element => {
  return (
    <div className="w-full h-full flex">
      <ManagementEquipments />
    </div>
  );
};

export default ManagementEquipmentsPage;