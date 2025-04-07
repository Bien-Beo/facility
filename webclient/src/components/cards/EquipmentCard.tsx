import { Tooltip, TooltipProps, Typography, Zoom, styled, tooltipClasses } from "@mui/material";
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

const statusBackgroundColors: Record<string, string> = {
  OPERATIONAL: "text-green-500",
  BROKEN: "text-red-500",
  UNDER_MAINTENANCE: "text-yellow-500",
};

const statusColors: Record<string, string> = {
  OPERATIONAL: "text-green-500",
  BROKEN: "text-red-500",
  UNDER_MAINTENANCE: "text-yellow-500",
};

const EquipmentCard: FC<EquipmentCardProps> = ({
  id,
  modelName,
  typeName,
  serialNumber,
  assetTag,
  status,
  purchaseDate,
  warrantyExpiryDate,
  defaultRoomName,
  notes,
  createdAt,
  updatedAt,
  imgModel
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
          {notes}
        </Typography>
      }
    >
      <div className={`w-[200px] h-full min-h-[300px] gap-4 m-6 p-2 pt-4 border-0 border-b-4 border-solid border-primary bg-white flex flex-col items-center justify-evenly shadow-card cursor-pointer rounded-md hover:-translate-y-1 hover:shadow-cardHover transition-all duration-150 ease-in  ${statusBackgroundColors[status]}`}>
        <img
          src={imgModel || "/default-equipment.png"}
          alt={`${modelName}-img`}
          className="w-[90%] object-cover"
        />
        <div className="w-full flex flex-col justify-center items-center gap-1 text-[#00275E]">
          <Typography
            variant="h5"
            component="h2"
            className="text-primary font-normal text-center"
          >
            {modelName}
          </Typography>
          <Typography
            variant="body1"
            component="h2"
            className="text-primary font-normal text-center"
          >
            <span className="font-bold">Default Room name</span>
            <br /> {defaultRoomName}
          </Typography>
          <Typography
            variant="body1"
            component="p"
            className={`${statusColors[status]} font-bold`}
          >
            {status.replace("_", " ")}
          </Typography>
          <Typography variant="body2" component="p" className="text-gray-500">
            Updated: {updatedAt ? new Date(updatedAt).toLocaleDateString("vi-VI", {year: "numeric", month: "2-digit", day: "2-digit"}) : "N/A"}
          </Typography>
        </div>
      </div>
    </HtmlTooltip>
  );
};

export default EquipmentCard;