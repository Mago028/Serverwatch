// src/api/server.ts
import api from "./client";

export interface ServerDto {
  id: number;
  name: string;
  host: string;
  createdAt: string;
}

export const getServers = async () => {
  const res = await api.get<ServerDto[]>("/api/servers");
  return res.data;
};
