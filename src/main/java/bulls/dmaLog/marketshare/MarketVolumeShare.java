package bulls.dmaLog.marketshare;

import com.fasterxml.jackson.databind.node.ObjectNode;
import bulls.analysis.contractType.ContractTypeChecker;
import bulls.dateTime.TimeCenter;
import bulls.designTemplate.JsonConvertible;
import bulls.dmaLog.TradeDMALog;
import bulls.order.CodeAndBook;
import bulls.staticData.AliasManager;
import bulls.staticData.TempConf;
import org.bson.Document;

import java.util.Date;

public class MarketVolumeShare implements MarketShare, JsonConvertible {
    private final CodeAndBook codeAndBook;
    private final MarketVolume volume;
    private final ContractTypeChecker checker;

    public MarketVolumeShare(CodeAndBook codeAndBook) {
        this.codeAndBook = codeAndBook;
        this.volume = new MarketVolume();
        this.checker = new ContractTypeChecker(codeAndBook);
    }

    public void update(TradeDMALog log) {
        volume.add(log.getAskBidType(), log.getKrxExecutionQuantity());
        checker.update(log);
    }

    public Document getDataDocument() {
        Document obj = new Document();

        obj.put("isinCode", codeAndBook.getCode());
        obj.put("bookCode", codeAndBook.getBookCode());
        obj.put("bidQuantity", volume.getBidVolume());
        obj.put("askQuantity", volume.getAskVolume());
        obj.put("totalQuantity", volume.getTotal());
        obj.put("location", TempConf.FEP_LOCATION.getLocationString());
        obj.put("date", TimeCenter.Instance.getDateAsDateType());
        obj.put("lastUpdate", new Date());
        // 220629 Nested Array가 포함된 Document를 MongoDB에 삽입하면 Nested Array 정보가 날아가는 문제가 있다.
        // 정보를 제대로 업로드하기 위해 obj에 Array를 삽입하지 않고 Array 정보들을 꺼내서 각각 obj에 삽입한다.
//        obj.put("contractTypeSummary", checker.toObjectNode());
        var map = checker.getData();
        for (var entry : map.entrySet()) {
            String key = entry.getKey();
            int value = entry.getValue();
            obj.put(key, value);
        }

        return obj;
    }

    public Document getQueryDocument() {
        Document obj = new Document();

        obj.put("isinCode", codeAndBook.getCode());
        obj.put("bookCode", codeAndBook.getBookCode());
        obj.put("location", TempConf.FEP_LOCATION.getLocationString());
        obj.put("date", TimeCenter.Instance.getDateAsDateType());

        return obj;
    }

    public String getCollectionName() {
        return "marketShare";
    }

    @Override
    public void fillObjectNode(ObjectNode node) {
        node.put("location", TempConf.FEP_LOCATION.getLocationString());
        node.put("issueName", AliasManager.Instance.getKoreanFromIsin(codeAndBook.getCode()));
        node.put("isinCode", codeAndBook.getCode());
        node.put("bookCode", codeAndBook.getBookCode());
        node.put("bidQuantity", volume.getBidVolume());
        node.put("askQuantity", volume.getAskVolume());
        node.put("totalQuantity", volume.getTotal());
        ObjectNode contractTypeSummaryNode = node.putObject("contractTypeSummary");
        checker.fillObjectNode(contractTypeSummaryNode);
    }
}
