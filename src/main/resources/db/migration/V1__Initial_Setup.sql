CREATE SEQUENCE customer_id_seq INCREMENT 1 START 1;

CREATE TABLE IF NOT EXISTS customer(
    id BIGINT DEFAULT nextval('customer_id_seq') PRIMARY KEY,
    name TEXT NOT NULL,
    email TEXT NOT NULL,
    age INT NOT NULL
);