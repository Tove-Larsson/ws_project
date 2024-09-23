package com.tove.ws_project.exception;

public class InvalidGameIdException extends RuntimeException {
    public InvalidGameIdException(String message) {
        super(message);
    }
}
