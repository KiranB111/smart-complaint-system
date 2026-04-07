import { Link, Navigate, Route, Routes } from "react-router-dom";
import { useEffect, useMemo, useState } from "react";
import { analyticsApi, authApi, complaintApi, notificationApi, userApi } from "./api";
import {
  AnalyticsBarChart,
  AppLayout,
  AttachmentGallery,
  AuthCard,
  ComplaintForm,
  ComplaintTable,
  DashboardCards,
  HeroPanel,
  OfficerLeaderboard,
  OfficerAvailabilityList,
  OfficerAvailabilityPanel,
  OfficerCreateForm,
  OfficerManagementList,
  ProofUploadPanel,
  ResolutionModal,
  SectionCard,
  TimelineList
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

const emptyOfficer = {
  name: "",
  email: "",
  password: "",
  phone: "",
  availability: "AVAILABLE"
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
        subtitle="Citizens can register, upload complaint evidence, and track progress while admins and officers coordinate the full workflow."
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
  const [form, setForm] = useState({ name: "", email: "", password: "", phone: "" });
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
        subtitle="Register once to raise locality complaints, upload images, and confirm final resolution yourself."
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
  const [notifications, setNotifications] = useState([]);
  const [assignmentState, setAssignmentState] = useState({});
  const [selectedComplaint, setSelectedComplaint] = useState(null);
  const [form, setForm] = useState(emptyComplaint);
  const [files, setFiles] = useState([]);
  const [proofFiles, setProofFiles] = useState([]);
  const [officerForm, setOfficerForm] = useState(emptyOfficer);
  const [message, setMessage] = useState("");
  const [resolutionDialog, setResolutionDialog] = useState({ open: false, complaintId: null, rating: 5 });

  const loadComplaints = async () => {
    const { data } = await complaintApi.list();
    setComplaints(data);
    setSelectedComplaint((current) => current ? data.find((item) => item.id === current.id) || current : data[0] || null);
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

  const loadNotifications = async () => {
    const { data } = await notificationApi.list();
    setNotifications(data);
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
    loadNotifications();
    const token = localStorage.getItem("pv_access_token");
    const eventSource = token
      ? new EventSource(`http://localhost:8080/api/stream?access_token=${encodeURIComponent(token)}`)
      : null;

    const handleRealtimeRefresh = () => {
      loadComplaints();
      loadNotifications();
      if (user.role !== "CITIZEN") {
        loadAnalytics();
      }
      if (user.role === "ADMIN") {
        loadOfficers();
      }
    };

    eventSource?.addEventListener("complaint", handleRealtimeRefresh);
    eventSource?.addEventListener("notification", handleRealtimeRefresh);

    const timer = setInterval(() => {
      loadComplaints();
      loadNotifications();
      if (user.role !== "CITIZEN") {
        loadAnalytics();
      }
      if (user.role === "ADMIN") {
        loadOfficers();
      }
    }, 30000);
    return () => {
      clearInterval(timer);
      eventSource?.close();
    };
  }, [user.role]);

  const complaintCards = useMemo(() => {
    const total = complaints.length;
    const open = complaints.filter((item) => item.status === "OPEN").length;
    const assigned = complaints.filter((item) => item.status === "ASSIGNED").length;
    const inProgress = complaints.filter((item) => ["IN_PROGRESS", "PENDING_CITIZEN_CONFIRMATION"].includes(item.status)).length;
    const resolved = complaints.filter((item) => item.status === "RESOLVED").length;
    return [
      { label: "Total Complaints", value: total },
      { label: "Open", value: open },
      { label: "Assigned", value: assigned },
      { label: "Resolved", value: resolved, helper: `${inProgress} active` }
    ];
  }, [complaints]);

  const chartData = useMemo(() => ({
    status: analytics?.complaintsByStatus || [],
    category: analytics?.complaintsByCategory || [],
    priority: analytics?.complaintsByPriority || []
  }), [analytics]);

  const submitComplaint = async (event) => {
    event.preventDefault();
    const { data } = await complaintApi.create({
      ...form,
      latitude: Number(form.latitude),
      longitude: Number(form.longitude)
    });
    for (const file of files) {
      await complaintApi.uploadAttachment(data.id, file);
    }
    setForm(emptyComplaint);
    setFiles([]);
    setMessage("Complaint filed successfully with evidence. The admin will assign it to an available officer.");
    await Promise.all([loadComplaints(), loadNotifications()]);
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
    await Promise.all([loadComplaints(), loadAnalytics(), loadNotifications()]);
  };

  const assignComplaint = async (complaintId) => {
    const officerId = Number(assignmentState[complaintId]);
    if (!officerId) return;
    await complaintApi.assign(complaintId, officerId);
    await Promise.all([loadComplaints(), loadAnalytics(), loadOfficers(), loadNotifications()]);
  };

  const autoAssignComplaint = async (complaintId) => {
    await complaintApi.autoAssign(complaintId);
    await Promise.all([loadComplaints(), loadAnalytics(), loadOfficers(), loadNotifications()]);
  };

  const confirmComplaint = async (complaintId, resolved) => {
    if (resolved) {
      setResolutionDialog({ open: true, complaintId, rating: 5 });
      return;
    }
    await complaintApi.confirm(complaintId, { resolved, officerRating: null });
    await Promise.all([loadComplaints(), loadOfficers(), loadNotifications()]);
  };

  const submitResolutionRating = async () => {
    await complaintApi.confirm(resolutionDialog.complaintId, {
      resolved: true,
      officerRating: resolutionDialog.rating
    });
    setResolutionDialog({ open: false, complaintId: null, rating: 5 });
    await Promise.all([loadComplaints(), loadOfficers(), loadNotifications()]);
  };

  const closeResolutionDialog = () => {
    setResolutionDialog({ open: false, complaintId: null, rating: 5 });
  };

  const updateAvailability = async (availability) => {
    const { data } = await userApi.updateAvailability(availability);
    onUserRefresh(data);
    await Promise.all([loadComplaints(), loadNotifications()]);
  };

  const createOfficer = async (event) => {
    event.preventDefault();
    await userApi.createOfficer(officerForm);
    setOfficerForm(emptyOfficer);
    await Promise.all([loadOfficers(), loadNotifications()]);
  };

  const changeOfficerDraft = (officerId, field, value) => {
    setOfficers((current) => current.map((officer) => (
      officer.id === officerId ? { ...officer, [field]: value } : officer
    )));
  };

  const saveOfficer = async (officer) => {
    await userApi.updateOfficer(officer.id, {
      name: officer.name,
      email: officer.email,
      phone: officer.phone,
      availability: officer.availability,
      active: officer.active
    });
    await Promise.all([loadOfficers(), loadNotifications()]);
  };

  const toggleOfficerActive = async (officer) => {
    await userApi.setOfficerActive(officer.id, !officer.active);
    await Promise.all([loadOfficers(), loadNotifications(), loadComplaints()]);
  };

  const markNotificationRead = async (id) => {
    await notificationApi.markRead(id);
    await loadNotifications();
  };

  const exportFile = async (type) => {
    const response = type === "pdf" ? await analyticsApi.exportPdf() : await analyticsApi.exportExcel();
    const blob = new Blob([response.data]);
    const link = document.createElement("a");
    link.href = URL.createObjectURL(blob);
    link.download = type === "pdf" ? "people-voice-report.pdf" : "people-voice-report.xlsx";
    link.click();
  };

  const uploadOfficerProof = async (event) => {
    event.preventDefault();
    if (!selectedComplaint || !proofFiles.length) return;
    for (const file of proofFiles) {
      await complaintApi.uploadAttachment(selectedComplaint.id, file);
    }
    setProofFiles([]);
    await Promise.all([loadComplaints(), loadNotifications()]);
  };

  const complaintList = user.role === "OFFICER"
    ? complaints.filter((item) => item.assignedOfficerId === user.id)
    : complaints;

  return (
    <AppLayout user={user} onLogout={onLogout} notifications={notifications} onMarkRead={markNotificationRead}>
      <ResolutionModal
        open={resolutionDialog.open}
        rating={resolutionDialog.rating}
        setRating={(rating) => setResolutionDialog((current) => ({ ...current, rating }))}
        onConfirm={submitResolutionRating}
        onCancel={closeResolutionDialog}
      />
      <DashboardCards items={complaintCards} />

      <div className="row g-4">
        <div className="col-12">
          {user.role === "CITIZEN" ? (
            <SectionCard title="Raise a complaint" description="Upload exact images of the issue, submit the complaint, and track each step until resolution.">
              {message ? <div className="alert alert-success py-2">{message}</div> : null}
              <ComplaintForm form={form} setForm={setForm} files={files} setFiles={setFiles} onSubmit={submitComplaint} submitLabel="Submit Complaint" />
            </SectionCard>
          ) : null}

          {user.role === "ADMIN" ? (
            <div className="row g-4">
              <div className="col-lg-8">
                <SectionCard title="Smart assignment desk" description="Assign work manually or use auto-assignment to route complaints to the best available officer by workload and rating.">
                  <ComplaintTable
                    complaints={complaintList}
                    user={user}
                    officers={officers}
                    assignmentState={assignmentState}
                    onAssignmentChange={(complaintId, officerId) => setAssignmentState((current) => ({ ...current, [complaintId]: officerId }))}
                    onAssign={assignComplaint}
                    onAutoAssign={autoAssignComplaint}
                    onOfficerUpdate={updateComplaint}
                    onCitizenConfirm={confirmComplaint}
                    onSelectComplaint={setSelectedComplaint}
                  />
                </SectionCard>
              </div>
              <div className="col-lg-4 d-grid gap-4">
                <SectionCard title="Officer management" description="Create officers and monitor availability and ratings.">
                  <OfficerCreateForm form={officerForm} setForm={setOfficerForm} onSubmit={createOfficer} />
                  <hr />
                  <OfficerManagementList
                    officers={officers}
                    onChange={changeOfficerDraft}
                    onSave={saveOfficer}
                    onToggleActive={toggleOfficerActive}
                  />
                </SectionCard>
                <SectionCard title="Analytics and exports" description="Generate reports and monitor service performance.">
                  {analytics ? (
                    <>
                      <div className="analytics-stack">
                        <div className="metric-block">
                          <span>Average Resolution Hours</span>
                          <strong>{analytics.averageResolutionHours.toFixed(1)}</strong>
                        </div>
                        <div className="metric-block">
                          <span>Complaint Throughput</span>
                          <strong>{analytics.totalComplaints}</strong>
                          <small>{analytics.openComplaints} open | {analytics.inProgressComplaints} in progress | {analytics.resolvedComplaints} resolved</small>
                        </div>
                      </div>
                      <div className="row g-3 mt-1">
                        <div className="col-12">
                          <AnalyticsBarChart title="Status Mix" items={chartData.status} colorClass="teal" />
                        </div>
                        <div className="col-12">
                          <AnalyticsBarChart title="Category Demand" items={chartData.category} colorClass="gold" />
                        </div>
                        <div className="col-12">
                          <AnalyticsBarChart title="Priority Spread" items={chartData.priority} colorClass="coral" />
                        </div>
                        <div className="col-12">
                          <div className="metric-block">
                            <span>Officer Performance Board</span>
                            <div className="mt-2">
                              <OfficerLeaderboard officers={officers} />
                            </div>
                          </div>
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
          ) : null}

          {user.role === "OFFICER" ? (
            <div className="row g-4">
              <div className="col-lg-8">
                <SectionCard title="Officer workflow" description="Handle assigned complaints, upload completion proof, and move cases to citizen confirmation.">
                  <ComplaintTable
                    complaints={complaintList}
                    user={user}
                    officers={[]}
                    assignmentState={{}}
                    onAssignmentChange={() => {}}
                    onAssign={() => {}}
                    onAutoAssign={() => {}}
                    onOfficerUpdate={updateComplaint}
                    onCitizenConfirm={confirmComplaint}
                    onSelectComplaint={setSelectedComplaint}
                  />
                </SectionCard>
              </div>
              <div className="col-lg-4">
                <SectionCard title="My availability" description="Set your capacity so admin can route tasks more effectively.">
                  <OfficerAvailabilityPanel currentAvailability={user.availability} onChange={updateAvailability} />
                  <hr />
                  {selectedComplaint && selectedComplaint.assignedOfficerId === user.id ? (
                    <ProofUploadPanel files={proofFiles} setFiles={setProofFiles} onSubmit={uploadOfficerProof} />
                  ) : (
                    <p className="small text-secondary mb-0">Select one of your assigned complaints below to upload completion proof images.</p>
                  )}
                </SectionCard>
              </div>
            </div>
          ) : null}

          {user.role === "CITIZEN" ? (
            <SectionCard title="My complaints" description="Follow progress, review uploaded evidence, and confirm when locality work is truly complete.">
              <ComplaintTable
                complaints={complaintList}
                user={user}
                officers={[]}
                assignmentState={{}}
                onAssignmentChange={() => {}}
                onAssign={() => {}}
                onAutoAssign={() => {}}
                onOfficerUpdate={updateComplaint}
                onCitizenConfirm={confirmComplaint}
                onSelectComplaint={setSelectedComplaint}
              />
            </SectionCard>
          ) : null}
        </div>

        <div className="col-12">
          <SectionCard title="Complaint detail" description="Open any complaint to review evidence and the full status timeline.">
            {selectedComplaint ? (
              <div className="row g-4">
                <div className="col-lg-5">
                  <h5>Attachments</h5>
                  <AttachmentGallery attachments={selectedComplaint.attachments} />
                  {user.role === "ADMIN" ? (
                    <>
                      <hr />
                      <h5 className="mb-2">Officer availability snapshot</h5>
                      <OfficerAvailabilityList officers={officers} />
                    </>
                  ) : null}
                </div>
                <div className="col-lg-7">
                  <h5>Timeline</h5>
                  <TimelineList timeline={selectedComplaint.timeline} />
                </div>
              </div>
            ) : <p className="text-secondary mb-0">Select a complaint to inspect attachments and timeline.</p>}
          </SectionCard>
        </div>
      </div>
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
