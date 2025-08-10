import { useState, useMemo, useEffect, useCallback, useRef } from "react";

/**
 * OwnerDashboard 
 * @todo: Ajust the refresh of the page when new data is in
 * @todo: Add in a user login for security
 */

const AUTO_REFRESH_TIME = 10_000;
export default function OwnerDashboard() {
  const [filters, setFilters] = useState({ phone: "", date: "" });
  const [reservations, setReservations] = useState([]);
  const [selectedNote, setSelectedNote] = useState(null);
  const setLastUpdated = useState(null);
  const [sortKeys, setSortKeys] = useState([{ key: "date", dir: "asc" }, { key: "time", dir: "asc" }, { key: "status", dir: "asc" }]);
  const fetching = useRef(false);

  const fetchReservations = useCallback(async() => {
    if (fetching.current) return;
    fetching.current = true;
    try{
      const res = await fetch(`/api/reservations/dashboard?ts=${Date.now()}`, { cache: "no-store" });
      const data = await res.json();
      setReservations(data);
      setLastUpdated(new Date());
    }catch (e) {
      console.error(e);
    }finally {
      fetching.current = false;
    }
  }, []);

  useEffect(fetchReservations, [fetchReservations]);

  useEffect(() => {
    const tick = () => {
      if (!document.hidden) fetchReservations();
    };
    const id = setInterval(tick, AUTO_REFRESH_TIME);
    return () => clearInterval(id);
  }, [fetchReservations]);
    

  const filteredReservations = useMemo(() => {
    const filt = reservations.filter((r) => {
      const phoneOk = filters.phone
        ? r.phone.toLowerCase().includes(filters.phone.toLowerCase())
        : true;
      const dateOk = filters.date ? r.date === filters.date : true;
      return phoneOk && dateOk;
    });
    const cmp = (v1, v2) => v1 === v2
            ? 0 : typeof v1 === "string"
            ? v1.localeCompare(v2) : v1 > v2
            ? 1 : -1;
    const compare = (a, b) => {
      for (const { key, dir } of sortKeys) {
        const res = cmp(a[key], b[key]);
        if (res !== 0) return dir === "asc" ? res : -res;
      }
      return 0;
    };
    return [...filt].sort(compare);
  }, [filters, reservations, sortKeys]);

  const handleChange = (e) =>
    setFilters((f) => ({ ...f, [e.target.name]: e.target.value.trim() }));

  const handleSort = (key) => {
    setSortKeys((prev) =>
      prev.map((s) =>
        s.key === key ? { ...s, dir: s.dir === "asc" ? "desc" : "asc" } : s
      )
    );
  };

  const arrow = (key) => {
    const s = sortKeys.find((k) => k.key === key);
    return s ? (s.dir === "asc" ? "▲" : "▼") : "";
  };

  const clearFilters = () => setFilters({ phone: "", date: "" });

  const STATUS_COLORS = {
    BOOKED:   "bg-blue-100  text-blue-800",
    CANCELED: "bg-yellow-100 text-yellow-800",
    COMPLETED: "bg-green-100 text-green-800",
  };

  const markComplete = async(id) =>{
    setReservations((prev) =>
      prev.map((r) => (r.id === id ? { ...r, status: "COMPLETED" } : r))
    );
    try{
      const res = await fetch(`/api/reservations/${id}/complete`, { method: "PATCH" });
      if (!res.ok) throw new Error("This Reservation has already been 'Completed'");
      await res.json();
    } catch (err) {
      console.error(err);
    }finally {
      fetchReservations();
    }

  }

  return (
    <div className="p-6 md:p-10 space-y-6">
      <h2 className="text-3xl md:text-4xl font-bold tracking-tight">
        预约面板
      </h2>

      {/* ───────── Filter Panel ───────── */}
      <div className="bg-white shadow-lg rounded-2xl p-6 grid gap-4 md:grid-cols-[repeat(auto-fit,minmax(220px,1fr))] items-end">
        <div className="flex flex-col gap-1">
          <label htmlFor="phone" className="text-sm font-medium">
            电话#
          </label>
          <input
            id="phone"
            name="phone"
            placeholder="e.g. 555-1234"
            value={filters.phone}
            onChange={handleChange}
            className="border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-500"
          />
        </div>

        <div className="flex flex-col gap-1">
          <label htmlFor="date" className="text-sm font-medium">
            日期
          </label>
          <input
            id="date"
            name="date"
            type="date"
            value={filters.date}
            onChange={handleChange}
            className="border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-500"
          />
        </div>

        <button
          onClick={clearFilters}
          className="mt-4 md:mt-0 inline-flex justify-center items-center px-4 py-2 border border-gray-300 rounded-lg text-sm font-medium text-gray-700 bg-white hover:bg-gray-50"
        >
          清除筛选
        </button>
      </div>

      {/* ───────── Reservations Table ───────── */}
      <div className="bg-white shadow-lg rounded-2xl overflow-x-auto">
        <table className="min-w-full text-sm">
          <thead className="bg-gray-100 text-left text-xs uppercase tracking-wider select-none">
            <tr>
              <th className="px-4 py-3">宠物名字</th>
              <th className="px-4 py-3">主人名字</th>
              <th className="px-4 py-3">电话号码</th>
              <th className="px-4 py-3 cursor-pointer select-none"
                onClick={() => handleSort("date")}>
                  预约日期 {arrow("date")}
              </th>
              <th className="px-4 py-3 cursor-pointer select-none" 
                onClick={() => handleSort("time")}>
                预约时间 {arrow("time")}
              </th>
              <th className="px-4 py-3 cursor-pointer select-none" 
                onClick={() => handleSort("status")}>
                预约状态 {arrow("status")}
                </th>
              <th className="px-4 py-3">宠物品种</th>
              <th className="px-4 py-3">预约服务</th>
              <th className="px-4 py-3">特殊提醒</th>
              <th className="px-4 py-3 text-center">完成</th>
            </tr>
          </thead>
          <tbody>
            {filteredReservations.map((r, idx) => (
              <tr key={r.id} className={idx % 2 === 0 ? "bg-white" : "bg-gray-50"}>
                <td className="px-4 py-2 whitespace-nowrap font-medium">{r.petName}</td>
                <td className="px-4 py-2 whitespace-nowrap">{r.ownerName}</td>
                <td className="px-4 py-2 whitespace-nowrap">{r.phone}</td>
                <td className="px-4 py-2 whitespace-nowrap">{r.date}</td>
                <td className="px-4 py-2 whitespace-nowrap">{r.time}</td>
                <td className="px-4 py-2 whitespace-nowrap">
                  <span className={`inline-block rounded-full px-2 py-0.5 text-xs font-semibold text-yellow-800 ` 
                    + (STATUS_COLORS[r.status] ?? "bg-gray-100 text-gray-800")}>
                    {r.status}
                  </span>
                </td>
                <td className="px-4 py-2 whitespace-nowrap">{r.species}</td>
                <td className="px-4 py-2 whitespace-nowrap">{r.service}</td>
                <td className="px-4 py-2 whitespace-nowrap">{r.notes ? (
                  <button
                    onClick={() => setSelectedNote(r.notes)}
                   className="text-grey-400 border-0 bg-transparent hover:underline"
                  >
                  {r.notes.length > 24 ? r.notes.slice(0, 24) + "…" : r.notes}
                  </button>
                ): <span className="text-gray-400">—</span>}
                </td>
                <td className="px-4 py-2 text-center">
                {r.status !== "COMPLETED" && r.status !== "CANCELED" && (
                  <button
                    onClick={() => markComplete(r.id) }
                    className="rounded-md bg-green-500 px-3 py-1 text-sm font-medium text-white border-0 hover:bg-green-600"
                  >
                    完成
                  </button>
                )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
      {selectedNote && (
      <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40">
        <div
          className="
            bg-white w-[min(90%,28rem)] max-h-[85vh] rounded-xl shadow-xl p-6 flex flex-col"
        >
          <h3 className="text-lg font-semibold">特殊提醒</h3>
          <div className="mt-3 mb-4 flex-1 overflow-y-auto">
            <p className="whitespace-pre-line break-words">{selectedNote}</p>
          </div>
          <button
            onClick={() => setSelectedNote(null)}
            className="self-end inline-flex items-center px-4 py-1.5
                      rounded-lg bg-red-500 text-white text-sm
                      hover:bg-red-700"
          >
            关闭
          </button>
        </div>
      </div>
    )}
    </div>
  );
}
