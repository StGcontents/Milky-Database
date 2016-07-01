CREATE TABLE continuous_flux(
	galaxy VARCHAR(20) NOT NULL,
	ion INTEGER NOT NULL,
	aperture VARCHAR(5) NOT NULL,
	flux REAL NOT NULL,
	error REAL,
	CONSTRAINT pk_continuous_flux PRIMARY KEY (galaxy, ion, aperture),
	CONSTRAINT fk_con_galaxy 
		FOREIGN KEY (galaxy) REFERENCES galaxy(name)
			ON DELETE CASCADE
			ON UPDATE CASCADE,
	CONSTRAINT fk_con_ion 
		FOREIGN KEY (ion) REFERENCES ion(id)
			ON DELETE CASCADE
			ON UPDATE CASCADE
);