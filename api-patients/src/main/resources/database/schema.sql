DROP DATABASE IF EXISTS abernathy_clinic ;

CREATE DATABASE abernathy_clinic;
USE abernathy_clinic;

CREATE TABLE patients (
    patient_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    family VARCHAR(50) NOT NULL,
    given VARCHAR(50) NOT NULL,
    date_of_birth DATE NOT NULL,
    gender VARCHAR(1),
    address VARCHAR(100),
    phone VARCHAR(50)
);
