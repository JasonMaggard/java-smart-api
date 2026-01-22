-- Create api_usage_logs table
CREATE TABLE api_usage_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    endpoint_path VARCHAR(255) NOT NULL,
    http_method VARCHAR(10) NOT NULL,
    params_used JSONB,
    response_time_ms INTEGER,
    status_code INTEGER,
    user_agent VARCHAR(255),
    ip_address VARCHAR(45),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create index on created_at for time-based queries
CREATE INDEX idx_api_usage_logs_created_at ON api_usage_logs(created_at);

-- Create index on endpoint_path and http_method for filtering by endpoint
CREATE INDEX idx_api_usage_logs_endpoint_method ON api_usage_logs(endpoint_path, http_method);
