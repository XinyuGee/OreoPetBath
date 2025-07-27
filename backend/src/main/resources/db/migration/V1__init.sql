CREATE TABLE pet (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(100) NOT NULL,
    species         VARCHAR(40)  NOT NULL,
    breed           VARCHAR(100),
    age             INT,
    owner_name      VARCHAR(100) NOT NULL,
    owner_phone     VARCHAR(30)  NOT NULL
);

CREATE TABLE service (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(60)  NOT NULL UNIQUE
);

INSERT INTO service (name) VALUES
('Grooming/Washing'),
('Daycare');

CREATE TABLE reservation (
    id                  BIGSERIAL PRIMARY KEY,
    pet_id              BIGINT NOT NULL REFERENCES pet(id) ON DELETE CASCADE,
    service_id          BIGINT NOT NULL REFERENCES service(id),
    reservation_time    TIMESTAMP NOT NULL,
    notes               TEXT
);

CREATE INDEX idx_reservation_time ON reservation (reservation_time);