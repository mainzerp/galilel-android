package store;

public class CantInsertAddressException extends Exception {

    public CantInsertAddressException(String message, Throwable cause) {
        super(message, cause);
    }

    public CantInsertAddressException(String message) {
        super(message);
    }
}
