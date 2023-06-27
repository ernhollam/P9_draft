package com.abernathyclinic.patients.exception;

/**
 * Exception thrown when the patient already exists in database.
 */
public class AlreadyExistsException extends RuntimeException {
    /**
     * Exception thrown when a user tries to create a patient who already exists in database.
     *
     * @param message Exception message.
     */
    public AlreadyExistsException(String message) {
        super(message);
    }
}