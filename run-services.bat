@echo off
echo Starting MeetMate Services...

echo 1. Starting Service Discovery (Eureka)...
cd backend\service-discovery
start "Service Discovery" cmd /k "java -jar target\service-discovery-1.0.0.jar"

timeout /t 10

echo 2. Starting Auth User Service...
cd ..\auth-user-service
start "Auth Service" cmd /k "java -jar target\auth-user-service-1.0.0.jar"

timeout /t 10

echo 3. Starting Gateway Service...
cd ..\gateway-service
start "Gateway Service" cmd /k "java -jar target\gateway-service-1.0.0.jar"

timeout /t 5

echo 4. Starting Group Service...
cd ..\group-service
start "Group Service" cmd /k "java -jar target\group-service-1.0.0.jar"

timeout /t 5

echo 5. Starting Place Recommendation Service...
cd ..\place-recommendation-service
start "Place Service" cmd /k "java -jar target\place-recommendation-service-1.0.0.jar"

echo All services started!
echo Access points:
echo - Eureka Dashboard: http://localhost:8761
echo - API Gateway: http://localhost:8080
echo - Auth Service: http://localhost:8081
echo - Group Service: http://localhost:8082
echo - Place Service: http://localhost:8083

pause
