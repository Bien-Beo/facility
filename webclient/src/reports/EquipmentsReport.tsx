import React, { JSX, FC, useMemo } from "react"; // Bỏ các import không dùng
import Paper from "@mui/material/Paper";
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell from "@mui/material/TableCell";
import TableContainer from "@mui/material/TableContainer";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import { Typography } from "@mui/material";

// Import các kiểu dữ liệu đã chuẩn hóa
import isoToTime from "../utils/isoToTime";
import isoToDate from "../utils/isoToDate";

// --- Định nghĩa Type cho Component này ---

// Dữ liệu một dòng trong bảng báo cáo (sau khi xử lý)
interface ReportRowData {
    id: string; // Dùng làm key
    name: JSX.Element;
    description: string | null;
    status: RoomStatusType;
    createdAt: JSX.Element | string;
    updatedAt: JSX.Element | string;
    // deletedAt: JSX.Element | string; // Có thể bỏ cột này trong báo cáo
    facilityManager: string | null;
}

// Định nghĩa cột cho bảng báo cáo
interface ReportColumnData {
    // id phải khớp với key của ReportRowData
    id: keyof Omit<ReportRowData, 'id'>;
    label: string;
    minWidth?: number;
    align?: 'left' | 'right' | 'center';
}

// Props cho component
interface FacilitiesReportProps {
    rooms: RoomData[]; // Sửa: Nhận mảng RoomData
    forwardedRef: React.Ref<HTMLDivElement>;
}

// --- Định nghĩa cột ---
const columns: readonly ReportColumnData[] = [
    { id: "name", label: "Name/Building", minWidth: 145 },
    { id: "description", label: "Description", minWidth: 140 },
    { id: "status", label: "Status", minWidth: 100 },
    { id: "createdAt", label: "Created At", minWidth: 150 },
    { id: "updatedAt", label: "Updated At", minWidth: 150 },
    // { id: "deletedAt", label: "Deleted At", minWidth: 150 }, // Bỏ nếu không cần
    { id: "facilityManager", label: "Facility Manager", minWidth: 170 }, // Sửa id
];

// --- Component ---
const EquipmentsReport: FC<FacilitiesReportProps> = ({
    rooms, 
    forwardedRef,
}): JSX.Element => {

    // Map dữ liệu sang định dạng hàng của bảng, dùng useMemo
    const rows: ReportRowData[] = useMemo(() =>
        rooms?.map((room) => ({
            id: room.id, // Thêm ID
            name: (
                <>
                    {room.name} /<br />
                    {/* Sửa: Dùng buildingName, kiểm tra null */}
                    {room.buildingName ?? 'N/A'}
                </>
            ),
            description: room.description,
             // Sửa: Dùng status trực tiếp
            status: room.status,
            // Sửa: Thêm kiểm tra null, bỏ toString()
            createdAt: room.createdAt ? (
                <>{isoToTime(room.createdAt)}<br />{isoToDate(room.createdAt)}</>
            ) : 'N/A',
            updatedAt: room.updatedAt ? (
                <>{isoToTime(room.updatedAt)}<br />{isoToDate(room.updatedAt)}</>
            ) : 'N/A',
            // deletedAt: room.deletedAt ? (
            //      <>{isoToTime(room.deletedAt)}<br />{isoToDate(room.deletedAt)}</>
            // ) : 'N/A',
            // Sửa: Dùng nameFacilityManager, kiểm tra null
            facilityManager: room.nameFacilityManager ?? 'N/A',
        })) || [],
    [rooms]); // Chỉ tính toán lại khi prop rooms thay đổi

    return (
        <div className="w-full flex flex-col gap-4 px-1" ref={forwardedRef}>
            {/* Header báo cáo */}
            <div className="w-full flex justify-between items-center">
                <Typography variant="h6" component="h1">
                    Facilities Report
                </Typography>
                <Typography variant="caption" component="p">
                    Generated at: {new Date().toLocaleString()}
                </Typography>
            </div>
            {/* Bảng dữ liệu */}
            <Paper sx={{ overflow: "hidden", width: '100%' }}>
                <TableContainer> {/* Có thể bỏ giới hạn chiều cao để in hết */}
                    <Table stickyHeader size="small"> {/* Dùng size nhỏ cho báo cáo */}
                        <TableHead>
                            <TableRow>
                                {columns.map((column) => (
                                    <TableCell
                                        key={column.id}
                                        align={column.align || "left"}
                                        sx={{
                                            minWidth: column.minWidth,
                                            backgroundColor: "#f0f0f0", // Màu nền header nhạt hơn
                                            color: "#333",
                                            fontWeight: 'bold',
                                            fontSize: "10px",
                                            padding: "4px 8px", // Padding nhỏ hơn
                                            border: '1px solid #ccc' // Thêm border cho rõ ràng khi in
                                        }}
                                    >
                                        {column.label}
                                    </TableCell>
                                ))}
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {rows.map((row) => ( // Dùng row.id làm key
                                <TableRow hover tabIndex={-1} key={row.id}>
                                    {columns.map((column) => {
                                        // Sửa lại cách lấy value và render
                                        const columnId = column.id as keyof ReportRowData;
                                        const value = row[columnId];
                                        return (
                                            <TableCell
                                                key={column.id}
                                                align={column.align || "left"}
                                                sx={{
                                                    fontSize: "10px",
                                                    padding: "4px 8px",
                                                    border: '1px solid #eee' // Thêm border
                                                }}
                                            >
                                                {/* Sửa: Render trực tiếp, dùng fallback ?? 'N/A' */}
                                                {value ?? 'N/A'}
                                            </TableCell>
                                        );
                                    })}
                                </TableRow>
                            ))}
                        </TableBody>
                    </Table>
                </TableContainer>
            </Paper>
        </div>
    );
};

export default EquipmentsReport;