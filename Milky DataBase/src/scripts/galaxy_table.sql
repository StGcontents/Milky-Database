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
	distance REAL NOT NULL,
	/*reference*/
	spectre VARCHAR(5) NOT NULL,
	/*luminosity*/
	/*metallicity*/
);