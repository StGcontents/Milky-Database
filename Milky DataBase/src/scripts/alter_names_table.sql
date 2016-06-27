CREATE TABLE alternative_names(
	name VARCHAR(20) PRIMARY KEY,
	alter_name VARCHAR(20) NOT NULL,
	FOREIGN KEY (name) REFERENCES galaxy(name)
); 