// src/api/client.ts
import axios, { AxiosError } from "axios";

// 기본 axios 인스턴스 생성
export const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  // timeout: 5000,
});

// ✅ 요청 인터셉터: 매 요청마다 JWT 헤더 자동 첨부
api.interceptors.request.use((config) => {
  const token = localStorage.getItem("accessToken");
  if (token) {
    // headers 객체가 없을 수도 있으니 방어 코드
    config.headers = config.headers ?? {};
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// ✅ 응답 인터셉터: 공통 에러 / 인증 만료 처리
api.interceptors.response.use(
  (response) => response,
  (error: AxiosError) => {
    const status = error.response?.status;

    // 401 / 403 → 토큰 삭제 + 로그인 페이지로 이동
    if (status === 401 || status === 403) {
      localStorage.removeItem("accessToken");

      // 이미 /login이면 또 보내지 않기
      if (window.location.pathname !== "/login") {
        window.location.href = "/login";
      }
    }

    // 이후 컴포넌트에서 쓰기 좋게 에러 메시지 정리
    let message: string;

    // 서버에서 { message: "..." } 형태로 내려보내는 경우
    const data: any = error.response?.data;
    if (data && typeof data === "object" && data.message) {
      message = data.message;
    } else {
      message = error.message || "요청 처리 중 오류가 발생했습니다.";
    }

    return Promise.reject({
      status,
      message,
      raw: error,
    });
  }
);

// 기본 export도 유지 (import api from "../api/client"; 형태용)
export default api;
