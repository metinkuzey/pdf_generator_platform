-- PDF Generator Platform Database Initialization

-- Create extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Templates table
CREATE TABLE IF NOT EXISTS templates (
    id VARCHAR(36) PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    category VARCHAR(50) NOT NULL,
    schema JSONB NOT NULL,
    metadata JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(36),
    active BOOLEAN DEFAULT true,
    version INTEGER DEFAULT 1
);

-- Template versions for history
CREATE TABLE IF NOT EXISTS template_versions (
    id VARCHAR(36) PRIMARY KEY DEFAULT uuid_generate_v4(),
    template_id VARCHAR(36) REFERENCES templates(id),
    version INTEGER NOT NULL,
    schema JSONB NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(36)
);

-- PDF generation logs
CREATE TABLE IF NOT EXISTS pdf_generation_logs (
    id VARCHAR(36) PRIMARY KEY DEFAULT uuid_generate_v4(),
    template_id VARCHAR(36) REFERENCES templates(id),
    request_data JSONB,
    status VARCHAR(20) NOT NULL,
    file_path VARCHAR(500),
    processing_time_ms INTEGER,
    error_message TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(36)
);

-- Users table for authentication
CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(36) PRIMARY KEY DEFAULT uuid_generate_v4(),
    username VARCHAR(100) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    role VARCHAR(50) DEFAULT 'USER',
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for performance
CREATE INDEX IF NOT EXISTS idx_templates_category ON templates(category);
CREATE INDEX IF NOT EXISTS idx_templates_created_by ON templates(created_by);
CREATE INDEX IF NOT EXISTS idx_templates_active ON templates(active);
CREATE INDEX IF NOT EXISTS idx_template_versions_template_id ON template_versions(template_id);
CREATE INDEX IF NOT EXISTS idx_pdf_logs_template_id ON pdf_generation_logs(template_id);
CREATE INDEX IF NOT EXISTS idx_pdf_logs_created_at ON pdf_generation_logs(created_at);
CREATE INDEX IF NOT EXISTS idx_pdf_logs_status ON pdf_generation_logs(status);
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);

-- Insert default admin user (password: admin123)
INSERT INTO users (username, email, password_hash, first_name, last_name, role) 
VALUES ('admin', 'admin@pdfgenerator.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9P2.nRuNWDbNWKy', 'Admin', 'User', 'ADMIN')
ON CONFLICT (username) DO NOTHING;