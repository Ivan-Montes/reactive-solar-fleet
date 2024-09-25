DROP DATABASE IF EXISTS shipclassReadDb;

CREATE DATABASE shipclassReadDb;

DROP TABLE IF EXISTS shipclasses;

CREATE TABLE shipclasses(
	shipclass_id uuid NOT NULL,
	shipclass_name varchar(255) NOT NULL UNIQUE,
	shipclass_description varchar(255) NOT NULL,
	CONSTRAINT shipclasses_PK PRIMARY KEY(shipclass_id)
);