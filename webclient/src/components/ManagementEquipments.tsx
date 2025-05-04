import DownloadIcon from "@mui/icons-material/Download";
import InsertInvitationIcon from "@mui/icons-material/InsertInvitation";
import {
	Alert,
	Button,
	CircularProgress,
	Snackbar,
	Typography,
} from "@mui/material";
import { useQuery } from "@tanstack/react-query";
import axios from "axios";
import { JSX, FC, useEffect, useRef, useState } from "react";
import generatePDF, { Options } from "react-to-pdf";

import EquipmentsReport from "../reports/EquipmentsReport";
import ErrorComponent from "./Error";
import AddEquipmentModal from "./modals/AddEquipmentModal";
import ManagementEquipmentsTable from "./tables/ManagementEquipmentsTable";

const ManagementEquipments: FC = (): JSX.Element => {
	const [equipmentsData, setEquipmentsData] =
		useState<EquipmentsTableProps>({
			equipments: [],
			totalEquipmentCount: 0,
			page: 0,
			rowsPerPage: 0,
			onPageChange: () => {},
			onRowsPerPageChange: () => {},
            defaultRoom: null,
		});
	const [isAddEquipmentModalOpen, setIsAddEquipmentModalOpen] =
		useState<boolean>(false);
	const [isPrint, setIsPrint] = useState<boolean>(false);
	const [openSnackbar, setOpenSnackbar] = useState<boolean>(false);

	const targetRef = useRef<HTMLDivElement>(null);

	const { data, isPending, isError, error } = useQuery({
		queryKey: ["managementequipments"],
		queryFn: async () => {
            const token = localStorage.getItem("token");
            if (!token) throw new Error("No token found");
			const response = await axios.get(
				`${import.meta.env.VITE_APP_SERVER_URL}/equipments`,
				{ headers: { Authorization: `Bearer ${token}` } }
			);
			return response.data;
		},
		refetchInterval: 5 * 1000,
		retry: 1,
		gcTime: 0,
	});

	const handleCloseSnackbar = (): void => {
		setOpenSnackbar(false);
	};

	useEffect(() => {
        if (!isPending && data?.result?.content) {
            setEquipmentsData({
                equipments: data.result.content,
                totalEquipmentCount: data.result.page.totalElements,
                page: data.result.page.number,
                rowsPerPage: data.result.page.size,
                onPageChange: () => {},
                onRowsPerPageChange: () => {},
                defaultRoom: null,
            });
        }
    }, [data, isPending]);
    

	useEffect(() => {
		if (isPrint) {
			setTimeout(() => {
				setIsPrint(false);
			}, 3000);
		}

		if (isPrint) {
			document.body.style.overflowY = "hidden";
		} else {
			document.body.style.overflowY = "auto";
		}
	}, [isPrint]);

	if (isError) {
		const errorData = error.response!.data as ErrorMessage;
		return (
			<ErrorComponent
				status={errorData.status!}
				message={errorData.message}
			/>
		);
	}

	if (isPending)
		return (
			<div className="w-[74vw] min-h-screen h-full flex flex-col items-center justify-center">
				<CircularProgress />
			</div>
		);

	const options: Options = {
		filename: "admin-facilities-bookings-report.pdf",
		page: {
			orientation: "landscape",
		},
	};

	return (
		<div className="w-full flex flex-col px-6 pt-8 gap-6">
			{isAddEquipmentModalOpen && (
				<AddEquipmentModal
					isOpen={isAddEquipmentModalOpen}
					setIsOpen={setIsAddEquipmentModalOpen}
					setOpenSnackbar={setOpenSnackbar}
                    defaultRoom={equipmentsData.defaultRoom}
				/>
			)}

			<Typography variant="h3" component="h1">
				Quản lý thiết bị
			</Typography>
			<div className="w-full flex justify-between items-center">
				<Button
					variant="contained"
					color="primary"
					endIcon={
						<InsertInvitationIcon
							sx={{ height: "20px", width: "20px" }}
						/>
					}
					sx={{ paddingX: "2em", height: "45px" }}
					size="large"
					onClick={() => {
						setIsAddEquipmentModalOpen(true);
					}}
				>
					Thêm thiết bị
				</Button>

				<Button
					variant="contained"
					color="primary"
					endIcon={
						<DownloadIcon sx={{ height: "20px", width: "20px" }} />
					}
					sx={{ paddingX: "2em", height: "45px" }}
					size="large"
					onClick={() => {
						setIsPrint(true);
						setTimeout(() => {
							generatePDF(targetRef, options);
						}, 1000);
					}}
				>
					Xuất báo cáo
				</Button>
			</div>
			{!isPending && (
				<ManagementEquipmentsTable
					equipments={equipmentsData.equipments}
					totalEquipmentCount={equipmentsData.totalEquipmentCount}
					page={equipmentsData.page}
					rowsPerPage={equipmentsData.rowsPerPage}
					onPageChange={equipmentsData.onPageChange}
					onRowsPerPageChange={equipmentsData.onRowsPerPageChange}
				/>
			)}
			{isPrint && (
				<div className="mt-[100dvh]">
					<EquipmentsReport
						equipments={equipmentsData.equipments}
						forwardedRef={targetRef}
					/>
				</div>
			)}
			<Snackbar
				open={openSnackbar}
				autoHideDuration={3000}
				onClose={handleCloseSnackbar}
			>
				<Alert
					onClose={handleCloseSnackbar}
					severity="success"
					sx={{ width: "100%" }}
				>
					Thiết bị đã được thêm thành công!
				</Alert>
			</Snackbar>
		</div>
	);
};

export default ManagementEquipments;