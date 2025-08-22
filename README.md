# Atlas - Task Management Backend

A Clojure-based backend API for project and task management, featuring automatic database migrations and a RESTful API.

## Features

- 🗃️ **Automatic Database Migrations** - Runs migrations on startup with enhanced error handling
- 🚀 **RESTful API** - Clean API endpoints for project/task/todo management
- 🐘 **PostgreSQL Integration** - Robust database integration with schema management
- 🔧 **Environment Configuration** - Configurable via environment variables
- 📋 **Schema Organization** - Uses dedicated `pjm` schema for project management

## Quick Start

### Prerequisites

- Java 8+
- Leiningen
- PostgreSQL 12+

### Database Setup

1. Create a PostgreSQL database:
   ```sql
   CREATE DATABASE atlas_dev;
   CREATE USER admin WITH PASSWORD 'your_password';
   GRANT ALL PRIVILEGES ON DATABASE atlas_dev TO admin;
   ```

2. Copy environment configuration:
   ```bash
   cp .env.example .env
   # Edit .env with your database credentials
   ```

### Running the Application

```bash
# Install dependencies and run
lein deps
lein run
```

The application will:
1. Display a startup banner
2. Automatically run any pending database migrations
3. Start the web server on port 3001 (or PORT environment variable)

### Migration Management

```bash
# Check migration status
lein run status

# Run migrations only
lein run migrate

# Rollback last migration
lein run rollback

# Create new migration
lein run create-migration add-tasks-table
```

See [MIGRATIONS.md](MIGRATIONS.md) for detailed migration documentation.

## API Endpoints

Once running, the API will be available at `http://localhost:3001/api`

- `GET /api/projects` - List projects
- `POST /api/projects` - Create project
- `POST /api/projects/:id/tasks` - Create task
- `POST /api/tasks/:id/todos` - Add todo
- And more...

## Configuration

Configure via environment variables:

- `DB_HOST` - Database host (default: localhost)
- `DB_PORT` - Database port (default: 5432)
- `DB_NAME` - Database name (default: atlas_dev)
- `DB_USER` - Database user (default: admin)
- `DB_PASSWORD` - Database password
- `PORT` - Server port (default: 3001)

## Development

```bash
# Run tests
lein test

# Check code
lein check

# Build uberjar
lein uberjar
```

## License

Copyright © 2025

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
