-- :up
CREATE TRIGGER update_project_updated_at
    BEFORE UPDATE ON project
    FOR EACH ROW
EXECUTE PROCEDURE update_updated_at();

-- ;;