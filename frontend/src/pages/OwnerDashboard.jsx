import { useState, useMemo } from "react";

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

  // TODO: replace with data fetched from backend in step‑2
  const sampleReservations = [
    {
      id: 1,
      petName: "Buddy",
      ownerName: "Alice Smith",
      phone: "555-1234",
      date: "2025-08-01",
      time: "14:00",
      species: "Dog",
      status: "Pending",
    },
    {
      id: 2,
      petName: "Mittens",
      ownerName: "Bob Lee",
      phone: "555-9876",
      date: "2025-08-02",
      time: "10:30",
      species: "Cat",
      status: "Pending",
    },
  ];

  const filteredReservations = useMemo(() => {
    return sampleReservations.filter((r) => {
      const phoneOk = filters.phone
        ? r.phone.toLowerCase().includes(filters.phone.toLowerCase())
        : true;
      const dateOk = filters.date ? r.date === filters.date : true;
      return phoneOk && dateOk;
    });
  }, [filters]);

  const handleChange = (e) =>
    setFilters((f) => ({ ...f, [e.target.name]: e.target.value.trim() }));

  const clearFilters = () => setFilters({ phone: "", date: "" });

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
                  <span className="inline-block rounded-full bg-yellow-100 px-2 py-0.5 text-xs font-semibold text-yellow-800">
                    {r.status}
                  </span>
                </td>
                <td className="px-4 py-2 text-center">
                  <button
                    disabled
                    className="inline-flex justify-center items-center px-3 py-1.5 border border-gray-300 rounded-lg text-xs font-medium text-gray-700 bg-white hover:bg-gray-50 disabled:opacity-50"
                  >
                    Complete
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
