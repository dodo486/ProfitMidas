package bulls.hephaestus.document;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StockLendingBorrowing {
    static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    public Date date;
    public int seq;
    public String account;
    public String accountName;
    public String isinCode;
    public String korean;
    public String slbType;
    public double rate;
    public long qty;
    public boolean isRequested;
    public boolean isProcessed;
    public Date lastUpdate;

    public String getKey() {
        return sdf.format(date) + "_" + seq;
    }
}
