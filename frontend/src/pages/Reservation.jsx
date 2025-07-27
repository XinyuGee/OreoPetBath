import { useEffect, useState } from "react";

const dayName = (yyyyMmDd) => new Date(yyyyMmDd).toLocaleDateString("en-US",{weekday:"long"}).toUpperCase();
const nextAllowedDate = (allowed) => {
  let d = new Date();                      // today
  for (let i=0;i<14;i++){                  // look ahead max 2 wks
    if (allowed.includes(dayName(d.toISOString().slice(0,10)))) break;
    d.setDate(d.getDate()+1);
  }
  return d.toISOString().slice(0,10);
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
  /* ------------------------------ state ------------------------------ */
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
  const [services, setServices]   = useState([]);
  const [loading, setLoading]     = useState(false);
  const [msg, setMsg]             = useState(null);
  const [rule, setRule]           = useState(null);
  const [fieldErr, setFieldErr]   = useState({ date:false, time:false });

  /* ------------------------- fetch services ------------------------- */
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

  /* -------------------------- handlers ----------------------------- */
  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });

    if (e.target.name === "date" && rule) {
      const bad = !rule.allowedDays.includes(dayName(e.target.value));
      setFieldErr(f => ({ ...f, date: bad }));
    }
    if (e.target.name === "time" && rule) {
      const bad = (rule.startTime && e.target.value < rule.startTime) ||
                  (rule.endTime   && e.target.value > rule.endTime);
      setFieldErr(f => ({ ...f, time: bad }));
    }

    if (e.target.name === "serviceId") {
      const srv = services.find(s => String(s.id) === e.target.value);
      setRule(srv ?? null);
      setFieldErr({ date:false, time:false });
      setFormData(f => ({ ...f, date:"", time:"" }));
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setMsg(null);

    // basic client‑side required fields
    const required = ["date", "time", "serviceId", "owner", "contact", "name", "species"];
    if (required.some(k => !formData[k])) {
      setMsg({ type: "error", text: "Please fill in all required fields." });
      return;
    }

    setLoading(true);
    try {
      /* 1️⃣  Create / update pet */
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

      /* 2️⃣  Create reservation */
      const reservationTime = `${formData.date}T${formData.time}`; // yyyy‑MM‑ddTHH:mm
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
      // optional: reset form
      // setFormData({...initialState});
    } catch (err) {
      setMsg({ type: "error", text: err.message });
    } finally {
      setLoading(false);
    }
  };

  /* ----------------------------- UI ----------------------------- */
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

        {/* Date and Time */}
        <div className="flex flex-col md:flex-row gap-4">
          <div className="flex-1">
            <label className="block font-semibold mb-1">📅 Reservation Date 预约日期</label>
            <input
              type="date"
              name="date"
              value={formData.date}
              onChange={handleChange}
              required
              className={`w-full border rounded-md p-2 ${fieldErr.date?'border-red-500':''}`}
              min={rule ? nextAllowedDate(rule.allowedDays) : undefined}
              onBlur={(ev)=>{
                if(rule && !rule.allowedDays.includes(dayName(ev.target.value))){
                  setFormData(f=>({...f, date:""}));
                }
              }}
            />
          </div>
          <div className="flex-1">
            <label className="block font-semibold mb-1">⏰ Time 时间</label>
            <input
              type="time"
              name="time"
              value={formData.time}
              onChange={handleChange}
              required
              className={`w-full border rounded-md p-2 ${fieldErr.time?'border-red-500':''}`}
              min={rule?.startTime}
              max={rule?.endTime}
              step="1800"
            />
          </div>
        </div>

        {/* Pet Info */}
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
