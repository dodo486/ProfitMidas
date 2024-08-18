package bulls.exception;

public class NotEnoughBidAskAmountException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 413759073558297828L;

    public Long remainAmount = 0L;

    public NotEnoughBidAskAmountException(Long remainAmount, long requestAmount) {
        super();
        System.out.println("remainAmount:" + remainAmount + " requestAmount:" + requestAmount);
        this.remainAmount = remainAmount;
    }

    public NotEnoughBidAskAmountException(Long remainAmount, String code) {
        super();
        System.out.println("remainAmount:" + remainAmount + " isinCode:" + code);
        this.remainAmount = remainAmount;
    }

}
