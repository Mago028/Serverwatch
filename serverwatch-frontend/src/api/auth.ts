// src/api/auth.ts
import api from "./client";

export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  token: string; // 백엔드에서 JWT를 어떤 필드명으로 주는지에 맞게 조정
}

export const login = async (data: LoginRequest) => {
  const res = await api.post<LoginResponse>("/api/auth/login", data);
  return res.data;
};
