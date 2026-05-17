CREATE TABLE report_history (
    id BIGSERIAL PRIMARY KEY,
    report_type VARCHAR(40) NOT NULL,
    format VARCHAR(20) NOT NULL,
    generated_by VARCHAR(100) NOT NULL,
    filters VARCHAR(500),
    generated_at TIMESTAMP NOT NULL
);
