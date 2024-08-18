package bulls.etf;

import bulls.json.index.CodeAndRatio;
import bulls.log.DefaultLogger;
import bulls.order.CodeAmount;
import bulls.staticData.AliasManager;
import bulls.staticData.ProdType.ProdTypeCenter;

import java.util.*;
import java.util.stream.Collectors;

public class PDF {

    public final String etfIsinCode;
    public final Double cash;
    public final int cuAmount;
    public final long netMoneyByClosingPrice;
    public final double yesterdayNAV;
    public HashMap<String, Double> amountMap;
    public List<CodeAmount> amountList = new ArrayList<>();
    public List<CodeAndRatio> codeAndRatioList;
    public List<CodeAmount> futuresList = new ArrayList<>();
    public HashMap<String, Double> ratioMap = new HashMap<>();

    public PDF(PDFBuilder builder) {
        this.etfIsinCode = builder.etfIsinCode;
        this.cash = builder.cash;
        this.amountMap = builder.amountMap;
        this.cuAmount = builder.cuAmount;
        this.netMoneyByClosingPrice = builder.netMoneyByClosingPrice;
        yesterdayNAV = (double) netMoneyByClosingPrice / cuAmount;

        amountMap.forEach((key, value) -> {
            var ca = new CodeAmount(key, value);
            amountList.add(ca);
            if (ProdTypeCenter.Instance.getProdType(key).isFutures())
                futuresList.add(ca);
        });

        codeAndRatioList = builder.crList;
        codeAndRatioList.forEach(cr -> ratioMap.put(cr.code, cr.ratio));
        print();
    }


    public void print() {
        DefaultLogger.logger.info("================ {}  cu 수량:{} 구성종목수:{} ", etfIsinCode, cuAmount, amountMap.size());
        amountMap.entrySet().stream().forEach(en -> {
            String korean = AliasManager.Instance.getKoreanFromIsin(en.getKey());
            DefaultLogger.logger.info("{} {} ", korean, en.getValue());
        });
    }

    public boolean contains(String code) {
        return amountMap.containsKey(code);
    }

    public List<String> sortedListByMoney() {
        List<String> list = ratioMap.entrySet().stream().sorted(Comparator.comparingDouble(Map.Entry::getValue)).map(Map.Entry::getKey).collect(Collectors.toList());
        Collections.reverse(list);
        return list;
    }
}
