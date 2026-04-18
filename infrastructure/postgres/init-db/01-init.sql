-- Keycloak
CREATE USER keycloak WITH PASSWORD 'keycloak_pass';
CREATE DATABASE keycloak OWNER keycloak;

-- File service
CREATE USER file_service WITH PASSWORD 'file_service_pass';
CREATE DATABASE file_service OWNER file_service;

-- Directory service
CREATE USER directory_service WITH PASSWORD 'directory_service_pass';
CREATE DATABASE directory_service OWNER directory_service;

-- User service
CREATE USER user_service WITH PASSWORD 'user_service_pass';
CREATE DATABASE user_service OWNER user_service;