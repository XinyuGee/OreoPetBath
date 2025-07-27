import { defineConfig, loadEnv } from 'vite'
import react from '@vitejs/plugin-react'

export default ({ mode }) => {
  // pulls values from .env, .env.local OR Docker-injected env-vars
  const env = loadEnv(mode, process.cwd(), '');

  return defineConfig({
    plugins: [react()],
    server: {
      host: true,
      port: 5173,
      proxy: {
        '/api': {
          target: env.VITE_BACKEND_URL || 'http://backend:8080',
          changeOrigin: true,
        },
      },
    },

    /* ↓ Optional safety-net if you still have process.env somewhere */
    define: { 'process.env': {} },
  });
};