-- Create airports table
CREATE TABLE airports (
    icao_code VARCHAR(4),
    iata_code VARCHAR(3),
    name VARCHAR(50),
    city VARCHAR(50),
    country VARCHAR(50),
    lat_deg INT,
    lat_min INT,
    lat_sec INT,
    lat_dir CHAR(1),
    lon_deg INT,
    lon_min INT,
    lon_sec INT,
    lon_dir CHAR(1),
    altitude INT,
    lat_decimal DOUBLE,
    lon_decimal DOUBLE,
    id BIGINT AUTO_INCREMENT PRIMARY KEY
);

