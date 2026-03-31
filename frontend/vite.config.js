import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    port: 3000,
    proxy: {
      // 代理所有API请求到后端
      '/users': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/RAG': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})