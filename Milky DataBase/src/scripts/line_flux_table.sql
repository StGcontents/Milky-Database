CREATE TABLE line_flux(
	galaxy VARCHAR(20) NOT NULL REFERENCES galaxy(name),
	ion INTEGER NOT NULL REFERENCES ion(id),
	aperture VARCHAR(5) NOT NULL,
	flux REAL NOT NULL,
	error REAL,
	CONSTRAINT pk_line_flux PRIMARY KEY (galaxy, ion, aperture),
	CONSTRAINT fk_line_galaxy 
		FOREIGN KEY (galaxy) REFERENCES galaxy(name)
			ON DELETE CASCADE
			ON UPDATE CASCADE,
	CONSTRAINT fk_line_ion 
		FOREIGN KEY (ion) REFERENCES ion(id)
			ON DELETE CASCADE
			ON UPDATE CASCADE
);