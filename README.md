# People Voice

People Voice is a full-stack smart citizen governance platform built to improve how public grievances are reported, prioritized, tracked, and resolved. It gives citizens a simple complaint portal, provides officers and administrators with role-based dashboards, and introduces intelligent complaint prioritization using locality density signals to support faster decision-making.

This project is structured as a portfolio-ready civic tech application that showcases secure authentication, backend service design, responsive frontend workflows, analytics reporting, and complete end-to-end product thinking.

## Highlights

- Full-stack architecture with Spring Boot backend and React frontend
- JWT-based authentication with role-based access control
- Citizen complaint filing and status tracking
- Officer and admin dashboards for grievance operations
- Smart prioritization using locality density and complaint category
- PDF and Excel report export for analytics workflows
- Demo-ready seeded users for quick evaluation

## Problem Statement

Public grievance systems are often difficult to track, slow to respond, and disconnected from operational priorities. People Voice addresses this by creating a transparent workflow where:

- Citizens can submit and monitor complaints
- Officers receive a prioritized queue of issues
- Administrators can review performance and complaint trends

## Tech Stack

**Backend**

- Java 17
- Spring Boot
- Spring Security
- JWT Authentication
- Spring Data JPA
- H2 for local development
- MySQL / PostgreSQL compatible configuration
- Apache POI for Excel export
- OpenPDF for PDF generation

**Frontend**

- React
- Vite
- Axios
- React Router
- Bootstrap

**Database**

- H2 for demo mode
- SQL schema included for relational database setup

## Features

### Citizen Portal

- Secure registration and login
- Complaint submission with title, category, description, and location
- Complaint status tracking
- Visibility into assigned priority and officer routing

### Officer and Admin Workflow

- Role-based access to grievance queues
- Update complaint status to `IN_PROGRESS` or `RESOLVED`
- Review operational workload and progress
- Export reports in PDF and Excel formats

### Intelligent Prioritization

Complaints are auto-prioritized using locality density heuristics and category importance. Issues in high-density areas or essential public-service categories can be escalated to higher urgency.

## Project Structure

```text
smart-complaint-system/
|-- backend/          Spring Boot REST API
|-- frontend/         React + Vite web client
|-- database/         SQL schema
|-- LICENSE
`-- README.md
```

## Demo Accounts

Use these accounts after starting the backend:

- Citizen: `citizen@peoplevoice.local` / `password`
- Officer: `officer@peoplevoice.local` / `password`
- Admin: `admin@peoplevoice.local` / `password`

## Getting Started

### Prerequisites

- Java 17 or later
- Maven
- Node.js 18 or later recommended
- npm

### Run the Backend

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

Backend endpoints:

- API base URL: `http://localhost:8080/api`
- H2 console: `http://localhost:8080/h2-console`

### Run the Frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend URL:

- Application: `http://localhost:3000`

## API Overview

### Authentication

- `POST /api/auth/register`
- `POST /api/auth/login`
- `POST /api/auth/refresh`

### Complaints

- `GET /api/complaints`
- `POST /api/complaints`
- `GET /api/complaints/{id}`
- `PUT /api/complaints/{id}`
- `DELETE /api/complaints/{id}`

### Analytics

- `GET /api/analytics/reports`
- `GET /api/analytics/export/pdf`
- `GET /api/analytics/export/excel`

## Architecture Notes

- The backend is organized with controller, service, repository, DTO, model, and security layers.
- Spring Security and JWT are used to enforce role-based access.
- The frontend delivers separate user experiences for citizens and operational users.
- Complaint data is periodically refreshed on the frontend for lightweight real-time visibility.

## Why This Project Works Well in a Portfolio

People Voice demonstrates more than basic CRUD. It combines:

- authentication and authorization
- structured backend layering
- real-world workflow logic
- export/reporting capabilities
- role-sensitive dashboards
- a practical civic technology use case

It is a strong showcase project for full-stack Java and React development, especially for illustrating secure REST API design and product-oriented dashboard implementation.

## Future Improvements

- Replace heuristics with live GIS or population-density APIs
- Add image or document uploads for complaint evidence
- Add email or SMS notifications
- Introduce WebSocket-based live status updates
- Expand analytics with charts and trend visualizations
- Deploy the stack to a public cloud environment

## License

This project is licensed under the MIT License. See the `LICENSE` file for details.
