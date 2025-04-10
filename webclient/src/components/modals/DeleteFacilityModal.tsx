import { Button, Fade, Modal, Typography } from "@mui/material";
import { FC, useState } from "react";
import ReportProblemOutlinedIcon from "@mui/icons-material/ReportProblemOutlined";
import { useMutation } from "@tanstack/react-query";
import axios from "axios";

import ErrorComponent from "../Error";

const DeleteFacilityModal: FC<EditFacilityModalProps> = ({
  isOpen,
  setIsOpen,
  setOpenSnackbar,
  facilityData,
}): JSX.Element => {
  const [error, setError] = useState<ErrorMessage>({
    error: {
      status: null,
      message: "",
    },
  });

  const mutation = useMutation({
    mutationFn: (data: { slug: string }) =>
      axios.post(
        `${import.meta.env.VITE_APP_SERVER_URL}/admin/facility`,
        data,
        {
          withCredentials: true,
        }
      ),
    onSuccess: () => {
      setIsOpen(false);
      setOpenSnackbar(true);
    },
    onError: (error) => {
      setError(error.response!.data as ErrorMessage);
      console.log(error);
    },
  });

  const handleSubmit = (): void => {
    mutation.mutate({ slug: facilityData.slug });
  };

  const handleCancel = (): void => {
    setIsOpen(false);
  };

  if (error.error.status) {
    return (
      <ErrorComponent
        status={error.error.status!}
        message={error.error.message}
      />
    );
  }

  return (
    <Modal
      open={isOpen}
      onClose={() => {
        setIsOpen(false);
      }}
    >
      <Fade in={isOpen}>
        <div className="bg-bgPrimary w-full max-w-[500px] px-10 py-10 absolute left-[50%] top-[50%] -translate-x-[50%] -translate-y-[50%] rounded-md flex flex-col gap-6 shadow-cardHover items-center justify-center">
          <ReportProblemOutlinedIcon
            color="error"
            sx={{ width: "100px", height: "100px" }}
          />
          <Typography variant="h4" component="h2">
            Are you sure?
          </Typography>
          <div className="w-full flex flex-col items-center justify-center">
            <Typography variant="h6" component="h2">
              Do you really want to delete this facility?
            </Typography>
            <Typography variant="h6" component="h2">
              This process cannot be undone!
            </Typography>
          </div>
          <div className="w-full flex gap-4 justify-center">
            <Button
              variant="contained"
              color="primary"
              sx={{ paddingX: "2em", height: "45px" }}
              size="large"
              onClick={handleCancel}
            >
              Cancel
            </Button>
            <Button
              variant="contained"
              color="error"
              sx={{ paddingX: "2em", height: "45px" }}
              size="large"
              onClick={handleSubmit}
            >
              Delete
            </Button>
          </div>
        </div>
      </Fade>
    </Modal>
  );
};

export default DeleteFacilityModal;