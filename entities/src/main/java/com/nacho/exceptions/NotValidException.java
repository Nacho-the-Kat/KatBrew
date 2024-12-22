package com.nacho.exceptions;

/**
 * Custom Runtime Exception, falls ein Objekt oder Wert nicht valide ist.
 */
public class NotValidException extends RuntimeException {

    public NotValidException() {
    }

    public NotValidException(String message) {
        super(message);
    }
}
