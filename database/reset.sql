-- Reinicia la base de datos de demostracion (borra todo y vuelve a crear). Util antes de la defensa.
DROP DATABASE IF EXISTS asociacion_comunal;
SOURCE schema.sql;
SOURCE data.sql;
