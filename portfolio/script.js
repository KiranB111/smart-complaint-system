const portfolioData = {
  name: "Kiran Bugatha",
  role: "Java Full-Stack Developer",
  intro:
    "I am a Computer Science graduate focused on Java full-stack development, backend systems, and practical web applications. I enjoy building dependable products with Spring Boot, REST APIs, MySQL, and modern frontend tools, with a strong interest in problem solving and scalable application design.",
  photo: "./assets/profile.jpg",
  resume: "file:///D:/KiranBugatha.pdf",
  metrics: [
    { value: "2024", label: "B.Tech Completed" },
    { value: "7.69", label: "B.Tech CGPA" },
    { value: "4+", label: "Certificates" },
    { value: "2", label: "Major Academic Projects" }
  ],
  focusAreas: [
    { label: "Java Development", icon: "https://cdn-icons-png.flaticon.com/512/226/226777.png" },
    { label: "Spring Boot", icon: "https://cdn-icons-png.flaticon.com/512/2721/2721297.png" },
    { label: "REST APIs", icon: "https://cdn-icons-png.flaticon.com/512/1055/1055687.png" },
    { label: "Responsive Design", icon: "https://cdn-icons-png.flaticon.com/512/1006/1006771.png" },
    { label: "MySQL", icon: "https://cdn-icons-png.flaticon.com/512/919/919836.png" }
  ],
  skills: [
    { name: "Java", icon: "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/java/java-original.svg" },
    { name: "Spring Boot", icon: "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/spring/spring-original.svg" },
    { name: "Hibernate", icon: "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/hibernate/hibernate-plain.svg" },
    { name: "MySQL", icon: "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/mysql/mysql-original.svg" },
    { name: "React", icon: "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/react/react-original.svg" },
    { name: "JavaScript", icon: "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/javascript/javascript-original.svg" },
    { name: "HTML", icon: "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/html5/html5-original.svg" },
    { name: "CSS", icon: "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/css3/css3-original.svg" },
    { name: "Git", icon: "https://cdn.jsdelivr.net/gh/devicons/devicon/icons/git/git-original.svg" }
  ],
  filters: ["All", "Full Stack", "Backend", "Frontend"],
  works: [
    {
      slug: "people-voice",
      title: "People Voice",
      type: "Full Stack",
      year: "2026",
      role: "Personal Project",
      summary:
        "A smart citizen governance system with complaint tracking, JWT authentication, role-based dashboards, analytics, and report generation.",
      image: "https://images.unsplash.com/photo-1516321318423-f06f85e504b3?auto=format&fit=crop&w=1200&q=80",
      stack: ["Spring Boot", "React", "MySQL", "JWT", "REST APIs"],
      challenge:
        "I wanted to build a practical complaint management system that handled citizen, admin, and officer workflows without becoming confusing to use.",
      solution:
        "I designed a role-based full-stack application with secure JWT authentication, assignment workflows, proof uploads, analytics, and export functionality.",
      highlights: [
        "Citizen complaint submission and evidence upload",
        "Admin assignment desk and analytics exports",
        "Officer workflow with completion proof",
        "Role-based dashboards and notifications"
      ],
      process: [
        "Planned entity relationships and complaint lifecycle states",
        "Built secure backend APIs in Spring Boot with JWT auth",
        "Created a React frontend for multiple user roles",
        "Integrated reporting modules for PDF and Excel exports"
      ],
      links: [
        { label: "Workspace Project", href: "../frontend" },
        { label: "Contact Me", href: "./contact.html" }
      ]
    },
    {
      slug: "school-management-system",
      title: "School Management System",
      type: "Backend",
      year: "2025",
      role: "Academic Project",
      summary:
        "A backend-focused management platform with layered architecture, validation, exception handling, and role-based access using Spring Security and JWT.",
      image: "https://images.unsplash.com/photo-1523240795612-9a054b0db644?auto=format&fit=crop&w=1200&q=80",
      stack: ["Spring Boot", "JPA", "Hibernate", "MySQL", "Spring Security"],
      challenge:
        "The goal was to structure a school system backend cleanly enough to manage students, teachers, courses, and fee operations without a tangled codebase.",
      solution:
        "I implemented a layered architecture with controller, service, and repository separation, plus JWT-based authentication and centralized validation.",
      highlights: [
        "RESTful APIs for students, teachers, courses, and fees",
        "Spring Security with role-based authorization",
        "Global exception handling and validation",
        "Clean layered architecture for maintainability"
      ],
      process: [
        "Modeled domain entities and database relationships",
        "Separated business logic into services and repositories",
        "Added validation and secure access control",
        "Tested API flows for reliability and clarity"
      ],
      links: [
        { label: "Works Page", href: "./works.html" },
        { label: "Contact Me", href: "./contact.html" }
      ]
    },
    {
      slug: "portfolio-experience",
      title: "Advanced Portfolio",
      type: "Frontend",
      year: "2026",
      role: "Personal Branding Project",
      summary:
        "A multi-page portfolio experience with animated transitions, detail pages, filtered works, modal certificate gallery, and stronger visual storytelling.",
      image: "https://images.unsplash.com/photo-1498050108023-c5249f4df085?auto=format&fit=crop&w=1200&q=80",
      stack: ["HTML", "CSS", "JavaScript", "Multi-page UX"],
      challenge:
        "I wanted a cleaner, more advanced portfolio that felt closer to a modern product website instead of a simple static resume page.",
      solution:
        "I restructured the portfolio into separate pages and added interactions such as filters, project detail views, a certificate gallery modal, and a richer contact flow.",
      highlights: [
        "Separate Home, About, Works, and Contact pages",
        "Project detail page rendering from shared data",
        "Certificate modal gallery for images and PDFs",
        "Animated reveal transitions and polished card interactions"
      ],
      process: [
        "Refactored the site into a data-driven shared structure",
        "Designed reusable page sections and card systems",
        "Added modal, filters, and form behavior in plain JavaScript",
        "Improved spacing, hierarchy, and motion to feel more premium"
      ],
      links: [
        { label: "Home Page", href: "./index.html" },
        { label: "Works Page", href: "./works.html" }
      ]
    }
  ],
  timeline: [
    {
      title: "Java Full-Stack Intern",
      meta: "IIDT, Vijayawada, Andhra Pradesh | 15-Feb-2024 to 15-July-2024",
      summary:
        "Worked on a Simple Bank Application and developed features such as account management, deposit and withdrawal operations, balance inquiry, and user authentication."
    },
    {
      title: "B.Tech in Computer Science",
      meta: "Andhra Loyola Institute of Engineering and Technology | 2020 - 2024",
      summary:
        "Completed Bachelor of Technology in Computer Science with a CGPA of 7.69 while building practical full-stack and backend projects."
    },
    {
      title: "Intermediate and SSC",
      meta: "PB Siddhartha College of Arts and Science, SKPVV Hindu High School | 2017 - 2020",
      summary:
        "Completed Intermediate with a CGPA of 9.67 and SSC with a CGPA of 9.3, building a strong academic foundation before entering engineering."
    }
  ],
  certificates: [
    {
      title: "Ideas for the Vision Viksit Bharat @2047",
      issuer: "MyGov India",
      period: "16 May 2024",
      description:
        "Certificate of participation awarded for contributing ideas toward realizing the vision of Viksit Bharat by 2047.",
      preview: "file:///C:/Users/Kiran%20B/Downloads/certificate.jpg",
      href: "file:///C:/Users/Kiran%20B/Downloads/certificate.jpg",
      kind: "image"
    },
    {
      title: "Programming, Data Structures and Algorithms Using Python",
      issuer: "NPTEL, IIT Madras",
      period: "Jan-Mar 2023",
      description:
        "Completed the NPTEL course in Python programming, data structures, and algorithms with a consolidated score of 55 percent.",
      preview: "file:///C:/Users/Kiran%20B/Downloads/Programming,%20Data%20Structures%20And%20Algorithms%20Using%20Python.jpeg",
      href: "file:///C:/Users/Kiran%20B/Downloads/Programming,%20Data%20Structures%20And%20Algorithms%20Using%20Python.jpeg",
      kind: "image"
    },
    {
      title: "Cloud Computing",
      issuer: "NPTEL",
      period: "Jul-Oct 2023",
      description:
        "Completed the NPTEL Cloud Computing course with an extracted final score of 75 in a 12-week program.",
      preview: "",
      href: "file:///C:/Users/Kiran%20B/Downloads/Cloud%20Computing.pdf",
      kind: "pdf"
    },
    {
      title: "Dream Factory 2024",
      issuer: "Certificate Program",
      period: "2024",
      description:
        "Added from the provided PDF certificate as part of the portfolio achievements section.",
      preview: "",
      href: "file:///C:/Users/Kiran%20B/Downloads/Dream_Factory_2024-B_Kiran.pdf",
      kind: "pdf"
    }
  ],
  contacts: [
    { label: "Phone", value: "7989363950", href: "tel:+917989363950" },
    { label: "Email", value: "kiranbugatha2001@gmail.com", href: "mailto:kiranbugatha2001@gmail.com" },
    { label: "WhatsApp", value: "Chat on WhatsApp", href: "https://wa.me/917989363950?text=Hello%20Kiran%2C%20I%20want%20to%20discuss%20a%20project." },
    { label: "GitHub", value: "github.com/KiranB111", href: "https://github.com/KiranB111" },
    { label: "LinkedIn", value: "linkedin.com/in/kiran-bugatha-7633b6239", href: "https://www.linkedin.com/in/kiran-bugatha-7633b6239/" },
    { label: "Resume", value: "Download PDF Resume", href: "file:///D:/KiranBugatha.pdf" }
  ]
};

const modal = document.getElementById("certificateModal");
const modalContent = document.getElementById("modalContent");

function setProfilePhoto() {
  document.querySelectorAll("#profilePhotoHome, #profilePhotoAbout, #contactProfilePhoto").forEach((img) => {
    img.src = portfolioData.photo;
  });
}

function setResumeButton() {
  const button = document.getElementById("resumeButton");
  if (!button) return;
  button.href = portfolioData.resume;
  button.setAttribute("download", "KiranBugatha.pdf");
}

function renderFocus(targetId) {
  const target = document.getElementById(targetId);
  if (!target) return;
  portfolioData.focusAreas.forEach((item) => {
    const pill = document.createElement("div");
    pill.className = "status-pill";
    pill.innerHTML = `<span>${item.label}</span>`;
    target.appendChild(pill);
  });
}

function createWorkCard(work) {
  const article = document.createElement("article");
  article.className = "work-card reveal";
  article.dataset.filter = work.type;
  article.innerHTML = `
    <a href="./project.html?slug=${work.slug}">
      <div class="work-visual">
        <div class="work-visual-inner">
          <img src="${work.image}" alt="${work.title}" />
          <span class="work-badge">${work.type}</span>
        </div>
      </div>
      <div class="work-body">
        <div class="project-meta">
          <span class="meta-chip">${work.year}</span>
          <span class="meta-chip">${work.role}</span>
        </div>
        <h3>${work.title}</h3>
        <div class="work-type-line">${work.type}</div>
        <p>${work.summary}</p>
        <div class="stack-row">${work.stack.map((item) => `<span class="stack-chip">${item}</span>`).join("")}</div>
        <span class="card-link">Open case study</span>
      </div>
    </a>
  `;
  return article;
}

function renderWorks(targetId, limit) {
  const target = document.getElementById(targetId);
  if (!target) return;
  const items = typeof limit === "number" ? portfolioData.works.slice(0, limit) : portfolioData.works;
  items.forEach((work) => target.appendChild(createWorkCard(work)));
}

function renderMetrics() {
  const target = document.getElementById("homeMetrics");
  if (!target) return;
  portfolioData.metrics.forEach((metric) => {
    const card = document.createElement("div");
    card.className = "metric-card";
    card.innerHTML = `<strong>${metric.value}</strong><span>${metric.label}</span>`;
    target.appendChild(card);
  });
}

function renderHome() {
  const name = document.getElementById("homeName");
  const role = document.getElementById("homeRole");
  const intro = document.getElementById("homeIntro");
  if (name) name.textContent = portfolioData.name;
  if (role) role.textContent = portfolioData.role;
  if (intro) intro.textContent = portfolioData.intro;
  renderFocus("homeFocus");
  renderMetrics();
  renderWorks("homeFeaturedWorks", 3);
}

function renderSkills() {
  const target = document.getElementById("skillsGrid");
  if (!target) return;
  portfolioData.skills.forEach((skill) => {
    const card = document.createElement("article");
    card.className = "skill-card reveal";
    card.innerHTML = `<img src="${skill.icon}" alt="${skill.name}" /><h3>${skill.name}</h3>`;
    target.appendChild(card);
  });
}

function renderTimeline() {
  const target = document.getElementById("timelineList");
  if (!target) return;
  portfolioData.timeline.forEach((item) => {
    const card = document.createElement("article");
    card.className = "timeline-card reveal";
    card.innerHTML = `<h3>${item.title}</h3><div class="timeline-meta">${item.meta}</div><p>${item.summary}</p>`;
    target.appendChild(card);
  });
}
function openCertificateModal(certificate) {
  if (!modal || !modalContent) return;
  const media = certificate.kind === "image"
    ? `<img class="modal-media" src="${certificate.href}" alt="${certificate.title}" />`
    : `<iframe class="modal-embed" src="${certificate.href}"></iframe>`;
  modalContent.innerHTML = `
    <div class="section-head stacked-head">
      <div>
        <span class="section-kicker">${certificate.issuer}</span>
        <h2>${certificate.title}</h2>
        <p class="page-copy wide-copy">${certificate.description}</p>
      </div>
    </div>
    ${media}
    <div class="project-actions" style="margin-top:1rem;">
      <a class="action-button action-primary" href="${certificate.href}" target="_blank" rel="noreferrer">Open original</a>
    </div>
  `;
  modal.classList.add("open");
  modal.setAttribute("aria-hidden", "false");
}

function closeModal() {
  if (!modal) return;
  modal.classList.remove("open");
  modal.setAttribute("aria-hidden", "true");
}

function bindModal() {
  document.querySelectorAll("[data-close-modal]").forEach((element) => {
    element.addEventListener("click", closeModal);
  });
  window.addEventListener("keydown", (event) => {
    if (event.key === "Escape") closeModal();
  });
}

function renderCertificates() {
  const target = document.getElementById("certificateGrid");
  if (!target) return;
  portfolioData.certificates.forEach((certificate) => {
    const card = document.createElement("article");
    card.className = "certificate-card reveal";
    const preview = certificate.preview
      ? `<img src="${certificate.preview}" alt="${certificate.title}" />`
      : `<div class="certificate-placeholder">PDF</div>`;
    card.innerHTML = `
      <div class="certificate-preview">${preview}</div>
      <div class="certificate-body">
        <div class="project-meta">
          <span class="meta-chip">${certificate.issuer}</span>
          <span class="meta-chip">${certificate.period}</span>
        </div>
        <h3>${certificate.title}</h3>
        <p>${certificate.description}</p>
        <span class="card-link">Open gallery view</span>
      </div>
    `;
    card.addEventListener("click", () => openCertificateModal(certificate));
    target.appendChild(card);
  });
}

function renderAbout() {
  const name = document.getElementById("aboutName");
  const role = document.getElementById("aboutRole");
  const about = document.getElementById("aboutMainText");
  if (name) name.textContent = portfolioData.name;
  if (role) role.textContent = portfolioData.role;
  if (about) about.textContent = portfolioData.intro;
  renderFocus("aboutFocus");
  renderSkills();
  renderTimeline();
  renderCertificates();
}

function renderFilters() {
  const target = document.getElementById("workFilters");
  const grid = document.getElementById("worksGrid");
  if (!target || !grid) return;

  portfolioData.filters.forEach((filter, index) => {
    const button = document.createElement("button");
    button.type = "button";
    button.className = `filter-chip${index === 0 ? " active" : ""}`;
    button.textContent = filter;
    button.addEventListener("click", () => {
      target.querySelectorAll(".filter-chip").forEach((chip) => chip.classList.remove("active"));
      button.classList.add("active");
      grid.querySelectorAll(".work-card").forEach((card) => {
        const matches = filter === "All" || card.dataset.filter === filter;
        card.style.display = matches ? "grid" : "none";
      });
    });
    target.appendChild(button);
  });
}

function renderWorksPage() {
  renderFilters();
  renderWorks("worksGrid");
}

function renderProjectPage() {
  const hero = document.getElementById("projectHero");
  const overview = document.getElementById("projectOverview");
  const deepDive = document.getElementById("projectDeepDive");
  if (!hero || !overview || !deepDive) return;

  const params = new URLSearchParams(window.location.search);
  const slug = params.get("slug") || portfolioData.works[0].slug;
  const project = portfolioData.works.find((item) => item.slug === slug) || portfolioData.works[0];
  document.title = `${project.title} | Kiran Bugatha`;

  hero.innerHTML = `
    <div class="project-hero-card reveal">
      <span class="section-kicker">${project.type} Project</span>
      <h1>${project.title}</h1>
      <p class="page-copy wide-copy">${project.summary}</p>
      <div class="project-meta">
        <span class="meta-chip">${project.year}</span>
        <span class="meta-chip">${project.role}</span>
      </div>
      <div class="stack-row">${project.stack.map((item) => `<span class="stack-chip">${item}</span>`).join("")}</div>
      <div class="project-actions">${project.links.map((link, index) => `<a class="action-button${index === 0 ? " action-primary" : ""}" href="${link.href}">${link.label}</a>`).join("")}</div>
    </div>
    <div class="project-visual-card reveal delay-1">
      <div class="project-visual-stage">
        <img src="${project.image}" alt="${project.title}" />
      </div>
    </div>
  `;

  overview.innerHTML = `
    <div class="section-head stacked-head">
      <div>
        <span class="section-kicker">Overview</span>
        <h2>From challenge to working product direction.</h2>
      </div>
    </div>
    <div class="overview-grid">
      <article class="overview-card"><h3>Challenge</h3><p>${project.challenge}</p></article>
      <article class="overview-card"><h3>Solution</h3><p>${project.solution}</p></article>
    </div>
  `;

  deepDive.innerHTML = `
    <div class="section-head stacked-head">
      <div>
        <span class="section-kicker">Deep Dive</span>
        <h2>Key features, process, and implementation decisions.</h2>
      </div>
    </div>
    <div class="detail-grid">
      <article class="info-card">
        <h3>Key Features</h3>
        <div class="stack-row">${project.highlights.map((item) => `<span class="stack-chip">${item}</span>`).join("")}</div>
      </article>
      <article class="info-card">
        <h3>Build Process</h3>
        <div class="stack-row">${project.process.map((item) => `<span class="stack-chip">${item}</span>`).join("")}</div>
      </article>
    </div>
  `;
}

function renderContacts() {
  const target = document.getElementById("contactGrid");
  if (!target) return;
  const contactIcons = {
    Email: "✉",
    Phone: "☎",
    WhatsApp: "WA",
    GitHub: "⌘",
    LinkedIn: "in"
  };
  portfolioData.contacts
    .filter((contact) => ["Email", "Phone", "WhatsApp", "GitHub", "LinkedIn"].includes(contact.label))
    .forEach((contact) => {
      const card = document.createElement("a");
      card.className = `contact-link-card reveal${contact.label === "WhatsApp" ? " whatsapp-card" : ""}`;
      card.href = contact.href;
      if (contact.href.startsWith("http")) {
        card.target = "_blank";
        card.rel = "noreferrer";
      }
      card.innerHTML = `
        <div class="contact-icon${contact.label === "WhatsApp" ? " whatsapp-icon" : ""}">${contactIcons[contact.label] || "•"}</div>
        <h3>${contact.label}</h3>
        <p>${contact.value}</p>
      `;
      target.appendChild(card);
    });
}

function bindContactForm() {
  const form = document.getElementById("contactForm");
  const status = document.getElementById("formStatus");
  const hiddenSubject = document.getElementById("contactHiddenSubject");
  const hiddenReplyTo = document.getElementById("contactHiddenReplyTo");
  if (!form || !status) return;

  form.addEventListener("submit", (event) => {
    const formData = new FormData(form);
    const email = String(formData.get("email") || "").trim();
    const subject = String(formData.get("subject") || "").trim();
    if (hiddenSubject) {
      hiddenSubject.value = subject ? `Portfolio Contact: ${subject}` : "New portfolio contact";
    }
    if (hiddenReplyTo) {
      hiddenReplyTo.value = email;
    }
    status.textContent = "Sending your message...";
  });

  form.addEventListener("reset", () => {
    status.textContent = "";
  });
}

function initRevealAnimations() {
  const observer = new IntersectionObserver(
    (entries) => {
      entries.forEach((entry) => {
        if (entry.isIntersecting) {
          entry.target.classList.add("in-view");
          observer.unobserve(entry.target);
        }
      });
    },
    { threshold: 0.14 }
  );

  document.querySelectorAll(".reveal").forEach((element) => observer.observe(element));
}

function init() {
  setProfilePhoto();
  setResumeButton();
  bindModal();

  const page = document.body.dataset.page;
  if (page === "home") renderHome();
  if (page === "about") renderAbout();
  if (page === "works") renderWorksPage();
  if (page === "project") renderProjectPage();
  if (page === "contact") {
    renderContacts();
    bindContactForm();
  }

  requestAnimationFrame(initRevealAnimations);
}

init();
