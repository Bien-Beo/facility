import { useState } from "react";
import { FC, JSX } from "react";
import { useQuery } from "@tanstack/react-query";
import { CircularProgress, Divider, Typography } from "@mui/material";

import ErrorComponent from "./Error";
import { API } from "../api";
import RoomDetail from "./cards/RoomDetail";
import RoomCard from "./cards/RoomCard";

const Rooms: FC = (): JSX.Element => {
  const [selectedFacility, setSelectedFacility] = useState<RoomData | null>(null);
  const [open, setOpen] = useState(false);

  const { data, isPending, isError, error } = useQuery({
    queryKey: ["rooms"],
    queryFn: async (): Promise<DashboardData[]> => {
      const response = await API.get<APIResponse<DashboardData[]>>(
        `${import.meta.env.VITE_APP_SERVER_URL}/dashboard/room`
      );
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
      <Typography variant="h2" component="h1">Rooms</Typography>
      <div className="w-full flex flex-col justify-center items-center flex-wrap pt-4 gap-10">
        {data?.map((section) =>
          Array.isArray(section.rooms) && section.rooms.length > 0 && (
            <div key={section.type} className="w-full flex flex-col gap-2">
              <Typography variant="h4" component="h2">{section.type}</Typography>
              <Divider color="gray" />
              <div className="w-full flex flex-wrap gap-4">
                {section.rooms.map((room) => (
                  <div key={room.slug} onClick={() => {
                    setSelectedFacility(room);
                    setOpen(true);
                  }}>
                    <RoomCard
                      name={room.name ?? "Unknown"}
                      description={room.description ?? ""}
                      img={room.img ?? ""}
                      manager={room.nameFacilityManager ?? "Chưa có quản lý"}
                      nameFacilityManager={room.nameFacilityManager ?? "Chưa có quản lý"}
                      capacity={room.capacity ?? 0}
                      updatedAt={room.updatedAt ?? ""}
                      building={typeof room.building === "string" ? room.building : "N/A"}
                      equipments={room.equipments ?? []}
                      status={room.status ?? "AVAILABLE"}
                    />
                  </div>
                ))}
              </div>
            </div>
          )
        )}
      </div>

      {selectedFacility && (
        <RoomDetail
          open={open}
          onClose={() => setOpen(false)}
          name={selectedFacility.name}
          description={selectedFacility.description ?? ""}
          img={selectedFacility.img ?? ""}
          manager={selectedFacility.nameFacilityManager ?? "Chưa có quản lý"}
          slug={selectedFacility.slug}
          type={"Unknown"}
        />
      )}
    </div>
  );
};

export default Rooms;
