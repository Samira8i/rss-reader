package ru.itis.exception;

public class InvalidRssUrlException extends RuntimeException {
    public InvalidRssUrlException(String message) {
        super(message);
    }
}