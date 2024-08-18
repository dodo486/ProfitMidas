package bulls.feed.next.data.statistics;

import bulls.dateTime.TimeCenter;
import bulls.db.rdb.SQLDBData;
import bulls.db.rdb.SQLDBDataConvertible;
import bulls.feed.abstraction.BidAskFillUpdater;
import bulls.feed.abstraction.Feed;
import bulls.feed.current.enums.FeedTRCode;
import bulls.feed.next.parser.statistics.종목별_투자자별_종가통계;
import bulls.marketData.MarketDataKRXInvestorCode;
import bulls.staticData.ProdType.ProdTypeCenter;

import java.util.ArrayList;
import java.util.List;

public class 종목별_투자자별_종가통계Feed extends Feed implements SQLDBDataConvertible {

    public 종목별_투자자별_종가통계Feed(FeedTRCode trCode, byte[] packet) {
        super(trCode, packet);
    }

    final int size = 64;

    @Override
    public void updateBidAskFill(BidAskFillUpdater dc) {
    }

    public byte[] getCodeByte() {
        return 종목별_투자자별_종가통계.isinCode.parser().parseByte(rawPacket);
    }

    public String getCode() {
        return new String(getCodeByte());
    }

    public String getInvestorCode(int idx) {
        return 종목별_투자자별_종가통계.투자자코드.plus(idx * size).parseStr(rawPacket, "").trim();
    }

    public Integer getBidVolume(int idx) {
        return 종목별_투자자별_종가통계.매수체결수량.plus(idx * size).parseInt(rawPacket);
    }

    public Long getBidMoney(int idx) {
        return 종목별_투자자별_종가통계.매수거래대금.plus(idx * size).parseLong(rawPacket);
    }

    public Integer getAskVolume(int idx) {
        return 종목별_투자자별_종가통계.매도체결수량.plus(idx * size).parseInt(rawPacket);
    }

    public Long getAskMoney(int idx) {
        return 종목별_투자자별_종가통계.매도거래대금.plus(idx * size).parseLong(rawPacket);
    }

    @Override
    public SQLDBData[] getSQLDBDataArray() {
        List<SQLDBData> ret = new ArrayList<>();
        String isincode = getCode();
        if (isincode.equals("999999999999"))
            return null;
        String table;
        if (ProdTypeCenter.Instance.getProdType(isincode).isEquityELW()) {
            table = "elw_investor_trading_stat";
        } else {
            table = "eqt_investor_trading_stat";
        }
        String sql = "INSERT INTO " + table + "(t,isincode,investor_code,bid_vol,bid_money,ask_vol,ask_money) VALUES(?,?,?,?,?,?,?)\n" +
                "ON CONFLICT (t,isincode,investor_code) " +
                "DO UPDATE SET bid_vol=excluded.bid_vol,bid_money=excluded.bid_money,ask_vol=excluded.ask_vol,ask_money=excluded.ask_money";
        for (int i = 0; i < 12; ++i) {
            //bid[i]
            String invCode = getInvestorCode(i);
            MarketDataKRXInvestorCode code = MarketDataKRXInvestorCode.parse(invCode);
            if (code != MarketDataKRXInvestorCode.UNKNOWN) {
                int bidVol = getBidVolume(i);
                int askVol = getAskVolume(i);
                if (bidVol == 0 || askVol == 0)
                    continue;
                SQLDBData data = new SQLDBData();
                data.sql = sql;
                data.values = new Object[]{
                        TimeCenter.Instance.today,
                        isincode,
                        invCode,
                        getBidVolume(i),
                        getBidMoney(i),
                        getAskVolume(i),
                        getAskMoney(i)
                };
                ret.add(data);
            }
        }
        SQLDBData[] arr = new SQLDBData[ret.size()];
        ret.toArray(arr);
        return arr;
    }
}
