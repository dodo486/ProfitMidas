package bulls.feed.next.data.etf;

import bulls.dateTime.TimeCenter;
import bulls.db.mongodb.DBData;
import bulls.db.mongodb.MongoDBCollectionName;
import bulls.db.mongodb.MongoDBDBName;
import bulls.etf.PDFCenter;
import bulls.exception.InvalidCodeException;
import bulls.exception.NoClosingPriceException;
import bulls.feed.abstraction.BidAskFillUpdater;
import bulls.feed.abstraction.Feed;
import bulls.feed.current.enums.FeedTRCode;
import bulls.feed.current.parser.etf.ETF_PDF;
import bulls.staticData.TempConf;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import java.util.Date;

public class ETF_PDFFeed extends Feed implements DBData {
    public ETF_PDFFeed(FeedTRCode trCode, byte[] packet) {
        super(trCode, packet);
        String line = new String(rawPacket);
        String etfCode = getETFCode();
        String isinCode = ETF_PDF.isinCodePdfMember.parser().parseStr(line);
        double amount = getAmount();
        if(TempConf.UPDATE_FROM_FEED) {
            try {
                PDFCenter.Instance.updateFromFeed(etfCode, isinCode, amount);
            } catch (InvalidCodeException | NoClosingPriceException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void updateBidAskFill(BidAskFillUpdater dc) {
        // PDF is batch info
    }

    public byte[] getETFCodeByte() {
        return ETF_PDF.isinCodeEtf.parser().parseByte(rawPacket);
    }

    public String getETFCode() {
        return new String(getETFCodeByte());
    }

    public byte[] getCodePdfMemberByte() {
        return ETF_PDF.codePdfMember.parser().parseByte(rawPacket);
    }

    public String getCodePdfMember() {
        return new String(getCodePdfMemberByte());
    }

    public Double getAmount() {
        return ETF_PDF.구성종목수량.parser().parseDoubleInsertDot(rawPacket);
    }

    public Integer getMarketCode() {
        return ETF_PDF.구성종목시장구분.parser().parseInt(rawPacket);
    }

    @Override
    public String getDBName() {
        return MongoDBDBName.BATCH;
    }

    @Override
    public String getCollectionName() {
        return MongoDBCollectionName.PDF;
    }

    @Override
    public DBObject toDBObject() {
        String line = new String(rawPacket);
        String etfCode = getETFCode();
        String isinCode = ETF_PDF.isinCodePdfMember.parser().parseStr(line);
        Double amount = getAmount();
        DBObject ob = new BasicDBObject();
        ob.put("etfIsinCode", etfCode);
        ob.put("amount", amount);
        ob.put("isinCode", isinCode);
        ob.put("date", TimeCenter.Instance.getDateAsDateType());
        ob.put("lastUpdate", new Date());

        //DefaultLogger.logger.debug("ETF PDF 정보 업데이트 : {}", ob.toString());
        return ob;
    }

    @Override
    public DBObject query() {

        String etfCode = getETFCode();
        String line = new String(rawPacket);
        String isinCode = ETF_PDF.isinCodePdfMember.parser().parseStr(line);
        DBObject ob = new BasicDBObject();
        ob.put("etfIsinCode", etfCode);
        ob.put("isinCode", isinCode);
        ob.put("date", TimeCenter.Instance.getDateAsDateType());
        return ob;
    }
}
