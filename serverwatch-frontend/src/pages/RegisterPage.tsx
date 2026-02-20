// src/pages/RegisterPage.tsx
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/client";
import "../styles/auth.css";

export default function RegisterPage() {
  const navigate = useNavigate();
  const [form, setForm] = useState({
    username: "",
    email: "",
    password: "",
  });
  const [error, setError] = useState("");

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setForm((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");

    try {
      await api.post("/api/auth/register", form);
      alert("회원가입이 완료되었습니다. 로그인 페이지로 이동합니다.");
      navigate("/login");
    } catch (err: any) {
      setError(err?.message || "회원가입에 실패했습니다.");
    }
  };

  return (
    <div className="auth-page">
      <div className="auth-card">
        <h1 className="auth-title">ServerWatch 회원가입</h1>

        <form
          onSubmit={handleSubmit}
          style={{ display: "flex", flexDirection: "column", gap: 10 }}
        >
          <input
            name="username"
            placeholder="아이디"
            value={form.username}
            onChange={handleChange}
            className="auth-input"
          />

          <input
            name="password"
            type="password"
            placeholder="비밀번호"
            value={form.password}
            onChange={handleChange}
            className="auth-input"
          />

         <input
            name="email"
            type="email"
            placeholder="이메일"
            value={form.email}
            onChange={handleChange}
            className="auth-input"
          />

          {error && (
            <div
              style={{
                marginTop: 4,
                padding: 8,
                borderRadius: 8,
                border: "1px solid rgba(248,113,113,0.5)",
                background: "rgba(127,29,29,0.5)",
                color: "#fecaca",
                fontSize: 13,
              }}
            >
              {error}
            </div>
          )}

          <button type="submit" className="auth-button-primary">
            회원가입
          </button>
        </form>

        <button
          type="button"
          className="auth-link"
          onClick={() => navigate("/login")}
        >
          이미 계정이 있으신가요? 로그인하기 →
        </button>
      </div>
    </div>
  );
}
