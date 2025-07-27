export default function Home() {
  return (
    <div className="w-full">
      <section className="w-full h-[70vh] bg-cover bg-center" style={{ backgroundImage: "url('/HomePage.png')" }}>
        <div className="w-full h-full bg-black/30 flex flex-col justify-center items-center text-white text-center px-4">
          <h1 className="text-5xl font-bold italic mb-4 drop-shadow-md">Welcome to Oreo Pet Bath & Care 🐾</h1>
          <p className="text-xl px-4 py-2 rounded">
          We offer top-tier grooming, bathing, and pet care for your furry friends!
          </p>
        </div>
      </section>

      <section className="bg-white py-8 px-6 text-center font-cute font-bold text-[#4b3832]">
        <h2 className="text-3xl font-bold text-gray-800 mb-4">About Us</h2>
        <p className="text-lg text-gray-700 max-w-3xl mx-auto">
          At <span className="italic">Oreo Pet Bath & Care</span>, we offer professional grooming services with a loving touch. 
          We also carry high-quality pet food, toys, grooming tools, and everything your pet needs to stay happy, healthy, and adorable.
          Because every pet deserves the best.
          <br /><br />
            在这里，我们提供专业又贴心的美容服务。<br />
            无论是泡泡浴、清爽修剪，还是可爱造型，我们都用满满的爱呵护您的毛孩子! <br />
            我们还提供高品质的宠物粮食、玩具、美容工具，以及各种让宠物开心健康的用品。<br />
            因为每一只宠物，都值得拥有最好的关爱 💖
        </p>
      </section>
    </div>
  );
}