package bulls.hephaestus.document;


import bulls.staticData.ProdType.DerivativesUnderlyingType;
import org.bson.codecs.pojo.annotations.BsonIgnore;

public class ETFLpDoc {
    public String isinCode;
    public String korean;
    public String underlying;
    public String 운용사;
    public boolean LP계약여부;
    public String group;
    public String lpAccount;
    public String hedgeAccount;
    public boolean transferAvailable;
    public boolean rebalancingNeeded;
    public int transferReadyCU;

    @BsonIgnore
    public DerivativesUnderlyingType duType;

    @Override
    public String toString() {
        return "ETFLpDoc{" +
                "isinCode='" + isinCode + '\'' +
                ", korean='" + korean + '\'' +
                ", underlying='" + underlying + '\'' +
                ", 운용사='" + 운용사 + '\'' +
                ", LP계약여부=" + LP계약여부 +
                ", group='" + group + '\'' +
                ", lpAccount='" + lpAccount + '\'' +
                ", hedgeAccount='" + hedgeAccount + '\'' +
                ", transferAvailable='" + transferAvailable + '\'' +
                ", transferReadyCU='" + transferReadyCU + '\'' +
                ", duType=" + duType +
                '}';
    }
}
