package bulls.exception;

public class InvalidBookException extends Exception {

    public InvalidBookException(String bookCode) {
        super(bookCode + " 가 없습니다.");
    }
}
