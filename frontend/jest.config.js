module.exports = {
  preset: 'react-scripts',
  transformIgnorePatterns: [
    "node_modules/(?!(axios)/)"
  ],
  setupFilesAfterEnv: ["<rootDir>/src/setupTests.js"]
};