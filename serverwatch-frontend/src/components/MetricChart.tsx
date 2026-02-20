import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  Tooltip,
  ResponsiveContainer,
} from "recharts";

interface MetricChartProps {
  data: { timestamp: string }[];
  dataKey: string;   // "cpu" | "memory" | "disk" | "loadAvg"
}

export default function MetricChart({ data, dataKey }: MetricChartProps) {
  if (!data || data.length === 0) {
    return <div style={{ fontSize: 12, color: "#9ca3af" }}>데이터 없음</div>;
  }

  const formatted = data.map((d) => ({
    ...d,
    time: new Date(d.timestamp).toLocaleTimeString("ko-KR", {
      hour: "2-digit",
      minute: "2-digit",
    }),
  }));

  return (
    <ResponsiveContainer width="100%" height={220}>
      <LineChart
        data={formatted}
        margin={{ top: 8, right: 8, left: -10, bottom: 0 }}
      >
        <XAxis dataKey="time" tick={{ fontSize: 10 }} />
        <YAxis tick={{ fontSize: 10 }} domain={[0, "auto"]} />
        <Tooltip />
        <Line
          type="monotone"
          dataKey={dataKey}
          stroke="#38bdf8"
          strokeWidth={2}
          dot={false}
        />
      </LineChart>
    </ResponsiveContainer>
  );
}
