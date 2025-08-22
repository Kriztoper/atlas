-- :up
-- This is a DDL migration.
-- We use a comment to avoid the "Too many update results" error
-- that occurs with DDL statements.

CREATE SCHEMA pjm;

CREATE TABLE IF NOT EXISTS pjm.project (
  id SERIAL PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  description TEXT,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP,
  deleted_at TIMESTAMP,
  is_active BOOLEAN DEFAULT true
);