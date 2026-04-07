import { Link, Navigate, Route, Routes } from "react-router-dom";
import { useEffect, useMemo, useState } from "react";
import { analyticsApi, authApi, complaintApi, userApi } from "./api";
import {
  AppLayout,
  AuthCard,
  ComplaintForm,
  ComplaintTable,
  DashboardCards,
  HeroPanel,
  OfficerAvailabilityList,
  OfficerAvailabilityPanel,
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

  return { user, saveSession, clearSession, setUser };
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
        subtitle="Citizens can create accounts and raise complaints, while admins and officers manage progress through a role-based workflow."
        error={error}
        submitLabel="Login"
        onSubmit={submit}
        footer={<span>First time here? <Link to="/register">Create your citizen account</Link></span>}
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
    phone: ""
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
        title="Create your citizen account"
        subtitle="New users can register once and start raising complaints for issues in their locality."
        error={error}
        submitLabel="Register"
        onSubmit={submit}
        footer={<span>Already registered? <Link to="/login">Login here</Link></span>}
      >
        <input className="form-control" placeholder="Name" value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} />
        <input className="form-control" placeholder="Email" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} />
        <input className="form-control" type="password" placeholder="Password" value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} />
        <input className="form-control" placeholder="Phone" value={form.phone} onChange={(e) => setForm({ ...form, phone: e.target.value })} />
      </AuthCard>
    </HeroPanel>
  );
}

function Dashboard({ user, onLogout, onUserRefresh }) {
  const [complaints, setComplaints] = useState([]);
  const [analytics, setAnalytics] = useState(null);
  const [officers, setOfficers] = useState([]);
  const [assignmentState, setAssignmentState] = useState({});
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

  const loadOfficers = async () => {
    if (user.role !== "ADMIN") return;
    const { data } = await userApi.officers();
    setOfficers(data);
  };

  const refreshUser = async () => {
    const { data } = await userApi.me();
    onUserRefresh(data);
  };

  useEffect(() => {
    refreshUser().catch(() => undefined);
    loadComplaints();
    loadAnalytics();
    loadOfficers();
    const timer = setInterval(loadComplaints, 12000);
    return () => clearInterval(timer);
  }, [user.role]);

  const complaintCards = useMemo(() => {
    const total = complaints.length;
    const open = complaints.filter((item) => item.status === "OPEN").length;
    const assigned = complaints.filter((item) => item.status === "ASSIGNED").length;
    const inProgress = complaints.filter((item) => item.status === "IN_PROGRESS" || item.status === "PENDING_CITIZEN_CONFIRMATION").length;
    const resolved = complaints.filter((item) => item.status === "RESOLVED").length;
    return [
      { label: "Total Complaints", value: total },
      { label: "Open", value: open },
      { label: "Assigned", value: assigned },
      { label: "Resolved", value: resolved || inProgress }
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
    setMessage("Complaint filed successfully. The admin will assign it to an available officer.");
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

  const assignComplaint = async (complaintId) => {
    const officerId = Number(assignmentState[complaintId]);
    if (!officerId) return;
    await complaintApi.assign(complaintId, officerId);
    await Promise.all([loadComplaints(), loadAnalytics(), loadOfficers()]);
  };

  const confirmComplaint = async (complaintId, resolved) => {
    let officerRating = null;
    if (resolved) {
      const value = window.prompt("Rate the officer's work from 1 to 5");
      if (value === null) {
        return;
      }
      officerRating = Number(value);
      if (!Number.isInteger(officerRating) || officerRating < 1 || officerRating > 5) {
        window.alert("Please enter a whole number between 1 and 5.");
        return;
      }
    }
    await complaintApi.confirm(complaintId, { resolved, officerRating });
    await loadComplaints();
    if (user.role === "ADMIN") {
      await loadOfficers();
    }
  };

  const updateAvailability = async (availability) => {
    const { data } = await userApi.updateAvailability(availability);
    onUserRefresh(data);
    await loadComplaints();
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
            <SectionCard title="Raise a complaint" description="First-time citizens can create accounts, submit locality issues, and wait for admin assignment to officers.">
              {message ? <div className="alert alert-success py-2">{message}</div> : null}
              <ComplaintForm form={form} setForm={setForm} onSubmit={submitComplaint} submitLabel="Submit Complaint" />
            </SectionCard>
          </div>
          <div className="col-lg-7">
            <SectionCard title="Track and confirm progress" description="When locality work is completed, you decide whether the complaint should be resolved or kept in progress.">
              <ComplaintTable complaints={complaints} user={user} officers={[]} assignmentState={{}} onAssignmentChange={() => {}} onAssign={() => {}} onOfficerUpdate={() => {}} onCitizenConfirm={confirmComplaint} />
            </SectionCard>
          </div>
        </div>
      ) : null}

      {user.role === "ADMIN" ? (
        <div className="row g-4">
          <div className="col-lg-8">
            <SectionCard title="Admin assignment desk" description="Assign complaints to available officers. Busy or offline officers are visible so work can be distributed more realistically.">
              <ComplaintTable
                complaints={complaints}
                user={user}
                officers={officers}
                assignmentState={assignmentState}
                onAssignmentChange={(complaintId, officerId) => setAssignmentState((current) => ({ ...current, [complaintId]: officerId }))}
                onAssign={assignComplaint}
                onOfficerUpdate={updateComplaint}
                onCitizenConfirm={confirmComplaint}
              />
            </SectionCard>
          </div>
          <div className="col-lg-4">
            <SectionCard title="Officer availability" description="Admins can quickly see which officers are free before assigning new work.">
              <OfficerAvailabilityList officers={officers} />
            </SectionCard>
            <div className="mt-4">
              <SectionCard title="Analytics and exports" description="Track progress and generate reports.">
                {analytics ? (
                  <>
                    <div className="analytics-stack">
                      <div className="metric-block">
                        <span>Average Resolution Hours</span>
                        <strong>{analytics.averageResolutionHours.toFixed(1)}</strong>
                      </div>
                    </div>
                    <div className="d-flex gap-2 mt-3">
                      <button className="btn btn-dark" onClick={() => exportFile("pdf")}>Export PDF</button>
                      <button className="btn btn-outline-dark" onClick={() => exportFile("excel")}>Export Excel</button>
                    </div>
                  </>
                ) : <p className="text-muted mb-0">Loading analytics...</p>}
              </SectionCard>
            </div>
          </div>
        </div>
      ) : null}

      {user.role === "OFFICER" ? (
        <div className="row g-4">
          <div className="col-lg-8">
            <SectionCard title="Officer workflow" description="Update your own availability, start assigned complaints, and mark field work complete for citizen confirmation.">
              <ComplaintTable
                complaints={complaints.filter((item) => item.assignedOfficerId === user.id)}
                user={user}
                officers={[]}
                assignmentState={{}}
                onAssignmentChange={() => {}}
                onAssign={() => {}}
                onOfficerUpdate={updateComplaint}
                onCitizenConfirm={confirmComplaint}
              />
            </SectionCard>
          </div>
          <div className="col-lg-4">
            <SectionCard title="My availability" description="Update availability so the admin can assign work based on officer capacity.">
              <OfficerAvailabilityPanel currentAvailability={user.availability} onChange={updateAvailability} />
            </SectionCard>
          </div>
        </div>
      ) : null}
    </AppLayout>
  );
}

export default function App() {
  const { user, saveSession, clearSession, setUser } = useSession();

  return (
    <Routes>
      <Route path="/" element={user ? <Dashboard user={user} onLogout={clearSession} onUserRefresh={setUser} /> : <Navigate to="/login" replace />} />
      <Route path="/login" element={user ? <Navigate to="/" replace /> : <LoginPage onLogin={saveSession} />} />
      <Route path="/register" element={user ? <Navigate to="/" replace /> : <RegisterPage onRegister={saveSession} />} />
    </Routes>
  );
}
