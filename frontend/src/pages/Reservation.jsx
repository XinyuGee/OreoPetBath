import { useEffect, useState } from "react";
import { DayPicker } from "react-day-picker";
import "react-day-picker/dist/style.css";

/* ─── helpers ────────────────────────────────────────────────────────── */

const isoToLocalDate = (iso) => {
  const [y, m, d] = iso.split("-").map(Number);
  return new Date(y, m - 1, d);
};

const dayName = (input) => {
  const dateObj = input instanceof Date ? input : isoToLocalDate(input);
  return dateObj
    .toLocaleDateString("en-US", { weekday: "long" })
    .toUpperCase();
};

const formatLocalIsoDate = (date) => {
  const year  = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const day   = String(date.getDate()).padStart(2, "0");
  return `${year}-${month}-${day}`; 
};

const nextAllowedDate = (allowed) => {
  let d = new Date();
  d.setHours(12, 0, 0, 0);             
  for (let i = 0; i < 14; i++) {
    if (allowed.includes(dayName(d))) break;
    d.setDate(d.getDate() + 1);
  }
  return d;                            
}

const weekdayNameToIndex = {
  SUNDAY: 0,
  MONDAY: 1,
  TUESDAY: 2,
  WEDNESDAY: 3,
  THURSDAY: 4,
  FRIDAY: 5,
  SATURDAY: 6,
};

const generateTimeSlots = (start, end, stepMinutes = 30) => {
  if (!start || !end) return [];
  const [sh, sm] = start.split(":").map(Number);
  const [eh, em] = end.split(":").map(Number);
  const slots = [];
  for (let t = sh * 60 + sm; t <= eh * 60 + em; t += stepMinutes) {
    const hh = String(Math.floor(t / 60)).padStart(2, "0");
    const mm = String(t % 60).padStart(2, "0");
    slots.push(`${hh}:${mm}`);
  }
  return slots;
};

/**
 * Reservation page – now wired to the Spring‑Boot backend
 * ------------------------------------------------------
 * 1. Dropdown of services comes from GET /api/services
 * 2. On submit:
 *    • POST /api/pets            → returns pet.id
 *    • POST /api/reservations    (with petId, serviceId, reservationTime, notes)
 */
export default function Reservation() {
  const [formData, setFormData] = useState({
    owner: "",
    contact: "",
    date: "",
    time: "",
    name: "",
    species: "",
    breed: "",
    age: "",
    serviceId: "",
    notes: "",
  });
  const [services, setServices] = useState([]);
  const [loading, setLoading] = useState(false);
  const [msg, setMsg] = useState(null);
  const [rule, setRule] = useState(null);
  const [fieldErr, setFieldErr] = useState({ date: false, time: false });

  useEffect(() => {
    const norm = (s) => s.split(',').map(d => d.trim());
    fetch("/api/services")
      .then(r => r.ok ? r.json() : [])
      .then(arr => {
        arr.forEach(s => {
          s.allowedDays = norm(s.allowedDays ?? "MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY,SATURDAY,SUNDAY");
        });
        setServices(arr);
      })
      .catch(() => setServices([]));
  }, []);

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });

    if (e.target.name === "date" && rule) {
      const bad = !rule.allowedDays.includes(dayName(e.target.value));
      setFieldErr((f) => ({ ...f, date: bad }));
    }
    if (e.target.name === "time" && rule) {
      const bad =
        (rule.startTime && e.target.value < rule.startTime) ||
        (rule.endTime && e.target.value > rule.endTime);
      setFieldErr((f) => ({ ...f, time: bad }));
    }

    if (e.target.name === "serviceId") {
      const srv = services.find((s) => String(s.id) === e.target.value);
      setRule(srv ?? null);
      setFieldErr({ date: false, time: false });
      const next = srv ? nextAllowedDate(srv.allowedDays) : null;
      setFormData((f) => ({ ...f, date: next, time: "" }));
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setMsg(null);

    const required = ["date", "time", "serviceId", "owner", "contact", "name", "species"];
    if (required.some(k => !formData[k])) {
      setMsg({ type: "error", text: "Please fill in all required fields." });
      return;
    }

    setLoading(true);
    try {
      const petRes = await fetch("/api/pets", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          name: formData.name,
          species: formData.species,
          breed: formData.breed,
          age: Number(formData.age || 0),
          ownerName: formData.owner,
          ownerPhone: formData.contact,
        }),
      });
      if (!petRes.ok) throw new Error("Failed to save pet");
      const pet = await petRes.json();

      const reservationTime = `${formatLocalIsoDate(formData.date)}T${formData.time}`;
      const resRes = await fetch("/api/reservations", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          petId: pet.id,
          serviceId: Number(formData.serviceId),
          reservationTime,
          notes: formData.notes,
        }),
      });

      if (!resRes.ok) {
        const body = await resRes.json().catch(() => null);
        const niceMsg =
          body?.message ||
          (resRes.status === 409
            ? "The chosen slot overlaps an existing reservation."
            : `Reservation failed (HTTP ${resRes.status})`);
        throw new Error(niceMsg);
      }

      setMsg({ type: "success", text: "Reservation submitted! We will contact you soon 🐾" });
    } catch (err) {
      setMsg({ type: "error", text: err.message });
    } finally {
      setLoading(false);
    }
  };

  return (
    <section className="bg-[#fefaf6] min-h-screen py-12 px-4 font-cute text-[#4b3832]">
      <h2 className="text-4xl font-bold text-center mb-10">Reservation 预约登记</h2>

      <form
        onSubmit={handleSubmit}
        className="max-w-2xl mx-auto bg-white shadow-lg rounded-2xl p-8 space-y-6"
      >
        {/* Owner Info */}
        <div>
          <label className="block font-semibold mb-1">👤 Owner's Name 主人姓名</label>
          <input
            type="text"
            name="owner"
            value={formData.owner}
            onChange={handleChange}
            required
            className="w-full border rounded-md p-2"
            placeholder="e.g. Jane Doe"
          />
        </div>

        <div>
          <label className="block font-semibold mb-1">📞 Contact Number 联系方式</label>
          <input
            type="tel"
            name="contact"
            value={formData.contact}
            onChange={handleChange}
            required
            className="w-full border rounded-md p-2"
            placeholder="e.g. (123) 456-7890"
          />
        </div>

        <div>
          <label className="block font-semibold mb-1">🐾 Pet Name 宠物名字</label>
          <input
            type="text"
            name="name"
            value={formData.name}
            onChange={handleChange}
            required
            className="w-full border rounded-md p-2"
            placeholder="e.g. Oreo"
          />
        </div>

        <div className="flex flex-col md:flex-row gap-4">
          <div className="flex-1">
            <label className="block font-semibold mb-1">Species 物种</label>
            <input
              type="text"
              name="species"
              value={formData.species}
              onChange={handleChange}
              required
              className="w-full border rounded-md p-2"
              placeholder="e.g. Dog, Cat"
            />
          </div>
          <div className="flex-1">
            <label className="block font-semibold mb-1">Breed 品种</label>
            <input
              type="text"
              name="breed"
              value={formData.breed}
              onChange={handleChange}
              className="w-full border rounded-md p-2"
              placeholder="e.g. Poodle"
            />
          </div>
        </div>

        <div>
          <label className="block font-semibold mb-1">Age 年龄</label>
          <input
            type="number"
            name="age"
            value={formData.age}
            onChange={handleChange}
            className="w-full border rounded-md p-2"
            placeholder="e.g. 2"
          />
        </div>

        {/* Service selection */}
        <div>
          <label className="block font-semibold mb-1">🛁/🏠 Service 服务类型</label>
          <select
            name="serviceId"
            value={formData.serviceId}
            onChange={handleChange}
            required
            className="w-full border rounded-md p-2"
          >
            <option value="" disabled>Choose a service…</option>
            {services.map(s => (
              <option key={s.id} value={s.id}>{s.name} - {s.description}</option>
            ))}
          </select>
        </div>

        <div className="flex flex-col md:flex-row gap-4">
          <div className="flex-1">
            <label className="block font-semibold mb-1">
              📅 Reservation Date 预约日期
            </label>
            <div className="border rounded-md p-2 bg-white">
              <DayPicker
                mode="single"
                selected={formData.date || undefined}
                onSelect={(date) => {
                  if (!date) return;
                  const noon = new Date(date);
                  noon.setHours(12, 0, 0, 0);
                  const valid =
                    rule && rule.allowedDays.includes(dayName(noon));
                  setFormData((f) => ({
                    ...f,
                    date: valid ? noon : null,
                  }));
                  setFieldErr((f) => ({ ...f, date: !valid }));
                }}
                disabled={(d) => {
                  if (!rule) return true;
                  return !rule.allowedDays
                    .map((w) => weekdayNameToIndex[w])
                    .includes(d.getDay());
                }}
                fromDate={new Date()}
                toDate={
                  new Date(new Date().setDate(new Date().getDate() + 13))
                }
              />
            </div>
            {fieldErr.date && (
              <p className="text-red-600 mt-1 text-sm">
                Selected day is not available for this service.
              </p>
            )}
          </div>

        
          <div className="flex-1">
            <label className="block font-semibold mb-1">⏰ Time 时间</label>
            <select
              name="time"
              value={formData.time}
              onChange={handleChange}
              required
              className={`w-full border rounded-md p-2 ${fieldErr.time ? 'border-red-500' : ''}`}
            >
              <option value="">Select a time…</option>
              {rule && generateTimeSlots(rule.startTime, rule.endTime).map((t) => (
                <option key={t} value={t}>{t}</option>
              ))}
            </select>
          </div>
        </div>

        <div>
          <label className="block font-semibold mb-1">📝 Special Notes 备注</label>
          <textarea
            name="notes"
            value={formData.notes}
            onChange={handleChange}
            rows="4"
            className="w-full border rounded-md p-2"
            placeholder="e.g. Allergic to beef, gets anxious easily, need pickup service"
          />
        </div>

        <button
          type="submit"
          disabled={loading}
          className="w-full bg-[#d6cbb8] text-white font-bold py-2 px-4 rounded-lg hover:bg-[#bfae99] transition-all"
        >
          {loading ? "Submitting…" : "Submit Reservation 提交预约"}
        </button>

        {msg && (
          <p className={`text-center ${msg.type === "error" ? "text-red-600" : "text-green-600"}`}>{msg.text}</p>
        )}
        {(fieldErr.date || fieldErr.time) && (
          <p className="text-center text-red-600">
            * Selected date / time is outside the allowed range for this service
          </p>
        )}
      </form>
    </section>
  );
}
