import {JSX, FC} from "react"
import ManagementAccounts from "../components/ManagementAccounts";

const ManagementAccountsPage: FC = (): JSX.Element => {
  return (
    <div className="w-full h-full flex">
      <ManagementAccounts />
    </div>
  );
};

export default ManagementAccountsPage;