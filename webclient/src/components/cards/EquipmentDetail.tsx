import React from "react";
import { Dialog, DialogContent } from "@mui/material";
import { useNavigate } from "react-router-dom";

interface FacilityDetailProps {
  open: boolean;
  onClose: () => void;
  id: string;
  modelName: string;
  typeName: string;
  serialNumber: string | null;
  assetTag?: string | null; 
  status: string; 
  purchaseDate: string | null;
  warrantyExpiryDate: string | null;
  defaultRoomName: string | null;
  notes: string | null;
  createdAt?: string | null;
  updatedAt?: string | null; 
  imgModel: string | null; 
}

const FacilityDetail: React.FC<FacilityDetailProps> = ({
  open,
  onClose,
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
}) => {
  const navigate = useNavigate();

  return (
    <Dialog open={open} onClose={onClose} maxWidth="md" fullWidth>
      <DialogContent className="w-full min-h-96 flex items-center justify-center bg-[url(/1.png)] bg-cover p-6">
        <div className="w-[800px] flex flex-row gap-x-10">
          {/* Left: Image */}
          <div className="rounded-2xl shadow-lg shadow-zinc-800 flex-1/2 flex items-center justify-center relative">
            <div className="absolute top-0 left-0 p-2 text-gray-500 text-sm">
                <img className="h-12 w-12 object-scale-down" src="/logo.png" alt="Logo" />
            </div>
            <img src={imgModel} alt={modelName} className="w-full max-h-[350px] object-cover rounded-lg" />
          </div>

          {/* Right: Details */}
          <div className="rounded-2xl shadow-lg shadow-zinc-800 flex-1/2 flex flex-col px-6 py-6">
            <h1 className="text-2xl font-bold text-gray-900">{modelName}</h1>
            <p className="text-gray-600 text-sm my-2">{notes}</p>

            {/* Default Room */}
            <p className="text-sm text-gray-500">
              <strong>Default Room name:</strong> {defaultRoomName}
            </p>
            <p className="text-sm text-gray-500">
              <strong>Serial Number:</strong> {serialNumber}
            </p>
            <p className="text-sm text-gray-500">
              <strong>Asset Tag:</strong> {assetTag}
            </p>
            <p className="text-sm text-gray-500">
              <strong>Status:</strong> {status.replace("_", " ")}
            </p>
            <p className="text-sm text-gray-500">
              <strong>Purchase Date:</strong> {purchaseDate}
            </p>
            <p className="text-sm text-gray-500">
              <strong>Warranty Expiry Date:</strong> {warrantyExpiryDate}
            </p>
            <p className="text-sm text-gray-500">
              <strong>Created At:</strong> {createdAt}
            </p>
            <p className="text-sm text-gray-500">
              <strong>Updated At:</strong> {updatedAt}
            </p>

            {/* Actions */}
            <div className="mt-4 flex space-x-4">
              <button
                onClick={() => navigate(`/${typeName}/${id}`)}
                className="bg-black text-white px-4 py-2 rounded-md shadow-md hover:bg-gray-800"
              >
                Add to Bag
              </button>
              <button
                onClick={() => navigate(`/report/${id}`)}
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

export default FacilityDetail;
