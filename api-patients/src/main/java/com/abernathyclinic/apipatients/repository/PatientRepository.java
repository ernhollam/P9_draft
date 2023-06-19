package com.abernathyclinic.apipatients.repository;

import com.abernathyclinic.apipatients.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Integer> {
    Optional<Patient> findById(Integer id);
    Optional<Patient> findByFamilyAndGivenAndDob(String family, String given, LocalDate dob);
}
