import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth'; 
import DashboardPage from './DashboardPage'; 
import { Box, CircularProgress } from '@mui/material'; 

const HomePage: React.FC = () => {
    const auth = useAuth();

    // Quan trọng: Xử lý trạng thái loading user từ context
    if (auth.loadingUser) {
        return (
            <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: 'calc(100vh - 64px)' }}> 
                <CircularProgress />
            </Box>
        );
    }

    // Nếu không có user (sau khi loading xong) -> nên được xử lý bởi RequireAuth nhưng check lại cho chắc
    if (!auth.user) {
         // Trường hợp này không nên xảy ra nếu có RequireAuth ở ngoài
         console.error("HomePage reached without authenticated user!");
         return <Navigate to="/auth/login" replace />;
    }

    const role = auth.user.roleName;
    console.log("HomePage rendering for role:", role);

    // Dựa vào vai trò để quyết định nội dung/điều hướng
    switch (role) {
        case "ADMIN":
            console.log("Redirecting ADMIN to /admin/rooms");
            return <Navigate to="/admin/rooms" replace />; 

        case "FACILITY_MANAGER":
        case "USER":
        case "TECHNICIAN":
            // Với FM và User, trang chủ là Dashboard phòng
            console.log("Rendering Room Dashboard for role:", role);
            return <DashboardPage type="room" />; 

        // case "TECHNICIAN":
        //     console.log("Redirecting TECHNICIAN to /technician/maintenance");
        //     return <Navigate to="/technician/maintenance" replace />; 

        default:
             console.warn("Unknown user role encountered in HomePage:", role);
             return <Navigate to="/auth/login" replace />; 
    }
};

export default HomePage;