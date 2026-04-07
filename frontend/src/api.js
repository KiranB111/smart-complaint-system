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
  me: () => api.get("/users/me"),
  officers: () => api.get("/users/officers"),
  updateAvailability: (availability) => api.put("/users/me/availability", { availability }),
  createOfficer: (payload) => api.post("/users/officers", payload),
  updateOfficer: (id, payload) => api.put(`/users/officers/${id}`, payload),
  setOfficerActive: (id, active) => api.put(`/users/officers/${id}/active`, { active })
};

export const complaintApi = {
  list: (params) => api.get("/complaints", { params }),
  create: (payload) => api.post("/complaints", payload),
  update: (id, payload) => api.put(`/complaints/${id}`, payload),
  assign: (id, officerId) => api.put(`/complaints/${id}/assign`, { officerId }),
  autoAssign: (id) => api.put(`/complaints/${id}/auto-assign`),
  confirm: (id, payload) => api.put(`/complaints/${id}/citizen-confirmation`, payload),
  uploadAttachment: (id, file) => {
    const form = new FormData();
    form.append("file", file);
    return api.post(`/complaints/${id}/attachments`, form, {
      headers: { "Content-Type": "multipart/form-data" }
    });
  },
  remove: (id) => api.delete(`/complaints/${id}`)
};

export const analyticsApi = {
  reports: () => api.get("/analytics/reports"),
  exportPdf: () => api.get("/analytics/export/pdf", { responseType: "blob" }),
  exportExcel: () => api.get("/analytics/export/excel", { responseType: "blob" })
};

export const notificationApi = {
  list: () => api.get("/notifications"),
  markRead: (id) => api.put(`/notifications/${id}/read`)
};

export default api;
