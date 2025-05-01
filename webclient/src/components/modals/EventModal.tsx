import { JSX, FC } from "react";
import { Fade, Modal, Typography } from "@mui/material";
import CloseIcon from "@mui/icons-material/Close";

const EventModal: FC<EventModalProps> = ({
  isOpen,
  setIsOpen,
  eventInfo,
}): JSX.Element => {
  return (
    <Modal
      open={isOpen}
      onClose={() => setIsOpen(false)}
      sx={{ border: "none" }}
    >
      <Fade in={isOpen}>
        <div className="bg-bgPrimary w-full max-w-[800px] flex flex-col gap-6 absolute left-[50%] top-[50%] -translate-x-[50%] -translate-y-[50%] rounded-md shadow-cardHover border-none">
          <div className="w-full flex items-center justify-between bg-primary px-10 py-8 rounded-md rounded-b-none border-none">
            <Typography
              variant="h4"
              component="h2"
              className="mb-1 text-white"
              style={{ fontWeight: "bold" }}
            >
              Chi tiết sự kiện
            </Typography>
            <CloseIcon
              className="cursor-pointer"
              sx={{ width: "35px", height: "35px", color: "white" }}
              onClick={() => setIsOpen(false)}
            />
          </div>
          <div className="flex flex-col gap-1 px-10 pb-10">
            <Typography variant="h6" component="p">
              <span className="font-bold tracking-normal">Phòng:</span>{" "}
              {eventInfo?.roomName}
            </Typography>
            <Typography variant="h6" component="p">
              <span className="font-bold tracking-normal">Mục đích mượn:</span>{" "}
              {eventInfo?.purpose}
            </Typography>
            {eventInfo?.bookedEquipments && eventInfo.bookedEquipments.length > 0 ? (
              <Typography variant="h6" component="p">
                <span className="font-bold tracking-normal">Thiết bị:</span>{" "}
                {eventInfo.bookedEquipments.map((item, index) => (
                  <span key={index}>
                    {item.equipmentModelName}
                    {index !== eventInfo.bookedEquipments.length - 1 && ", "}
                  </span>
                ))}
              </Typography>
            ) : (
              <Typography variant="h6" component="p">
                <span className="font-bold tracking-normal">Thiết bị:</span>{" "}
                Không có thiết bị nào được yêu cầu
              </Typography>
            )}
            <Typography variant="h6" component="p">
              <span className="font-bold tracking-normal">Ngày mượn:</span>{" "}
              {eventInfo.date}
            </Typography>
            <Typography variant="h6" component="p">
              <span className="font-bold tracking-normal">Thời gian mượn:</span>{" "}
              {eventInfo.start} - {eventInfo.end}
            </Typography>
            {eventInfo.status === "PENDING_APPROVAL" && (
              <Typography variant="h6" component="p">
                <span className="font-bold tracking-normal">Trạng thái:</span>{" "}
                Đang chờ phê duyệt
              </Typography>
            )}

            <Typography variant="h6" component="p">
              <span className="font-bold tracking-normal">Người mượn:</span>{" "}
              {eventInfo?.requestBy}
            </Typography>
            <Typography variant="h6" component="p">
              <span className="font-bold tracking-normal">Ngày tạo:</span>{" "}
              {eventInfo?.createdAt}
            </Typography>
            <Typography variant="h6" component="p">
              <span className="font-bold tracking-normal">Ngày cập nhật:</span>{" "}
              {eventInfo?.updatedAt}
            </Typography>
            <Typography variant="h6" component="p">
              <span className="font-bold tracking-normal">Ghi chú:</span>{" "}
              {eventInfo?.note || "Không có ghi chú"}
            </Typography>
          </div>
        </div>
      </Fade>
    </Modal>
  );
};

export default EventModal;