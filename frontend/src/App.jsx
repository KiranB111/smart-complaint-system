import { Navigate, Route, Routes } from "react-router-dom";
import { useEffect, useMemo, useState } from "react";
import { analyticsApi, authApi, complaintApi, userApi } from "./api";
import {
  AppLayout,
  AuthCard,
  ComplaintForm,
  ComplaintTable,
  DashboardCards,
  HeroPanel,
  SectionCard
} from "./components";

const emptyComplaint = {
  title: "",
  description: "",
  category: "Sanitation",
  location: "",
  latitude: 0,
  longitude: 0,
  locality: "Bengaluru Central",
  status: "OPEN",
  assignedOfficerId: null
};

function useSession() {
  const [user, setUser] = useState(() => {
    const raw = localStorage.getItem("pv_user");
    return raw ? JSON.parse(raw) : null;
  });

  const saveSession = (response) => {
    localStorage.setItem("pv_access_token", response.accessToken);
    localStorage.setItem("pv_refresh_token", response.refreshToken);
    localStorage.setItem("pv_user", JSON.stringify(response.user));
    setUser(response.user);
  };

  const clearSession = () => {
    localStorage.removeItem("pv_access_token");
    localStorage.removeItem("pv_refresh_token");
    localStorage.removeItem("pv_user");
    setUser(null);
  };

  return { user, saveSession, clearSession };
}

function LoginPage({ onLogin }) {
  const [form, setForm] = useState({ email: "citizen@peoplevoice.local", password: "password" });
  const [error, setError] = useState("");

  const submit = async (event) => {
    event.preventDefault();
    try {
      const { data } = await authApi.login(form);
      onLogin(data);
    } catch (err) {
      setError(err.response?.data?.message || "Unable to sign in");
    }
  };

  return (
    <HeroPanel>
      <AuthCard
        title="Secure civic grievance access"
        subtitle="Citizens, officers, and admins sign in to a role-based workspace with JWT security."
        error={error}
        submitLabel="Login"
        onSubmit={submit}
      >
        <input className="form-control" placeholder="Email" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} />
        <input className="form-control" type="password" placeholder="Password" value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} />
      </AuthCard>
    </HeroPanel>
  );
}

function RegisterPage({ onRegister }) {
  const [form, setForm] = useState({
    name: "",
    email: "",
    password: "",
    phone: "",
    role: "CITIZEN"
  });
  const [error, setError] = useState("");

  const submit = async (event) => {
    event.preventDefault();
    try {
      const { data } = await authApi.register(form);
      onRegister(data);
    } catch (err) {
      setError(err.response?.data?.message || "Unable to register");
    }
  };

  return (
    <HeroPanel>
      <AuthCard
        title="Create a People Voice account"
        subtitle="Register as a citizen by default, or seed officer/admin access for demos."
        error={error}
        submitLabel="Register"
        onSubmit={submit}
      >
        <input className="form-control" placeholder="Name" value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} />
        <input className="form-control" placeholder="Email" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} />
        <input className="form-control" type="password" placeholder="Password" value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} />
        <input className="form-control" placeholder="Phone" value={form.phone} onChange={(e) => setForm({ ...form, phone: e.target.value })} />
        <select className="form-select" value={form.role} onChange={(e) => setForm({ ...form, role: e.target.value })}>
          <option value="CITIZEN">Citizen</option>
          <option value="OFFICER">Officer</option>
          <option value="ADMIN">Admin</option>
        </select>
      </AuthCard>
    </HeroPanel>
  );
}

function Dashboard({ user, onLogout }) {
  const [complaints, setComplaints] = useState([]);
  const [analytics, setAnalytics] = useState(null);
  const [form, setForm] = useState(emptyComplaint);
  const [message, setMessage] = useState("");

  const loadComplaints = async () => {
    const { data } = await complaintApi.list();
    setComplaints(data);
  };

  const loadAnalytics = async () => {
    if (user.role === "CITIZEN") return;
    const { data } = await analyticsApi.reports();
    setAnalytics(data);
  };

  useEffect(() => {
    userApi.me().catch(() => undefined);
    loadComplaints();
    loadAnalytics();
    const timer = setInterval(loadComplaints, 12000);
    return () => clearInterval(timer);
  }, [user.role]);

  const complaintCards = useMemo(() => {
    const total = complaints.length;
    const open = complaints.filter((item) => item.status === "OPEN").length;
    const inProgress = complaints.filter((item) => item.status === "IN_PROGRESS").length;
    const resolved = complaints.filter((item) => item.status === "RESOLVED").length;
    return [
      { label: "Total Complaints", value: total },
      { label: "Open", value: open },
      { label: "In Progress", value: inProgress },
      { label: "Resolved", value: resolved }
    ];
  }, [complaints]);

  const submitComplaint = async (event) => {
    event.preventDefault();
    await complaintApi.create({
      ...form,
      latitude: Number(form.latitude),
      longitude: Number(form.longitude)
    });
    setForm(emptyComplaint);
    setMessage("Complaint filed successfully.");
    loadComplaints();
  };

  const updateComplaint = async (complaint, status) => {
    await complaintApi.update(complaint.id, {
      title: complaint.title,
      description: complaint.description,
      category: complaint.category,
      location: complaint.location,
      latitude: complaint.latitude,
      longitude: complaint.longitude,
      locality: complaint.locality,
      status,
      assignedOfficerId: complaint.assignedOfficerId
    });
    loadComplaints();
    loadAnalytics();
  };

  const exportFile = async (type) => {
    const response = type === "pdf" ? await analyticsApi.exportPdf() : await analyticsApi.exportExcel();
    const blob = new Blob([response.data]);
    const link = document.createElement("a");
    link.href = URL.createObjectURL(blob);
    link.download = type === "pdf" ? "people-voice-report.pdf" : "people-voice-report.xlsx";
    link.click();
  };

  return (
    <AppLayout user={user} onLogout={onLogout}>
      <DashboardCards items={complaintCards} />

      {user.role === "CITIZEN" ? (
        <div className="row g-4">
          <div className="col-lg-5">
            <SectionCard title="File a complaint" description="Submit location-aware issues with auto-priority based on locality density.">
              {message ? <div className="alert alert-success py-2">{message}</div> : null}
              <ComplaintForm form={form} setForm={setForm} onSubmit={submitComplaint} submitLabel="Submit Complaint" />
            </SectionCard>
          </div>
          <div className="col-lg-7">
            <SectionCard title="Track complaints" description="See live status, assigned officer, and AI-assisted prioritization details.">
              <ComplaintTable complaints={complaints} user={user} onUpdate={updateComplaint} />
            </SectionCard>
          </div>
        </div>
      ) : (
        <div className="row g-4">
          <div className="col-lg-8">
            <SectionCard title="Operations queue" description="Prioritized grievance list for officer action and admin oversight.">
              <ComplaintTable complaints={complaints} user={user} onUpdate={updateComplaint} />
            </SectionCard>
          </div>
          <div className="col-lg-4">
            <SectionCard title="Analytics and exports" description="Track resolution health and generate PDF or Excel reports.">
              {analytics ? (
                <>
                  <div className="analytics-stack">
                    <div className="metric-block">
                      <span>Average Resolution Hours</span>
                      <strong>{analytics.averageResolutionHours.toFixed(1)}</strong>
                    </div>
                    <div className="metric-block">
                      <span>Priority Mix</span>
                      <ul className="list-unstyled mb-0">
                        {analytics.complaintsByPriority.map((item) => (
                          <li key={item.label}>{item.label}: {item.count}</li>
                        ))}
                      </ul>
                    </div>
                  </div>
                  <div className="d-flex gap-2 mt-3">
                    <button className="btn btn-dark" onClick={() => exportFile("pdf")}>Export PDF</button>
                    <button className="btn btn-outline-dark" onClick={() => exportFile("excel")}>Export Excel</button>
                  </div>
                </>
              ) : (
                <p className="text-muted mb-0">Loading analytics...</p>
              )}
            </SectionCard>
          </div>
        </div>
      )}
    </AppLayout>
  );
}

export default function App() {
  const { user, saveSession, clearSession } = useSession();

  return (
    <Routes>
      <Route path="/" element={user ? <Dashboard user={user} onLogout={clearSession} /> : <Navigate to="/login" replace />} />
      <Route path="/login" element={user ? <Navigate to="/" replace /> : <LoginPage onLogin={saveSession} />} />
      <Route path="/register" element={user ? <Navigate to="/" replace /> : <RegisterPage onRegister={saveSession} />} />
    </Routes>
  );
}
