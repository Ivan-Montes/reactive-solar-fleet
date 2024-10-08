DROP DATABASE IF EXISTS crewmemberReadDb;

CREATE DATABASE crewmemberReadDb;

DROP TABLE IF EXISTS crewmembers;

CREATE TABLE crewmembers(
	crewmember_id uuid NOT NULL,
	crewmember_name varchar(255) NOT NULL,
	crewmember_surname varchar(255) NOT NULL,
	position_id uuid,
	spacecraft_id uuid,
	CONSTRAINT crewmembers_PK PRIMARY KEY(crewmember_id)
);