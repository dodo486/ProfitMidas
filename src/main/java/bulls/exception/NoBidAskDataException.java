package bulls.exception;

public class NoBidAskDataException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 2193331078794226473L;

    public final String code;

    public NoBidAskDataException(String msg, String code) {
        super(code + msg);
        this.code = code;
    }

}
