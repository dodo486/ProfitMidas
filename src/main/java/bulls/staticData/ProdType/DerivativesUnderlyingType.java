package bulls.staticData.ProdType;

import bulls.db.mongodb.DocumentConvertible;
import bulls.db.mongodb.MongoDBCollectionName;
import bulls.db.mongodb.MongoDBDBName;
import bulls.db.mongodb.MongoDBData;
import org.bson.Document;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public enum DerivativesUnderlyingType implements MongoDBData, DocumentConvertible {
    UNKNOWN("UNKNOWN", ""),
    NA("NA", ""),
    K2I("코스피200", "KRD020020016"),
    WKI("코스피위클리", "KRD020020016"),
    MKI("미니 코스피200", "KRD020020016"),
    KQI("코스닥 150", "KRD020021378"),
    EST("유로스톡스50", ""),
    VKI("변동성지수", "KRD020021139"),
    XA0("에너지화학", "KRD020020362"),
    XA1("정보기술", "KRD020020370"),
    XA2("금융지수", "KRD020020388"),
    XA3("경기소비재", "KRD020020404"),
    XA4("고배당50", "KRD020021329"),
    XA5("배당성장50", "KRD020021311"),
    XA6("건설", "KRD020020339"),
    XA7("중공업", "KRD020020347"),
    XA8("헬스케어", "KRD020021394"),
    XA9("철강소재", "KRD020020354"),
    XAA("생활소비재", "KRD020020396"),
    XAB("산업재", "KRD020021386"),
    XAC("BBIG K-뉴딜", "KRD020023085"),
    XAD("2차전지 K-뉴딜", "KRD020023127"),
    XAE("바이오 K-뉴딜", "KRD020023119"),
    XI3("KRX300", "KRD020022087"),
    S11("삼성전자", "KR7005930003"),
    S12("SK텔레콤", "KR7017670001"),
    S13("POSCO", "KR7005490008"),
    S14("KT", "KR7030200000"),
    S15("한국전력", "KR7015760002"),
    S16("현대차", "KR7005380001"),
    S17("삼성증권", "KR7016360000"),
    S18("신한지주", "KR7055550008"),
    S19("기아", "KR7000270009"),
    S20("현대모비스", "KR7012330007"),
    S22("삼성SDI", "KR7006400006"),
    S23("삼성전기", "KR7009150004"),
    S24("LG전자", "KR7066570003"),
    S28("한국가스공사", "KR7036460004"),
    S32("현대제철", "KR7004020004"),
    S34("LG", "KR7003550001"),
    S35("GS", "KR7078930005"),
    S36("케이티앤지", "KR7033780008"),
    S39("한국조선해양", "KR7009540006"),
    S40("하나지주", "KR7086790003"),
    S41("SK이노베이", "KR7096770003"),
    S42("CJ", "KR7001040005"),
    S45("LGD", "KR7034220004"),
    S46("KB금융", "KR7105560007"),
    S47("LG화학", "KR7051910008"),
    S48("미래에셋증", "KR7006800007"),
    S49("현대두산인", "KR7042670000"),
    S50("SK하이닉스", "KR7000660001"),
    S51("GS건설", "KR7006360002"),
    S54("이마트", "KR7139480008"),
    S55("한국타이어", "KR7161390000"),
    S56("NAVER", "KR7035420009"),
    S57("대한항공", "KR7003490000"),
    SB0("LG유플러스", "KR7032640005"),
    SB2("S-Oil", "KR7010950004"),
    SB3("고려아연", "KR7010130003"),
    SB4("기업은행", "KR7024110009"),
    SB5("대림산업", "KR7000210005"),
    SB6("대상", "KR7001680008"),
    SB7("포스코인터", "KR7047050000"),
    SB8("대우조선해양", ""),
    SB9("두산중공업", "KR7034020008"),
    SBA("롯데쇼핑", "KR7023530009"),
    SBB("롯데케미칼", "KR7011170008"),
    SBD("삼성생명", "KR7032830002"),
    SBF("삼성중공업", "KR7010140002"),
    SBG("삼성카드", "KR7029780004"),
    SBH("한화에어로", "KR7012450003"),
    SBJ("엔씨소프트", "KR7036570000"),
    SBK("하이트진로", "KR7000080002"),
    SBL("한국금융지주", "KR7071050009"),
    SBM("한국항공우주", "KR7047810007"),
    SBN("현대건설", "KR7000720003"),
    SBP("현대위아", "KR7011210002"),
    SBQ("호텔신라", "KR7008770000"),
    SBR("우리은행", ""),
    SBS("강원랜드", "KR7035250000"),
    SBT("BNK금융지주", "KR7138930003"),
    SBV("DGB금융지주", "KR7139130009"),
    SBW("GKL", "KR7114090004"),
    SBX("LX인터내셔", "KR7001120005"),
    SBY("LG이노텍", "KR7011070000"),
    SBZ("NH투자증권", "KR7005940002"),
    SC0("OCI", "KR7010060002"),
    SC1("SK", "KR7034730002"),
    SC2("SK네트웍스", "KR7001740000"),
    SC3("금호석유", "KR7011780004"),
    SC4("넥센타이어", "KR7002350007"),
    SC5("삼성SDS", "KR7018260000"),
    SC6("아모레퍼시픽", "KR7090430000"),
    SC7("제일기획", "KR7030000004"),
    SC8("삼성물산", "KR7028260008"),
    SC9("한화", "KR7000880005"),
    SCA("한화생명", "KR7088350004"),
    SCB("한화솔루션", "KR7009830001"),
    SCC("현대글로비스", "KR7086280005"),
    SCD("현대미포조선", "KR7010620003"),
    SCE("현대해상", "KR7001450006"),
    SCF("카카오", "KR7035720002"),
    SCG("파라다이스", "KR7034230003"),
    SCH("CJ E&M", ""),
    SCJ("서울반도체", "KR7046890000"),
    SCK("웹젠", "KR7069080000"),
    SCL("씨젠", "KR7096530001"),
    SCN("포스코 ICT", "KR7022100002"),
    SCP("셀트리온", "KR7068270008"),
    SCQ("와이지엔터테인먼트", "KR7122870009"),
    SCR("LG생활건강", "KR7051900009"),
    SCS("아모레G", "KR7002790004"),
    SCT("삼성화재", "KR7000810002"),
    SCV("한미사이언스", "KR7008930000"),
    SCW("코웨이", "KR7021240007"),
    SCX("한미약품", "KR7128940004"),
    SCY("한온시스템", "KR7018880005"),
    SCZ("BGF리테일", "KR7027410000"),
    SD0("DB손해보험", "KR7005830005"),
    SD1("CJ제일제당", "KR7097950000"),
    SD2("한샘", "KR7009240003"),
    SD3("KCC", "KR7002380004"),
    SD4("GS리테일", "KR7007070006"),
    SD5("에스원", "KR7012750006"),
    SD6("유한양행", "KR7000100008"),
    SD7("한전KPS", "KR7051600005"),
    SD8("현대백화점", "KR7069960003"),
    SD9("CJ CGV", "KR7079160008"),
    SDA("농심", "KR7004370003"),
    SDB("LIG넥스원", "KR7079550000"),
    SDC("만도", "KR7204320006"),
    SDD("신세계", "KR7004170007"),
    SDE("한국콜마", "KR7161890009"),
    SDF("KB손해보험", ""),
    SDG("영원무역", "KR7111770004"),
    SDH("메리츠증권", "KR7008560005"),
    SDJ("대한유화", "KR7006650006"),
    SDK("코스맥스", "KR7192820009"),
    SDL("LX하우시스", "KR7108670001"),
    SDM("다우기술", "KR7023590003"),
    SDN("하나투어", "KR7039130000"),
    SDP("메디톡스", "KR7086900008"),
    SDQ("로엔", ""),
    SDR("컴투스", "KR7078340007"),
    SDS("SK머티리얼즈", "KR7036490001"),
    SDT("CJ ENM", "KR7035760008"),
    SDV("에스에프에이", "KR7056190002"),
    SDW("두산밥캣", "KR7241560002"),
    SDX("현대산업", ""),
    SDY("현대엘리베이", "KR7017800004"),
    SDZ("한세실업", "KR7105630008"),
    SE0("원익IPS", "KR7240810002"),
    SE1("JW중외제약", "KR7001060003"),
    SE2("안랩", "KR7053800009"),
    SE3("파트론", "KR7091700005"),
    SE4("에스엠", "KR7041510009"),
    SE5("넷마블", "KR7251270005"),
    SE6("셀트리온헬스케어", "KR7091990002"),
    SE7("아이엔지생명", "KR7079440004"),
    SE8("포스코케미칼", "KR7003670007"),
    SE9("영진약품", "KR7003520004"),
    SEA("PI첨단소재", "KR7178920005"),
    SEB("우리금융지주", "KR7316140003"),
    SEC("HDC현대산업개발", "KR7294870001"),
    SED("일진머티리얼즈", "KR7020150009"),
    SEE("한올바이오파마", "KR7009420001"),
    SEF("JYP Ent.", "KR7035900000"),
    SEG("쌍용C&E", "KR7003410008"),
    SEH("팬오션", "KR7028670008"),
    SEJ("SKC", "KR7011790003"),
    SEK("DB하이텍", "KR7000990002"),
    SEL("후성", "KR7093370005"),
    SEM("RFHIC", "KR7218410009"),
    SEN("동진쎄미켐", "KR7005290002"),
    SEP("네패스", "KR7033640004"),
    SEQ("비에이치", "KR7090460007"),
    SER("DL이앤씨", "KR7375500006"),
    SES("삼성바이오로직스", "KR7207940008"),
    SET("삼성엔지니어링", "KR7028050003"),
    SEV("한화시스템", "KR7272210006"),
    SEW("보령제약", "KR7003850005"),
    SEX("카카오게임즈", "KR7293490009"),
    SEY("에코프로비엠", "KR7247540008"),
    SEZ("스튜디오드래곤", "KR7253450001"),
    SF0("천보", "KR7278280003"),
    SF1("콜마비앤에이치", "KR7200130003"),
    SF2("SFA반도체", "KR7036540003"),
    SF3("NHN한국사이버결제", "KR7060250008"),
    SF4("삼천당제약", "KR7000250001"),
    SF5("LG에너지솔루션", "KR7373220003"),
    SF6("카카오뱅크", "KR7323410001"),
    SF7("카카오페이", "KR7377300009"),
    SF8("크래프톤", "KR7259960003"),
    SF9("SK바이오사이언스", "KR7302440003"),
    SFA("HMM", "KR7011200003"),
    SFB("SK아이이테크놀로지", "KR7361610009"),
    SFC("하이브", "KR7352820005"),
    SFD("현대중공업", "KR7329180004"),
    SFE("SK스퀘어", "KR7402340004"),
    SFF("SK바이오팜", "KR7326030004"),
    SFG("F&F", "KR7383220001"),
    SFH("HD현대", "KR7267250009"),
    SFJ("오리온", "KR7271560005"),
    SFK("메리츠금융지주", "KR7138040001"),
    SFL("펄어비스", "KR7263750002"),
    SFM("엘앤에프", "KR7066970005"),
    SFN("위메이드", "KR7112040001"),
    SFP("알테오젠", "KR7196170005"),
    SFQ("리노공업", "KR7058470006"),
    SM0("KODEX 삼성그룹", "KR7102780004"),
    SM1("TIGER 헬스케어", "KR7143860005"),
    SM2("ARIRANG 고배당주", "KR7161510003"),
    SM3("TIGER 차이나CSI300", "KR7192090009"),
    SM4("KODEX Top5PlusTR", "KR7315930008"),
    SM5("TIGER 미국나스닥100", "KR7133690008"),
    BM3("신3년국채", ""),
    BM5("신5년국채", ""),
    BMA("신10년국채", ""),
    B3A("3년-10년국채선물스프레드", ""),
    EUR("유로", "EUD018980004"),
    JPY("엔", "JPD013920004"),
    USD("미국달러", "USD018400003"),
    CNH("위안", "CND011560000"),
    KGD("금", "KRD040200002"),
    LHG("돈육", ""),
    RFR("3개월무위험금리", "");
    static final HashMap<String, DerivativesUnderlyingType> dutStrToDutMap;
    static final HashMap<String, List<DerivativesUnderlyingType>> isinCodeToDutListMap;

    static {
        dutStrToDutMap = new HashMap<>();
        isinCodeToDutListMap = new HashMap<>();
        for (DerivativesUnderlyingType dut : values()) {
            dutStrToDutMap.put(dut.toString(), dut);
            if (!dut.underlyingIsinCode.equals("")) {
                if (!isinCodeToDutListMap.containsKey(dut.underlyingIsinCode))
                    isinCodeToDutListMap.put(dut.underlyingIsinCode, new ArrayList<>());
                isinCodeToDutListMap.get(dut.underlyingIsinCode).add(dut);
            }
        }
    }

    final String korean;
    final String underlyingIsinCode;

    DerivativesUnderlyingType(String korean, String underlyingIsinCode) {
        this.korean = korean;
        this.underlyingIsinCode = underlyingIsinCode;
    }

    public String getKorean() {
        return korean;
    }

    public String getUnderlyingIsinCode() {
        return underlyingIsinCode;
    }

    @NotNull
    public static List<DerivativesUnderlyingType> getTypeListFromIsinCode(String isinCode) {
        List<DerivativesUnderlyingType> list = isinCodeToDutListMap.get(isinCode);
        if (list == null)
            return new ArrayList<>();
        return list;
    }

    @NotNull
    public static DerivativesUnderlyingType getTypeFromDutCode(String dutCode) {
        DerivativesUnderlyingType dut = dutStrToDutMap.get(dutCode);
        if (dut == null)
            return DerivativesUnderlyingType.UNKNOWN;
        return dut;
    }

    public static DerivativesUnderlyingType getFromKorean(String korean) {
        for (var value : values()) {
            if (value.korean.equals(korean))
                return value;
        }
        return DerivativesUnderlyingType.UNKNOWN;
    }

    // UNKNOWN, NA가 아니면 valid한 DUT
    public boolean isValid() {
        return this != UNKNOWN && this != NA;
    }

    @Override
    public String getDBName() {
        return MongoDBDBName.MANAGE_DATA;
    }

    @Override
    public String getCollectionName() {
        return MongoDBCollectionName.DUT_LIST;
    }

    @Override
    public Document getDataDocument() {
        Document d = new Document();
        d.append("dut", this.name());
        d.append("korean", korean);
        d.append("underlyingIsinCode", underlyingIsinCode);
        return d;
    }

    @Override
    public Document getQueryDocument() {
        Document d = new Document();
        d.append("dut", this.name());
        return d;
    }
}