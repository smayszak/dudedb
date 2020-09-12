package org.mayszak.io.exceptions;

/**
 * This exception indicates that the configured PartitionResolver was unable to locate a partition for the given
 * key. Subclasses of Throwable are not permitted to carry a generic parameter in Java, so we can only return the key in
 * String form.
 */
public class UnknownPartitionException extends Exception {
    private final String key;

    public UnknownPartitionException(String message, String key) {
        super(message);
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
