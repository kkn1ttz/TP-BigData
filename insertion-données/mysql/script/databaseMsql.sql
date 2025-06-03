CREATE DATABASE IF NOT EXISTS vehicledb;
USE vehicledb;

CREATE TABLE IF NOT EXISTS electric_vehicles (
    vin VARCHAR(50),
    county VARCHAR(100),
    city VARCHAR(100),
    state VARCHAR(50),
    postal_code VARCHAR(20),
    model_year INT,
    make VARCHAR(100),
    model VARCHAR(100),
    electric_vehicle_type VARCHAR(100),
    clean_alternative_fuel_vehicle_eligibility VARCHAR(100),
    electric_range INT,
    base_msrp INT,
    legislative_district VARCHAR(100),
    dol_vehicle_id BIGINT,
    vehicle_location VARCHAR(255),
    electric_utility VARCHAR(255),
    census_tract VARCHAR(255)
);
