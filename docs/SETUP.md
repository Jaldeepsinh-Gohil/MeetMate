# MeetMate Setup Guide

## Prerequisites

- **Java 17+** - Runtime for Spring Boot services
- **Node.js 18+** - Frontend development and building
- **Docker & Docker Compose** - Containerized deployment
- **Maven 3.8+** - Java dependency management
- **PostgreSQL 15+** - Database (if running locally without Docker)

## Environment Variables

Create a `.env.prod` file for production deployment:

```bash
# Database
DATABASE_NAME=meetmate
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=your_secure_password

# JWT
JWT_SECRET=your-256-bit-secret-key-minimum-for-production

# Docker
DOCKER_USERNAME=your_dockerhub_username

# CORS
ALLOWED_ORIGINS=https://yourdomain.com
```

## Local Development Setup

### Option 1: Docker Compose (Recommended)

1. **Clone the repository**
   ```bash
   git clone <your-repo-url>
   cd meetmate
   ```

2. **Start all services**
   ```bash
   docker-compose -f infra/docker-compose.dev.yml up
   ```

3. **Access the application**
   - Frontend: http://localhost:5173
   - API Gateway: http://localhost:8080
   - Auth Service: http://localhost:8081
   - Group Service: http://localhost:8082
   - Place Service: http://localhost:8083
   - Database: localhost:5432

4. **Stop services**
   ```bash
   docker-compose -f infra/docker-compose.dev.yml down
   ```

### Option 2: Manual Setup

1. **Database Setup**
   ```bash
   # Install PostgreSQL locally or use Docker
   docker run --name postgres -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=meetmate -p 5432:5432 -d postgres:15
   ```

2. **Backend Services**
   ```bash
   # Auth Service
   cd backend/auth-user-service
   mvn spring-boot:run -Dspring-boot.run.profiles=dev

   # Group Service
   cd ../group-service
   mvn spring-boot:run -Dspring-boot.run.profiles=dev

   # Place Service
   cd ../place-recommendation-service
   mvn spring-boot:run -Dspring-boot.run.profiles=dev

   # Gateway Service
   cd ../gateway-service
   mvn spring-boot:run -Dspring-boot.run.profiles=dev
   ```

3. **Frontend**
   ```bash
   cd frontend/meetmate-web
   npm install
   npm run dev
   ```

## Production Deployment

### Using Docker Compose

1. **Build and deploy**
   ```bash
   # Make deploy script executable
   chmod +x infra/deploy/deploy.sh

   # Run deployment
   ./infra/deploy/deploy.sh
   ```

2. **Manual deployment**
   ```bash
   # Build and start services
   docker-compose -f infra/docker-compose.yml up -d --build
   ```

### Environment Configuration

Create `.env.prod` file with production values:
```bash
DATABASE_NAME=meetmate_prod
DATABASE_USERNAME=meetmate_user
DATABASE_PASSWORD=secure_password_here
JWT_SECRET=very_long_secure_jwt_secret_key_minimum_256_bits
DOCKER_USERNAME=your_dockerhub_username
ALLOWED_ORIGINS=https://meetmate.yourdomain.com
```

## Development Workflow

### Running Tests

```bash
# Backend tests
cd backend/auth-user-service
mvn test

# Frontend tests
cd frontend/meetmate-web
npm test
```

### Code Quality

```bash
# Frontend linting
cd frontend/meetmate-web
npm run lint

# Type checking
npm run type-check
```

### Database Migrations

Database schema is managed by Flyway. Migrations run automatically on startup.

## Troubleshooting

### Common Issues

1. **Port conflicts**
   - Ensure ports 5432, 8080-8083, 5173 are available
   - Check running processes: `netstat -tulpn | grep :8080`

2. **Database connection issues**
   - Verify PostgreSQL is running
   - Check database credentials in `application-dev.yml`
   - Ensure database exists: `createdb meetmate`

3. **Docker issues**
   - Clean up: `docker system prune -a`
   - Rebuild: `docker-compose -f infra/docker-compose.dev.yml up --build`

4. **JWT authentication fails**
   - Check JWT_SECRET in environment variables
   - Verify token format: `Bearer <token>`
   - Check token expiration (24h for access, 7d for refresh)

5. **Frontend build issues**
   - Clear node_modules: `rm -rf node_modules && npm install`
   - Check Node.js version: `node --version`
   - Verify environment variables in `.env`

### Logs

```bash
# View service logs
docker-compose -f infra/docker-compose.dev.yml logs -f auth-user-service

# View all logs
docker-compose -f infra/docker-compose.dev.yml logs

# Application logs (manual setup)
tail -f backend/auth-user-service/logs/application.log
```

### Health Checks

All services expose health endpoints:
- Gateway: http://localhost:8080/actuator/health
- Auth: http://localhost:8081/actuator/health
- Group: http://localhost:8082/actuator/health
- Place: http://localhost:8083/actuator/health

### Database Issues

```bash
# Connect to database
docker exec -it meetmate-postgres psql -U postgres -d meetmate

# View tables
\d

# Check migrations
SELECT * FROM flyway_schema_history ORDER BY installed_rank;
```

## Architecture Overview

```
User → React Frontend (Port 5173)
         ↓
API Gateway (Port 8080)
         ↓
    ┌────┴────┬────────┬─────────────┐
    ↓         ↓        ↓             ↓
Auth-User  Group   Place-Rec     PostgreSQL
(8081)     (8082)    (8083)         (5432)
```

- **Gateway**: Routes requests, validates JWT, handles CORS
- **Auth-User**: User registration, login, JWT management
- **Group**: Group management, member preferences
- **Place-Recommendation**: Venue catalog, recommendation algorithm

## Security Notes

- JWT tokens expire in 24 hours (access) and 7 days (refresh)
- Passwords are BCrypt hashed
- CORS is configured for specific origins
- All endpoints require authentication except public auth routes
- Database credentials should never be committed to version control

## Performance Tips

- Use Docker for consistent environments
- Enable database connection pooling
- Configure appropriate JVM heap sizes for production
- Use CDN for static assets in production
- Implement rate limiting for API endpoints
