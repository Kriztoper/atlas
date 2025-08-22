# Atlas Database Migrations

This document describes how to use the enhanced migration system in Atlas.

## Overview

Atlas uses [Migratus](https://github.com/yogthos/migratus) for database schema management. The migration system is configured to automatically run migrations when the application starts, with enhanced error handling and logging.

## Configuration

### Environment Variables

The migration system can be configured using environment variables:

- `DB_HOST` - Database host (default: localhost)
- `DB_PORT` - Database port (default: 5432)
- `DB_NAME` - Database name (default: atlas_dev)
- `DB_USER` - Database user (default: admin)
- `DB_PASSWORD` - Database password (default: empty)

Copy `.env.example` to `.env` and customize as needed.

## Migration Files

Migration files are stored in `resources/migrations/` and follow the pattern:
```
YYYYMMDDHHMMSSSSSS-description.up.sql    (forward migration)
YYYYMMDDHHMMSSSSSS-description.down.sql  (rollback migration)
```

## Usage

### Automatic Migration on Startup

Migrations run automatically when starting the application:

```bash
lein run
```

The startup process will:
1. Check database connectivity
2. Initialize the migration table if needed
3. Display pending migrations
4. Run all pending migrations
5. Start the web server

### Manual Migration Commands

You can also run migration commands separately:

#### Run Migrations
```bash
lein run migrate
```

#### Check Migration Status
```bash
lein run status
```

#### Rollback Last Migration
```bash
lein run rollback
```

#### Create New Migration
```bash
lein run create-migration add-user-table
```

This will create two files:
- `resources/migrations/TIMESTAMP-add-user-table.up.sql`
- `resources/migrations/TIMESTAMP-add-user-table.down.sql`

## Migration Best Practices

### 1. Always Create Both Up and Down Migrations
```sql
-- up migration
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- down migration  
DROP TABLE users;
```

### 2. Use Schema Prefixes
Use the `pjm` schema for project-related tables:
```sql
CREATE TABLE pjm.tasks (
    id SERIAL PRIMARY KEY,
    project_id INTEGER REFERENCES pjm.project(id),
    title VARCHAR(255) NOT NULL
);
```

### 3. Make Migrations Idempotent
Use `IF NOT EXISTS` where appropriate:
```sql
CREATE TABLE IF NOT EXISTS pjm.project (
    -- columns
);

ALTER TABLE pjm.project 
ADD COLUMN IF NOT EXISTS description TEXT;
```

### 4. Handle Data Migrations Carefully
When changing data structure:
```sql
-- Add new column
ALTER TABLE pjm.project ADD COLUMN status VARCHAR(20) DEFAULT 'active';

-- Populate existing data
UPDATE pjm.project SET status = 'active' WHERE status IS NULL;

-- Add constraints if needed
ALTER TABLE pjm.project ALTER COLUMN status SET NOT NULL;
```

## Error Handling

The enhanced migration system provides:

- **Database connectivity checks** before attempting migrations
- **Detailed logging** of migration status and progress
- **Graceful error handling** with clear error messages
- **Automatic initialization** of migration tracking table
- **Safe startup** - server won't start if migrations fail

## Troubleshooting

### Migration Fails on Startup
1. Check PostgreSQL is running
2. Verify database exists and credentials are correct
3. Check migration SQL syntax
4. Look at detailed error messages in console

### Check Current Status
```bash
lein run status
```

### Force Rollback Bad Migration
```bash
lein run rollback
```

### Manual Database Inspection
Check the migration table:
```sql
SELECT * FROM schema_migrations ORDER BY id DESC;
```

## Example Migration Workflow

1. **Create new migration:**
   ```bash
   lein run create-migration add-tasks-table
   ```

2. **Edit the up migration** (`resources/migrations/TIMESTAMP-add-tasks-table.up.sql`):
   ```sql
   CREATE TABLE pjm.tasks (
       id SERIAL PRIMARY KEY,
       project_id INTEGER NOT NULL REFERENCES pjm.project(id) ON DELETE CASCADE,
       title VARCHAR(255) NOT NULL,
       description TEXT,
       status VARCHAR(20) DEFAULT 'todo',
       created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
       updated_at TIMESTAMP WITH TIME ZONE,
       completed_at TIMESTAMP WITH TIME ZONE
   );
   
   CREATE INDEX idx_tasks_project_id ON pjm.tasks(project_id);
   CREATE INDEX idx_tasks_status ON pjm.tasks(status);
   ```

3. **Edit the down migration** (`resources/migrations/TIMESTAMP-add-tasks-table.down.sql`):
   ```sql
   DROP INDEX IF EXISTS pjm.idx_tasks_status;
   DROP INDEX IF EXISTS pjm.idx_tasks_project_id;
   DROP TABLE IF EXISTS pjm.tasks;
   ```

4. **Run migration:**
   ```bash
   lein run migrate
   # or just start the app
   lein run
   ```

5. **Verify:**
   ```bash
   lein run status
   ```

## Production Considerations

- Always backup your database before running migrations in production
- Test migrations on a staging environment first  
- Consider using database connection pooling for production
- Monitor migration execution time for large tables
- Have a rollback plan ready

## Schema Design

Current schema structure:
```
pjm (schema)
├── project (table)
├── tasks (planned)
└── todos (planned)
```

The `pjm` schema keeps project management tables organized and separate from other potential schemas.
