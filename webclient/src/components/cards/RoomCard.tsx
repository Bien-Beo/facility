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
  img,
  manager,
  status,
  capacity,
  //updatedAt,
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
          className="text-primary font-normal"
        >
          {description}
        </Typography>
      }
    >
      <div
        className={`w-[200px] h-full min-h-[200px] gap-0 pt-0 m-6 p-2 border-0 border-b-4 border-solid border-primary bg-white flex flex-col items-center justify-evenly shadow-card cursor-pointer rounded-md hover:-translate-y-1 hover:shadow-cardHover transition-all duration-150 ease-in ${statusBackgroundColors[status]}`}
      >
        <img
          src={`http://localhost:8080/facility/images/${img}`}
          alt={`${name}-img`}
          className="w-[100%] object-cover rounded-md"
        />
        <div className="w-full flex flex-col justify-center items-center gap-1 text-[#00275E]">
          <Typography
            variant="h5"
            component="h2"
            className="text-primary font-normal text-center"
          >
            {name}
          </Typography>
          <Typography
            variant="body1"
            component="h2"
            className="text-primary font-normal text-center"
          >
            <span className="font-bold">Room Manager</span>
            <br /> {manager}
          </Typography>
          <Typography
            variant="body2"
            component="p"
            className="text-gray-600 text-center"
          >
            Capacity: {capacity} people
          </Typography>
          {/* <Typography
            variant="body2"
            component="p"
            className="text-gray-600 text-center"
          >
            Last updated: {new Date(updatedAt).toLocaleDateString()}
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