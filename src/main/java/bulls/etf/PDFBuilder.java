package bulls.etf;

import bulls.exception.InvalidCodeException;
import bulls.exception.NoClosingPriceException;
import bulls.json.index.CodeAndRatio;
import bulls.log.DefaultLogger;
import bulls.staticData.AliasManager;
import bulls.staticData.EquityInfoCenter;
import bulls.staticData.FuturesInfo;
import bulls.staticData.FuturesInfoCenter;
import bulls.staticData.ProdType.ProdType;
import bulls.staticData.ProdType.ProdTypeCenter;
import org.apache.commons.math3.util.FastMath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PDFBuilder {

    public final String etfIsinCode;
    public final int cuAmount;
    //public final String etfShortCode;
    public Double cash = 0d;
    public HashMap<String, Double> amountMap = new HashMap<>();
    public Long netMoneyByClosingPrice = 0L;
    public List<CodeAndRatio> crList;
    public double navYesterday;

    public PDFBuilder(String etfIsinCode, int cuAmount) {
        //this.etfShortCode = etfShortCode;
        this.etfIsinCode = etfIsinCode;//AliasManager.Instance.getIsinFromAlias(etfShortCode);
        this.cuAmount = cuAmount;
        this.crList = new ArrayList<>();
    }

    public PDFBuilder addCash(Double amount) {
        cash += amount;
        return this;
    }

    public PDFBuilder setNavYesterday(double navYesterday) {
        this.navYesterday = navYesterday;
        return this;
    }

    public PDFBuilder addProduct(String isinCode, Double amount) {

        if (isinCode == null) {
            DefaultLogger.logger.error("PDF를 구성하는 isinCode 없음, isinCode:" + isinCode);
        }
        amountMap.put(isinCode, amount);
        return this;
    }

    public PDF build() throws InvalidCodeException, NoClosingPriceException {

        String korean = AliasManager.Instance.getKoreanFromIsin(etfIsinCode);

        for (Map.Entry<String, Double> en : amountMap.entrySet()) {
            String code = en.getKey();
            Double amount = en.getValue();

            ProdType pType = ProdTypeCenter.Instance.getProdType(code);

            if (pType.isEquity()) {
                Integer price = EquityInfoCenter.Instance.get기준가(code); // ClosingPriceCenter.Instance.getClosingPrice(code);
                Double money = amount * price;
                DefaultLogger.logger.info(" PDF {}({}) 의 Equity종목 {}({}) 의 기준가: {} , PDF 수량: {}", korean, etfIsinCode, AliasManager.Instance.getKoreanFromIsin(code), code, price, amount);
                netMoneyByClosingPrice += FastMath.round(money);
            } else {
                FuturesInfo fInfo = FuturesInfoCenter.Instance.getFuturesInfo(code);
                if (fInfo == null)
                    throw new InvalidCodeException(code + " 의 FuturesInfo를 찾을 수 없습니다.");
                Double money = amount * fInfo.기준가 * fInfo.multiplier;
                DefaultLogger.logger.info(" PDF {}({}) 의 파생종목 {}({}) 의 기준가: {} , PDF 수량: {}", korean, etfIsinCode, AliasManager.Instance.getKoreanFromIsin(code), code, fInfo.기준가, amount);
                netMoneyByClosingPrice += FastMath.round(money);
            }

        }

        // 주 목적은 지수 추종이므로 현금분을 고려하지 않는다.
//        netMoneyByClosingPrice += FastMath.round(cash);


        amountMap.entrySet().forEach(en -> {
            String code = en.getKey();
            Double amount = en.getValue();
            try {
                double price;
                double multiplier = 1;
                if (ProdTypeCenter.Instance.getProdType(code).isEquity()) {
                    price = EquityInfoCenter.Instance.get기준가(code);
                } else {
                    price = FuturesInfoCenter.Instance.get기준가(code);
                    multiplier = FuturesInfoCenter.Instance.getPureMultiplier(code);
                }
                Double money = amount * price * multiplier;

                double ratio = money / netMoneyByClosingPrice;
                crList.add(new CodeAndRatio(code, ratio));

            } catch (NoClosingPriceException e) {
                DefaultLogger.logger.error("error found", e);
            }
        });
        double ratioSum = crList.stream().map(cr -> cr.ratio).reduce(0d, (a, b) -> a + b);
        DefaultLogger.logger.info("{}({}) ETF 구성 종목의 Net Money(현금제외) : {} , RatioSum: {} ", korean, etfIsinCode, netMoneyByClosingPrice, ratioSum);
        PDF pdf = new PDF(this);
        return pdf;
    }
}
