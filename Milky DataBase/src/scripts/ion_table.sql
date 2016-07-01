CREATE TABLE ion (
	id SERIAL PRIMARY KEY,
	name VARCHAR(2) NOT NULL,
	charges SMALLINT NOT NULL,
	line REAL NOT NULL,
	UNIQUE (name, charges, line)
);