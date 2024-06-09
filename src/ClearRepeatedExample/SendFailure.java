package ClearRepeatedExample;

public class SendFailure {
    private final Throwable cause;

    public SendFailure(Throwable cause) {
        this.cause = cause;
    }

    public Throwable getCause() {
        return cause;
    }

    public String getMessage() {
        return cause.getMessage();
    }
}
