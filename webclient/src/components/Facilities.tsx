import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { FC, JSX } from "react";
import { useQuery } from "@tanstack/react-query";
import { Link } from "react-router-dom";
import { Button, CircularProgress, Dialog, DialogActions, DialogContent, DialogTitle, Divider, Typography } from "@mui/material";

import FacilityCard from "./cards/FacilityCard";
import ErrorComponent from "./Error";
import { API } from "../api";
import FacilityDetail from "./cards/FacilityDetail";

const Facilities: FC<FacilitiesProps> = ({ type }): JSX.Element => {
  const [selectedFacility, setSelectedFacility] = useState<(RoomData & { type: string }) | (EquipmentData & { type: string }) | null>(null);
  const [open, setOpen] = useState(false);

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
              {/* {section[type]?.map((item: RoomData | EquipmentData) => (
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
              ))} */}

                {section[type]?.map((item) => (
                  <div
                    key={item.name}
                    onClick={() => {
                      setSelectedFacility({ ...item, type });
                      setOpen(true);
                    }}
                    className="cursor-pointer"
                  >
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
                  </div>
                ))}
            </div>
          </div>
        ) : null
      )}
      </div>
      
      {selectedFacility && (
        <FacilityDetail
          open={open}
          onClose={() => setOpen(false)}
          name={selectedFacility.name}
          description={selectedFacility.description ?? ""}
          img={selectedFacility.img ?? ""}
          manager={
            "nameFacilityManager" in selectedFacility && typeof selectedFacility.nameFacilityManager === "string"
              ? selectedFacility.nameFacilityManager
              : "Chưa có quản lý"
          }
          slug={selectedFacility.slug}
          type={selectedFacility.type}
        />
      )}
    </div>
  );
};

export default Facilities;