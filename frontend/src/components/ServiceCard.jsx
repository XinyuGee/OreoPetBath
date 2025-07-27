export default function ServiceCard({ icon, title, subtitle, description }) {
  return (
    <div className="bg-white shadow-md rounded-2xl p-6 text-center hover:shadow-xl transition-all space-y-3">
      <div className="text-4xl">{icon}</div>
      <h3 className="text-xl font-bold">{title}</h3>
      <h4 className="text-lg font-semibold text-gray-700">{subtitle}</h4>
      <p className="text-sm leading-relaxed text-gray-600 whitespace-pre-line">
        {description}
      </p>
    </div>
  );
}
