import ServiceCard from '../components/ServiceCard';

export default function Services() {
  return (
    <div className="w-full">
    <section className="w-full h-[70vh] bg-cover bg-center" style={{ backgroundImage: "url('/ServicePage.png')" }}>
      <div className="w-full h-full bg-black/30 flex flex-col justify-center items-center text-white text-center px-4">
        <h1 className="text-5xl font-bold italic mb-4 drop-shadow-md">Services Offered</h1>
      </div>
    </section>

    <section className="bg-[#f9f9f9] py-12 px-4">
      <h2 className="text-4xl font-bold text-center mb-10 font-cute">Our Services 服务项目</h2>
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6 max-w-6xl mx-auto">
        <ServiceCard
          icon="✂️"
          title="Pet Grooming"
          subtitle="宠物美容"
          description={'Dog 🐶: Bath 洗澡 / Full Grooming 美发美容\nCat 🐱: Bath 洗澡 / Grooming 美容'}
        />
        <ServiceCard
          icon="🐕"
          title="Pet Daycare"
          subtitle="当日托管"
          description={'我们提供当日托管服务，在您繁忙时照看小宝贝 ❤️\n\nWe provide daycare service when you are busy.'}
        />
        <ServiceCard
          icon="🏠"
          title="Pet Boarding"
          subtitle="宠物寄养"
          description={'我们提供宠物寄养服务，在您旅行时为宠物提供舒适环境 🏡\n\nWe provide boarding when you are traveling.'}
        />
        <ServiceCard
          icon="🍰"
          title="Pet Foods"
          subtitle="宠物零食"
          description={'我们提供手工制作宠物零食，纯天然不含添加剂🥩\n\nWe offer handmade pet snacks with no artificial additive.'}
        />
        <ServiceCard
          icon="🛍️"
          title="Pet Retails"
          subtitle="宠物商店"
          description={'提供宠物零食、玩具及日用品 🛒\n\nWe provide pet food, toys, and supplies.'}
        />
        <ServiceCard
          icon="🚗"
          title="Pet Pick Ups/Drop Offs"
          subtitle="宠物接送"
          description={'我们有提供宠物接送服务，不用担心没有时间接送您的毛孩子 ⌚️\n\nWe provide pickup and dropoff service for pet, don\'t worry about not having time.'}
        />
      </div>
    </section>
  </div>
  );
}
