package bulls.hephaestus.document;

public class UnderlyingMergedSnapShot {

    public long sDelta;
    public long rDelta;
    public long nDelta;
    public long netDelta;
    public long sPrice;
    public long rPrice;
    public long nPrice;
    public long moneyOpen;
    public double tEval;
    public double tTheo;
    public double tRealized;
    public double eval;
    public double theo;
    public double realized;
    public double rExpTheoIncrement;
    public double nExpTheoIncrement;
    public UnderlyingMergedSnapShot(long stockDelta, long recentSFDelta, long nextSFDelta, long stockPrice, long recentSFPrice, long nextSFPrice,
                                    double tEval, double tTheo, double tRealized, double eval, double theo, double realized, double rExpTheoIncrement, double nExpTheoIncrement){
        this.sDelta = stockDelta;
        this.rDelta = recentSFDelta;
        this.nDelta = nextSFDelta;
        this.netDelta = stockDelta + recentSFDelta + nextSFDelta;
        this.sPrice = stockPrice;
        this.rPrice = recentSFPrice;
        this.nPrice = nextSFPrice;
        this.moneyOpen = (stockPrice * stockDelta) + (recentSFDelta * recentSFPrice) + (nextSFDelta * nextSFPrice);
        this.tEval = tEval;
        this.tTheo = tTheo;
        this.tRealized = tRealized;
        this.eval = eval;
        this.theo = theo;
        this.realized = realized;
        this.rExpTheoIncrement = rExpTheoIncrement;
        this.nExpTheoIncrement = nExpTheoIncrement;
    }
}
