CREATE TABLE continuous_flux(
	galaxy VARCHAR(20) NOT NULL REFERENCES galaxy(name),
	aperture VARCHAR(5) NOT NULL,
	oiii_52_flux REAL,
	oiii_52_err REAL,
	niii_57_flux REAL,
	niii_57_err REAL,
	oi_63_flux REAL,
	oi_63_err REAL,
	oiii_88_flux REAL,
	oiii_88_err REAL,
	nii_122_flux REAL,
	nii_122_err REAL,
	oi_145_flux REAL,
	oi_145_err REAL,
	cii_158_flux REAL,
	cii_158_err REAL,
	PRIMARY KEY (galaxy, aperture)
);