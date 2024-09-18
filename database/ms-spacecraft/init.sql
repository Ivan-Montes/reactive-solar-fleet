DROP DATABASE IF EXISTS spacecraftReadDb;

CREATE DATABASE spacecraftReadDb;

DROP TABLE IF EXISTS spacecrafts;

CREATE TABLE spacecrafts(
	spacecraft_id uuid NOT NULL,
	spacecraft_name VARCHAR(255) NOT NULL UNIQUE,
	shipclass_id uuid NOT NULL,
	CONSTRAINT spacecrafts_PK PRIMARY KEY(spacecraft_id)
);