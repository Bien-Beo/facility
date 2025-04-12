import React, {
  FC,
  FormEvent,
  MouseEvent,
  useEffect,
  useState,
  JSX,
} from "react";
import { useMutation } from "@tanstack/react-query";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import {
  Button,
  FormControl,
  FormHelperText,
  IconButton,
  InputAdornment,
  InputLabel,
  OutlinedInput,
  TextField,
  Typography,
  Box,
  Alert,
  Avatar,
  Paper,
  CircularProgress
} from "@mui/material";
import Visibility from "@mui/icons-material/Visibility";
import VisibilityOff from "@mui/icons-material/VisibilityOff";
import LockOutlinedIcon from '@mui/icons-material/LockOutlined';

import { useAuth } from "../hooks/useAuth";

const LoginPage: FC = (): JSX.Element => {
  const [username, setUsername] = useState<string>("");
  const [password, setPassword] = useState<string>("");
  const [isError, setIsError] = useState<boolean>(false);
  const [showPassword, setShowPassword] = useState<boolean>(false);
  const [error, setError] = useState<string>("");

  const auth = useAuth();
  const navigate = useNavigate();

  const redirectPath = "/";

  const mutation = useMutation({
    mutationFn: (data: AuthenticationRequest) =>
      axios.post(`${import.meta.env.VITE_APP_SERVER_URL}/auth/token`, data, {
        withCredentials: true,
      }),
      onSuccess: (data) => {
        const result = data.data.result;
        if (result?.token) {
          localStorage.setItem("token-info", JSON.stringify(result));
          auth?.login(result);
          setTimeout(() => {
            navigate(redirectPath, { replace: true, preventScrollReset: true });
          }, 100);
        }
      },
    onError: (error) => {
      if (error.response) {
        const errorData = error.response.data as ErrorMessage;
        setError(errorData.message);
      }
      console.log(error);
    },
  });

  const handleClickShowPassword = (): void => setShowPassword((show) => !show);

  const handleMouseDownPassword = (
    event: MouseEvent<HTMLButtonElement>
  ): void => {
    event.preventDefault();
  };

  const handleSubmit = (e: FormEvent<HTMLFormElement>): void => {
    e.preventDefault();
    if (password.length < 5) {
      setIsError(true);
      return;
    }
  
  setIsError(false);
  mutation.mutate({
    username: username,
    password: password,
  });
  };

  useEffect(() => {
    if (error) {
      setTimeout(() => {
        setError("");
      }, 3000);
    }
  }, [error]);

//   return (
//     <div className="w-full h-full min-h-screen flex justify-center items-center">
//       <div
//         className="w-full min-h-screen h-full flex flex-col justify-center items-left p-10 relative text-white"
//         style={{
//           backgroundImage: "url('/background.png')",
//           backgroundSize: "cover",
//           backgroundRepeat: "no-repeat",
//           backgroundPosition: "center",
//         }}
//       >
//         <Typography variant="h5" className="absolute top-10 left-10 text-black text-center">
//           TRƯỜNG ĐẠI HỌC GIAO THÔNG VẬN TẢI <br/> PHÂN HIỆU TẠI TP. HỒ CHÍ MINH
//         </Typography>
//         <Typography variant="h5" className="text-stone-400">
//           Nice to see you again
//         </Typography>
//         <Typography variant="h2" className="uppercase">
//           Welcome back
//         </Typography>
//       </div>


//       <div className="absolute right-[15%] w-[30%] backdrop-blur-3xl flex flex-col items-center justify-center p-10 gap-6">
//         <Typography
//           variant="h4"
//           sx={{
//             fontWeight: "500",
//             marginBottom: ".5em",
//           }}
//         >
//           ĐĂNG NHẬP
//         </Typography>
//         <form
//           onSubmit={handleSubmit}
//           autoComplete="off"
//           className="flex flex-col gap-4 max-w-[660px]"
//         >
//           <TextField
//             id="username"
//             label="Enter Username"
//             variant="outlined"
//             className="w-full"
//             value={username}
//             onChange={(e) => setUsername(e.target.value)}
//             required
//           />
//           <FormControl variant="outlined" fullWidth>
//             <InputLabel htmlFor="outlined-adornment-password">
//               Enter Password
//             </InputLabel>
//             <OutlinedInput
//               id="outlined-adornment-password"
//               type={showPassword ? "text" : "password"}
//               value={password}
//               onChange={(e) => setPassword(e.target.value)}
//               endAdornment={
//                 <InputAdornment position="end">
//                   <IconButton
//                     aria-label="toggle password visibility"
//                     onClick={handleClickShowPassword}
//                     onMouseDown={handleMouseDownPassword}
//                     edge="end"
//                   >
//                     {showPassword ? <VisibilityOff /> : <Visibility />}
//                   </IconButton>
//                 </InputAdornment>
//               }
//               label="Enter Password"
//               required
//             />
//             <FormHelperText error={isError}>
//               Mật khẩu phải dài tối thiểu 5 ký tự
//             </FormHelperText>
//             {error && (
//               <FormHelperText error={true}>
//                 {error ? error : "Invalid User ID or password"}
//               </FormHelperText>
//             )}
//           </FormControl>
//           <Button
//             type="submit"
//             variant="contained"
//             color="primary"
//             sx={{
//               minWidth: "40%",
//               paddingBlock: "1em",
//               marginTop: "1em",
//               fontWeight: "bold",
//               letterSpacing: "2px",
//             }}
//           >
//             ĐĂNG NHẬP
//           </Button>
//         </form>
//       </div>
//     </div>
//   );
// };

return (
    // Container chính bao phủ màn hình, đặt ảnh nền
    <Box
        sx={{
            minHeight: '100vh',
            width: '100%', 
            position: 'relative', // Để định vị tuyệt đối các thành phần con
            backgroundImage: "url('/background.png')",
            backgroundSize: "cover",
            backgroundRepeat: "no-repeat",
            backgroundPosition: "center",
            overflow: 'hidden', // Tránh scroll không cần thiết
            display: 'flex', // Giúp căn giữa form nếu cần điều chỉnh sau
            alignItems: 'center', // Căn form theo chiều dọc
        }}
    >
        {/* Lớp phủ mờ */}
         <Box sx={{ position: 'absolute', top: 0, left: 0, right: 0, bottom: 0, bgcolor: 'rgba(0, 0, 0, 0.2)', zIndex: 1 }} />

        <Box
            sx={{
                position: 'absolute',
                top: '50%',
                left: { xs: '5%', sm: '8%', md: '10%', lg: '15%' }, 
                transform: 'translateY(-50%)',
                color: 'common.white',
                textShadow: '1px 1px 4px rgba(0,0,0,0.8)', 
                maxWidth: { xs: 'calc(90% - 50px)', sm: '45%', md: '40%' }, 
                zIndex: 2, // Nằm trên lớp phủ
                display: { xs: 'none', md: 'block' } 
            }}
        >
             <Typography variant="h6" component="div" sx={{ mb: 6, fontWeight: 500 }}>
                TRƯỜNG ĐẠI HỌC GIAO THÔNG VẬN TẢI <br /> PHÂN HIỆU TẠI TP. HỒ CHÍ MINH
             </Typography>
            <Typography variant="h2" component="h1" sx={{ fontWeight: 'bold', textTransform: 'uppercase' }}>
                Chào mừng
            </Typography>
            <Typography variant="h5" sx={{ mb: 1, opacity: 0.9 }}>
                Rất vui được gặp lại !
            </Typography>
        </Box>

        {/* Form Đăng nhập */}
        <Paper
            elevation={10} // Tăng độ nổi khối
            sx={{
                position: 'absolute',
                top: '50%',
                right: { xs: '50%', sm:'15%', md: '10%', lg: '10%' }, // Vị trí từ lề phải
                transform: {xs: 'translate(50%, -50%)', sm: 'translateY(-50%)'}, // Căn giữa ngang trên mobile, căn giữa dọc
                width: { xs: '90%', sm: '400px', md: '450px' }, // Chiều rộng responsive
                maxWidth: '450px',
                p: { xs: 3, sm: 4 }, // Padding responsive
                borderRadius: '12px', // Bo góc nhiều hơn
                // Nền trắng mờ + hiệu ứng blur 
                bgcolor: 'rgba(255, 255, 255, 0.9)', 
                zIndex: 2, // Nằm trên lớp phủ
                display: 'flex',
                flexDirection: 'column',
                alignItems: 'center',
            }}
        >
             <Avatar src="/logo.png" alt="UTC2" sx={{ m: 2, bgcolor: 'secondary.main', width: 80, height: 80 }} />
             <Typography component="h1" variant="h4" sx={{ mb: 3, fontWeight: 'bold', color: 'text.primary' }}>
                ĐĂNG NHẬP
             </Typography>

             {/* Form */}
             <Box component="form" noValidate onSubmit={handleSubmit} sx={{ mt: 1, width: '100%' }}>
                 <TextField
                     margin="normal" required fullWidth
                     id="username" label="Tên đăng nhập / Mã số" 
                     name="username" autoComplete="username" autoFocus
                     value={username} onChange={(e) => setUsername(e.target.value)}
                     error={!!error} size="medium" 
                     color="secondary"
                 />
                 <TextField
                     margin="normal" required fullWidth
                     name="password" label="Mật khẩu"
                     type={showPassword ? 'text' : 'password'}
                     id="password" autoComplete="current-password"
                     value={password} onChange={(e) => setPassword(e.target.value)}
                     error={!!error} size="medium" 
                     color="secondary"
                     InputProps={{
                         endAdornment: (
                             <InputAdornment position="end">
                                 <IconButton aria-label="toggle password visibility" onClick={handleClickShowPassword} onMouseDown={handleMouseDownPassword} edge="end">
                                     {showPassword ? <VisibilityOff /> : <Visibility />}
                                 </IconButton>
                             </InputAdornment>
                         ),
                     }}
                 />
                 {/* Hiển thị lỗi rõ ràng hơn */}
                  {error && (
                      <Alert severity="error" sx={{ width: '100%', mt: 1.5, mb: 1 }}>{error}</Alert>
                  )}

                 <Button
                     type="submit" fullWidth variant="contained"
                     sx={{ mt: 3, mb: 2, py: 1.5, fontSize: '1.1rem', fontWeight: 'bold', letterSpacing: '1px' }} 
                     disabled={mutation.isLoading} 
                 >
                     {mutation.isLoading ? <CircularProgress size={24} color="inherit" /> : 'ĐĂNG NHẬP'}
                 </Button>

                 {/* Link quên mật khẩu (ví dụ) */}
                 {/* <Grid container justifyContent="flex-end">
                     <Grid item>
                         <Link component={RouterLink} to="/auth/forgot-password" variant="body2">
                             Quên mật khẩu?
                         </Link>
                     </Grid>
                 </Grid> */}
             </Box>
        </Paper>
    </Box>
);
};

export default LoginPage;