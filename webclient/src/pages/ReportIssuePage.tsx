import { JSX, FC } from "react"; 
import { useParams } from "react-router-dom"; 
import { Box, Container } from "@mui/material"; 

import ReportIssue from "../components/ReportIssue"; 
import ErrorComponent from "../components/Error"; 

const ReportIssuePage: FC = (): JSX.Element => {
    const { id: roomId } = useParams<{ id: string }>();

    if (!roomId) {
        console.error("ReportIssuePage: Không tìm thấy roomId trong URL.");
        return <ErrorComponent status={400} message="Đường dẫn không hợp lệ, thiếu ID phòng." />;
    }

    // --- Render giao diện ---
    return (
        <Box
            sx={{
                width: '100%',
                minHeight: 'calc(100vh - 64px)', 
                display: 'flex',
                flexDirection: 'column',
                alignItems: 'center', 
                pt: { xs: 2, sm: 3, md: 4 }, 
                pb: 4, 
                px: { xs: 1, sm: 2 } 
            }}
        >
             <Container maxWidth="md">
                 <ReportIssue roomId={roomId} />
             </Container>
         </Box>
    );
};

export default ReportIssuePage;