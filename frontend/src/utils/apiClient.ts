import axios from 'axios';

export const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080/api/v1',
  headers: {
    'Content-Type': 'application/json',
  },
  // La inclusión de credenciales es esencial para que CORS funcione seguro si en un futuro hay auth
  withCredentials: true,
});

// Interceptor base para manejar respuestas y errores globalmente si fuera necesario
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    console.error('API Error:', error.response?.data || error.message);
    return Promise.reject(error);
  }
);
