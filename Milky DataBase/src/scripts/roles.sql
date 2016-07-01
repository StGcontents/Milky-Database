CREATE ROLE admin LOGIN PASSWORD 'password';
CREATE ROLE common LOGIN PASSWORD 'user';
CREATE ROLE rd_only LOGIN PASSWORD 'login';
GRANT ALL ON ALL TABLES IN SCHEMA public TO admin;
GRANT SELECT ON user_admin TO rd_only;
GRANT SELECT ON galaxy, ion, line_flux, continuous_flux, alternative_names TO common; 