CREATE TABLE galaxy (
	name VARCHAR(20) PRIMARY KEY,
	hours INTEGER NOT NULL,
	minutes INTEGER NOT NULL,
	seconds REAL NOT NULL,
	sign CHAR(1) NOT NULL,
	degrees INTEGER NOT NULL,
	arcmin INTEGER NOT NULL,
	arcsec REAL NOT NULL,
	redshift REAL NOT NULL,
	distance INTEGER,
	spectre VARCHAR(5) NOT NULL,
	lum_nev_1 REAL,
	lum_nev_1_flag BOOLEAN,
	lum_nev_2 REAL,
	lum_nev_2_flag BOOLEAN,
	lum_oiv REAL,
	lum_oiv_flag BOOLEAN,
	metallicity INTEGER,
	metallicity_err INTEGER
);