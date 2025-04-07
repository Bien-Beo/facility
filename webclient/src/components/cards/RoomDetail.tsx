import React from "react";
import { Dialog, DialogContent } from "@mui/material";
import { useNavigate } from "react-router-dom";

interface RoomDetailProps {
  open: boolean;
  onClose: () => void;
  id: string;
  name: string;
  description: string;
  capacity: number;
  img: string;
  status: "AVAILABLE" | "UNDER_MAINTENANCE";
  buildingName: string;
  roomTypeName: string;
  nameFacilityManager: string;
  location?: string;
  createdAt: string;
  updatedAt?: string;
  deletedAt?: string;
  defaultEquipments?: EquipmentItemData[] | null;
}

const RoomDetail: React.FC<RoomDetailProps> = ({
  open,
  onClose,
  id,
  name,
  description,
  capacity,
  img,
  status,
  buildingName,
  nameFacilityManager,
  location,
  createdAt,
  updatedAt,
  defaultEquipments = [],
}) => {
  const navigate = useNavigate();
  const imageUrl = img ? `${import.meta.env.VITE_APP_SERVER_URL || 'http://localhost:8080'}/images/${img}` : '/logo.png'; 

  return (
    <Dialog open={open} onClose={onClose} maxWidth="md" fullWidth>
      <DialogContent className="w-full h-auto flex items-center justify-center bg-[url(/1.png)] bg-cover p-6">
        <div className="w-[1000px] flex gap-x-10">
          {/* Left: Image */}
          <div className="rounded-2xl flex-1/2 flex items-center justify-center relative">
            <div className="absolute top-0 left-0 p-2 text-gray-500 text-sm">
                <img className="h-12 w-12 object-scale-down" src="/logo.png" alt="Logo" />
            </div>
            <img
              src={imageUrl} 
              alt={name}
              className="w-full max-h-[350px] object-cover rounded-lg"
              onError={(e) => {
                console.error("Lỗi tải ảnh:", imageUrl);
                (e.target as HTMLImageElement).src = '/logo.png'; // Fallback về logo nếu lỗi
              }}
            />
          </div>

          {/* Right: Details */}
          <div className="rounded-2xl shadow-lg shadow-zinc-800 flex-1/2 flex flex-col px-6 py-6">
            <h1 className="text-2xl font-bold text-gray-900">{name}</h1>
            <p className="text-gray-600 text-sm my-2">{description}</p>
            <p className="text-sm text-gray-500">
              <strong>Capacity:</strong> {capacity} people
            </p>
            <p className="text-sm text-gray-500">
              <strong>Building:</strong> {buildingName}
            </p>
            <p className="text-sm text-gray-500">
              <strong>Status:</strong> {status}
            </p>
            <p className="text-sm text-gray-500">
              <strong>Created At:</strong> {new Date(createdAt).toLocaleDateString("vi-VI", {year: "numeric", month: "2-digit", day: "2-digit"})}
            </p>
            <p className="text-sm text-gray-500">
              <strong>Updated At:</strong> {updatedAt ? new Date(updatedAt).toLocaleDateString("vi-VI", {year: "numeric", month: "2-digit", day: "2-digit"}) : "N/A"}
            </p>
            <p className="text-sm text-gray-500">
              <strong>Equipments:</strong> {defaultEquipments ? defaultEquipments.map((item) => item.modelName).join(", ") : "None"}
            </p>
            <p className="text-sm text-gray-500">
              <strong>Location:</strong> {location ? location : "N/A"}
            </p>
            {/* Manager */}
            <p className="text-sm text-gray-500">
              <strong>Manager:</strong> {nameFacilityManager}
            </p>

            {/* Actions */}
            <div className="mt-auto pt-4 flex space-x-4">
             <button
               // SỬA onClick: Điều hướng đến route /rooms/:id
               onClick={() => navigate(`/rooms/${id}`)}
               className="bg-blue-600 text-white px-4 py-2 rounded-md shadow-md hover:bg-blue-700"
             >
               Xem lịch & Đặt phòng
             </button>
             {/* Nút Report giữ nguyên hoặc sửa tương tự nếu cần */}
             <button
               onClick={() => navigate(`/report/room/${id}`)}
               className="border border-red-600 text-red-600 px-4 py-2 rounded-md shadow-md hover:bg-red-50"
             >
               Báo cáo sự cố
             </button>
           </div>
          </div>
        </div>
      </DialogContent>
    </Dialog>
  );
};

export default RoomDetail;
