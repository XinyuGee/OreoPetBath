import { useState } from 'react';

export default function Reservation() {
  const [formData, setFormData] = useState({
    owner: '',
    contact: '',
    date: '',
    time: '',
    name: '',
    species: '',
    breed: '',
    age: '',
    notes: '',
  });

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    console.log('Reservation Submitted:', formData);
    alert('Reservation submitted! We will contact you soon 🐾');
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
              className="w-full border rounded-md p-2"
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
              className="w-full border rounded-md p-2"
            />
          </div>
        </div>

        {/* Pet Info */}
        <div>
          <label className="block font-semibold mb-1"> Pet Name 宠物名字</label>
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

        <div>
          <label className="block font-semibold mb-1">📝 Special Notes 备注</label>
          <textarea
            name="notes"
            value={formData.notes}
            onChange={handleChange}
            rows="4"
            className="w-full border rounded-md p-2"
            placeholder="e.g. Allergic to beef, gets anxious easily"
          />
        </div>

        <button
          type="submit"
          className="w-full bg-[#d6cbb8] text-white font-bold py-2 px-4 rounded-lg hover:bg-[#bfae99] transition-all"
        >
          Submit Reservation 提交预约
        </button>
      </form>
    </section>
  );
}
