import { useState, useMemo, useEffect, useCallback } from "react";

/**
 * OwnerDashboard – step‑1 UI scaffold (plain Tailwind, no external icons)
 * -------------------------------------------------------------------
 * ‣ Displays filter controls (phone number & date).
 * ‣ Shows a read‑only table of reservations with a placeholder
 *   "Complete" button per row.
 * ‣ Absolutely zero third‑party UI libraries – just React + Tailwind.
 */
export default function OwnerDashboard() {
  const [filters, setFilters] = useState({ phone: "", date: "" });
  const [reservations, setReservations] = useState([]);
  const [selectedNote, setSelectedNote] = useState(null);

  const fetchReservations = useCallback(() => {
        fetch(`/api/reservations/dashboard?ts=${Date.now()}`, { cache: "no-store" })
          .then((r) => r.json())
          .then(setReservations)
          .catch(console.error);
      }, []);

  useEffect(fetchReservations, [fetchReservations]);

  const filteredReservations = useMemo(() => {
    return reservations.filter((r) => {
      const phoneOk = filters.phone
        ? r.phone.toLowerCase().includes(filters.phone.toLowerCase())
        : true;
      const dateOk = filters.date ? r.date === filters.date : true;
      return phoneOk && dateOk;
    });
  }, [filters, reservations]);

  const handleChange = (e) =>
    setFilters((f) => ({ ...f, [e.target.name]: e.target.value.trim() }));

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
        Reservation Dashboard
      </h2>

      {/* ───────── Filter Panel ───────── */}
      <div className="bg-white shadow-lg rounded-2xl p-6 grid gap-4 md:grid-cols-[repeat(auto-fit,minmax(220px,1fr))] items-end">
        <div className="flex flex-col gap-1">
          <label htmlFor="phone" className="text-sm font-medium">
            Phone #
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
            Date
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
          Clear Filters
        </button>
      </div>

      {/* ───────── Reservations Table ───────── */}
      <div className="bg-white shadow-lg rounded-2xl overflow-x-auto">
        <table className="min-w-full text-sm">
          <thead className="bg-gray-100 text-left text-xs uppercase tracking-wider select-none">
            <tr>
              <th className="px-4 py-3">Pet Name</th>
              <th className="px-4 py-3">Owner Name</th>
              <th className="px-4 py-3">Phone</th>
              <th className="px-4 py-3">Date</th>
              <th className="px-4 py-3">Time</th>
              <th className="px-4 py-3">Species</th>
              <th className="px-4 py-3">Status</th>
              <th className="px-4 py-3">Notes</th>
              <th className="px-4 py-3 text-center">Actions</th>
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
                <td className="px-4 py-2 whitespace-nowrap">{r.species}</td>
                <td className="px-4 py-2 whitespace-nowrap">
                  <span className={`inline-block rounded-full px-2 py-0.5 text-xs font-semibold text-yellow-800 ` 
                    + (STATUS_COLORS[r.status] ?? "bg-gray-100 text-gray-800")}>
                    {r.status}
                  </span>
                </td>
                <td className="px-4 py-2 whitespace-nowrap">{r.notes ? (
                  <button
                    onClick={() => setSelectedNote(r.notes)}
                   className="text-grey-300 border-0 hover:underline"
                  >
                  {r.notes.length > 24 ? r.notes.slice(0, 24) + "…" : r.notes}
                  </button>
                ): <span className="text-gray-400">—</span>}
                </td>
                <td className="px-4 py-2 text-center">
                {r.status !== "COMPLETE" && (
                  <button
                    onClick={() => markComplete(r.id) }
                    className="rounded-md bg-green-500 px-3 py-1 text-sm font-medium text-white border-0 hover:bg-green-600"
                  >
                    Complete
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
          <h3 className="text-lg font-semibold">Special Notes</h3>
          <div className="mt-3 mb-4 flex-1 overflow-y-auto">
            <p className="whitespace-pre-line break-words">{selectedNote}</p>
          </div>
          <button
            onClick={() => setSelectedNote(null)}
            className="self-end inline-flex items-center px-4 py-1.5
                      rounded-lg bg-red-500 text-white text-sm
                      hover:bg-red-700"
          >
            Close
          </button>
        </div>
      </div>
    )}
    </div>
  );
}
