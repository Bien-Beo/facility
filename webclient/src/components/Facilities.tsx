import { FC, JSX } from "react";
import { useQuery } from "@tanstack/react-query";
import { Link } from "react-router-dom";
import { CircularProgress, Divider, Typography } from "@mui/material";

import FacilityCard from "./cards/FacilityCard";
import ErrorComponent from "./Error";
import { API } from "../api";

const Facilities: FC = (): JSX.Element => {
  const { data, isPending, isError, error } = useQuery({
    queryKey: ["rooms"],
    queryFn: async (): Promise<DashboardData[]> => {
      const response = await API.get<APIResponse<DashboardData[]>>("/dashboard");
      return response.data.result; 
    },
    retry: 1,
    gcTime: 0,
  });  

  if (isError) {
    console.error("Chi tiết lỗi:", error); 
  
    const errorData = error?.response?.data as ErrorMessage | undefined;
    
    return (
      <ErrorComponent
        status={errorData?.error?.status ?? 500}
        message={errorData?.error?.message ?? "Unexpected error occurred"}
      />
    );
  }  

  if (isPending)
    return (
      <div className="w-full min-h-screen h-full flex flex-col items-center justify-center">
        <CircularProgress />
      </div>
    );

  return (
    <div className="w-full h-full flex flex-col items-center justify-center pt-12 px-6">
      <Typography variant="h2" component="h1">
        Rooms
      </Typography>
      <div className="w-full flex flex-col justify-center items-center flex-wrap pt-4 gap-2">
      {data?.map((section) =>
        section.room.length > 0 ? (
          <div key={section.type} className="w-full flex flex-col gap-2">
            <Typography variant="h4" component="h2">{section.type}</Typography>
            <Divider color="gray" />
            <div className="w-full flex items-center justify-center flex-wrap">
              {section.room.map((room: RoomData) => (
                <Link to={`/room/${room.slug}`} key={room.name}>
                  <FacilityCard
                    name={room.name}
                    description={room.description}
                    icon={room.img}
                    manager={room.nameFacilityManager ?? "Chưa có quản lý"}
                  />
                </Link>
              ))}
            </div>
          </div>
        ) : null
      )}
      </div>
    </div>
  );
};

export default Facilities;