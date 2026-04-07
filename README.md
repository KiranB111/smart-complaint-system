# People Voice - Smart Citizen Governance System

People Voice is a full-stack grievance management platform for citizens, officers, and administrators. It includes a Spring Boot REST API, JWT-based authentication, role-based workflows, a React dashboard, complaint prioritization driven by locality density, and exportable analytics.

## Project Structure

```text
smart-complaint-system/
├── backend/          # Spring Boot REST API
├── frontend/         # React + Vite client
├── database/         # SQL schema
└── README.md
```

## Core Features

- Citizen complaint portal with status tracking
- JWT authentication with role-based dashboards
- Officer and admin complaint operations
- Intelligent prioritization using locality density heuristics
- Analytics summary plus PDF and Excel exports
- H2 default setup for local demos, with MySQL/PostgreSQL drivers included

## Demo Accounts

- Citizen: `citizen@peoplevoice.local` / `password`
- Officer: `officer@peoplevoice.local` / `password`
- Admin: `admin@peoplevoice.local` / `password`

## Backend Setup

1. Open a terminal in `backend/`
2. Run `mvn clean install`
3. Start the API with `mvn spring-boot:run`
4. API base URL: `http://localhost:8080/api`
5. H2 console: `http://localhost:8080/h2-console`

The backend uses in-memory H2 by default. To switch to MySQL or PostgreSQL, update [application.properties](C:/Users/Kiran%20B/OneDrive/Documents/New%20project/backend/src/main/resources/application.properties) with your JDBC URL, username, password, and `ddl-auto` choice.

## Frontend Setup

1. Open a terminal in `frontend/`
2. Run `npm install`
3. Start the app with `npm run dev`
4. Open `http://localhost:3000`

## Key API Endpoints

### Authentication

- `POST /api/auth/login`
- `POST /api/auth/register`
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

## Implementation Notes

- Complaint prioritization is currently powered by an internal locality density map, designed so you can later swap in a live population-density API.
- Citizens only see their own complaints.
- Officers and admins can review and update operational queues.
- The frontend polls complaint data every 12 seconds to provide lightweight real-time tracking.

## Next Enhancements

- Replace density heuristics with a live GIS or census API
- Add file uploads for complaint evidence
- Add email or SMS notifications
- Add WebSocket push updates for status changes

## License

This project is released under the MIT License.
