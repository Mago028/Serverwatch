// src/api/metrics.ts
import api from "./client";

export interface MetricPoint {
  timestamp: string;
  cpu: number;
  memory: number;
  disk: number;
  loadAvg: number;
}

export async function getMetrics(range = "10m"): Promise<MetricPoint[]> {
  const res = await api.get<MetricPoint[]>("/api/servers/metrics", {
    params: { range },
  });
  return res.data;
}
