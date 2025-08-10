import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import Header from './components/Header';
import Footer from './components/Footer';
import Home from './pages/Home';
import Services from './pages/Services';
import Pricing from './pages/Pricing';
import Gallery from './pages/Gallery';
import Reservation from './pages/Reservation';
import OwnerDashboard from './pages/OwnerDashboard';
import Login from './pages/Login'
import RoleRoute from "./routes/RoleRoute";
import { AuthProvider } from "./auth/AuthContext";

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <div className="min-h-screen bg-white flex flex-col">
          <Header />
          <main className="flex-grow">
            <Routes>
              <Route path="/" element={<Home />} />
              <Route path="/services" element={<Services />} />
              <Route path="/pricing" element={<Pricing />} />
              <Route path="/gallery" element={<Gallery />} />
              <Route path="/reservation" element={<Reservation />} />
              <Route path="/login" element={<Login />} />
              <Route path="/owner" element={<RoleRoute roles={["OWNER"]}> <OwnerDashboard /> </RoleRoute>} />
              <Route path="*" element={<Navigate to="/" replace />} />
            </Routes>
          </main>
          <Footer />
        </div>
      </AuthProvider>
    </BrowserRouter>
  );
}
