CREATE TABLE line_flux(
	galaxy VARCHAR(20) NOT NULL REFERENCES galaxy(name),
	ion INTEGER NOT NULL REFERENCES ion(id),
	aperture VARCHAR(5) NOT NULL,
	flux REAL NOT NULL,
	error REAL,
	PRIMARY KEY (galaxy, id, aperture)
);