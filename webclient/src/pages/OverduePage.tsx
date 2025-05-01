import { JSX, FC } from "react";
import OverdueList from "../components/OverdueList";

const OverduePage: FC = (): JSX.Element => {
  return (
    <div className="w-full h-full flex">
      <OverdueList />
    </div>
  );
};

export default OverduePage;