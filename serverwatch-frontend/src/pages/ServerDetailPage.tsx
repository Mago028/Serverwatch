// src/pages/ServerDetailPage.tsx
import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import api from "../api/client";
import type { ServerDto } from "../api/server";
import "../styles/servers.css";
import { getMetrics, type MetricPoint } from "../api/metrics";
import MetricChart from "../components/MetricChart";

export default function ServerDetailPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();

  const [server, setServer] = useState<ServerDto | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [metrics, setMetrics] = useState<MetricPoint[] | null>(null);
  const [metricsError, setMetricsError] = useState("");

  useEffect(() => {
    if (!id) return;

    setLoading(true);
    setError("");

    api
      .get<ServerDto>(`/api/servers/${id}`)
      .then((res) => setServer(res.data))
      .catch((err: any) =>
        setError(err?.message || "서버 정보를 불러오는 데 실패했습니다.")
      )
      .finally(() => setLoading(false));

    getMetrics("10m")
      .then((data) => {
        setMetrics(data);
        setMetricsError("");
      })
      .catch((err: any) =>
        setMetricsError(
          err?.message || "메트릭 정보를 불러오는 데 실패했습니다."
        )
      );
  }, [id]);

  const handleBack = () => navigate("/servers");

  const formatDateTime = (iso: string) => {
    try {
      return new Date(iso).toLocaleString();
    } catch {
      return iso;
    }
  };

  const latest = metrics && metrics.length > 0 ? metrics[metrics.length - 1] : null;

  if (loading) return <LoadingScreen />;

  if (error || !server) {
    return <ErrorScreen error={error} onBack={handleBack} />;
  }

  const metricConfigs: Array<{
    key: keyof MetricPoint;
    title: string;
  }> = [
    { key: "cpu",     title: "CPU 사용률" },
    { key: "memory",  title: "메모리 사용률" },
    { key: "disk",    title: "디스크 사용률" },
    { key: "loadAvg", title: "Load Average" },
  ];

  return (
    <div className="servers-page">
      <Header />

      <main className="servers-main">
        <div className="servers-container servers-container--detail">
          <div className="server-detail-header">
            <div>
              <h1 className="server-detail-name">{server.name}</h1>
              <p className="server-detail-host">{server.host}</p>
              <p className="server-detail-meta">
                ID: {server.id} · Created: {formatDateTime(server.createdAt)}
              </p>
            </div>
            <button className="button button-ghost" onClick={handleBack}>
              ← 서버 목록으로
            </button>
          </div>

          <div className="server-detail-grid">
            {metricConfigs.map((m) => (
              <MetricCard
                key={m.key}
                title={m.title}
                dataKey={m.key}
                latest={latest}
                metrics={metrics}
                error={metricsError}
              />
            ))}
          </div>
        </div>
      </main>
    </div>
  );
}

/* ─────────────────────────────────────────── */

function Header() {
  return (
    <header className="app-header servers-header">
      <div className="servers-container servers-container--detail servers-header-inner">
        <div className="app-logo">
          <div className="app-logo-icon">SW</div>
          <div>
            <div className="app-logo-text-main">SERVERWATCH</div>
            <div className="app-logo-text-sub">
              Multi-tenant server monitoring console
            </div>
          </div>
        </div>
      </div>
    </header>
  );
}

function LoadingScreen() {
  return (
    <div className="servers-page">
      <Header />
      <main className="servers-main">
        <div className="servers-container servers-container--detail">
          <p className="text-muted">서버 정보를 불러오는 중입니다...</p>
        </div>
      </main>
    </div>
  );
}

function ErrorScreen({
  error,
  onBack,
}: {
  error: string;
  onBack: () => void;
}) {
  return (
    <div className="servers-page">
      <Header />
      <main className="servers-main">
        <div className="servers-container servers-container--detail">
          <div className="alert-error">
            <strong style={{ display: "block", marginBottom: 4 }}>
              서버 정보를 불러오지 못했습니다.
            </strong>
            {error || "존재하지 않는 서버입니다."}
          </div>
          <button
            type="button"
            className="button button-ghost"
            style={{ marginTop: 16 }}
            onClick={onBack}
          >
            ← 목록으로 돌아가기
          </button>
        </div>
      </main>
    </div>
  );
}

type MetricCardProps = {
  title: string;
  dataKey: keyof MetricPoint;
  latest: MetricPoint | null;
  metrics: MetricPoint[] | null;
  error: string;
};

function MetricCard({ title, dataKey, latest, metrics, error }: MetricCardProps) {
  const raw = latest ? latest[dataKey] : null;
  const current =
    typeof raw === "number"
      ? dataKey === "loadAvg"
        ? raw.toFixed(2)
        : raw.toFixed(1)
      : "--";

  return (
    <div className="server-detail-card">
      <div className="server-detail-card-header">
        <div>
          <div className="server-detail-card-title">{title}</div>
          <div className="server-detail-card-sub">최근 10분</div>
        </div>
        <div className="server-detail-card-current">
          {current}
          {dataKey === "loadAvg" ? "" : current === "--" ? "" : "%"}
        </div>
      </div>
      <div className="server-detail-card-body">
        {error ? (
          <span>{error}</span>
        ) : metrics ? (
          <MetricChart data={metrics} dataKey={dataKey} />
        ) : (
          <span>로딩 중...</span>
        )}
      </div>
    </div>
  );
}
