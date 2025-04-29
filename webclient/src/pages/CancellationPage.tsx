import { JSX, FC } from "react";
import CancellationStatus from "../components/PendingCancellationList";

const FMCancellationsPage: FC = (): JSX.Element => {
  return (
    <div className="w-full h-full flex">
      <CancellationStatus />
    </div>
  );
};

export default FMCancellationsPage;