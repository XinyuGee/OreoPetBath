-- Add version column for optimistic locking
ALTER TABLE reservation ADD COLUMN version BIGINT NOT NULL DEFAULT 0;

-- Create index on version for better performance
CREATE INDEX idx_reservation_version ON reservation (version);

