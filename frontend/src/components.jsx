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

export function AppLayout({ user, onLogout, notifications, onMarkRead, children }) {
  const unread = notifications.filter((item) => !item.isRead).length;
  return (
    <div className="app-shell">
      <nav className="navbar navbar-expand-lg nav-surface">
        <div className="container align-items-start">
          <div>
            <span className="navbar-brand fw-bold">People Voice</span>
            <div className="small text-secondary">Smart civic grievance platform</div>
          </div>
          <div className="d-flex align-items-center gap-3 flex-wrap justify-content-end">
            <span className="badge text-bg-light">{user.role}</span>
            {user.availability ? <span className="badge text-bg-warning">{user.availability}</span> : null}
            <span>{user.name}</span>
            <span className="badge text-bg-dark">Notifications {unread}</span>
            <button className="btn btn-sm btn-outline-dark" onClick={onLogout}>Logout</button>
          </div>
        </div>
      </nav>
      <main className="container py-4 py-lg-5">
        <div className="row g-4">
          <div className="col-xl-9">{children}</div>
          <div className="col-xl-3">
            <section className="section-card sticky-panel">
              <div className="mb-3">
                <h2>Notifications</h2>
                <p className="text-secondary mb-0">Recent activity across your workflow.</p>
              </div>
              <div className="d-grid gap-2">
                {notifications.length === 0 ? <p className="text-secondary mb-0">No notifications yet.</p> : null}
                {notifications.slice(0, 8).map((item) => (
                  <button key={item.id} className={`notification-item ${item.isRead ? "read" : "unread"}`} onClick={() => onMarkRead(item.id)} type="button">
                    <strong>{item.title}</strong>
                    <span>{item.message}</span>
                  </button>
                ))}
              </div>
            </section>
          </div>
        </div>
      </main>
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
            {item.helper ? <small>{item.helper}</small> : null}
          </div>
        </div>
      ))}
    </div>
  );
}

export function SectionCard({ title, description, actions, children }) {
  return (
    <section className="section-card">
      <div className="section-header mb-3">
        <div>
          <h2>{title}</h2>
          <p className="text-secondary mb-0">{description}</p>
        </div>
        {actions ? <div>{actions}</div> : null}
      </div>
      {children}
    </section>
  );
}

export function ComplaintForm({ form, setForm, files, setFiles, onSubmit, submitLabel }) {
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
      <div>
        <label className="form-label fw-semibold">Complaint images</label>
        <input className="form-control" type="file" multiple accept="image/*" onChange={(e) => setFiles(Array.from(e.target.files || []))} />
        {files.length ? <div className="small text-secondary mt-2">{files.length} file(s) selected</div> : null}
      </div>
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

export function OfficerCreateForm({ form, setForm, onSubmit }) {
  return (
    <form className="d-grid gap-2" onSubmit={onSubmit}>
      <input className="form-control form-control-sm" placeholder="Officer name" value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} />
      <input className="form-control form-control-sm" placeholder="Officer email" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} />
      <input className="form-control form-control-sm" type="password" placeholder="Temporary password" value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} />
      <input className="form-control form-control-sm" placeholder="Phone" value={form.phone} onChange={(e) => setForm({ ...form, phone: e.target.value })} />
      <select className="form-select form-select-sm" value={form.availability} onChange={(e) => setForm({ ...form, availability: e.target.value })}>
        <option value="AVAILABLE">AVAILABLE</option>
        <option value="BUSY">BUSY</option>
        <option value="OFFLINE">OFFLINE</option>
      </select>
      <button className="btn btn-sm btn-dark" type="submit">Create Officer</button>
    </form>
  );
}

export function AttachmentGallery({ attachments }) {
  if (!attachments?.length) return <p className="text-secondary mb-0">No attachments yet.</p>;
  return (
    <div className="attachment-grid">
      {attachments.map((attachment) => (
        <a key={attachment.id} className="attachment-card" href={`http://localhost:8080${attachment.url}`} target="_blank" rel="noreferrer">
          <span>{attachment.fileName}</span>
          <small>{attachment.uploadedByRole}</small>
        </a>
      ))}
    </div>
  );
}

export function TimelineList({ timeline }) {
  if (!timeline?.length) return <p className="text-secondary mb-0">No timeline entries yet.</p>;
  return (
    <div className="timeline-list">
      {timeline.map((item) => (
        <div className="timeline-item" key={item.id}>
          <div className="timeline-dot" />
          <div>
            <strong>{item.actorName} ({item.actorRole})</strong>
            <div className="small text-secondary">{item.message}</div>
            <div className="tiny-text">{item.fromStatus || "Created"} to {item.toStatus || "Created"}</div>
          </div>
        </div>
      ))}
    </div>
  );
}

export function ComplaintTable({ complaints, user, officers, assignmentState, onAssignmentChange, onAssign, onAutoAssign, onOfficerUpdate, onCitizenConfirm, onSelectComplaint }) {
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
                <button className="link-button" onClick={() => onSelectComplaint(complaint)} type="button">{complaint.title}</button>
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
                    <select className="form-select form-select-sm" value={assignmentState[complaint.id] || complaint.assignedOfficerId || ""} onChange={(e) => onAssignmentChange(complaint.id, e.target.value)}>
                      <option value="">Select officer</option>
                      {officers.map((officer) => (
                        <option key={officer.id} value={officer.id} disabled={officer.availability !== "AVAILABLE" && officer.id !== complaint.assignedOfficerId}>
                          {officer.name} ({officer.availability})
                        </option>
                      ))}
                    </select>
                    <div className="d-flex gap-2">
                      <button className="btn btn-sm btn-outline-dark" onClick={() => onAssign(complaint.id)} type="button">Assign</button>
                      <button className="btn btn-sm btn-dark" onClick={() => onAutoAssign(complaint.id)} type="button">Auto</button>
                    </div>
                  </div>
                ) : null}
                {user.role === "OFFICER" ? (
                  <div className="d-flex gap-2 flex-wrap">
                    <button className="btn btn-sm btn-outline-dark" onClick={() => onOfficerUpdate(complaint, "IN_PROGRESS")} type="button">Start</button>
                    <button className="btn btn-sm btn-dark" onClick={() => onOfficerUpdate(complaint, "PENDING_CITIZEN_CONFIRMATION")} type="button">Mark Done</button>
                  </div>
                ) : null}
                {user.role === "CITIZEN" && complaint.status === "PENDING_CITIZEN_CONFIRMATION" ? (
                  <div className="d-flex gap-2 flex-wrap">
                    <button className="btn btn-sm btn-dark" onClick={() => onCitizenConfirm(complaint.id, true)} type="button">Confirm</button>
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
