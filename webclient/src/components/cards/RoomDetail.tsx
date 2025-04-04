import React from "react";
import { Dialog, DialogContent } from "@mui/material";
import { useNavigate } from "react-router-dom";

interface RoomDetailProps {
  open: boolean;
  onClose: () => void;
  name: string;
  description: string;
  capacity: number;
  buildingName: string;
  status?: "AVAILABLE" | "BOOKED" | "UNDER_MAINTENANCE";
  img: string;
  isActive?: boolean;
  createdAt?: string;
  updatedAt?: string;
  manager: string;
  equipments: EquipmentData[];
  slug: string;
}

const RoomDetail: React.FC<RoomDetailProps> = ({
  open,
  onClose,
  name,
  description,
  capacity,
  buildingName,
  status,
  img,
  isActive,
  createdAt,
  updatedAt,
  manager,
  equipments,
  slug
}) => {
  const navigate = useNavigate();
  const imageUrl = img ? `${import.meta.env.VITE_APP_SERVER_URL || 'http://localhost:8080'}/images/${img}` : '/logo.png'; 

  return (
    <Dialog open={open} onClose={onClose} maxWidth="md" fullWidth>
      <DialogContent className="w-full h-auto flex items-center justify-center bg-[url(/1.png)] bg-cover p-6">
        <div className="w-[1000px] flex gap-x-10">
          {/* Left: Image */}
          <div className="rounded-2xl shadow-lg shadow-zinc-800 flex-1/2 flex items-center justify-center relative">
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
              <strong>Created At:</strong> {createdAt}
            </p>
            <p className="text-sm text-gray-500">
              <strong>Updated At:</strong> {updatedAt}
            </p>
            <p className="text-sm text-gray-500">
              <strong>Active:</strong> {isActive ? "Yes" : "No"}
            </p>
            <p className="text-sm text-gray-500">
              <strong>Equipments:</strong> {equipments.map((item) => item.name).join(", ")}
            </p>
            {/* Manager */}
            <p className="text-sm text-gray-500">
              <strong>Manager:</strong> {manager}
            </p>

            {/* Actions */}
            <div className="mt-4 flex space-x-4">
              <button
                onClick={() => navigate(`/borrow/${slug}`)}
                className="bg-black text-white px-4 py-2 rounded-md shadow-md hover:bg-gray-800"
              >
                Borrow
              </button>
              <button
                onClick={() => navigate(`/report/${slug}`)}
                className="border border-black px-4 py-2 rounded-md shadow-md hover:bg-gray-200"
              >
                Report
              </button>
            </div>
          </div>
        </div>
      </DialogContent>
    </Dialog>
  );
};

export default RoomDetail;
