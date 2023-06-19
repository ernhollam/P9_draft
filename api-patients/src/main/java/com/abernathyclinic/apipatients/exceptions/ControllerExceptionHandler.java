package com.abernathyclinic.apipatients.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Exception thrown when a resource is not found.
 */
@Slf4j
@RestControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(PatientNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String patientNotFoundException(PatientNotFoundException notFoundException) {
        log.error("Patient was not found.", notFoundException);
        return "Patient was not found:\n" + notFoundException.getMessage();
    }

    @ExceptionHandler(AlreadyExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String alreadyExistsException(AlreadyExistsException alreadyExistsException) {
        log.error("Illegal argument value.", alreadyExistsException);
        return "Illegal argument value:\n" + alreadyExistsException.getMessage();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String returnMessage(Exception exception) {
        log.error("An error occurred.", exception);
        return "An error occurred:\n " + exception.getMessage();
    }
}
