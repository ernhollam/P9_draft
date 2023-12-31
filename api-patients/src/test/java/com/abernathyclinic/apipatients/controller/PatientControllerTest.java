package com.abernathyclinic.apipatients.controller;

import com.abernathyclinic.apipatients.exceptions.AlreadyExistsException;
import com.abernathyclinic.apipatients.exceptions.PatientNotFoundException;
import com.abernathyclinic.apipatients.model.Patient;
import com.abernathyclinic.apipatients.service.PatientService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = PatientController.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PatientControllerTest {
    @Autowired
    MockMvc    mockMvc;
    @MockBean
    PatientService service;

    private Patient testNone;
    private List<Patient> patients;

    @BeforeAll
    public void setUp() {
        testNone = new Patient(1, "TestNone", "Test", LocalDate.of(1966, 12, 31), "F", "1 Brookside St", "100-222-3333");
        Patient testBorderline = new Patient(2, "TestBorderline", "Test", LocalDate.of(1945, 6, 24), "M", "2 High St", "200-333-4444");
        patients = List.of(testNone, testBorderline);
    }

    @Test
    @DisplayName("Return list of patients")
    public void homeTest() throws Exception {
        when(service.getPatients()).thenReturn(patients);

        mockMvc.perform(get("/patient/list"))
                .andDo(print())
               .andExpect(status().is2xxSuccessful())
               .andExpect(model().attributeExists("patients"));
    }

    @Test
    @DisplayName("Show add patient form")
    public void addPatientFormTest() throws Exception {
        mockMvc.perform(get("/patient/add"))
                .andDo(print())
               .andExpect(status().is2xxSuccessful())
               .andExpect(view().name("patient/add"));
    }

    @Test
    @DisplayName("Add new patient successful")
    public void validateTest() throws Exception {
        when(service.createPatient(testNone)).thenReturn(testNone);

        mockMvc.perform(post("/patient/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("family", "TestNone")
                        .param("given", "Test")
                        .param("dob", "1966-12-31"))
                .andDo(print())
               .andExpect(status().is3xxRedirection())
               .andExpect(flash().attributeExists("success"))
               .andExpect(view().name("redirect:/patient/list"));
    }

    @Test
    @DisplayName("Add new patient with patient who already exists")
    public void createPatientFailed() throws Exception {
        when(service.createPatient(any(Patient.class))).thenThrow(AlreadyExistsException.class);

        mockMvc.perform(post("/patient/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("family", "TestNone")
                        .param("given", "Test")
                        .param("dob", "1966-12-31"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("error"))
                .andExpect(view().name("redirect:/patient/list"));
    }

    @Test
    @DisplayName("Show update form successful")
    public void showUpdateFormIsSuccessful() throws Exception {
        when(service.getPatientById(1)).thenReturn(Optional.of(testNone));
        when(service.getPatients()).thenReturn(patients);

        mockMvc.perform(get("/patient/update/{id}", "1"))
                .andDo(print())
               .andExpect(status().is2xxSuccessful())
               .andExpect(model().attributeExists("patient"))
               .andExpect(view().name("patient/update"));
    }

    @Test
    @DisplayName("Show update form failed")
    public void showUpdateFormFails() throws Exception {
        when(service.getPatientById(3)).thenReturn(Optional.empty());
        when(service.getPatients()).thenReturn(patients);

        mockMvc.perform(get("/patient/update/{id}", "3"))
                .andDo(print())
               .andExpect(status().is3xxRedirection())
               .andExpect(flash().attributeExists("error"))
               .andExpect(view().name("redirect:/patient/list"));
    }

    @Test
    @DisplayName("Update patient successful")
    public void updatePatientTest() throws Exception {
        when(service.updatePatient(testNone)).thenReturn(testNone);

        mockMvc.perform(post("/patient/update/{id}", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("family", "TestNone")
                        .param("given", "Test")
                        .param("dob", "1966-11-30"))
                .andDo(print())
               .andExpect(status().is3xxRedirection())
               .andExpect(flash().attributeExists("success"))
               .andExpect(view().name("redirect:/patient/list"));
    }

    @Test
    @DisplayName("Update patient failed")
    public void updatePatientFailed() throws Exception {
        when(service.updatePatient(any(Patient.class))).thenThrow(PatientNotFoundException.class);

        mockMvc.perform(post("/patient/update/{id}", "100")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("family", "TestNone")
                        .param("given", "Test")
                        .param("dob", "1966-11-30"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("error"))
                .andExpect(view().name("redirect:/patient/list"));
    }

    @Test
    @DisplayName("Delete patient successful")
    public void deletePatientIsSuccessful() throws Exception {
        when(service.getPatientById(any(Integer.class))).thenReturn(Optional.of(testNone));
        when(service.getPatients()).thenReturn(patients);

        mockMvc.perform(get("/patient/delete/{id}", "1"))
                .andDo(print())
               .andExpect(status().is3xxRedirection())
               .andExpect(flash().attributeExists("success"))
               .andExpect(view().name("redirect:/patient/list"));
    }

    @Test
    @DisplayName("Delete patient failed")
    public void deletePatientFailed() throws Exception {
        when(service.getPatientById(any(Integer.class))).thenReturn(Optional.empty());
        when(service.getPatients()).thenReturn(patients);

        mockMvc.perform(get("/patient/delete/{id}", "1"))
                .andDo(print())
               .andExpect(status().is3xxRedirection())
               .andExpect(flash().attributeExists("error"))
               .andExpect(view().name("redirect:/patient/list"));
    }
}
