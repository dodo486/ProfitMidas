package bulls.datastructure;

public class BidAskData {

    public String isinCode;
    public int[] askPrice;
    public int[] askAmount;
    public int[] bidPrice;
    public int[] bidAmount;

    public int totalBidAmount;
    public int totalAskAmount;

    public BidAskData(int depth) {
        askPrice = new int[depth];
        askAmount = new int[depth];
        bidPrice = new int[depth];
        bidAmount = new int[depth];
    }

    public BidAskData(int depth, String code) {
        askPrice = new int[depth];
        askAmount = new int[depth];
        bidPrice = new int[depth];
        bidAmount = new int[depth];
        this.isinCode = code;
    }


    //dummy constructor for JSON parsing do not erase
    public BidAskData() {

    }


}
