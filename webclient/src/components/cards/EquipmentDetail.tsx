import React from "react";
import { Dialog, DialogContent } from "@mui/material";
import { useNavigate } from "react-router-dom";

interface FacilityDetailProps {
  open: boolean;
  onClose: () => void;
  name: string;
  description: string;
  img: string;
  manager: string;
  price?: number;
  slug: string;
  type: string;
}

const FacilityDetail: React.FC<FacilityDetailProps> = ({
  open,
  onClose,
  name,
  description,
  img,
  manager,
  price = 0,
  slug,
  type,
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
            <img src={img} alt={name} className="w-full max-h-[350px] object-cover rounded-lg" />
          </div>

          {/* Right: Details */}
          <div className="rounded-2xl shadow-lg shadow-zinc-800 flex-1/2 flex flex-col px-6 py-6">
            <h1 className="text-2xl font-bold text-gray-900">{name}</h1>
            <p className="text-gray-600 text-sm my-2">{description}</p>

            {/* Price */}
            {price > 0 && <p className="text-lg font-semibold text-black">${price}</p>}

            {/* Manager */}
            <p className="text-sm text-gray-500">
              <strong>Manager:</strong> {manager}
            </p>

            {/* Actions */}
            <div className="mt-4 flex space-x-4">
              <button
                onClick={() => navigate(`/${type}/${slug}`)}
                className="bg-black text-white px-4 py-2 rounded-md shadow-md hover:bg-gray-800"
              >
                Add to Bag
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

export default FacilityDetail;
