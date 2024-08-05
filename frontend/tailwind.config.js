/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/**/*.{html,ts}",
  ],
  theme: {
    extend: {
      fontFamily: {
        custom: ["apercu", "Verdana"],
      },
    },
    colors: {
      baseWhite: "#F8F8F8",
      lightGray: "#EDEDED",
      mediumGray: "#B0B0B0",
      darkGray: "#383838",
      baseBlack: "#222222",
      baseRed: "#FF535B",
    },
  },
  plugins: [],
}

