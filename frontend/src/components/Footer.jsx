export default function Footer() {
  return (
    <footer className="bg-[#d6cbb8] text-white text-center py-6 mt-12 shadow-inner">
      <div className="max-w-4xl mx-auto px-4 space-y-2 font-cute">
        <p className="text-lg font-bold">📞 联系方式 / Contact: (646) 339-5088</p>
        <p className="text-lg font-bold">
          📍 地址 / Location: {" "}
            <a
              href="https://www.google.com/maps/place/Oreo.Pet+Bath%26Care/@40.7598833,-73.8022749,17z/data=!4m15!1m8!3m7!1s0x89c2602dc26b3647:0x4b24f4e3ed61ba38!2s166-13+Northern+Blvd,+Flushing,+NY+11358!3b1!8m2!3d40.7598793!4d-73.7997!16s%2Fg%2F11fm_s2tmv!3m5!1s0x89c261081d49432d:0x62fcf7678d956607!8m2!3d40.7598793!4d-73.7997!16s%2Fg%2F11xm0f16s9?entry=ttu&g_ep=EgoyMDI1MDcyMy4wIKXMDSoASAFQAw%3D%3D"
              target="_blank"
              rel="noopener noreferrer"
              className="underline text-lg font-bold text-white hover:text-yellow-200"
            >
              166-13 Northern Blvd, Flushing, NY 11358
            </a>
          </p>
        <p className="text-lg font-bold">⏰ 营业时间 / Open: Mon-Sun, 10:00 AM - 7:00 PM</p>
        <p className="text-sm italic mt-4">© 2025 Oreo Pet Bath & Care. All rights reserved.</p>
      </div>
    </footer>
  );
}
