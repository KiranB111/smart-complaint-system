export function HeroPanel({ children }) {
  return (
    <div className="hero-shell">
      <div className="container py-5">
        <div className="row justify-content-center">
          <div className="col-lg-7">{children}</div>
        </div>
      </div>
    </div>
  );
}

export function AuthCard({ title, subtitle, children, error, submitLabel, onSubmit, footer }) {
  return (
    <div className="glass-card p-4 p-lg-5">
      <p className="eyebrow">People Voice</p>
      <h1>{title}</h1>
      <p className="text-secondary mb-4">{subtitle}</p>
      {error ? <div className="alert alert-danger py-2">{error}</div> : null}
      <form className="d-grid gap-3" onSubmit={onSubmit}>
        {children}
        <button className="btn btn-primary btn-lg" type="submit">{submitLabel}</button>
      </form>
      {footer ? <div className="mt-3 text-secondary">{footer}</div> : null}
    </div>
  );
}

export function AppLayout({ user, onLogout, children }) {
  return (
    <div className="app-shell">
      <nav className="navbar navbar-expand-lg nav-surface">
        <div className="container">
          <span className="navbar-brand fw-bold">People Voice</span>
          <div className="d-flex align-items-center gap-3 flex-wrap justify-content-end">
            <span className="badge text-bg-light">{user.role}</span>
            {user.availability ? <span className="badge text-bg-warning">{user.availability}</span> : null}
            <span>{user.name}</span>
            <button className="btn btn-sm btn-outline-dark" onClick={onLogout}>Logout</button>
          </div>
        </div>
      </nav>
      <main className="container py-4 py-lg-5">{children}</main>
    </div>
  );
}

export function DashboardCards({ items }) {
  return (
    <div className="row g-3 mb-4">
      {items.map((item) => (
        <div className="col-md-6 col-xl-3" key={item.label}>
          <div className="stat-card">
            <span>{item.label}</span>
            <strong>{item.value}</strong>
          </div>
        </div>
      ))}
    </div>
  );
}

export function SectionCard({ title, description, children }) {
  return (
    <section className="section-card">
      <div className="mb-3">
        <h2>{title}</h2>
        <p className="text-secondary mb-0">{description}</p>
      </div>
      {children}
    </section>
  );
}

export function ComplaintForm({ form, setForm, onSubmit, submitLabel }) {
  return (
    <form className="d-grid gap-3" onSubmit={onSubmit}>
      <input className="form-control" placeholder="Complaint title" value={form.title} onChange={(e) => setForm({ ...form, title: e.target.value })} />
      <textarea className="form-control" rows="4" placeholder="Describe the issue" value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} />
      <div className="row g-3">
        <div className="col-md-6">
          <select className="form-select" value={form.category} onChange={(e) => setForm({ ...form, category: e.target.value })}>
            <option>Sanitation</option>
            <option>Water</option>
            <option>Roads</option>
            <option>Electrical</option>
            <option>Public Safety</option>
          </select>
        </div>
        <div className="col-md-6">
          <input className="form-control" placeholder="Location" value={form.location} onChange={(e) => setForm({ ...form, location: e.target.value })} />
        </div>
      </div>
      <div className="row g-3">
        <div className="col-md-6">
          <input className="form-control" type="number" step="any" placeholder="Latitude" value={form.latitude} onChange={(e) => setForm({ ...form, latitude: e.target.value })} />
        </div>
        <div className="col-md-6">
          <input className="form-control" type="number" step="any" placeholder="Longitude" value={form.longitude} onChange={(e) => setForm({ ...form, longitude: e.target.value })} />
        </div>
      </div>
      <input className="form-control" placeholder="Locality" value={form.locality} onChange={(e) => setForm({ ...form, locality: e.target.value })} />
      <button className="btn btn-primary" type="submit">{submitLabel}</button>
    </form>
  );
}

export function OfficerAvailabilityPanel({ currentAvailability, onChange }) {
  return (
    <div className="d-flex gap-2 flex-wrap">
      {["AVAILABLE", "BUSY", "OFFLINE"].map((value) => (
        <button
          key={value}
          className={`btn ${currentAvailability === value ? "btn-dark" : "btn-outline-dark"}`}
          onClick={() => onChange(value)}
          type="button"
        >
          {value}
        </button>
      ))}
    </div>
  );
}

export function OfficerAvailabilityList({ officers }) {
  return (
    <div className="d-grid gap-2">
      {officers.map((officer) => (
        <div className="metric-block" key={officer.id}>
          <strong>{officer.name}</strong>
          <span>{officer.email}</span>
          <span className="badge text-bg-light w-fit">{officer.availability}</span>
          <span className="small text-secondary">Rating: {(officer.averageRating || 0).toFixed(1)} / 5 ({officer.ratingCount || 0})</span>
        </div>
      ))}
    </div>
  );
}

export function ComplaintTable({ complaints, user, officers, assignmentState, onAssignmentChange, onAssign, onOfficerUpdate, onCitizenConfirm }) {
  return (
    <div className="table-responsive">
      <table className="table align-middle">
        <thead>
          <tr>
            <th>Complaint</th>
            <th>Status</th>
            <th>Priority</th>
            <th>Assigned</th>
            <th>Reason</th>
            <th>Action</th>
          </tr>
        </thead>
        <tbody>
          {complaints.map((complaint) => (
            <tr key={complaint.id}>
              <td>
                <strong>{complaint.title}</strong>
                <div className="small text-secondary">{complaint.category} • {complaint.location}</div>
              </td>
              <td><span className="badge text-bg-light">{complaint.status}</span></td>
              <td><span className="badge priority-badge">{complaint.priority}</span></td>
              <td>
                <div>{complaint.assignedOfficerName || "Unassigned"}</div>
                {complaint.assignedOfficerAvailability ? <div className="small text-secondary">{complaint.assignedOfficerAvailability}</div> : null}
                {complaint.officerRating ? <div className="small text-secondary">Rated {complaint.officerRating}/5</div> : null}
              </td>
              <td className="small text-secondary">{complaint.priorityReason}</td>
              <td>
                {user.role === "ADMIN" ? (
                  <div className="d-grid gap-2">
                    <select
                      className="form-select form-select-sm"
                      value={assignmentState[complaint.id] || complaint.assignedOfficerId || ""}
                      onChange={(e) => onAssignmentChange(complaint.id, e.target.value)}
                    >
                      <option value="">Select officer</option>
                      {officers.map((officer) => (
                        <option key={officer.id} value={officer.id} disabled={officer.availability !== "AVAILABLE" && officer.id !== complaint.assignedOfficerId}>
                          {officer.name} ({officer.availability})
                        </option>
                      ))}
                    </select>
                    <button className="btn btn-sm btn-outline-dark" onClick={() => onAssign(complaint.id)} type="button">Assign</button>
                  </div>
                ) : null}
                {user.role === "OFFICER" ? (
                  <div className="d-flex gap-2 flex-wrap">
                    <button className="btn btn-sm btn-outline-dark" onClick={() => onOfficerUpdate(complaint, "IN_PROGRESS")} type="button">Start</button>
                    <button className="btn btn-sm btn-dark" onClick={() => onOfficerUpdate(complaint, "PENDING_CITIZEN_CONFIRMATION")} type="button">Mark Work Done</button>
                  </div>
                ) : null}
                {user.role === "CITIZEN" && complaint.status === "PENDING_CITIZEN_CONFIRMATION" ? (
                  <div className="d-flex gap-2 flex-wrap">
                    <button className="btn btn-sm btn-dark" onClick={() => onCitizenConfirm(complaint.id, true)} type="button">Confirm Resolved</button>
                    <button className="btn btn-sm btn-outline-dark" onClick={() => onCitizenConfirm(complaint.id, false)} type="button">Still Pending</button>
                  </div>
                ) : null}
                {user.role === "CITIZEN" && complaint.status !== "PENDING_CITIZEN_CONFIRMATION" ? <span className="small text-secondary">Track progress</span> : null}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
