export default function Pricing() {
  return (

    <div className="w-full">
    <section className="w-full h-[70vh] bg-cover bg-center" style={{ backgroundImage: "url('/PricingPage.png')" }}>
      <div className="w-full h-full bg-black/30 flex flex-col justify-center items-center text-white text-center px-4">
        <h1 className="text-5xl font-bold italic mb-4 drop-shadow-md">Pricings</h1>
      </div>
    </section>
    
    <section className="bg-[#fefaf6] py-12 px-4 font-cute text-[#4b3832]">
      <h2 className="text-4xl font-bold text-center mb-10">Pricing 价格表</h2>
      <div className="max-w-4xl mx-auto space-y-12">

        {/* Pet Grooming */}
        <div>
          <h3 className="text-2xl font-extrabold mb-4">✂️ Pet Grooming 宠物美容</h3>
          <ul className="space-y-2">
            <li className="flex justify-between border-b pb-1">
              <span>🐶 Dog Full Grooming (全套美容)</span>
              <span>$60+</span>
            </li>
            <li className="flex justify-between border-b pb-1">
              <span>🐱 Cat Bath (猫咪洗澡)</span>
              <span>$35+</span>
            </li>
          </ul>
        </div>

        {/* Pet Boarding */}
        <div>
          <h3 className="text-2xl font-extrabold mb-4">🏠 Pet Boarding 宠物寄养</h3>
          <ul className="space-y-2">
            <li className="flex justify-between border-b pb-1">
              <span>🐶 Small Dog (小型犬) Per Day</span>
              <span>$40</span>
            </li>
            <li className="flex justify-between border-b pb-1">
              <span>🐕 Medium Dog (中型犬) Per Day</span>
              <span>$50</span>
            </li>
            <li className="flex justify-between border-b pb-1">
              <span>🐕‍🦺 Large Dog (大型犬) Per Day</span>
              <span>$65</span>
            </li>
            <li className="flex justify-between border-b pb-1">
              <span>🐱 Cat (猫咪) Per Day</span>
              <span>$35</span>
            </li>
          </ul>
        </div>
        
        {/* Handmade Snacks */}
        <div>
          <h3 className="text-2xl font-extrabold mb-4">🍰 Handmade Snacks 手工零食</h3>
          <p className="text-sm italic mb-4 text-gray-600">
            All ingredients are human-grade. No additives. <br />
            所有食材均为人食用级别，无添加剂。<br />
          </p>
          <ul className="space-y-2">
            <li className="flex justify-between border-b pb-1">
              <span>🐓 Crispy Chicken Jerky 香酥鸡肉干</span>
              <span>70g / $12</span>
            </li>
            <li className="flex justify-between border-b pb-1">
              <span>🍪 Chicken, Veg & Goat Milk Cookies 鸡肉果蔬羊奶曲奇</span>
              <span>70g / $15</span>
            </li>
            <li className="flex justify-between border-b pb-1">
              <span>🍘 Okra Chicken Patties 秋葵鸡肉饼</span>
              <span>90g / $15</span>
            </li>
            <li className="flex justify-between border-b pb-1">
              <span>🥠 Chicken Chips 鸡肉薯片</span>
              <span>55g / $10</span>
            </li>
            <li className="flex justify-between border-b pb-1">
              <span>🍖 Chicken Chop 鸡肉小排</span>
              <span>100g / $18</span>
            </li>
            <li className="flex justify-between border-b pb-1">
              <span>🍗 Chicken Drumstick 小鸡腿</span>
              <span>70g / $18</span>
            </li>
            <li className="flex justify-between border-b pb-1">
              <span>🌼 Little Daisy Feta Biscuit 小雏菊羊奶酪饼干</span>
              <span>60g / $20</span>
            </li>
            <li className="flex justify-between border-b pb-1">
              <span>🐾 Corgi PP Cookies 柯基pp饼干</span>
              <span>8pcs / $10</span>
            </li>
            <li className="flex justify-between border-b pb-1">
              <span>🍎 Chicken with Apple 鸡肉绕苹果</span>
              <span>70g / $12</span>
            </li>
            <li className="flex justify-between border-b pb-1">
              <span>🧂 Roasted Sesame Chicken Heart 香烤芝麻鸡心</span>
              <span>85g / $15</span>
            </li>
            <li className="flex justify-between border-b pb-1">
              <span>🔥 Roasted Chicken Gizzard 香烤鸡胗</span>
              <span>80g / $15</span>
            </li>
          </ul>
        </div>

      </div>
    </section>
    </div>
  );
}
