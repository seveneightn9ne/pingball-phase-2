package common;

/**
 * Exception to signify that a rep invariant has been violated.
 */
public class RepInvariantException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a RepInvariantException with a detail message.
     * @param message description of the error
     */
    public RepInvariantException(String message) {
        super(message);
    }

    /**
     * Constructs a RepInvariantException with a detail message and cause.
     * @param message description of the error
     * @param cause exception that caused this error
     */
    public RepInvariantException(String message, Throwable cause) {
        super(message, cause);
    }
}