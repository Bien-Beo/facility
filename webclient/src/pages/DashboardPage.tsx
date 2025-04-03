import { FC, JSX } from "react";
import Facilities from "../components/Facilities";

const DashboardPage: FC<DashboardPageProps> = ({ type }): JSX.Element => {
  return (
    <div className="w-full h-full flex">
      <Facilities type={type} />
    </div>
  );
};

export default DashboardPage;