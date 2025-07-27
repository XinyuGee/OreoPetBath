/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./index.html",
    "./src/**/*.{js,jsx}"
  ],
  theme: { 
    extend: {
      fontFamily: {
        cute: ['"Fredoka"', 'sans-serif'],
      },
    } 
  },
  plugins: [],
};