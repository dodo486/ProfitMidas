package bulls.dmaLog.shortSell;

import bulls.designTemplate.HasTime;
import bulls.staticData.AliasManager;
import org.bson.Document;

import java.time.LocalTime;

public class BorrowingData implements HasTime {
    public final String isinCode;
    public final String account;
    public final String type;
    public final int amount;

    public LocalTime t;

    public BorrowingData(LocalTime t, String isinCode, String account, String type, int amount) {
        this.t = t;
        this.isinCode = isinCode;
        this.account = account;
        this.type = type;
        this.amount = amount;
    }

    @Override
    public LocalTime getTime() {
        return t;
    }

    @Override
    public void setTime(LocalTime t) {
        this.t = t;
    }

    @Override
    public String toString() {
        return "BorrowingData{" +
                "isinCode='" + isinCode + '\'' +
                ", account='" + account + '\'' +
                ", amount=" + amount +
                ", t=" + t +
                '}';
    }

    public Document getDocument() {
        Document d = new Document();
        d.append("대차거래시각", t)
                .append("종목코드", isinCode)
                .append("종목명", AliasManager.Instance.getKoreanFromIsin(isinCode))
                .append("유형", type)
                .append("계좌번호", account);
        if (amount > 0)
            d.append("차입", amount);
        else
            d.append("상환", -amount);

        return d;
    }
}
