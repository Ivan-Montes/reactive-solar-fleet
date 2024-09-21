DROP DATABASE IF EXISTS positionReadDb;

CREATE DATABASE positionReadDb;

DROP TABLE IF EXISTS positions;

CREATE TABLE positions(
	position_id uuid NOT NULL,
	position_name varchar(255) NOT NULL UNIQUE,
	position_description varchar(255) NOT NULL,
	CONSTRAINT positions_PK PRIMARY KEY(position_id)
);


