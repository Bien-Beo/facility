import {
  Tooltip,
  TooltipProps,
  Typography,
  Zoom,
  styled,
  tooltipClasses,
} from "@mui/material";
import { FC, JSX } from "react";

const HtmlTooltip = styled(({ className, ...props }: TooltipProps) => (
  <Tooltip {...props} classes={{ popper: className }} />
))(() => ({
  [`& .${tooltipClasses.tooltip}`]: {
    backgroundColor: "white",
    color: "#00275E",
    maxWidth: 400,
    border: "1px solid #dadde9",
  },
}));

// Các màu nền dựa trên status
const statusBackgroundColors: Record<string, string> = {
  AVAILABLE: "text-green-500",
  BOOKED: "text-orange-500",
  UNDER_MAINTENANCE: "text-red-500",
};

// Các màu chữ cho phần text trạng thái
const statusTextColors: Record<string, string> = {
  AVAILABLE: "text-green-500",
  BOOKED: "text-yellow-500",
  UNDER_MAINTENANCE: "text-red-500", 
};

const RoomCard: FC<RoomCardProps> = ({
  name,
  description,
  capacity,
  img,
  status,
  nameFacilityManager,
  updatedAt,
}): JSX.Element => {
  return (
    <HtmlTooltip
      placement="right"
      arrow
      TransitionComponent={Zoom}
      title={
        <Typography
          variant="body1"
          component="h2"
          className="text-[#271756] font-normal"
        >
          {description}
        </Typography>
      }
    >
      <div
        className={`w-[180px] h-full min-h-[200px] gap-0 pt-0 m-6 mt-4 p-0 border-0 border-b-4 border-solid bg-white flex flex-col items-center justify-evenly shadow-card cursor-pointer rounded-md hover:-translate-y-1 hover:shadow-cardHover transition-all duration-150 ease-in ${statusBackgroundColors[status]}`}
      >
        <img
          src={`http://localhost:8080/facility/images/${img}`}
          alt={`Ảnh phòng ${name}`}
          className="w-[160px] h-[100px] object-cover rounded-md"
        />
        <div className="w-full flex flex-col justify-center items-center gap-1 text-[#00275E]">
          <Typography
            variant="h5"
            component="h2"
            className="text-[#271756] font-normal text-center"
          >
            {name}
          </Typography>
          <Typography
            variant="body1"
            component="h2"
            className="text-[#271756] font-normal text-center"
          >
            <span className="font-bold">Quản lý phòng</span>
            <br /> {nameFacilityManager}
          </Typography>
          <Typography
            variant="body2"
            component="p"
            className="text-gray-600 text-center"
          >
            Sức chứa: {capacity} người
          </Typography>
          {/* <Typography
            variant="body2"
            component="p"
            className="text-gray-600 text-center"
          >
            Last updated: {updatedAt ? new Date(updatedAt).toLocaleDateString("vi-VI", {year: "numeric", month: "2-digit", day: "2-digit"}) : "N/A"}
          </Typography> */}
          <Typography
            variant="body1"
            component="p"
            className={`${statusTextColors[status]} font-bold text-center`}
          >
            {status.replace("_", " ")}
          </Typography>
        </div>
      </div>
    </HtmlTooltip>
  );
};

export default RoomCard;