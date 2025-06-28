import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';


// vite.config.js
export default defineConfig({
  plugins: [react()],
  server: {
    port: 3000,
    proxy: {
      '/api': {
	target: 'http://127.0.0.1:8000', // ← 여기!
 
	changeOrigin: true
      }
    },
    historyApiFallback: true
  }
});
