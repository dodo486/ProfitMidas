package bulls.exception;

public class ConfigurationException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = -295695825078044277L;

    public ConfigurationException(String message) {
        super(message);
    }

    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
