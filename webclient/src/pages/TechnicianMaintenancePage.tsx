import { JSX, FC } from "react";
import TechnicianMaintenance from "../components/TechnicianMaintenance";

const TechnicianMaintenancePage: FC = (): JSX.Element => {
  return (
    <div className="w-full h-full flex">
      <TechnicianMaintenance />
    </div>
  );
};

export default TechnicianMaintenancePage;