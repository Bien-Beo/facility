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
} from "@mui/material";
import Visibility from "@mui/icons-material/Visibility";
import VisibilityOff from "@mui/icons-material/VisibilityOff";

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
    mutationFn: (data: LoginData) =>
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

  return (
    <div className="w-full h-full min-h-screen flex justify-center items-center">
      <div
        className="w-[50%] min-h-screen h-full flex flex-col justify-center items-center p-10 relative text-white"
        style={{
          backgroundImage: "url('/background.jpg')",
          backgroundSize: "cover",
          backgroundRepeat: "no-repeat",
          backgroundPosition: "center",
        }}
      >
        <Typography variant="h5" className="absolute top-10 left-10">
          Facility Management System
        </Typography>
        <Typography variant="h5" className="text-stone-400">
          Nice to see you again
        </Typography>
        <Typography variant="h2" className="uppercase">
          Welcome back
        </Typography>
      </div>
      <div className="w-[50%] flex flex-col items-center justify-center p-10 gap-6">
        <Typography
          variant="h4"
          sx={{
            fontWeight: "500",
            marginBottom: ".5em",
          }}
        >
          Login Account
        </Typography>
        <form
          onSubmit={handleSubmit}
          autoComplete="off"
          className="flex flex-col gap-4 max-w-[660px]"
        >
          <TextField
            id="username"
            label="Enter Username"
            variant="outlined"
            className="w-full"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            required
          />
          <FormControl variant="outlined" fullWidth>
            <InputLabel htmlFor="outlined-adornment-password">
              Enter Password
            </InputLabel>
            <OutlinedInput
              id="outlined-adornment-password"
              type={showPassword ? "text" : "password"}
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              endAdornment={
                <InputAdornment position="end">
                  <IconButton
                    aria-label="toggle password visibility"
                    onClick={handleClickShowPassword}
                    onMouseDown={handleMouseDownPassword}
                    edge="end"
                  >
                    {showPassword ? <VisibilityOff /> : <Visibility />}
                  </IconButton>
                </InputAdornment>
              }
              label="Enter Password"
              required
            />
            <FormHelperText error={isError}>
              The password must be minimum of 5 characters long
            </FormHelperText>
            {error && (
              <FormHelperText error={true}>
                {error ? error : "Invalid User ID or password"}
              </FormHelperText>
            )}
          </FormControl>
          <Button
            type="submit"
            variant="contained"
            color="primary"
            sx={{
              minWidth: "40%",
              paddingBlock: "1em",
              marginTop: "1em",
              fontWeight: "bold",
              letterSpacing: "2px",
            }}
          >
            Login
          </Button>
        </form>
      </div>
    </div>
  );
};

export default LoginPage;