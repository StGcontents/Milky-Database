CREATE TABLE alternative_names(
	name VARCHAR(20) NOT NULL,
	alter_name VARCHAR(20) NOT NULL,
	CONSTRAINT pk_alter_names PRIMARY KEY (name, alter_name),
	CONSTRAINT fk_galaxy 
		FOREIGN KEY (name) REFERENCES galaxy(name)
			ON DELETE CASCADE
			ON UPDATE CASCADE
); 