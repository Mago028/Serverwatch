// src/App.tsx
import { Routes, Route, Navigate } from "react-router-dom";
import LoginPage from "./pages/LoginPage";
import ServerListPage from "./pages/ServerListPage";
import RegisterPage from "./pages/RegisterPage"; // or InlineRegisterPage 쓰면 요 줄 대신 그걸 넣기
import "./styles/global.css";
import ServerCreatePage from "./pages/ServerCreatePage";
import ServerDetailPage from "./pages/ServerDetailPage";

function App() {
  return (
    <div className="app-root">
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route path="/servers" element={<ServerListPage />} />
        <Route path="/servers/new" element={<ServerCreatePage />} />
        <Route path="/servers/:id" element={<ServerDetailPage />} />
        <Route path="*" element={<Navigate to="/login" replace />} />
      </Routes>
    </div>
  );
}

export default App;
