// src/pages/ServerCreatePage.tsx
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/client";
import "../styles/auth.css";

export default function ServerCreatePage() {
  const navigate = useNavigate();
  const [form, setForm] = useState({
    name: "",
    host: "",
    description: "",
  });
  const [error, setError] = useState("");

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
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
      await api.post("/api/servers", form);
      alert("서버가 등록되었습니다.");
      navigate("/servers");
    } catch (err: any) {
      setError(err?.message || "서버 등록에 실패했습니다.");
    }
  };

  return (
    <div className="auth-page">
      <div className="auth-card">
        <h1 className="auth-title">새 서버 등록</h1>

        <form
          onSubmit={handleSubmit}
          style={{ display: "flex", flexDirection: "column", gap: 10 }}
        >
          <input
            name="name"
            placeholder="서버 이름 (예: prod-api-1)"
            value={form.name}
            onChange={handleChange}
            className="auth-input"
          />
          <input
            name="host"
            placeholder="호스트 또는 IP (예: 192.168.0.10)"
            value={form.host}
            onChange={handleChange}
            className="auth-input"
          />
          <textarea
            name="description"
            placeholder="설명 (선택)"
            value={form.description}
            onChange={handleChange}
            className="auth-input"
            style={{ minHeight: 80, resize: "vertical" }}
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
            서버 등록
          </button>
        </form>

        <button
          type="button"
          className="auth-link"
          onClick={() => navigate("/servers")}
        >
          취소하고 목록으로 돌아가기 →
        </button>
      </div>
    </div>
  );
}
