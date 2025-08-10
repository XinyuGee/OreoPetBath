import { useMemo, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { useAuth } from "../auth/useAuth";

export default function Login() {
  const [username, setU] = useState("");
  const [password, setP] = useState("");
  const [err, setErr]     = useState("");
  const { login } = useAuth();
  const nav = useNavigate();
  const loc = useLocation();

  const next = useMemo(() => {
    const p = new URLSearchParams(loc.search).get("next");
    return p || "/owner";
  }, [loc.search]);

  const submit = async (e) => {
    e.preventDefault(); setErr("");
    try {
      const res = await fetch("/api/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username, password })
      });
      if (!res.ok) {
        const msg = (await res.json().catch(()=>({message:"Invalid credentials"}))).message;
        setErr(msg); return;
      }
      const data = await res.json();
      if (data.role !== "OWNER") { setErr("Owner account required"); return; }
      login(data);
      nav(next, { replace: true });
    } catch {
      setErr("Network error");
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 p-6">
      <form onSubmit={submit} className="bg-white rounded-2xl shadow p-6 w-full max-w-sm space-y-4">
        <h1 className="text-2xl font-bold">Owner Sign in</h1>
        {err && <p className="text-sm text-red-600">{err}</p>}
        <div>
          <label className="text-sm">Username</label>
          <input className="border rounded w-full px-3 py-2"
                 value={username} onChange={e=>setU(e.target.value)} />
        </div>
        <div>
          <label className="text-sm">Password</label>
          <input type="password" className="border rounded w-full px-3 py-2"
                 value={password} onChange={e=>setP(e.target.value)} />
        </div>
        <button className="w-full bg-indigo-600 hover:bg-indigo-700 text-white rounded py-2">
          Login
        </button>
      </form>
    </div>
  );
}
