import { createContext, useState } from "react";

export const AuthCtx = createContext(null);

export function AuthProvider({ children }) {
  const [token, setToken] = useState(() => localStorage.getItem("jwt") || "");
  const [user, setUser] = useState(() => {
    const raw = localStorage.getItem("user");
    return raw ? JSON.parse(raw) : null;
  });

  const login = ({ token, username, role }) => {
    setToken(token);
    setUser({ username, role });
    localStorage.setItem("jwt", token);
    localStorage.setItem("user", JSON.stringify({ username, role }));
  };

  const logout = () => {
    setToken("");
    setUser(null);
    localStorage.removeItem("jwt");
    localStorage.removeItem("user");
  };

  return (
    <AuthCtx.Provider value={{ token, user, login, logout }}>
      {children}
    </AuthCtx.Provider>
  );
}
