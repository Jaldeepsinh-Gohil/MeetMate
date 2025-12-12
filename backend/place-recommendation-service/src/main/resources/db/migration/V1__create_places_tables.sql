CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS places (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    category VARCHAR(50) NOT NULL,
    area VARCHAR(100),
    address TEXT,
    lat DECIMAL(10, 8) NOT NULL,
    lng DECIMAL(11, 8) NOT NULL,
    cost_level VARCHAR(20) NOT NULL,
    has_veg BOOLEAN DEFAULT FALSE,
    has_non_veg BOOLEAN DEFAULT FALSE,
    rating DECIMAL(2, 1),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS recommendations (
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

CREATE INDEX IF NOT EXISTS idx_places_category ON places(category);
CREATE INDEX IF NOT EXISTS idx_places_area ON places(area);
CREATE INDEX IF NOT EXISTS idx_places_cost ON places(cost_level);

