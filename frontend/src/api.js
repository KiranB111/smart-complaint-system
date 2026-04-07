import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080/api"
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem("pv_access_token");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export const authApi = {
  login: (payload) => api.post("/auth/login", payload),
  register: (payload) => api.post("/auth/register", payload),
  refresh: (refreshToken) => api.post("/auth/refresh", { refreshToken })
};

export const userApi = {
  me: () => api.get("/users/me")
};

export const complaintApi = {
  list: (params) => api.get("/complaints", { params }),
  create: (payload) => api.post("/complaints", payload),
  update: (id, payload) => api.put(`/complaints/${id}`, payload),
  remove: (id) => api.delete(`/complaints/${id}`)
};

export const analyticsApi = {
  reports: () => api.get("/analytics/reports"),
  exportPdf: () => api.get("/analytics/export/pdf", { responseType: "blob" }),
  exportExcel: () => api.get("/analytics/export/excel", { responseType: "blob" })
};

export default api;
