import { FC, JSX } from "react";
import { useQuery } from "@tanstack/react-query";
import { Link } from "react-router-dom";
import { CircularProgress, Divider, Typography } from "@mui/material";

import FacilityCard from "./cards/FacilityCard";
import ErrorComponent from "./Error";
import { API } from "../api";

const Facilities: FC<FacilitiesProps> = ({ type }): JSX.Element => {
  const { data, isPending, isError, error } = useQuery({
    queryKey: [type],
    queryFn: async (): Promise<DashboardData[]> => {
      const endpoint = type === "room" ? "/dashboard/room" : "/dashboard/equipment";
      const response = await API.get<APIResponse<DashboardData[]>>(endpoint);
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
        status={errorData?.status ?? 500}
        message={errorData?.message ?? "Unexpected error occurred"}
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
      {type === "room" ? "Rooms" : "Equipments"}
      </Typography>
      <div className="w-full flex flex-col justify-center items-center flex-wrap pt-4 gap-2">
      {data?.map((section) =>
        (section[type] ?? []).length > 0 ? (
          <div key={section.type} className="w-full flex flex-col gap-2">
            <Typography variant="h4" component="h2">{section.type}</Typography>
            <Divider color="gray" />
            <div className="w-full flex items-center justify-center flex-wrap">
              {section[type]?.map((item: RoomData | EquipmentData) => (
                <Link to={`/${type}/${item.slug}`} key={item.name}>
                  <FacilityCard
                    name={item.name}
                    description={item.description ?? ""}
                    img={item.img ?? ""}
                    manager={
                      "nameFacilityManager" in item
                        ? item.nameFacilityManager ?? "Chưa có quản lý"
                        : "N/A"
                    }
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