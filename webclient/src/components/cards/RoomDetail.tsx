import React from "react";
import { Dialog, DialogContent, Button } from "@mui/material";
import { useNavigate } from "react-router-dom";

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
      <DialogContent className="w-full h-auto flex items-center justify-center p-6">
        <div className="w-[1000px] flex gap-x-10">
          {/* Left: Image */}
          <div className="rounded-2xl flex-1/2 flex items-center justify-center relative">
            <div className="absolute top-0 left-0 p-2 text-sm">
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
          <div className="rounded-2xl shadow-lg shadow-zinc-500 flex-1/2 flex flex-col px-6 py-8">
            <h1 className="text-2xl font-bold text-black">{name}</h1>
            <p className="text-black text-sm my-2">{description}</p>
            <p className="text-sm text-black">
              <strong>Sức chứa:</strong> {capacity} người
            </p>
            <p className="text-sm text-black">
              <strong>Tòa:</strong> {buildingName}
            </p>
            <p className="text-sm text-black">
              <strong>Trạng thái:</strong> {status}
            </p>
            <p className="text-sm text-black">
              <strong>Ngày khánh thành:</strong> {new Date(createdAt).toLocaleDateString("vi-VI", {year: "numeric", month: "2-digit", day: "2-digit"})}
            </p>
            <p className="text-sm text-black">
              <strong>Bảo trì lần cuối:</strong> {updatedAt ? new Date(updatedAt).toLocaleDateString("vi-VI", {year: "numeric", month: "2-digit", day: "2-digit"}) : "N/A"}
            </p>
            <p className="text-sm text-black">
              <strong>Các thiết bị:</strong> {defaultEquipments ? defaultEquipments.map((item) => item.modelName).join(", ") : "None"}
            </p>
            <p className="text-sm text-black">
              <strong>Vị trí:</strong> {location ? location : "N/A"}
            </p>
            {/* Manager */}
            <p className="text-sm text-black">
              <strong>Quản lý:</strong> {nameFacilityManager}
            </p>

            {/* Actions */}
            <div className="mt-auto pt-4 flex space-x-4">
             <Button
               onClick={() => navigate(`/rooms/${id}`)}
               className="px-4 py-4 rounded-md shadow-md hover:bg-[#271756]"
             >
               Xem lịch & Đặt phòng
             </Button>
             <Button
               onClick={() => navigate(`/report/room/${id}`)}
               className="border px-4 py-4 rounded-md shadow-md hover:bg-[#271756]"
             >
               Báo cáo sự cố
             </Button>
           </div>
          </div>
        </div>
      </DialogContent>
    </Dialog>
  );
};

export default RoomDetail;
