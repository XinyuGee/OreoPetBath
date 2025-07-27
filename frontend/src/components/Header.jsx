import { Link } from 'react-router-dom';

export default function Header() {
  return (
    <header className="bg-[#d6cbb8] text-white px-6 py-8 flex justify-between items-center shadow">
      <h1 className="text-3xl font-bold italic">Oreo Pet Bath & Care</h1>
      <nav className="space-x-4">
        <Link to="/" className="text-white text-xl font-semibold px-4 py-2 hover:text-black">Home</Link>
        <Link to="/services" className="text-white text-xl font-semibold px-4 py-2 hover:text-black">Service</Link>
        <Link to="/pricing" className="text-white text-xl font-semibold px-4 py-2 hover:text-black">Pricing</Link>
        <Link to="/gallery" className="text-white text-xl font-semibold px-4 py-2 hover:text-black">Gallery</Link>
        <Link to="/reservation" className="text-white text-xl font-semibold px-4 py-2 hover:text-black">Reservation</Link>
      </nav>
    </header>
  );
}
