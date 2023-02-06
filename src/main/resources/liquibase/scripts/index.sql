-- liquibase formatted sql

-- changeSet aShadrin:1
CREATE TABLE users
(
    id         SERIAL PRIMARY KEY,
    city       TEXT NOT NULL,
    email      TEXT NOT NULL UNIQUE,
    first_name TEXT NOT NULL,
    last_name  TEXT NOT NULL,
    phone      TEXT,
    reg_date   TEXT,
    password   TEXT NOT NULL,
    role       INTEGER
);

CREATE TABLE ads
(
    id          SERIAL PRIMARY KEY,
    title       TEXT NOT NULL,
    price       INTEGER,
    description TEXT,
    user_id     INTEGER REFERENCES users (id)
);

CREATE TABLE avatars
(
    id         SERIAL PRIMARY KEY,
    data       BYTEA,
    file_path  TEXT,
    file_size  BIGINT NOT NULL,
    media_type TEXT,
    user_id    INTEGER REFERENCES users (id)
);

CREATE TABLE images
(
    id         SERIAL PRIMARY KEY,
    data       BYTEA,
    file_path  TEXT,
    file_size  BIGINT NOT NULL,
    media_type TEXT,
    ads_id     INTEGER REFERENCES ads (id)
);

CREATE TABLE comments
(
    id         SERIAL PRIMARY KEY,
    created_at TEXT,
    text       TEXT,
    ads_id     INTEGER REFERENCES ads (id),
    user_id    INTEGER REFERENCES users (id)
);
