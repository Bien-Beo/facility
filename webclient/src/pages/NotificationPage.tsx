import { JSX, FC } from "react";
import Notification from "../components/Notification";

const NotificationPage: FC = (): JSX.Element => {
  return (
    <div className="w-full h-full flex">
      <Notification />
    </div>
  );
};

export default NotificationPage;