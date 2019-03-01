package galilel.org.galilelwallet.rate;

public class RequestGalilelRateException extends Exception {
    public RequestGalilelRateException(String message) {
        super(message);
    }

    public RequestGalilelRateException(String message, Throwable cause) {
        super(message, cause);
    }

    public RequestGalilelRateException(Exception e) {
        super(e);
    }
}
