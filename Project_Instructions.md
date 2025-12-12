# MeetMate - AI Agent Master Instructions

## 🎯 Project Overview

You are building **MeetMate**, a Spring Boot microservices application that helps friend groups find optimal meetup spots based on everyone's location, transport options, budget, and food preferences.

**Tech Stack:**
- Backend: Java 17, Spring Boot 3.2+, PostgreSQL, JWT, Flyway
- Frontend: React 18, TypeScript, Vite, Material-UI, TanStack Query
- DevOps: Docker, Docker Compose, GitHub Actions

---

## 📋 Complete Project Structure

Create this exact repository structure:

```
meetmate/
├── backend/
│   ├── gateway-service/
│   ├── auth-user-service/
│   ├── group-service/
│   ├── place-recommendation-service/
│   └── pom.xml (parent)
├── frontend/
│   └── meetmate-web/
├── infra/
│   ├── docker-compose.yml
│   ├── docker-compose.dev.yml
│   └── deploy/
│       └── deploy.sh
├── .github/
│   └── workflows/
│       ├── backend-ci.yml
│       └── frontend-ci.yml
├── docs/
│   ├── API.md
│   └── SETUP.md
├── .gitignore
└── README.md
```

---

## 🏗️ Service Architecture

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

**Service Responsibilities:**
1. **Gateway**: Route requests, validate JWT, CORS
2. **Auth-User**: Registration, login, JWT, user profiles
3. **Group**: Manage groups, members, preferences
4. **Place-Recommendation**: Venue catalog, recommendation algorithm

---

## 📊 Database Schema

### Users Table (auth-user-service)
```sql
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    name VARCHAR(100) NOT NULL,
    default_location VARCHAR(255),
    default_lat DECIMAL(10, 8),
    default_lng DECIMAL(11, 8),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Groups Tables (group-service)
```sql
CREATE TABLE groups (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    owner_id UUID NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE group_members (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    group_id UUID NOT NULL REFERENCES groups(id),
    user_id UUID NOT NULL,
    nickname VARCHAR(50),
    is_active BOOLEAN DEFAULT true,
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(group_id, user_id)
);

CREATE TABLE member_preferences (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    group_id UUID NOT NULL REFERENCES groups(id),
    user_id UUID NOT NULL,
    current_location VARCHAR(255),
    current_lat DECIMAL(10, 8),
    current_lng DECIMAL(11, 8),
    transport_modes TEXT[], -- ['BIKE', 'CAR', 'METRO', 'BUS', 'WALK']
    max_distance_km INTEGER,
    travel_willingness VARCHAR(20), -- 'LOW', 'MEDIUM', 'HIGH'
    budget_level VARCHAR(20), -- 'LOW', 'MEDIUM', 'HIGH'
    food_preference VARCHAR(30), -- 'VEG_ONLY', 'VEG_FRIENDLY', 'NO_PREFERENCE'
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(group_id, user_id)
);
```

### Places Tables (place-recommendation-service)
```sql
CREATE TABLE places (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    category VARCHAR(50) NOT NULL, -- 'RESTAURANT', 'CAFE', 'FOOD_COURT', 'MALL', 'ENTERTAINMENT'
    area VARCHAR(100),
    address TEXT,
    lat DECIMAL(10, 8) NOT NULL,
    lng DECIMAL(11, 8) NOT NULL,
    cost_level VARCHAR(20), -- 'LOW', 'MEDIUM', 'HIGH'
    has_veg BOOLEAN DEFAULT false,
    has_non_veg BOOLEAN DEFAULT false,
    rating DECIMAL(2, 1),
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE recommendations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    group_id UUID NOT NULL,
    place_id UUID NOT NULL REFERENCES places(id),
    requested_by UUID NOT NULL,
    member_ids UUID[],
    score DECIMAL(5, 2),
    avg_distance_km DECIMAL(6, 2),
    max_distance_km DECIMAL(6, 2),
    reasoning TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

---

## 🔧 Implementation Instructions

Follow these steps IN ORDER. Generate complete, production-ready code for each step.

---

### STEP 1: Setup Parent POM & Project Structure

**Task:** Create parent POM and initialize all Spring Boot modules.

**Files to Create:**

1. **backend/pom.xml** (Parent POM)
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.1</version>
    </parent>
    
    <groupId>com.meetmate</groupId>
    <artifactId>meetmate-backend</artifactId>
    <version>1.0.0</version>
    <packaging>pom</packaging>
    
    <modules>
        <module>gateway-service</module>
        <module>auth-user-service</module>
        <module>group-service</module>
        <module>place-recommendation-service</module>
    </modules>
    
    <properties>
        <java.version>17</java.version>
        <spring-cloud.version>2023.0.0</spring-cloud.version>
    </properties>
    
    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
</project>
```

2. **README.md**
```markdown
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
```

3. **.gitignore**
```
# Maven
target/
pom.xml.tag
pom.xml.releaseBackup
pom.xml.versionsBackup
dependency-reduced-pom.xml

# IDE
.idea/
*.iml
.vscode/
*.code-workspace

# OS
.DS_Store
Thumbs.db

# Logs
*.log

# Environment
.env
.env.local
.env.prod

# Node
node_modules/
dist/
build/
*.tsbuildinfo

# Docker
*.pid
```

---

### STEP 2: Auth & User Service (Complete Implementation)

**Task:** Create authentication service with JWT, user registration, login, and profile management.

**Package Structure:**
```
auth-user-service/src/main/java/com/meetmate/auth/
├── config/
│   ├── SecurityConfig.java
│   └── JwtConfig.java
├── controller/
│   ├── AuthController.java
│   └── UserController.java
├── service/
│   ├── AuthService.java
│   └── UserService.java
├── repository/
│   └── UserRepository.java
├── entity/
│   └── User.java
├── dto/
│   ├── request/
│   │   ├── LoginRequest.java
│   │   ├── RegisterRequest.java
│   │   └── RefreshTokenRequest.java
│   └── response/
│       ├── AuthResponse.java
│       └── UserResponse.java
├── exception/
│   ├── GlobalExceptionHandler.java
│   ├── UserAlreadyExistsException.java
│   ├── InvalidCredentialsException.java
│   └── UserNotFoundException.java
├── util/
│   └── JwtUtil.java
└── AuthUserServiceApplication.java
```

**Key Requirements:**

1. **User Entity** (`entity/User.java`):
```java
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(unique = true, nullable = false)
    @Email
    private String email;
    
    @Column(nullable = false)
    private String passwordHash;
    
    @Column(nullable = false)
    private String name;
    
    private String defaultLocation;
    
    @Column(precision = 10, scale = 8)
    private BigDecimal defaultLat;
    
    @Column(precision = 11, scale = 8)
    private BigDecimal defaultLng;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
```

2. **JWT Utility** (`util/JwtUtil.java`):
- Generate access token (24h expiry)
- Generate refresh token (7d expiry)
- Validate token and extract claims
- Secret key from environment variable `JWT_SECRET`

3. **Security Config** (`config/SecurityConfig.java`):
- BCrypt password encoder
- Public endpoints: `/api/auth/register`, `/api/auth/login`, `/api/auth/refresh`
- Protected endpoints: `/api/users/**`
- JWT filter for authentication

4. **Controllers**:

**AuthController endpoints:**
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login and get JWT
- `POST /api/auth/refresh` - Refresh access token

**UserController endpoints:**
- `GET /api/users/me` - Get current user profile
- `PUT /api/users/me` - Update profile

5. **Global Exception Handler** (`exception/GlobalExceptionHandler.java`):
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExists(UserAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                "User Already Exists",
                ex.getMessage(),
                null
            ));
    }
    
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(InvalidCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.UNAUTHORIZED.value(),
                "Invalid Credentials",
                ex.getMessage(),
                null
            ));
    }
    
    // Add handlers for other exceptions
}
```

6. **Configuration Files**:

**application.yml:**
```yaml
spring:
  application:
    name: auth-user-service
  profiles:
    active: ${SPRING_PROFILES_ACTIVE:dev}

server:
  port: 8081

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: always
```

**application-dev.yml:**
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/meetmate
    username: postgres
    password: postgres
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: true
    properties:
      hibernate:
        format_sql: true
  flyway:
    enabled: true
    locations: classpath:db/migration

logging:
  level:
    com.meetmate: DEBUG
    org.springframework.security: DEBUG

jwt:
  secret: ${JWT_SECRET:dev-secret-key-change-in-production-min-256-bits}
  access-token-expiry: 86400000  # 24 hours
  refresh-token-expiry: 604800000  # 7 days
```

**application-prod.yml:**
```yaml
spring:
  datasource:
    url: ${DATABASE_URL}
    username: ${DATABASE_USERNAME}
    password: ${DATABASE_PASSWORD}
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false

logging:
  level:
    com.meetmate: INFO

jwt:
  secret: ${JWT_SECRET}
  access-token-expiry: 86400000
  refresh-token-expiry: 604800000
```

7. **Flyway Migration** (`resources/db/migration/V1__create_users_table.sql`):
```sql
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    name VARCHAR(100) NOT NULL,
    default_location VARCHAR(255),
    default_lat DECIMAL(10, 8),
    default_lng DECIMAL(11, 8),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_email ON users(email);
```

8. **Dockerfile:**
```dockerfile
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8081
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8081/actuator/health || exit 1
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**Generate complete, working code for all files listed above.**

---

### STEP 3: Group & Preferences Service (Complete Implementation)

**Task:** Create service for managing groups, members, and their preferences.

**Package Structure:** Same layered approach as auth-user-service

**Key Entities:**

1. **Group.java**
2. **GroupMember.java**
3. **MemberPreference.java**

**Enums to Create:**
```java
public enum TravelWillingness { LOW, MEDIUM, HIGH }
public enum BudgetLevel { LOW, MEDIUM, HIGH }
public enum FoodPreference { VEG_ONLY, VEG_FRIENDLY, NO_PREFERENCE }
public enum TransportMode { BIKE, CAR, METRO, BUS, WALK, CAB }
```

**Controllers:**

**GroupController endpoints:**
- `POST /api/groups` - Create group (requires JWT)
- `GET /api/groups` - List user's groups
- `GET /api/groups/{id}` - Get group details with members
- `PUT /api/groups/{id}` - Update group name (owner only)
- `DELETE /api/groups/{id}` - Delete group (owner only)
- `POST /api/groups/{id}/members` - Add member
- `DELETE /api/groups/{groupId}/members/{userId}` - Remove member

**PreferenceController endpoints:**
- `PUT /api/groups/{groupId}/preferences` - Update own preferences
- `GET /api/groups/{groupId}/preferences` - Get all members' preferences
- `GET /api/groups/{groupId}/preferences/{userId}` - Get specific member's preferences

**Business Logic:**
- User extracted from JWT (via gateway adds X-User-Id header)
- Verify group ownership for modifications
- Validate at least one transport mode selected
- Max distance: 1-50 km

**Flyway Migration** (`V1__create_groups_tables.sql`):
```sql
-- Create groups, group_members, and member_preferences tables
-- Add proper indexes and foreign keys
```

**Configuration:** Similar to auth-user-service, port 8082

**Generate complete, working code for this service.**

---

### STEP 4: Place & Recommendation Service (Complete Implementation)

**Task:** Create venue catalog and smart recommendation engine.

**Key Components:**

1. **Place Entity** with validation
2. **Recommendation Entity** for history tracking
3. **DistanceCalculator Utility** (Haversine formula):
```java
public class DistanceCalculator {
    private static final double EARTH_RADIUS_KM = 6371.0;
    
    public static double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        // Haversine formula implementation
        // Return distance in kilometers
    }
}
```

4. **Recommendation Algorithm** (`RecommendationService`):
```java
public List<RecommendationResponse> generateRecommendations(
    UUID groupId, 
    List<UUID> memberIds, 
    int maxResults
) {
    // 1. Fetch group members and their preferences
    // 2. Fetch all active places
    // 3. For each place:
    //    - Calculate distance from each member
    //    - Check constraints (max distance, budget, food preference)
    //    - Calculate score (0-100):
    //      * Distance fairness (40 pts): Lower std deviation
    //      * Average distance (30 pts): Closer is better
    //      * Budget match (15 pts): Matches lowest budget in group
    //      * Rating (15 pts): Higher rating
    // 4. Sort by score DESC
    // 5. Return top N with reasoning
}
```

**Controllers:**

**PlaceController (Admin):**
- `POST /api/places` - Create place
- `GET /api/places` - List with filters (category, area, costLevel)
- `GET /api/places/{id}` - Get place details
- `PUT /api/places/{id}` - Update place
- `DELETE /api/places/{id}` - Soft delete

**RecommendationController:**
- `POST /api/recommendations/generate` - Generate recommendations
  - Request: `{ groupId, memberIds (optional), maxResults (default 5) }`
  - Response: Array of recommendations with detailed reasoning

**Seed Data** (`V2__seed_ahmedabad_places.sql`):
Insert 30 places across Ahmedabad:
- Areas: Maninagar, Vastrapur, Satellite, SG Highway, CG Road, Prahlad Nagar, Thaltej, Bodakdev, Navrangpura, Ashram Road
- Mix of restaurants, cafes, food courts, malls
- Varied cost levels and ratings
- Accurate lat/lng coordinates

**Configuration:** Port 8083

**Generate complete, working code including the recommendation algorithm.**

---

### STEP 5: API Gateway Service (Complete Implementation)

**Task:** Create gateway for routing and JWT validation.

**Dependencies:**
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-gateway</artifactId>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.3</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.3</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.3</version>
</dependency>
```

**Key Components:**

1. **GatewayConfig** (application.yml):
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: auth-service
          uri: ${AUTH_SERVICE_URL:http://localhost:8081}
          predicates:
            - Path=/api/auth/**
          filters:
            - StripPrefix=1
        
        - id: user-service
          uri: ${AUTH_SERVICE_URL:http://localhost:8081}
          predicates:
            - Path=/api/users/**
          filters:
            - StripPrefix=1
        
        - id: group-service
          uri: ${GROUP_SERVICE_URL:http://localhost:8082}
          predicates:
            - Path=/api/groups/**
          filters:
            - StripPrefix=1
        
        - id: place-service
          uri: ${PLACE_SERVICE_URL:http://localhost:8083}
          predicates:
            - Path=/api/places/**
          filters:
            - StripPrefix=1
        
        - id: recommendation-service
          uri: ${PLACE_SERVICE_URL:http://localhost:8083}
          predicates:
            - Path=/api/recommendations/**
          filters:
            - StripPrefix=1
      
      default-filters:
        - DedupeResponseHeader=Access-Control-Allow-Credentials Access-Control-Allow-Origin
      
      globalcors:
        corsConfigurations:
          '[/**]':
            allowedOrigins: "${ALLOWED_ORIGINS:http://localhost:5173}"
            allowedMethods:
              - GET
              - POST
              - PUT
              - DELETE
              - OPTIONS
            allowedHeaders: "*"
            allowCredentials: true
            maxAge: 3600
```

2. **JwtAuthenticationFilter** (GlobalFilter):
```java
@Component
@Order(-1)
public class JwtAuthenticationFilter implements GlobalFilter {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    private static final List<String> PUBLIC_PATHS = Arrays.asList(
        "/api/auth/register",
        "/api/auth/login",
        "/api/auth/refresh",
        "/actuator/health"
    );
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        
        // Allow public paths
        if (PUBLIC_PATHS.stream().anyMatch(path::startsWith)) {
            return chain.filter(exchange);
        }
        
        // Extract and validate JWT
        String token = extractToken(exchange.getRequest());
        
        if (token == null || !jwtUtil.validateToken(token)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        
        // Extract user info and add headers
        String userId = jwtUtil.extractUserId(token);
        String email = jwtUtil.extractEmail(token);
        
        ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
            .header("X-User-Id", userId)
            .header("X-User-Email", email)
            .build();
        
        return chain.filter(exchange.mutate().request(modifiedRequest).build());
    }
    
    private String extractToken(ServerHttpRequest request) {
        String bearerToken = request.getHeaders().getFirst("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
```

3. **JwtUtil** (similar to auth-user-service)

**Configuration:** Port 8080

**Generate complete gateway configuration and JWT filter.**

---

### STEP 6: Frontend - React Application (Complete Implementation)

**Task:** Create React frontend with TypeScript, MUI, and React Query.

**Initialize Project:**
```bash
npm create vite@latest meetmate-web -- --template react-ts
cd meetmate-web
npm install @mui/material @emotion/react @emotion/styled @tanstack/react-query axios react-router-dom zustand react-hook-form zod
```

**Folder Structure:**
```
src/
├── components/
│   ├── layout/
│   │   ├── Layout.tsx
│   │   ├── Header.tsx
│   │   └── ProtectedRoute.tsx
│   ├── auth/
│   │   ├── LoginForm.tsx
│   │   └── RegisterForm.tsx
│   ├── groups/
│   │   ├── GroupCard.tsx
│   │   ├── GroupMemberList.tsx
│   │   ├── CreateGroupDialog.tsx
│   │   └── PreferenceForm.tsx
│   ├── recommendations/
│   │   ├── RecommendationCard.tsx
│   │   └── RecommendationFilters.tsx
│   └── common/
│       ├── LoadingSpinner.tsx
│       └── ErrorAlert.tsx
├── pages/
│   ├── auth/
│   │   ├── LoginPage.tsx
│   │   └── RegisterPage.tsx
│   ├── groups/
│   │   ├── GroupsPage.tsx
│   │   ├── GroupDetailPage.tsx
│   │   └── CreateGroupPage.tsx
│   └── recommendations/
│       └── RecommendationsPage.tsx
├── services/
│   ├── api.ts
│   ├── authService.ts
│   ├── groupService.ts
│   └── recommendationService.ts
├── store/
│   └── authStore.ts
├── types/
│   ├── auth.types.ts
│   ├── group.types.ts
│   ├── place.types.ts
│   └── recommendation.types.ts
├── utils/
│   └── constants.ts
├── theme/
│   └── theme.ts
├── App.tsx
└── main.tsx
```

**Key Implementations:**

1. **API Client** (`services/api.ts`):
```typescript
import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

export const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor: Add JWT token
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('accessToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor: Handle 401
apiClient.interceptors.response.use(
  (response) => response,
  async (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('accessToken');
      localStorage.removeItem('refreshToken');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);
```

2. **Auth Store** (`store/authStore.ts`):
```typescript
import { create } from 'zustand';
import { persist } from 'zustand/middleware';

interface User {
  id: string;
  email: string;
  name: string;
}

interface AuthStore {
  user: User | null;
  accessToken: string | null;
  refreshToken: string | null;
  login: (user: User, accessToken: string, refreshToken: string) => void;
  logout: () => void;
  isAuthenticated: () => boolean;
}

export const useAuthStore = create<AuthStore>()(
  persist(
    (set, get) => ({
      user: null,
      accessToken: null,
      refreshToken: null,
      login: (user, accessToken, refreshToken) => {
        localStorage.setItem('accessToken', accessToken);
        localStorage.setItem('refreshToken', refreshToken);
        set({ user, accessToken, refreshToken });
      },
      logout: () => {
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        set({ user: null, accessToken: null, refreshToken: null });
      },
      isAuthenticated: () => !!get().accessToken,
    }),
    {
      name: 'auth-storage',
    }
  )
);
```

3. **Protected Route Component**:
```typescript
import { Navigate, Outlet } from 'react-router-dom';
import { useAuthStore } from '../../store/authStore';

export const ProtectedRoute = () => {
  const isAuthenticated = useAuthStore((state) => state.isAuthenticated());
  
  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }
  
  return <Outlet />;
};
```

4. **Routing** (`App.tsx`):
```typescript
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { ThemeProvider } from '@mui/material';
import { theme } from './theme/theme';

// Pages
import LoginPage from './pages/auth/LoginPage';
import RegisterPage from './pages/auth/RegisterPage';
import GroupsPage from './pages/groups/GroupsPage';
import GroupDetailPage from './pages/groups/GroupDetailPage';
import RecommendationsPage from './pages/recommendations/RecommendationsPage';

// Components
import { Layout } from './components/layout/Layout';
import { ProtectedRoute } from './components/layout/ProtectedRoute';

const queryClient = new QueryClient();

function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <ThemeProvider theme={theme}>
        <BrowserRouter>
          <Routes>
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />
            
            <Route element={<Layout />}>
              <Route element={<ProtectedRoute />}>
                <Route path="/groups" element={<GroupsPage />} />
                <Route path="/groups/:id" element={<GroupDetailPage />} />
                <Route path="/recommendations/:groupId" element={<RecommendationsPage />} />
                <Route path="/" element={<Navigate to="/groups" replace />} />
              </Route>
            </Route>
          </Routes>
        </BrowserRouter>
      </ThemeProvider>
    </QueryClientProvider>
  );
}

export default App;
```

5. **MUI Theme** (`theme/theme.ts`):
```typescript
import { createTheme } from '@mui/material/styles';

export const theme = createTheme({
  palette: {
    primary: {
      main: '#1976d2',
    },
    secondary: {
      main: '#dc004e',
    },
  },
  typography: {
    fontFamily: '"Inter", "Roboto", "Helvetica", "Arial", sans-serif',
  },
  components: {
    MuiButton: {
      styleOverrides: {
        root: {
          textTransform: 'none',
          borderRadius: 8,
        },
      },
    },
  },
});
```

6. **Example Login Page** (`pages/auth/LoginPage.tsx`):
```typescript
import { useState } from 'react';
import { useNavigate, Link as RouterLink } from 'react-router-dom';
import { Box, Card, TextField, Button, Typography, Link, Alert } from '@mui/material';
import { useMutation } from '@tanstack/react-query';
import { authService } from '../../services/authService';
import { useAuthStore } from '../../store/authStore';

export default function LoginPage() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const navigate = useNavigate();
  const login = useAuthStore((state) => state.login);
  
  const loginMutation = useMutation({
    mutationFn: () => authService.login({ email, password }),
    onSuccess: (data) => {
      login(data.user, data.accessToken, data.refreshToken);
      navigate('/groups');
    },
  });
  
  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    loginMutation.mutate();
  };
  
  return (
    <Box sx={{ 
      minHeight: '100vh', 
      display: 'flex', 
      alignItems: 'center', 
      justifyContent: 'center',
      bgcolor: 'grey.100'
    }}>
      <Card sx={{ p: 4, maxWidth: 400, width: '100%' }}>
        <Typography variant="h4" gutterBottom align="center">
          MeetMate
        </Typography>
        <Typography variant="body2" color="text.secondary" align="center" sx={{ mb: 3 }}>
          Find the perfect meetup spot
        </Typography>
        
        {loginMutation.isError && (
          <Alert severity="error" sx={{ mb: 2 }}>
            Invalid email or password
          </Alert>
        )}
        
        <form onSubmit={handleSubmit}>
          <TextField
            fullWidth
            label="Email"
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            sx={{ mb: 2 }}
            required
          />
          <TextField
            fullWidth
            label="Password"
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            sx={{ mb: 3 }}
            required
          />
          <Button
            fullWidth
            variant="contained"
            type="submit"
            size="large"
            disabled={loginMutation.isPending}
          >
            {loginMutation.isPending ? 'Logging in...' : 'Login'}
          </Button>
        </form>
        
        <Box sx={{ mt: 2, textAlign: 'center' }}>
          <Typography variant="body2">
            Don't have an account?{' '}
            <Link component={RouterLink} to="/register">
              Register
            </Link>
          </Typography>
        </Box>
      </Card>
    </Box>
  );
}
```

**Generate complete frontend with all pages, components, services, and proper TypeScript types.**

---

### STEP 7: Docker & Docker Compose Setup

**Task:** Create Docker configuration for local development and production.

**1. docker-compose.dev.yml** (for local development):
```yaml
version: '3.8'

services:
  postgres:
    image: postgres:15-alpine
    container_name: meetmate-postgres
    environment:
      POSTGRES_DB: meetmate
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U postgres"]
      interval: 10s
      timeout: 5s
      retries: 5

  gateway-service:
    build:
      context: ./backend/gateway-service
      dockerfile: Dockerfile
    container_name: meetmate-gateway
    ports:
      - "8080:8080"
    environment:
      SPRING_PROFILES_ACTIVE: dev
      JWT_SECRET: dev-secret-key-change-in-production-min-256-bits
      AUTH_SERVICE_URL: http://auth-user-service:8081
      GROUP_SERVICE_URL: http://group-service:8082
      PLACE_SERVICE_URL: http://place-recommendation-service:8083
      ALLOWED_ORIGINS: http://localhost:5173
    depends_on:
      - auth-user-service
      - group-service
      - place-recommendation-service
    restart: unless-stopped

  auth-user-service:
    build:
      context: ./backend/auth-user-service
      dockerfile: Dockerfile
    container_name: meetmate-auth
    ports:
      - "8081:8081"
    environment:
      SPRING_PROFILES_ACTIVE: dev
      DATABASE_URL: jdbc:postgresql://postgres:5432/meetmate
      DATABASE_USERNAME: postgres
      DATABASE_PASSWORD: postgres
      JWT_SECRET: dev-secret-key-change-in-production-min-256-bits
    depends_on:
      postgres:
        condition: service_healthy
    restart: unless-stopped

  group-service:
    build:
      context: ./backend/group-service
      dockerfile: Dockerfile
    container_name: meetmate-group
    ports:
      - "8082:8082"
    environment:
      SPRING_PROFILES_ACTIVE: dev
      DATABASE_URL: jdbc:postgresql://postgres:5432/meetmate
      DATABASE_USERNAME: postgres
      DATABASE_PASSWORD: postgres
    depends_on:
      postgres:
        condition: service_healthy
    restart: unless-stopped

  place-recommendation-service:
    build:
      context: ./backend/place-recommendation-service
      dockerfile: Dockerfile
    container_name: meetmate-place
    ports:
      - "8083:8083"
    environment:
      SPRING_PROFILES_ACTIVE: dev
      DATABASE_URL: jdbc:postgresql://postgres:5432/meetmate
      DATABASE_USERNAME: postgres
      DATABASE_PASSWORD: postgres
    depends_on:
      postgres:
        condition: service_healthy
    restart: unless-stopped

  frontend:
    build:
      context: ./frontend/meetmate-web
      dockerfile: Dockerfile
      args:
        VITE_API_BASE_URL: http://localhost:8080/api
    container_name: meetmate-frontend
    ports:
      - "5173:80"
    depends_on:
      - gateway-service
    restart: unless-stopped

volumes:
  postgres_data:
```

**2. Frontend Dockerfile** (`frontend/meetmate-web/Dockerfile`):
```dockerfile
# Build stage
FROM node:18-alpine AS build

WORKDIR /app

COPY package*.json ./
RUN npm ci

COPY . .

ARG VITE_API_BASE_URL
ENV VITE_API_BASE_URL=$VITE_API_BASE_URL

RUN npm run build

# Production stage
FROM nginx:alpine

COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
```

**3. Nginx Config** (`frontend/meetmate-web/nginx.conf`):
```nginx
server {
    listen 80;
    server_name localhost;
    root /usr/share/nginx/html;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    location /api {
        proxy_pass http://gateway-service:8080;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host $host;
        proxy_cache_bypass $http_upgrade;
    }
}
```

**4. Deployment Script** (`infra/deploy/deploy.sh`):
```bash
#!/bin/bash
set -e

echo "🚀 MeetMate Deployment Script"
echo "================================"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Check if .env.prod exists
if [ ! -f .env.prod ]; then
    echo -e "${RED}Error: .env.prod file not found${NC}"
    exit 1
fi

# Load environment variables
echo -e "${YELLOW}Loading environment variables...${NC}"
source .env.prod

# Pull latest code
echo -e "${YELLOW}Pulling latest code from Git...${NC}"
git pull origin main

# Stop existing containers
echo -e "${YELLOW}Stopping existing containers...${NC}"
docker-compose -f infra/docker-compose.yml down

# Build services
echo -e "${YELLOW}Building Docker images...${NC}"
docker-compose -f infra/docker-compose.yml build --no-cache

# Start services
echo -e "${YELLOW}Starting services...${NC}"
docker-compose -f infra/docker-compose.yml up -d

# Wait for services to be ready
echo -e "${YELLOW}Waiting for services to be healthy...${NC}"
sleep 30

# Health checks
echo -e "${YELLOW}Running health checks...${NC}"

check_health() {
    local service=$1
    local url=$2
    
    if curl -f -s "$url" > /dev/null; then
        echo -e "${GREEN}✓ $service is healthy${NC}"
        return 0
    else
        echo -e "${RED}✗ $service is unhealthy${NC}"
        return 1
    fi
}

HEALTH_CHECK_FAILED=0

check_health "Gateway" "http://localhost:8080/actuator/health" || HEALTH_CHECK_FAILED=1
check_health "Auth Service" "http://localhost:8081/actuator/health" || HEALTH_CHECK_FAILED=1
check_health "Group Service" "http://localhost:8082/actuator/health" || HEALTH_CHECK_FAILED=1
check_health "Place Service" "http://localhost:8083/actuator/health" || HEALTH_CHECK_FAILED=1
check_health "Frontend" "http://localhost:80" || HEALTH_CHECK_FAILED=1

if [ $HEALTH_CHECK_FAILED -eq 1 ]; then
    echo -e "${RED}Deployment failed - some services are unhealthy${NC}"
    echo -e "${YELLOW}Check logs with: docker-compose -f infra/docker-compose.yml logs${NC}"
    exit 1
fi

echo ""
echo -e "${GREEN}✅ Deployment successful!${NC}"
echo ""
echo "Services running:"
docker-compose -f infra/docker-compose.yml ps

echo ""
echo "Access points:"
echo "  Frontend: http://localhost"
echo "  API Gateway: http://localhost:8080"
echo ""
echo "To view logs: docker-compose -f infra/docker-compose.yml logs -f"
echo "To stop: docker-compose -f infra/docker-compose.yml down"
```

Make the script executable:
```bash
chmod +x infra/deploy/deploy.sh
```

**Generate complete Docker configuration files.**

---

### STEP 8: GitHub Actions CI/CD

**Task:** Create automated build, test, and deployment pipelines.

**1. Backend CI** (`.github/workflows/backend-ci.yml`):
```yaml
name: Backend CI

on:
  push:
    branches: [ main, develop ]
    paths:
      - 'backend/**'
  pull_request:
    branches: [ main ]
    paths:
      - 'backend/**'

jobs:
  test:
    name: Test Services
    runs-on: ubuntu-latest
    
    services:
      postgres:
        image: postgres:15-alpine
        env:
          POSTGRES_DB: meetmate_test
          POSTGRES_USER: postgres
          POSTGRES_PASSWORD: postgres
        ports:
          - 5432:5432
        options: >-
          --health-cmd pg_isready
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5
    
    strategy:
      matrix:
        service: [auth-user-service, group-service, place-recommendation-service, gateway-service]
    
    steps:
      - name: Checkout code
        uses: actions/checkout@v4
      
      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: maven
      
      - name: Run tests for ${{ matrix.service }}
        working-directory: backend/${{ matrix.service }}
        run: mvn clean test
        env:
          DATABASE_URL: jdbc:postgresql://localhost:5432/meetmate_test
          DATABASE_USERNAME: postgres
          DATABASE_PASSWORD: postgres
          JWT_SECRET: test-secret-key-min-256-bits-for-testing-purposes
      
      - name: Upload test results
        if: always()
        uses: actions/upload-artifact@v3
        with:
          name: test-results-${{ matrix.service }}
          path: backend/${{ matrix.service }}/target/surefire-reports

  build:
    name: Build Services
    runs-on: ubuntu-latest
    needs: test
    
    strategy:
      matrix:
        service: [auth-user-service, group-service, place-recommendation-service, gateway-service]
    
    steps:
      - name: Checkout code
        uses: actions/checkout@v4
      
      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: maven
      
      - name: Build ${{ matrix.service }}
        working-directory: backend/${{ matrix.service }}
        run: mvn clean package -DskipTests
      
      - name: Upload JAR artifact
        uses: actions/upload-artifact@v3
        with:
          name: ${{ matrix.service }}-jar
          path: backend/${{ matrix.service }}/target/*.jar

  docker-build:
    name: Build and Push Docker Images
    runs-on: ubuntu-latest
    needs: build
    if: github.ref == 'refs/heads/main'
    
    strategy:
      matrix:
        service: [auth-user-service, group-service, place-recommendation-service, gateway-service]
    
    steps:
      - name: Checkout code
        uses: actions/checkout@v4
      
      - name: Set up Docker Buildx
        uses: docker/setup-buildx-action@v3
      
      - name: Log in to Docker Hub
        uses: docker/login-action@v3
        with:
          username: ${{ secrets.DOCKER_USERNAME }}
          password: ${{ secrets.DOCKER_PASSWORD }}
      
      - name: Build and push ${{ matrix.service }}
        uses: docker/build-push-action@v5
        with:
          context: backend/${{ matrix.service }}
          push: true
          tags: |
            ${{ secrets.DOCKER_USERNAME }}/meetmate-${{ matrix.service }}:latest
            ${{ secrets.DOCKER_USERNAME }}/meetmate-${{ matrix.service }}:${{ github.sha }}
          cache-from: type=registry,ref=${{ secrets.DOCKER_USERNAME }}/meetmate-${{ matrix.service }}:buildcache
          cache-to: type=registry,ref=${{ secrets.DOCKER_USERNAME }}/meetmate-${{ matrix.service }}:buildcache,mode=max
```

**2. Frontend CI** (`.github/workflows/frontend-ci.yml`):
```yaml
name: Frontend CI

on:
  push:
    branches: [ main, develop ]
    paths:
      - 'frontend/**'
  pull_request:
    branches: [ main ]
    paths:
      - 'frontend/**'

jobs:
  test:
    name: Test Frontend
    runs-on: ubuntu-latest
    
    steps:
      - name: Checkout code
        uses: actions/checkout@v4
      
      - name: Set up Node.js
        uses: actions/setup-node@v4
        with:
          node-version: '18'
          cache: 'npm'
          cache-dependency-path: frontend/meetmate-web/package-lock.json
      
      - name: Install dependencies
        working-directory: frontend/meetmate-web
        run: npm ci
      
      - name: Run linter
        working-directory: frontend/meetmate-web
        run: npm run lint
      
      - name: Type check
        working-directory: frontend/meetmate-web
        run: npm run type-check || npx tsc --noEmit
      
      - name: Run tests
        working-directory: frontend/meetmate-web
        run: npm test || echo "No tests yet"

  build:
    name: Build Frontend
    runs-on: ubuntu-latest
    needs: test
    
    steps:
      - name: Checkout code
        uses: actions/checkout@v4
      
      - name: Set up Node.js
        uses: actions/setup-node@v4
        with:
          node-version: '18'
          cache: 'npm'
          cache-dependency-path: frontend/meetmate-web/package-lock.json
      
      - name: Install dependencies
        working-directory: frontend/meetmate-web
        run: npm ci
      
      - name: Build
        working-directory: frontend/meetmate-web
        run: npm run build
        env:
          VITE_API_BASE_URL: http://localhost:8080/api
      
      - name: Upload build artifact
        uses: actions/upload-artifact@v3
        with:
          name: frontend-dist
          path: frontend/meetmate-web/dist

  docker-build:
    name: Build and Push Frontend Docker Image
    runs-on: ubuntu-latest
    needs: build
    if: github.ref == 'refs/heads/main'
    
    steps:
      - name: Checkout code
        uses: actions/checkout@v4
      
      - name: Set up Docker Buildx
        uses: docker/setup-buildx-action@v3
      
      - name: Log in to Docker Hub
        uses: docker/login-action@v3
        with:
          username: ${{ secrets.DOCKER_USERNAME }}
          password: ${{ secrets.DOCKER_PASSWORD }}
      
      - name: Build and push frontend
        uses: docker/build-push-action@v5
        with:
          context: frontend/meetmate-web
          push: true
          build-args: |
            VITE_API_BASE_URL=http://localhost:8080/api
          tags: |
            ${{ secrets.DOCKER_USERNAME }}/meetmate-frontend:latest
            ${{ secrets.DOCKER_USERNAME }}/meetmate-frontend:${{ github.sha }}
          cache-from: type=registry,ref=${{ secrets.DOCKER_USERNAME }}/meetmate-frontend:buildcache
          cache-to: type=registry,ref=${{ secrets.DOCKER_USERNAME }}/meetmate-frontend:buildcache,mode=max
```

**GitHub Secrets to Add:**
- `DOCKER_USERNAME`: Your Docker Hub username
- `DOCKER_PASSWORD`: Your Docker Hub password or access token

**Generate complete CI/CD workflow files.**

---

### STEP 9: Documentation

**Task:** Create comprehensive documentation.

**1. API Documentation** (`docs/API.md`):
```markdown
# MeetMate API Documentation

Base URL: `http://localhost:8080/api`

## Authentication

### Register
**POST** `/auth/register`

Request:
```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "SecurePass123"
}
```

Response (201):
```json
{
  "user": {
    "id": "uuid",
    "name": "John Doe",
    "email": "john@example.com"
  },
  "accessToken": "jwt-token",
  "refreshToken": "refresh-token"
}
```

### Login
**POST** `/auth/login`

[Continue with all endpoints...]
```

**2. Setup Guide** (`docs/SETUP.md`):
```markdown
# MeetMate Setup Guide

## Prerequisites
- Java 17 or higher
- Node.js 18 or higher
- Docker & Docker Compose
- Maven 3.8+
- PostgreSQL 15+ (if running locally without Docker)

## Local Development Setup

### Option 1: Docker Compose (Recommended)
[Steps...]

### Option 2: Manual Setup
[Steps...]

## Environment Variables
[List all required environment variables...]

## Troubleshooting
[Common issues and solutions...]
```

**Generate complete documentation.**

---

## 🎯 EXECUTION SUMMARY

**To build this entire project, execute the steps IN ORDER:**

1. ✅ Create repository structure and parent POM
2. ✅ Implement Auth & User Service (with JWT, Flyway, Docker)
3. ✅ Implement Group & Preferences Service
4. ✅ Implement Place & Recommendation Service (with algorithm)
5. ✅ Implement API Gateway (routing + JWT validation)
6. ✅ Implement React Frontend (all pages, components, services)
7. ✅ Setup Docker Compose for local dev
8. ✅ Setup GitHub Actions CI/CD
9. ✅ Write documentation

**Testing checklist after completion:**
- [ ] Can register and login
- [ ] Can create groups and add members
- [ ] Can set member preferences
- [ ] Can generate recommendations
- [ ] Recommendations are logical (distance, budget, preferences)
- [ ] Docker Compose runs all services
- [ ] CI/CD pipeline passes

---

## 💡 Key Implementation Notes

### For the AI Agent:
1. **Follow the order** - each step builds on the previous
2. **Generate COMPLETE code** - no placeholders or TODOs
3. **Include ALL validations** - request validation, business logic validation
4. **Implement proper error handling** - global exception handlers, meaningful error messages
5. **Add comments** - explain complex logic, especially the recommendation algorithm
6. **Use production-ready patterns** - DTO mapping, proper service layers, transaction management
7. **Include Flyway migrations** - all database changes must be in migration scripts
8. **Docker health checks** - ensure services wait for dependencies
9. **Environment-specific configs** - dev and prod profiles
10. **Security first** - never log passwords, validate JWT properly, check ownership

### Recommendation Algorithm Details:
```
Score = (Distance Fairness × 0.4) + (Avg Distance × 0.3) + (Budget × 0.15) + (Rating × 0.15)

Where:
- Distance Fairness = 100 - (std_deviation / max_distance × 100)
  Lower deviation = fairer for everyone
  
- Avg Distance = 100 - (avg_distance / 50 × 100)
  Closer = better (50km is maximum considered)
  
- Budget = 100 if place.cost <= min(member.budgets), else penalty
  
- Rating = (place.rating / 5.0) × 100
```

### Critical Constraints to Check:
- ✅ All members within their max_distance_km
- ✅ Place cost_level ≤ lowest budget_level in group
- ✅ If any member has VEG_ONLY, place must have has_veg=true
- ✅ Transport modes informational only (future: route planning)

---

## 🚀 Ready to Build?

Copy this entire document and provide it to your AI coding assistant. They will have all the context needed to build MeetMate from scratch!

**Remember:** Work through the steps sequentially. Test each service before moving to the next. Good luck! 🎉