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
  id            bigserial     PRIMARY KEY,
  code          varchar(20)   NOT NULL,
  name          varchar(60)   NOT NULL,
  description   varchar       NOT NULL,
  allowed_days  varchar(100)  NOT NULL,
  start_time    time          NOT NULL,
  end_time      time          NOT NULL
);

INSERT INTO service (id, code, name, description, allowed_days, start_time, end_time) VALUES
  (1, 'BOARD', 'Pet Boarding', '寄养服务', 'MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY,SATURDAY,SUNDAY', '10:00', '19:00'),
  (2, 'GROOM', 'Pet Grooming', '全套美容', 'THURSDAY,SUNDAY',                                          '10:00', '18:00'),
  (3, 'WASH' , 'Pet Washing',  '单次洗护', 'MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY,SATURDAY,SUNDAY', '10:00', '18:00');

CREATE TABLE reservation (
    id                  BIGSERIAL PRIMARY KEY,
    pet_id              BIGINT NOT NULL REFERENCES pet(id) ON DELETE CASCADE,
    service_id          BIGINT NOT NULL REFERENCES service(id),
    reservation_time    TIMESTAMP NOT NULL,
    status              VARCHAR(20) NOT NULL DEFAULT 'BOOKED',
    notes               TEXT,
    owner_phone         VARCHAR(32) NOT NULL,
    created_at          TIMESTAMP DEFAULT now()
);
CREATE INDEX idx_reservations_phone ON reservation (owner_phone);
CREATE INDEX idx_reservation_time ON reservation (reservation_time);