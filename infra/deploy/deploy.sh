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
check_health "Frontend" "http://localhost" || HEALTH_CHECK_FAILED=1

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
