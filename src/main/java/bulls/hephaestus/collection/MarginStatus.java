package bulls.hephaestus.collection;

import bulls.channel.hanwhaDMA.증거금;
import bulls.dateTime.TimeCenter;
import bulls.db.mongodb.DBCenter;
import bulls.db.mongodb.MongoDBCollectionName;
import bulls.db.mongodb.MongoDBDBName;
import bulls.hephaestus.document.MarginDoc;
import bulls.thread.LazyWorkCenter;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;

public enum MarginStatus {
    Instance;

    public void parseMarginAndUpdateDB(byte[] packet) {
        Runnable r = () -> {
            try {
                String account = 증거금.계좌번호.parser().parseStr(packet);
                double 총한도 = 증거금.총한도.parser().parseDouble(packet);
                double 사용한도 = 증거금.사용한도.parser().parseDouble(packet);

                MarginDoc doc = new MarginDoc();
                doc.account = account;
                doc.총한도 = 총한도;
                doc.사용한도 = 사용한도;
                doc.percentage = 사용한도 / 총한도;
                doc.lastUpdate = TimeCenter.getLocalDateTimeAsDateType(LocalDateTime.now());

                DBCenter.Instance.updateBulk(MongoDBDBName.RISK, MongoDBCollectionName.ACCOUNT_MARGIN_INFO, doc);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        };
        LazyWorkCenter.instance.executeLazy(r);
    }
}
