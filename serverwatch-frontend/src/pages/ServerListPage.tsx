// src/pages/ServerListPage.tsx
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { getServers } from "../api/server";
import type { ServerDto } from "../api/server";
import "../styles/servers.css";

export default function ServerListPage() {
  const [servers, setServers] = useState<ServerDto[]>([]);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    getServers()
      .then(setServers)
      .catch((err: any) => {
        setError(err?.message || "서버 목록을 불러오는 데 실패했습니다.");
      })
      .finally(() => setLoading(false));
  }, []);

  const handleLogout = () => {
    localStorage.removeItem("accessToken");
    navigate("/login");
  };

  const handleCreateServer = () => {
    navigate("/servers/new");
  };

  const formatDateTime = (iso: string) => {
    try {
      return new Date(iso).toLocaleString();
    } catch {
      return iso;
    }
  };

  return (
    <div className="servers-page">
      {/* 상단 헤더 */}
      <header className="app-header servers-header">
        <div className="servers-container servers-header-inner">
          <div className="app-logo">
            <div className="app-logo-icon">SW</div>
            <div>
              <div className="app-logo-text-main">SERVERWATCH</div>
              <div className="app-logo-text-sub">
                Multi-tenant server monitoring console
              </div>
            </div>
          </div>
          <div className="app-header-right">
            <span>Signed in</span>
            <button className="button button-ghost" onClick={handleLogout}>
              ⏏ Logout
            </button>
          </div>
        </div>
      </header>

      {/* 메인 내용 */}
      <main className="servers-main">
        <div className="servers-container">
          <div className="page-header">
            <div className="page-title">
              <div className="page-title-main">My Servers</div>
              <div className="page-title-sub">
                등록된 서버들의 상태와 기본 정보를 한눈에 확인하세요.
              </div>
            </div>

            <button
              className="button button-primary"
              onClick={handleCreateServer}
            >
              ＋ Add Server
            </button>
          </div>

          {loading && (
            <p className="text-muted">서버 목록을 불러오는 중입니다...</p>
          )}

          {!loading && error && (
            <div className="alert-error">
              <strong style={{ display: "block", marginBottom: 4 }}>
                오류가 발생했습니다
              </strong>
              <span>{error}</span>
            </div>
          )}

          {!loading && !error && servers.length === 0 && (
            <p className="text-muted">
              아직 등록된 서버가 없습니다. 우측 상단의{" "}
              <b>Add Server</b> 버튼으로 첫 서버를 등록해 보세요.
            </p>
          )}

          {!loading && !error && servers.length > 0 && (
            <div className="server-grid">
              {servers.map((s) => (
                <div
                  key={s.id}
                  className="server-card"
                  onClick={() => {
                    navigate(`/servers/${s.id}`);
                  }}
                >
                  <div className="server-card-header">
                    <div className="server-name">{s.name}</div>
                  </div>
                  <div className="server-host">{s.host}</div>
                  <div className="server-meta">
                    <span>ID: {s.id}</span>
                    <span>Created: {formatDateTime(s.createdAt)}</span>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </main>
    </div>
  );
}
