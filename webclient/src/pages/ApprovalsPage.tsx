import { JSX, FC } from "react";
import ApprovalStatus from "../components/BookingApprovalList";

const GDApprovalsPage: FC = (): JSX.Element => {
  return (
    <div className="w-full h-full flex">
      <ApprovalStatus />
    </div>
  );
};

export default GDApprovalsPage;