package com.abernathyclinic.apipatients.controller;

import com.abernathyclinic.apipatients.model.Patient;
import com.abernathyclinic.apipatients.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Optional;

@Controller
public class PatientController {

    @Autowired
    private PatientService patientService;

    /**
     * Home page for patients. It shows the list of patients from database.
     *
     * @param model holder for context data to be passed from controller to the view, contains list of patients
     * @return patients list page
     */
    @GetMapping({"/", "/patient/list"})
    public String home(Model model) {
        model.addAttribute("patients", patientService.getPatients());
        return "patient/list";
    }

    /**
     * Shows add patient form. User should provide at least a family name, a given name and a date of birth
     *
     * @param patient patient to be added
     * @return add patient page
     */
    @GetMapping("/patient/add")
    public String showAddPatientForm(Patient patient) {
        return "patient/add";
    }

    /**
     * Validates add patient form and redirects to patients list if successful.
     *
     * @param patient            patient to be added
     * @param result             result of form validation
     * @param model              holder for context data to be passed from controller to the view, contains list of patients
     * @param redirectAttributes redirection attributes, contains success popup
     * @return add patient page if an error occurred, list of patients otherwise
     */
    @PostMapping("/patient/add")
    public String addPatient(@Valid Patient patient, BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (!result.hasErrors()) {
            patientService.createPatient(patient);
            redirectAttributes.addFlashAttribute("success", "Patient " + patient.getFamily() + " " + patient.getGiven() + " was successfully created.");
            model.addAttribute("patients", patientService.getPatients());
            // redirect to list of patients page
            return "redirect:/patient/list";
        }
        return "patient/add";
    }

    /**
     * Shows update a patient form.
     *
     * @param id    ID of patient to update
     * @param model holder for context data to be passed from controller to the view, contains patient to be updated
     * @return update patient page if error, list of patients otherwise
     */
    @GetMapping("/patient/update/{id}")
    public String showUpdateForm(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Patient> existingPatient = patientService.getPatientById(id);
        if (existingPatient.isPresent()) {
            model.addAttribute("patient", existingPatient.get());
            return "patient/update";
        }
        redirectAttributes.addFlashAttribute("error", "Patient with ID " + id + " does not exist.");
        return "redirect:/patient/list";
    }

    /**
     * Updates a patient.
     *
     * @param id                 ID of patient to be updated
     * @param patient            updated patient
     * @param result             result of validation form
     * @param model              holder for context data to be passed from controller to the view, contains patient to update and list of patients
     * @param redirectAttributes redirection attributes, contains success popup
     * @return list of patients if update is successful, update patient page otherwise
     */
    @PutMapping("/patient/update/{id}")
    public String updatePatient(@PathVariable("id") Integer id, @Valid Patient patient,
                                BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        // check required fields
        if (result.hasErrors()) {
            patient.setId(id);
            model.addAttribute("patient", patient);
            return "patient/update";
        }
        // if valid call service to update Patient
        patient.setId(id);
        patientService.updatePatient(patient);
        // add redirect message
        redirectAttributes.addFlashAttribute("success", "Patient " + patient.getFamily() + " " + patient.getGiven() + " was successfully updated.");
        // update list and return list Patient
        model.addAttribute("patients", patientService.getPatients());
        return "redirect:/patient/list";
    }

    /**
     * Deletes a patient.
     *
     * @param id                 ID of patient to be deleted
     * @param model              holder for context data to be passed from controller to the view, contains list of patients
     * @param redirectAttributes redirection attributes, contains success or failure popup
     * @return list of patients page
     */
    @GetMapping("/patient/delete/{id}")
    public String deletePatient(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        // Find Patient by ID
        Optional<Patient> existingPatient = patientService.getPatientById(id);
        // and delete the patient
        if (existingPatient.isPresent()) {
            Patient patient = existingPatient.get();
            patientService.deletePatient(patient);
            redirectAttributes.addFlashAttribute("success", "Patient " + patient.getFamily() + " " + patient.getGiven() + " was successfully deleted.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Patient with ID " + id + " does not exist.");
        }
        // return to Patient list
        model.addAttribute("patients", patientService.getPatients());
        return "redirect:/patient/list";
    }
}
