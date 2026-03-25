package ru.itis.exception;

public class SourceAlreadyExistsException extends RuntimeException {
    public SourceAlreadyExistsException(String message) {
        super(message);
    }
}