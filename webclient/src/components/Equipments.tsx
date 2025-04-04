import { useState } from "react";
import { FC, JSX } from "react";
import { useQuery } from "@tanstack/react-query";
import {
  CircularProgress,
  Divider,
  Typography,
} from "@mui/material";

import ErrorComponent from "./Error";
import { API } from "../api";
import EquipmentDetail from "./cards/EquipmentDetail";
import EquipmentCard from "./cards/EquipmentCard";

const Equipments: FC = (): JSX.Element => {
  const [selectedFacility, setSelectedFacility] = useState<EquipmentData | null>(null);
  const [open, setOpen] = useState(false);

  const { data, isPending, isError, error } = useQuery({
    queryKey: ["equipments"],
    queryFn: async (): Promise<DashboardData[]> => {
      const response = await API.get<APIResponse<DashboardData[]>>(
        `${import.meta.env.VITE_APP_SERVER_URL}/dashboard/equipment`
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
        <Typography variant="h2" component="h1">Equipments</Typography>
        <div className="w-full flex flex-col justify-center items-center flex-wrap pt-4 gap-2">
          {data?.map((section) =>
            Array.isArray(section.equipments) && section.equipments.length > 0 && (
              <div key={section.type} className="w-full flex flex-col gap-2">
                <Typography variant="h4" component="h2">{section.type}</Typography>
                <Divider color="gray" />
                <div className="w-full flex flex-wrap gap-4">
                  {section.equipments.map((equipment) => (
                    <div key={equipment.slug} onClick={() => {
                      setSelectedFacility(equipment);
                      setOpen(true);
                    }}>
                      <EquipmentCard
                        name={equipment.name ?? "Unknown"}
                        description={equipment.description ?? ""}
                        img={equipment.img ?? ""}
                        manager={"nameFacilityManager" in equipment && typeof equipment.nameFacilityManager === "string"
                          ? equipment.nameFacilityManager
                          : "Chưa có quản lý"}
                        updatedAt={equipment.updatedAt ?? ""}
                        status={
                          equipment.status === "BROKEN" || equipment.status === "OPERATIONAL"
                            ? "UNDER_MAINTENANCE"
                            : equipment.status ?? "AVAILABLE"
                        }
                        type={"type" in equipment && typeof equipment.type === "string" ? equipment.type : "Unknown"}
                      />
                    </div>
                  ))}
                </div>
              </div>
            )
          )}
        </div>

      {selectedFacility && (
        <EquipmentDetail
          open={open}
          onClose={() => setOpen(false)}
          name={selectedFacility.name}
          description={selectedFacility.description ?? ""}
          img={selectedFacility.img ?? ""}
          manager={
            "nameFacilityManager" in selectedFacility &&
            typeof selectedFacility.nameFacilityManager === "string"
              ? selectedFacility.nameFacilityManager
              : "Chưa có quản lý"
          }
          slug={selectedFacility.slug}
          type={"type" in selectedFacility && typeof selectedFacility.type === "string" ? selectedFacility.type : "Unknown"}
        />
      )}
    </div>
  );
};

export default Equipments;
