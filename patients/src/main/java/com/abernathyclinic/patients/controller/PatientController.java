package com.abernathyclinic.patients.controller;

import com.abernathyclinic.patients.exception.AlreadyExistsException;
import com.abernathyclinic.patients.exception.PatientNotFoundException;
import com.abernathyclinic.patients.model.Patient;
import com.abernathyclinic.patients.service.PatientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/patient")
@Slf4j
public class PatientController {

    @Autowired
    private PatientService patientService;

    /**
     * Returns the list of patients from database.
     *
     * @return list of patients
     */
    @GetMapping({"/list"})
    public List<Patient> list() {
        return patientService.getPatients();
    }

    /**
     * Validates add patient form and redirects to patients list if successful.
     *
     * @param patient patient to be added
     * @return created patient or else AlreadyExistsException
     */
    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public Patient addPatient(@Valid Patient patient, BindingResult result) throws AlreadyExistsException {
        if (result.hasErrors()) {
            String errorMessage = "Patient information are not valid\n" + result.getAllErrors();
            log.error(errorMessage);
            return null;
        }
        return patientService.createPatient(patient);
    }

    /**
     * Updates a patient.
     *
     * @param id      ID of patient to be updated
     * @param patient updated patient
     * @param result  result of validation
     * @return list of patients if update is successful, update patient page otherwise
     */
    @PostMapping("/update/{id}")
    public Patient updatePatient(@PathVariable("id") Integer id, @Valid Patient patient,
                                 BindingResult result) throws PatientNotFoundException {
        // check required fields
        if (result.hasErrors()) {
            patient.setId(id);
            log.error("Patient information are not valid\n" + result.getAllErrors());
            return null;
        }
        // if valid call service to update Patient
        patient.setId(id);
        return patientService.updatePatient(patient);
    }

    /**
     * Deletes a patient.
     *
     * @param id ID of patient to be deleted
     */
    @GetMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePatient(@PathVariable("id") Integer id) throws PatientNotFoundException {
        // Find Patient by ID
        Optional<Patient> existingPatient = patientService.getPatientById(id);
        // and delete the patient
        if (existingPatient.isPresent()) {
            Patient patient = existingPatient.get();
            patientService.deletePatient(patient);
            log.debug("Patient with ID " + id + " was successfully removed");
        } else {
            log.error("Patient with ID " + id + " does not exist.");
            throw new PatientNotFoundException("Patient with ID " + id + " does not exist.");
        }
    }
}