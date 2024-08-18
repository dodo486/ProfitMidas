package bulls.feed.current.data.statistics;

import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import bulls.dateTime.TimeCenter;
import bulls.db.rdb.SQLDBData;
import bulls.db.rdb.SQLDBDataConvertible;
import bulls.feed.abstraction.BidAskFillUpdater;
import bulls.feed.abstraction.Feed;
import bulls.feed.current.enums.FeedTRCode;
import bulls.feed.current.parser.statistics.시장통계_거래원;
import bulls.staticData.ProdType.ProdTypeCenter;

import java.util.ArrayList;
import java.util.List;

public class 시장통계_거래원Feed extends Feed implements SQLDBDataConvertible {

    public 시장통계_거래원Feed(FeedTRCode trCode, byte[] packet) {
        super(trCode, packet);
    }

    final int size = 70;

    @Override
    public void updateBidAskFill(BidAskFillUpdater dc) {
    }

    public byte[] getCodeByte() {
        return 시장통계_거래원.isinCode.parser().parseByte(rawPacket);
    }

    public String getCode() {
        return new String(getCodeByte());
    }

    public String getBidMemberCode(int idx) {
        return 시장통계_거래원.매수거래원번호.plus(idx * size).parseStr(rawPacket, "").trim();
    }

    public Integer getBidVolume(int idx) {
        return 시장통계_거래원.매수체결수량.plus(idx * size).parseInt(rawPacket);
    }

    public Long getBidAmount(int idx) {
        return 시장통계_거래원.매수거래대금.plus(idx * size).parseLong(rawPacket);
    }

    public String getAskMemberCode(int idx) {
        return 시장통계_거래원.매도거래원번호.plus(idx * size).parseStr(rawPacket, "").trim();
    }

    public Integer getAskVolume(int idx) {
        return 시장통계_거래원.매도체결수량.plus(idx * size).parseInt(rawPacket);
    }

    public Long getAskAmount(int idx) {
        return 시장통계_거래원.매도거래대금.plus(idx * size).parseLong(rawPacket);
    }

    @Override
    public SQLDBData[] getSQLDBDataArray() {
        List<SQLDBData> ret = new ArrayList<>();
        String isincode = getCode();
        String table;
        if (ProdTypeCenter.Instance.getProdType(isincode).isEquityELW()) {
            table = "elw_trader_activity_raw";
        } else {
            table = "eqt_trader_activity_raw";
        }
        String sql = "INSERT INTO " + table + "(t,isincode,dt,bid_cnt,bid_members,bid_volumes,bid_moneys,ask_cnt,ask_members,ask_volumes,ask_moneys) VALUES(?,?,?,?,?,?,?,?,?,?,?)\n" +
                "ON CONFLICT (t,isincode) " +
                "DO UPDATE SET dt=excluded.dt,\n" +
                "bid_cnt=excluded.bid_cnt,bid_members=excluded.bid_members,bid_volumes=excluded.bid_volumes,bid_moneys=excluded.bid_moneys,\n" +
                "ask_cnt=excluded.ask_cnt,ask_members=excluded.ask_members,ask_volumes=excluded.ask_volumes,ask_moneys=excluded.ask_moneys";

        ArrayList<String> bidMembers = new ArrayList<>();
        ArrayList<Integer> bidVolumes = new ArrayList<>();
        ArrayList<Long> bidMoneys = new ArrayList<>();
        ArrayList<String> askMembers = new ArrayList<>();
        ArrayList<Integer> askVolumes = new ArrayList<>();
        ArrayList<Long> askMoneys = new ArrayList<>();
        for (int i = 0; i < 5; ++i) {
            String mcode1 = getBidMemberCode(i);
            if (!mcode1.equals("00000")) {
                bidMembers.add(mcode1);
                bidVolumes.add(getBidVolume(i));
                bidMoneys.add(getBidAmount(i));
            }
            String mcode2 = getAskMemberCode(i);
            if (!mcode2.equals("00000")) {
                askMembers.add(mcode2);
                askVolumes.add(getAskVolume(i));
                askMoneys.add(getAskAmount(i));
            }
        }
        int bidCnt = bidMembers.size();
        String[] bidMembersA = new String[bidCnt];
        bidMembers.toArray(bidMembersA);
//        int[] bidVolumesA = new int[bidCnt];
//        bidVolumes.toArray(bidVolumesA);
//        long[] bidMoneysA = new long[bidCnt];
//        bidMoneys.toArray(bidMoneysA);
        int askCnt = askMembers.size();
        String[] askMembersA = new String[askCnt];
        askMembers.toArray(askMembersA);
//        int[] askVolumesA = new int[askCnt];
//        Long[] askMoneysA = new Long[askCnt];
//        askVolumes.toArray(askVolumesA);
//        askMoneys.toArray(askMoneysA);
        SQLDBData data = new SQLDBData();
        data.sql = sql;
        data.values = new Object[]{
                TimeCenter.Instance.getLocalDateTimeFromFeedTimestamp(this.arrivalStamp),
                isincode,
                TimeCenter.Instance.getDateAsLocalDateTimeType(),
                bidCnt,
                bidMembersA,
                Ints.toArray(bidVolumes),
                Longs.toArray(bidMoneys),
                askCnt,
                askMembersA,
                Ints.toArray(askVolumes),
                Longs.toArray(askMoneys)
        };
        return new SQLDBData[]{data};

//        List<SqlDBData> ret = new ArrayList<>();
//        String isincode = getCode();
//        String table;
//        if (ProdTypeCenter.Instance.getProdType(isincode).isEquityELW()) {
//            table = "elw_trader_activity";
//        } else {
//            table = "eqt_trader_activity";
//        }
//        String sql = "INSERT INTO " + table + "(dt,t,isincode,member_code,is_bid,vol,amt) VALUES(?,?,?,?,?,?,?)\n" +
//                "ON CONFLICT (t,isincode,member_code,is_bid) " +
//                "DO UPDATE SET dt=excluded.dt,vol=excluded.vol,amt=excluded.amt";
//        for (int i = 0; i < 5; ++i) {
//            //bid[i]
//            String mcode1 = getBidMemberCode(i);
//            if (!mcode1.equals("00000")) {
//                SqlDBData data1 = new SqlDBData();
//                data1.sql = sql;
//                data1.values = new Object[]{
//                        TimeCenter.Instance.getDateAsLocalDateTimeType(),
//                        TimeCenter.Instance.getLocalDateTimeFromFeedTimestamp(this.arrivalStamp),
//                        isincode,
//                        MarketDataKRXMemberCode.getName(mcode1),
//                        true,
//                        getBidVolume(i),
//                        getBidAmount(i),
//                };
//                ret.add(data1);
//            }
//            //ask[i]
//            String mcode2 = getAskMemberCode(i);
//            if (!mcode2.equals("00000")) {
//                SqlDBData data2 = new SqlDBData();
//                data2.sql = sql;
//                data2.values = new Object[]{
//                        TimeCenter.Instance.getDateAsLocalDateTimeType(),
//                        TimeCenter.Instance.getLocalDateTimeFromFeedTimestamp(this.arrivalStamp),
//                        isincode,
//                        MarketDataKRXMemberCode.getName(mcode2),
//                        false,
//                        getAskVolume(i),
//                        getAskAmount(i),
//                };
//                ret.add(data2);
//            }
//        }
//        SqlDBData[] arr = new SqlDBData[ret.size()];
//        ret.toArray(arr);
//        return arr;
    }
}
