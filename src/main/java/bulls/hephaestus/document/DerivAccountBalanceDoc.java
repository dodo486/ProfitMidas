package bulls.hephaestus.document;

import java.util.Date;

public class DerivAccountBalanceDoc {

    public int seq;
    public String account;
    public String isinCode;
    public long prevRemainQty;
    public long remainQty;

    public long sellQty;
    public long sellAmt;

    public long buyQty;
    public long buyAmt;

    public long sQty;
    public long sPrice;

    public Date date;
    public Date lastUpdate;
}
