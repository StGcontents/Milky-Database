CREATE TABLE user_admin(
	id VARCHAR(20) PRIMARY KEY,
	name VARCHAR(20) NOT NULL,
	surname VARCHAR(30) NOT NULL,
	password VARCHAR(20) NOT NULL,
	mail VARCHAR(30),
	is_admin BOOLEAN NOT NULL
);