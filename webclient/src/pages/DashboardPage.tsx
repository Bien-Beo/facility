import { FC, JSX } from "react";
import Rooms from "../components/Rooms";
import Equipments from "../components/Equipments";

const DashboardPage: FC<DashboardPageProps> = ({ type }): JSX.Element => {
  return (
    <div className="w-full h-full flex">
      {
        type === "room" ? (
        <Rooms />
      ) : (
        <Equipments />
      )
      }
    </div>
  );
};

export default DashboardPage;