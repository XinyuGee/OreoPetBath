import { Navigate, useLocation } from "react-router-dom";

export default function RoleRoute({ roles = [], children }) {
  const loc = useLocation();
  const raw = localStorage.getItem("user");
  const user = raw ? JSON.parse(raw) : null;

  if (!user) {
    const next = encodeURIComponent(loc.pathname + loc.search);
    return <Navigate to={`/login?next=${next}`} replace />;
  }
  return roles.includes(user.role) ? children : <Navigate to="/login" replace />;
}
