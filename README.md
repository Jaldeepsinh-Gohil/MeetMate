# MeetMate - Smart Meetup Spot Finder

Find the perfect meetup spot for your squad - fair for everyone, every time.

## Quick Start

### Prerequisites
- Java 17+
- Docker & Docker Compose
- Node.js 18+
- Maven 3.8+

### Run Locally
```bash
# Start all services
docker-compose -f infra/docker-compose.dev.yml up

# Access
Frontend: http://localhost:5173
API Gateway: http://localhost:8080
```

### Development
```bash
# Backend (each service)
cd backend/auth-user-service
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Frontend
cd frontend/meetmate-web
npm install
npm run dev
```

## Architecture
- Microservices: Gateway, Auth, Group, Place-Recommendation
- Database: PostgreSQL
- Frontend: React + TypeScript + MUI

