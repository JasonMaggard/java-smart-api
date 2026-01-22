-- Create documentation table
CREATE TABLE documentation (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    endpoint_path VARCHAR(255) NOT NULL,
    http_method VARCHAR(10) NOT NULL,
    description TEXT,
    parameters JSONB,
    response_schema JSONB,
    code_examples JSONB,
    llm_model VARCHAR(50),
    token_count INTEGER,
    generated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_documentation_endpoint_method UNIQUE (endpoint_path, http_method)
);

-- Create index on endpoint_path and http_method
CREATE INDEX idx_documentation_endpoint_method ON documentation(endpoint_path, http_method);
