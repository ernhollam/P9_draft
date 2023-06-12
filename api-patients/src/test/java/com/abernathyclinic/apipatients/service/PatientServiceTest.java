package com.abernathyclinic.apipatients.service;

import com.abernathyclinic.apipatients.exceptions.AlreadyExistsException;
import com.abernathyclinic.apipatients.exceptions.PatientNotFoundException;
import com.abernathyclinic.apipatients.model.Patient;
import com.abernathyclinic.apipatients.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@Import(PatientService.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PatientServiceTest {
    /**
     * Class under test
     */
    @Autowired
    PatientService patientService;

    @MockBean
    PatientRepository patientRepository;

    private Patient testNone;
    private Patient testBorderline;

    @BeforeEach
    void setUp() {
        testNone = new Patient(1, "TestNone", "Test", LocalDate.of(1966, 12, 31), "F", "1 Brookside St", "100-222-3333");
        testBorderline = new Patient(2, "TestBorderline", "Test", LocalDate.of(1945, 6, 24), "M", "2 High St", "200-333-4444");
    }

    @Test
    @DisplayName("Registering new patient with valid information should save patient to database")
    void createPatient_whoDoesNotAlreadyExist_shouldCreate_newPatient() {
        when(patientRepository.existsById(any(Integer.class))).thenReturn(false);
        when(patientRepository.save(any(Patient.class))).thenReturn(testNone);

        testNone = patientService.createPatient(testNone);

        verify(patientRepository, times(1)).save(any(Patient.class));
        assertThat(testNone).isNotNull();
    }

    @Test
    @DisplayName("Registering a patient who already exists should throw AlreadyExistException")
    void createPatient_whoAlreadyExists_shouldThrow_AlreadyExistException() {
        when(patientRepository.existsById(any(Integer.class))).thenReturn(true);
        assertThrows(AlreadyExistsException.class, () -> patientService.createPatient(testNone));

    }

    @Test
    @DisplayName("getPatients() should return list of existing patients")
    void getPatients() {
        when(patientRepository.findAll()).thenReturn(List.of(testNone, testBorderline));

        List<Patient> result = patientService.getPatients();

        assertTrue(result.contains(testNone));
        assertTrue(result.contains(testBorderline));
    }

    @Test
    @DisplayName("Id should not be null when calling getPatientById")
    void getPatientById_whenIDIsNull_shouldThrowException() {
        assertThrows(IllegalArgumentException.class,
                () -> patientService.getPatientById(null));
    }

    @Test
    @DisplayName("getPatientById() should return Optional of existing patient")
    void getPatientById() {
        when(patientRepository.findById(testNone.getId())).thenReturn(Optional.ofNullable(testNone));
        Optional<Patient> result = patientService.getPatientById(testNone.getId());
        assertTrue(result.isPresent());
        assertEquals(testNone.getId(), result.get().getId());
    }

    @Test
    @DisplayName("Updating patient who does not exist should throw PatientNotFoundException")
    void updatePatient_whoDoesNotExist_shouldThrow_PatientNotFoundException() {
        when(patientRepository.existsById(any(Integer.class))).thenReturn(false);
        assertThrows(PatientNotFoundException.class, () -> patientService.updatePatient(testNone));
    }

    @Test
    @DisplayName("Updating existing patient should save changes to database")
    void updatePatient_whoExists_shouldUpdate_existingPatient() {
        when(patientRepository.existsById(any(Integer.class))).thenReturn(true);
        String expectedFamilyName = "New family name";
        testNone.setFamily(expectedFamilyName);
        when(patientRepository.save(any(Patient.class))).thenReturn(testNone);

        testNone = patientService.updatePatient(testNone);

        verify(patientRepository, times(1)).save(any(Patient.class));
        assertThat(testNone).isNotNull();
        assertEquals(expectedFamilyName, testNone.getFamily());
    }

    @Test
    @DisplayName("Patient should not be null when calling deletePatient()")
    void deletePatient_whenPatientIsNull_shouldThrowException() {
        assertThrows(IllegalArgumentException.class,
                () -> patientService.deletePatient(null));
    }
    
    @Test
    @DisplayName("Updating existing patient should delete them from database")
    void deletePatient() {
        when(patientRepository.existsById(any(Integer.class))).thenReturn(true);
        Integer idBeforeDeletion = testNone.getId();
        patientService.deletePatient(testNone);
        assertTrue(patientService.getPatientById(idBeforeDeletion).isEmpty());
    }
}