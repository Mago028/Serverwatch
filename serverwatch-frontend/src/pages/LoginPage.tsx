import type { FormEvent } from "react";
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { login } from "../api/auth";
import "../styles/auth.css";

export default function LoginPage() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const navigate = useNavigate();

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    try {
      const res = await login({ username, password });
      localStorage.setItem("accessToken", res.token);
      setError("");
      navigate("/servers");
    } catch (e) {
      setError("로그인에 실패했습니다. 아이디/비밀번호를 확인하세요.");
    }
  };

  return (
    <div className="auth-page">
      <div className="auth-card">
        <h1 className="auth-title">ServerWatch 로그인</h1>

        <form onSubmit={handleSubmit}>
          <div>
            <input
              className="auth-input"
              placeholder="아이디"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
            />
          </div>
          <div>
            <input
              className="auth-input"
              placeholder="비밀번호"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />
          </div>
          {error && <p style={{ color: "#fca5a5" }}>{error}</p>}
          <button type="submit" className="auth-button-primary">
            로그인
          </button>
        </form>

        <button
          type="button"
          className="auth-link"
          onClick={() => navigate("/register")}
        >
          아직 계정이 없으신가요? 회원가입 →
        </button>
      </div>
    </div>
  );
}
