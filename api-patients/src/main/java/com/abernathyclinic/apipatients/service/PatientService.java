package com.abernathyclinic.apipatients.service;

import com.abernathyclinic.apipatients.exceptions.AlreadyExistsException;
import com.abernathyclinic.apipatients.exceptions.PatientNotFoundException;
import com.abernathyclinic.apipatients.model.Patient;
import com.abernathyclinic.apipatients.repository.PatientRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class PatientService {
    @Autowired
    PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    /**
     * Saves new patient to database
     *
     * @param patient Patient to save
     * @return Patient with ID if no error
     * @throws AlreadyExistsException Exception thrown when the patient to be created already exists in database
     */
    @Transactional
    public Patient createPatient(Patient patient) {
        if (patientRepository.findByFamilyAndGivenAndDob(patient.getFamily(), patient.getGiven(), patient.getDob()).isPresent()) {
            String alreadyExistsErrorMessage = "Patient " + patient.getFamily() + " " + patient.getGiven() + ", born the " + patient.getDob() + " already exists.";
            log.error(alreadyExistsErrorMessage);
            throw new AlreadyExistsException(alreadyExistsErrorMessage);
        }
        return patientRepository.save(patient);
    }

    public List<Patient> getPatients() {
        return patientRepository.findAll();
    }

    /**
     * Finds a patient thanks to their ID.
     *
     * @param id Patient ID.
     * @return found patient or empty optional.
     */
    public Optional<Patient> getPatientById(Integer id) {
        Assert.notNull(id, "Patient ID must not be empty. Please provide an ID");
        return patientRepository.findById(id);
    }


    /**
     * Updates a patient.
     *
     * @param patient Patient to be updated
     * @return updated patient
     */
    public Patient updatePatient(Patient patient) {
        Assert.notNull(patient, "Please provide a Patient to update");
        if (patientRepository.existsById(patient.getId())) {
            return patientRepository.save(patient);
        } else {
            String patientNotFoundErrorMessage = "Patient " + patient.getFamily() + " " + patient.getGiven()
                    + " born the " + patient.getDob() + " does not exist.";
            log.error(patientNotFoundErrorMessage);
            throw new PatientNotFoundException(patientNotFoundErrorMessage);
        }
    }

    /**
     * Deletes a patient if exists. Throws PatientNotFoundException if the provided patient does not exist in database.
     *
     * @param patient Patient to delete
     */
    @Transactional
    public void deletePatient(Patient patient) {
        Assert.notNull(patient, "Patient must be provided.");
        if (patientRepository.existsById(patient.getId())) {
            patientRepository.delete(patient);
        } else {
            String deleteErrorMessage = "Patient " + patient.getFamily() + " " + patient.getGiven()
                    + " born the " + patient.getDob() + " does not exist.";
            log.error(deleteErrorMessage);
            throw new PatientNotFoundException(deleteErrorMessage);
        }
    }
}
