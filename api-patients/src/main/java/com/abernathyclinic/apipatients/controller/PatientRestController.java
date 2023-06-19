package com.abernathyclinic.apipatients.controller;

import com.abernathyclinic.apipatients.exceptions.AlreadyExistsException;
import com.abernathyclinic.apipatients.exceptions.PatientNotFoundException;
import com.abernathyclinic.apipatients.model.Patient;
import com.abernathyclinic.apipatients.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/patient")
public class PatientRestController {
    @Autowired
    PatientService patientService;

    @GetMapping
    public List<Patient> getPatients() {
        return patientService.getPatients();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Patient createPatient(Patient patient) throws AlreadyExistsException {
        return patientService.createPatient(patient);
    }

    @GetMapping("/{id}")
    public Patient getPatient(@PathVariable String id) {
        return patientService.getPatientById(Integer.valueOf(id))
                .orElseThrow(() -> new PatientNotFoundException("Patient with the provided ID does not exist."));
    }

    @PutMapping("/{id}")
    public Patient updatePatient(@PathVariable String id, @RequestBody Patient patient) {
        if (patientService.getPatientById(Integer.valueOf(id)).isEmpty()) {
            throw new PatientNotFoundException("Patient with the provided ID does not exist.");
        }
        return patientService.updatePatient(patient);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePatient(@PathVariable String id) {
        Assert.notNull(id, "ID must not be empty");
        if (patientService.getPatientById(Integer.valueOf(id)).isEmpty()) {
            throw new PatientNotFoundException("Patient with the provided ID does not exist.");
        }
        patientService.deletePatient(patientService.getPatientById(Integer.valueOf(id)).get());
    }

}
