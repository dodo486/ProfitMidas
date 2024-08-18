package bulls.etf;

import bulls.dateTime.DateCenter;
import bulls.dateTime.TimeCenter;
import bulls.db.mongodb.DBCenter;
import bulls.db.mongodb.MongoDBCollectionName;
import bulls.db.mongodb.MongoDBDBName;
import bulls.designTemplate.EarlyInitialize;
import bulls.exception.InvalidCodeException;
import bulls.exception.NoClosingPriceException;
import bulls.exception.UnidentifiedStockCodeException;
import bulls.hephaestus.HephaLogType;
import bulls.hephaestus.collection.ETFLpMaster;
import bulls.hephaestus.document.ServerMsgDoc;
import bulls.log.DefaultLogger;
import bulls.server.ServerMessageSender;
import bulls.staticData.AliasManager;
import bulls.staticData.PredefinedIsinCode;
import bulls.staticData.TempConf;
import org.bson.Document;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.*;

public enum PDFCenter implements EarlyInitialize {
    Instance;

    //    public static final double rCarry = 0.0165;
//    public static final double rBid = 0.0165;
    public static final double rTrade = 0.0001;
    public final Set<String> compactPdfSet;
    public final HashMap<String, PDF> pdfMap = new HashMap<>();
    public final HashMap<String, Double> navMap = new HashMap<>();
    public HashMap<String, PDFBuilder> pdfBuilderMap = new HashMap<>();


    PDFCenter() {
        Date today = TimeCenter.Instance.getDateAsDateType();
        List<String> compactPdfList = List.of(
                PredefinedIsinCode.KODEX_200,
                PredefinedIsinCode.KODEX_LEVERAGE,
                PredefinedIsinCode.KODEX_INVERSE,
                PredefinedIsinCode.KODEX_200_FUTURES_INVERSE_2X,
                PredefinedIsinCode.KODEX_KRX300,
                PredefinedIsinCode.KODEX_KQ150,
                PredefinedIsinCode.KODEX_KQ150_LEVERAGE,
                PredefinedIsinCode.KODEX_KQ150_FUTURES_INVERSE,
                PredefinedIsinCode.TIGER_200,
                PredefinedIsinCode.TIGER_LEVERAGE,
                PredefinedIsinCode.TIGER_200_FUTURES_LEVERAGE,
                PredefinedIsinCode.TIGER_200_FUTURES_INVERSE_2X,
                PredefinedIsinCode.TIGER_KRX300,
                PredefinedIsinCode.TIGER_KQ150,
                PredefinedIsinCode.TIGER_KQ150_LEVERAGE,
                PredefinedIsinCode.TIGER_KQ150_FUTURES_INVERSE,
                PredefinedIsinCode.TIGER200_HEAVY_INDUSTRY,
                PredefinedIsinCode.TIGER200_IT, // TIGER 200 IT
                PredefinedIsinCode.TIGER200_HEALTHCARE,
                PredefinedIsinCode.TIGER200_CONSTRUCT,
                PredefinedIsinCode.TIGER200_ENERGY,
                PredefinedIsinCode.TIGER200_FINANCE,
                PredefinedIsinCode.TIGER200_LIFE,
                PredefinedIsinCode.TIGER200_산업재,
                PredefinedIsinCode.TIGER200_STEEL,
                PredefinedIsinCode.TIGER200_경기소비재,
                PredefinedIsinCode.TIGER_배당성장,
                PredefinedIsinCode.TIGER_고배당,
                PredefinedIsinCode.TIGER_KRX_BBIG_K뉴딜,
                PredefinedIsinCode.TIGER_KRX_바이오_K뉴딜,
                PredefinedIsinCode.TIGER_KRX_2차전지_K뉴딜
        );

        // ETF LP 컬렉션에 등록된 종목들도 추가해준다.
        Set<String> etfLPIsinCodeSet = new HashSet<>();
        etfLPIsinCodeSet.addAll(ETFLpMaster.Instance.getEtfLpIsinCodeSet());
        etfLPIsinCodeSet.addAll(compactPdfList);
        compactPdfSet = etfLPIsinCodeSet;

        if (TempConf.LOAD_MINIMAL_PDF)
            loadCompactPDF(today);
        else
            loadAllPDF(today);

        pdfMap.keySet().forEach(etfCode -> DefaultLogger.logger.info("{}({}) 종목의 PDF 정보가 로딩 완료 되었습니다.", AliasManager.Instance.getKoreanFromIsin(etfCode), etfCode));

        if (TempConf.LOAD_PREVIOUS_PDF_ISINCODE_LIST == null)
            return;

        Set<String> usingPreviousPdfEtfIsinCodeSet = TempConf.getStringAsSet(TempConf.LOAD_PREVIOUS_PDF_ISINCODE_LIST);
        for (String etfIsinCode : usingPreviousPdfEtfIsinCodeSet) {
            LocalDate previousDay = TimeCenter.Instance.today.minusDays(1);
            boolean findPreviousDay = false;
            // 어제부터 8일 전까지 찾아보기
            for (int i = 0; i < 7; i++) {
                Date queryDate = TimeCenter.getLocalDateAsDateType(previousDay);
                Document query = new Document("date", queryDate).append("etfIsinCode", etfIsinCode);
                Document d = DBCenter.Instance.findIterable(MongoDBDBName.BATCH, MongoDBCollectionName.PDF, query).first();
                if (d != null) {
                    findPreviousDay = true;
                    break;
                }

                previousDay = previousDay.minusDays(1);
            }

            if (!findPreviousDay) {
                String msg = "이전 영업일의 PDF 정보를 찾지 못해 이전 영업일의 PDF 정보를 불러오는데 실패했습니다 - " + AliasManager.Instance.getKoreanFromIsin(etfIsinCode) + " (" + etfIsinCode + ")";
                DefaultLogger.logger.error(msg);
                ServerMessageSender.writeServerMessage(this.getClass(), "OracleArena", "알림", msg);
                continue;
            }

            try {
                Document etfBatchDoc = DBCenter.Instance.findIterable(MongoDBDBName.BATCH, MongoDBCollectionName.ETF_BATCH, new Document("isinCode", etfIsinCode)).first();
                if (etfBatchDoc == null) {
                    String errorMsg = "ETF 배치정보를 찾지 못해 이전 영업일의 PDF 정보를 불러오는데 실패했습니다 - " + AliasManager.Instance.getKoreanFromIsin(etfIsinCode) + " (" + etfIsinCode + ")";
                    ServerMessageSender.writeServerMessage(this.getClass(), "OracleArena", "알림", errorMsg);
                    System.out.println(errorMsg);
                    continue;
                }

                int cuAmt = etfBatchDoc.getInteger("cuAmt");
                Double nav = etfBatchDoc.getDouble("nav");
                if (nav != null)
                    navMap.put(etfIsinCode, nav);

                loadFromDB(etfIsinCode, cuAmt, TimeCenter.getLocalDateAsDateType(previousDay));

                String msg = "이전 영업일의 PDF 정보를 불러왔습니다 - " + AliasManager.Instance.getKoreanFromIsin(etfIsinCode) + " (" + etfIsinCode + ") : " + previousDay;
                ServerMessageSender.writeServerMessage(this.getClass(), "OracleArena", "알림", msg);
                System.out.println(msg);

            } catch (Exception e) {
                e.printStackTrace();
                String errorMsg = "예외가 발생하여 이전 영업일의 PDF 정보를 불러오는데 실패했습니다 - " + AliasManager.Instance.getKoreanFromIsin(etfIsinCode) + " (" + etfIsinCode + ")";
                ServerMessageSender.writeServerMessage(this.getClass(), "OracleArena", "알림", errorMsg, e);
                System.out.println(errorMsg);
            }
        }
    }

    public static Set<String> getIsinUnionOfPDFFromDB(List<String> etfIsinCodeList) {
        HashSet<String> ret = new HashSet<>();

        Document query = new Document("etfIsinCode", new Document("$in", etfIsinCodeList));
        var col = DBCenter.Instance.findIterable(MongoDBDBName.BATCH, MongoDBCollectionName.PDF, query);
        for (Document doc : col) {
            String isinCode = doc.getString("isinCode");
            ret.add(isinCode);
        }

        return ret;
    }

    public static void main(String[] args) throws UnidentifiedStockCodeException, NoClosingPriceException, IOException, InvalidCodeException {
        System.out.println(new Date(System.currentTimeMillis() / TempConf.DAY_IN_MILSEC * TempConf.DAY_IN_MILSEC));
        System.out.println(DateCenter.Instance.getTodayDate());
        PDFCenter.Instance.loadFromDB("139220", 20000, DateCenter.Instance.getTodayDate());
    }

    public void updateFromFeed(String etfIsinCode, String isinCode, Double amount) throws InvalidCodeException, NoClosingPriceException {
        Document etfBatchDoc = DBCenter.Instance.findIterable(MongoDBDBName.BATCH, MongoDBCollectionName.ETF_BATCH, new Document("isinCode", etfIsinCode)).first();
        if (etfBatchDoc == null) {
            String errorMsg = "ETF 배치정보를 찾지 못해 이전 영업일의 PDF 정보를 불러오는데 실패했습니다 - " + AliasManager.Instance.getKoreanFromIsin(etfIsinCode) + " (" + etfIsinCode + ")";
            ServerMessageSender.writeServerMessage(this.getClass(), "OracleArena", "알림", errorMsg);
            System.out.println(errorMsg);
        }

        int cuAmount = etfBatchDoc.getInteger("cuAmt");
        String code = isinCode;

        // etf별 매핑된 map을 만들어어서 넣어준다uilder를 map으로 저장했다가 구성종목이 추가될 때마다 해당 빌더를 꺼내서 값을 추가
        pdfBuilderMap.computeIfAbsent(etfIsinCode, k-> new PDFBuilder(etfIsinCode, cuAmount));
        PDFBuilder builder = pdfBuilderMap.get(etfIsinCode);

        if (code.equals(PredefinedIsinCode.CODE_CASH) || code.equals(PredefinedIsinCode.CODE_CASH2))
            builder.addCash(amount);
        else
            builder.addProduct(code, amount);

        PDF pdf = builder.build();

        pdfBuilderMap.put(etfIsinCode, builder);
        pdfMap.put(etfIsinCode, pdf);
    }

    public double get기준NAV(String isinCode) throws InvalidCodeException {

        Double nav = navMap.get(isinCode);
        if (nav == null)
            throw new InvalidCodeException(isinCode + " 의 전일 NAV를 구할 수 없습니다.");

        return nav;
    }

    public double getAmountFromPDF(String etfCode, String elementCode) {
        PDF pdf = pdfMap.get(etfCode);
        if (pdf == null)
            return 0;

        Double amount = pdf.amountMap.get(elementCode);
        if (amount == null)
            return 0;

        return amount;
    }

    public void loadAllPDF(Date date) {
        var col = DBCenter.Instance.findIterable(MongoDBDBName.BATCH, MongoDBCollectionName.ETF_BATCH);
        for (Document doc : col) {
            String isinCode = doc.getString("isinCode");
            int cuAmt = doc.getInteger("cuAmt");
            Double nav = doc.getDouble("nav");
            if (nav != null)
                navMap.put(isinCode, nav);

            try {
                loadFromDB(isinCode, cuAmt, date);
            } catch (InvalidCodeException | NoClosingPriceException e) {
                e.printStackTrace();
            }
        }
    }

    public void loadCompactPDF(Date date) {
        var col = DBCenter.Instance.findIterable(MongoDBDBName.BATCH, MongoDBCollectionName.ETF_BATCH);
        for (Document doc : col) {
            String isinCode = doc.getString("isinCode");
            if (!compactPdfSet.contains(isinCode))
                continue;
            int cuAmt = doc.getInteger("cuAmt");
            double nav = doc.getDouble("nav");
            navMap.put(isinCode, nav);

            try {
                loadFromDB(isinCode, cuAmt, date);
            } catch (InvalidCodeException | NoClosingPriceException e) {
                e.printStackTrace();
            }
        }
    }

    public void reloadFromDate(String yyyyMMdd) throws ParseException {
        Date date = DateCenter.Instance.parse_yyyyMMdd(yyyyMMdd);
        try {
            loadFromDB("KR7069500007", 50000, date);
            loadFromDB("KR7102110004", 50000, date);
            loadFromDB("KR7232080002", 20000, date);
            loadFromDB("KR7139220008", 20000, date);
            loadFromDB("KR7139240006", 20000, date);
            loadFromDB("KR7227540002", 20000, date);
            loadFromDB("KR7139290001", 20000, date);
            loadFromDB("KR7139230007", 20000, date);
            loadFromDB("KR7139260004", 20000, date);
            loadFromDB("KR7292190006", 20000, date);
            loadFromDB("KR7292160009", 100000, date);
        } catch (InvalidCodeException | NoClosingPriceException e) {
            e.printStackTrace();
        }
    }


    // 하나의 ETF에 해당하는 정보를 다 불러오는 것
    private void loadFromDB(String etfIsinCode, int cuAmount, Date date) throws InvalidCodeException, NoClosingPriceException {
        PDFBuilder builder = new PDFBuilder(etfIsinCode, cuAmount);

        boolean pdfExist = false;
        Document query = new Document("date", date).append("etfIsinCode", etfIsinCode);
        var col = DBCenter.Instance.findIterable(MongoDBDBName.BATCH, MongoDBCollectionName.PDF, query);
        for (Document doc : col) {
            pdfExist = true;
            String code = doc.getString("isinCode");
            Double amount = doc.getDouble("amount");

            if (code.equals(PredefinedIsinCode.CODE_CASH) || code.equals(PredefinedIsinCode.CODE_CASH2))
                builder.addCash(amount);
            else
                builder.addProduct(code, amount);
        }

        // PDF 수신 실패
        if (!pdfExist) {
            String msg = etfIsinCode +
                    "(" +
                    AliasManager.Instance.getKoreanFromIsin(etfIsinCode) +
                    ") ETF 의 금일 PDF 정보가 없습니다.";
            ServerMsgDoc doc = ServerMsgDoc.now(HephaLogType.운영장애, "NoPDF", msg);
            doc.fire();
            return;
        }

        PDF pdf = builder.build();
        if (etfIsinCode != null)
            pdfMap.put(etfIsinCode, pdf);
    }

    public Collection<PDF> getAllPDF() {
        return pdfMap.values();
    }

    public PDF getPDF(String etfIsinCode) {
        return pdfMap.get(etfIsinCode);
    }

    public boolean isETF(String isinCode) {
        return pdfMap.containsKey(isinCode);
    }
}
