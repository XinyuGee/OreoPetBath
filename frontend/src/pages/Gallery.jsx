import galleryImages from '../lib/galleryData';

export default function Gallery() {
  return (
    <div className="w-full">
      <section className="w-full h-[70vh] bg-cover bg-center" style={{ backgroundImage: "url('/GalleryPage.png')" }}>
        <div className="w-full h-full bg-black/30 flex flex-col justify-center items-center text-white text-center px-4">
          <h1 className="text-5xl font-bold italic mb-4 drop-shadow-md">Gallery</h1>
        </div>
      </section>
      
      <section className="bg-[#fefaf6] py-12 px-4 min-h-screen">
        <h2 className="text-4xl font-bold text-center mb-10 font-cute">Gallery 图集</h2>
        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-6 max-w-6xl mx-auto">
          {galleryImages.map((img, index) => (
            <div key={index} className="rounded-lg shadow hover:shadow-xl overflow-hidden transition-all">
              <img
                src={img.src}
                alt={img.alt}
                className="w-full h-64 object-cover"
              />
            </div>
          ))}
        </div>
      </section>
    </div>
  );
}
