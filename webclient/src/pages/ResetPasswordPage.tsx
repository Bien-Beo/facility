import React, { JSX, FC, FormEvent, useState } from "react";
import { useMutation } from "@tanstack/react-query";
import axios, { AxiosError } from "axios";
import { Alert, Box, Button, CircularProgress, Container, Paper, Snackbar, TextField, Typography, InputAdornment, IconButton, Avatar } from "@mui/material";
import Visibility from '@mui/icons-material/Visibility';
import VisibilityOff from '@mui/icons-material/VisibilityOff';
import LockResetIcon from '@mui/icons-material/LockReset'; 
import { useAuth } from "../hooks/useAuth";

// Component trang đặt lại mật khẩu
const ResetPasswordPage: FC = (): JSX.Element => {
    // --- State ---
    const [oldPassword, setOldPassword] = useState<string>("");
    const [newPassword, setNewPassword] = useState<string>("");
    const [showOldPassword, setShowOldPassword] = useState<boolean>(false);
    const [showNewPassword, setShowNewPassword] = useState<boolean>(false);
    const [error, setError] = useState<string | null>(null); 
    const [openSnackbar, setOpenSnackbar] = useState<boolean>(false); 

    const auth = useAuth();

    // --- Mutation để gọi API đổi mật khẩu ---
    const mutation = useMutation<
        ApiResponse<string> | void, 
        AxiosError<ErrorMessage>,
        ResetPasswordRequest 
    >({
        mutationFn: async (data: ResetPasswordRequest) => {
            const token = localStorage.getItem("token");
            if (!token) throw new Error("Not authenticated for password reset");

            console.log("Sending password change request for user:", auth.user?.username);
            const response = await axios.post<ApiResponse<string>>( 
                `${import.meta.env.VITE_APP_SERVER_URL || 'http://localhost:8080'}/auth/password/reset`,
                data, 
                { headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' } } 
            );
             // Kiểm tra response nếu API trả về wrapper
             if (response.data?.code !== 0 && response.data?.message) {
                  throw new Error(response.data.message);
             }
             // Hoặc không cần return nếu API trả về 200/204 không body
             return response.data;
        },
        onSuccess: () => {
            console.log("Password reset successful");
            setOpenSnackbar(true); // Hiện thông báo thành công
            // Đăng xuất sau khi thông báo hiển thị một lúc
            setTimeout(async () => {
                console.log("Logging out after password change...");
                await auth.logout(); // Gọi logout từ context SAU KHI thành công
                // AuthProvider nên tự động điều hướng về login sau khi logout
            }, 2500); 
        },
        onError: (error) => {
            console.error("Password reset failed:", error);
            const errorMsg = error.response?.data?.message || error.message || "Đặt lại mật khẩu thất bại. Vui lòng kiểm tra lại mật khẩu cũ.";
            setError(errorMsg);
        },
    });

    // --- Handlers ---
    const handleSubmit = (e: FormEvent<HTMLFormElement>): void => {
        e.preventDefault();
        setError(null); 

        // --- Validation ---
        if (!oldPassword || !newPassword) { setError("Vui lòng nhập đủ mật khẩu cũ và mới."); return; }
        if (newPassword.length < 5) { setError("Mật khẩu mới phải có ít nhất 5 ký tự."); return; }
        if (newPassword === oldPassword) { setError("Mật khẩu mới phải khác mật khẩu cũ."); return; }

        // --- Gọi Mutation ---
        mutation.mutate({ oldPassword, newPassword });
    };

    const handleClickShowOldPassword = () => setShowOldPassword((show) => !show);
    const handleMouseDownOldPassword = (event: React.MouseEvent<HTMLButtonElement>) => event.preventDefault();
    const handleClickShowNewPassword = () => setShowNewPassword((show) => !show);
    const handleMouseDownNewPassword = (event: React.MouseEvent<HTMLButtonElement>) => event.preventDefault();

    // --- Render JSX ---
    return (
         <Container component="main" maxWidth="sm" sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center', minHeight: 'calc(100vh - 64px)' }}>
             <Paper elevation={3} sx={{ p: {xs: 2, sm: 4}, mt: -8, display: 'flex', flexDirection: 'column', alignItems: 'center', width: '100%' }}>
                 <Avatar sx={{ m: 1, bgcolor: 'secondary.main' }}> <LockResetIcon /> </Avatar>
                 <Typography component="h1" variant="h5" sx={{ mb: 2 }}> Đặt lại mật khẩu </Typography>

                 <Box component="form" noValidate onSubmit={handleSubmit} sx={{ mt: 1, width: '100%' }}>
                     <TextField
                         margin="normal" required fullWidth
                         id="oldPassword" label="Mật khẩu cũ" 
                         name="oldPassword" type={showOldPassword ? 'text' : 'password'}
                         autoComplete="current-password"
                         value={oldPassword} onChange={(e) => setOldPassword(e.target.value)}
                         error={!!error && (error.includes('cũ') || error.includes('nhập hoặc mật khẩu không đúng'))} 
                         size="medium"
                         InputProps={{
                             endAdornment: (
                                 <InputAdornment position="end">
                                     <IconButton aria-label="toggle old password visibility" onClick={handleClickShowOldPassword} onMouseDown={handleMouseDownOldPassword} edge="end">
                                         {showOldPassword ? <VisibilityOff /> : <Visibility />}
                                     </IconButton>
                                 </InputAdornment>
                             ),
                         }}
                     />
                      <TextField
                          margin="normal" required fullWidth
                          id="newPassword" label="Mật khẩu mới" 
                          name="newPassword" type={showNewPassword ? 'text' : 'password'}
                          autoComplete="new-password"
                          value={newPassword} onChange={(e) => setNewPassword(e.target.value)}
                          error={!!error}
                          helperText="Mật khẩu mới phải có ít nhất 5 ký tự." 
                          size="medium"
                          InputProps={{
                              endAdornment: (
                                  <InputAdornment position="end">
                                      <IconButton aria-label="toggle new password visibility" onClick={handleClickShowNewPassword} onMouseDown={handleMouseDownNewPassword} edge="end">
                                          {showNewPassword ? <VisibilityOff /> : <Visibility />}
                                      </IconButton>
                                  </InputAdornment>
                              ),
                          }}
                      />

                     {/* Hiển thị lỗi chung/backend */}
                      {error && (
                          <Alert severity="error" sx={{ width: '100%', mt: 1.5 }}>{error}</Alert>
                      )}

                     <Button
                         type="submit" fullWidth variant="contained"
                         sx={{ mt: 3, mb: 2, py: 1.2, fontSize: '1rem', fontWeight: 'bold' }}
                         disabled={mutation.isPending} 
                     >
                         {mutation.isPending ? <CircularProgress size={24} color="inherit" /> : 'Xác nhận đổi mật khẩu'}
                     </Button>
                 </Box>
             </Paper>

             {/* Snackbar cho thông báo thành công */}
              <Snackbar open={openSnackbar} autoHideDuration={5000} onClose={() => setOpenSnackbar(false)} anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}>
                  <Alert onClose={() => setOpenSnackbar(false)} severity="success" variant="filled" sx={{ width: '100%' }}>
                      Đổi mật khẩu thành công! Đang đăng xuất...
                  </Alert>
              </Snackbar>
         </Container>
     );
 };

 export default ResetPasswordPage;