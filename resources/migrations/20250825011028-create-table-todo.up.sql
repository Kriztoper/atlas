-- :up
CREATE TABLE IF NOT EXISTS todo (
    id SERIAL PRIMARY KEY,
    task_id INTEGER NOT NULL REFERENCES task(id) ON DELETE CASCADE,
    text VARCHAR(255) NOT NULL,
    description TEXT,
    is_completed BOOLEAN DEFAULT false,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE,
    completed_at TIMESTAMP WITH TIME ZONE,
    is_active BOOLEAN DEFAULT true
);

-- ;;