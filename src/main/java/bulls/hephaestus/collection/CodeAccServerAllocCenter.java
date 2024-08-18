package bulls.hephaestus.collection;

import bulls.db.mongodb.DBCenter;
import bulls.db.mongodb.MongoDBCollectionName;
import bulls.db.mongodb.MongoDBDBName;
import bulls.designTemplate.EarlyInitialize;
import bulls.hephaestus.document.CodeAccServerAllocDoc;
import bulls.log.DefaultLogger;
import bulls.order.CodeAndAccount;
import bulls.order.enums.LongShort;
import bulls.server.enums.ServerPurpose;
import bulls.staticData.AliasManager;
import bulls.staticData.TempConf;

import java.util.*;
import java.util.stream.Collectors;

public enum CodeAccServerAllocCenter implements EarlyInitialize {
    Instance;
    private final HashMap<String, CodeAccServerAllocDoc> longToShortMap = new HashMap<>();
    private final HashMap<String, CodeAccServerAllocDoc> shortToLongMap = new HashMap<>();

    private final HashSet<CodeAndAccount> blackListForMainHedge = new HashSet<>();
    private final HashSet<CodeAndAccount> whiteListForNonMainHedge = new HashSet<>();


    CodeAccServerAllocCenter() {
        Init();
    }

    public void Init() {
        longToShortMap.clear();
        shortToLongMap.clear();
        blackListForMainHedge.clear();
        whiteListForNonMainHedge.clear();

        var col = DBCenter.Instance.findIterable(MongoDBDBName.MANAGE_DATA, MongoDBCollectionName.CODE_ACC_SERVER_ALLOCATION, CodeAccServerAllocDoc.class);
        for (CodeAccServerAllocDoc doc : col) {
            if (TempConf.SERVER_PURPOSE == ServerPurpose.MAIN_HEDGE) {
                if (!doc.serverId.equals(TempConf.SERVER_ID)) {
                    //build blacklist, 타 서버의 whiteList에 있는 code acc는 MAIN_HEDGE에서 매매하면 안됨.
                    for (String isinCode : doc.whiteList) {
                        blackListForMainHedge.add(CodeAndAccount.getOrCreate(isinCode, doc.longAccount));
                        blackListForMainHedge.add(CodeAndAccount.getOrCreate(isinCode, doc.shortAccount));
                    }
                } else {
                    //자신에게 할당된 AccountPair 정보가 있다면 입력
                    longToShortMap.put(doc.longAccount, doc);
                    shortToLongMap.put(doc.shortAccount, doc);
                }
            } else {
                if (doc.serverId.equals(TempConf.SERVER_ID)) {
                    //build whiteList, NON MAIN_HEDGE의 경우 자신이 할당받은 code acc만 매매할 수 있음.
                    for (String isinCode : doc.whiteList) {
                        whiteListForNonMainHedge.add(CodeAndAccount.getOrCreate(isinCode, doc.longAccount));
                        whiteListForNonMainHedge.add(CodeAndAccount.getOrCreate(isinCode, doc.shortAccount));
                    }
                    //자신에게 할당된 AccountPair 정보가 있다면 입력
                    longToShortMap.put(doc.longAccount, doc);
                    shortToLongMap.put(doc.shortAccount, doc);
                }
            }
        }
        PrintStatus();
    }

    public Set<String> getMainHedgeBlackListForAcc(String account) {
        return blackListForMainHedge.stream().filter(ca -> ca.getAccount().equals(account)).map(ca -> ca.getCode()).collect(Collectors.toSet());
    }

    public Set<String> getSubHedgeWhiteListForAcc(String account) {
        return whiteListForNonMainHedge.stream().filter(ca -> ca.getAccount().equals(account)).map(ca -> ca.getCode()).collect(Collectors.toSet());
    }

    public List<CodeAccServerAllocDoc> getCurrentServerCodeAccServerAllocList() {
        ArrayList<CodeAccServerAllocDoc> list = new ArrayList<>();
        if (TempConf.SERVER_PURPOSE != ServerPurpose.MAIN_HEDGE)
            list.addAll(longToShortMap.values());

        return list;
    }

    /**
     * 주어진 codeAndAccount가 현재 서버에서 매매 가능한지 여부 반환. MAIN_HEDGE 서버의 경우 타 서버의 whiteList로 등록되어 있지 않으면 매매 가능하다고 판단
     * MAIN_HEDGE가 아닌 경우 whiteList에 등록이 되어있는 코드/계좌만 매매 가능하다고 판단
     *
     * @param codeAndAccount
     * @return 매매 가능 여부
     */
    public boolean isTradable(CodeAndAccount codeAndAccount) {
        if (TempConf.SERVER_PURPOSE == ServerPurpose.MAIN_HEDGE) {
            return !blackListForMainHedge.contains(codeAndAccount);
        } else {
            return whiteListForNonMainHedge.contains(codeAndAccount);
        }
    }

    /**
     * 주어진 codeAndAccount가 현재 서버에서 매매 가능한지 여부 반환. MAIN_HEDGE 서버의 경우 타 서버의 whiteList로 등록되어 있지 않으면 매매 가능하다고 판단
     * MAIN_HEDGE가 아닌 경우 whiteList에 등록이 되어있는 코드/계좌만 매매 가능하다고 판단
     *
     * @param isinCode 종목코드
     * @param account  계좌
     * @return 매매가능여부
     */
    public boolean isTradable(String isinCode, String account) {
        CodeAndAccount caa = CodeAndAccount.getOrCreate(isinCode, account);
        return isTradable(caa);
    }

    /**
     * 매수/매도 계좌가 지정되어 있는 CodeAndAccount에 대해 적절한 매수/매도 계좌를 반환해준다. 매칭되는 계좌가 없을 경우 입력받은 계좌를 그대로 리턴한다.
     *
     * @param isinCode     종목코드
     * @param givenAccount 대상 계좌
     * @param longShort    매수/매도 여부
     * @return 주어진 계좌에 대한 매수/매도 계좌
     */
    public String getApptAccnt(String isinCode, String givenAccount, LongShort longShort) {
        if (longShort == LongShort.LONG) {
            CodeAccServerAllocDoc doc = shortToLongMap.get(givenAccount);
            if (doc != null && doc.whiteList.contains(isinCode))
                return doc.longAccount;
        } else if (longShort == LongShort.SHORT) {
            CodeAccServerAllocDoc doc = longToShortMap.get(givenAccount);
            if (doc != null && doc.whiteList.contains(isinCode))
                return doc.shortAccount;
        }
        return givenAccount;
    }

    void PrintStatus() {
        StringBuilder sb = new StringBuilder();
        sb.append("======================================\nCodeAccServerAllocCenter\n======================================\n");
        if (TempConf.SERVER_PURPOSE == ServerPurpose.MAIN_HEDGE) {
            sb.append("======================================\nblackListForMainHedge\n======================================\n");
            for (CodeAndAccount codeAndAccount : blackListForMainHedge) {
                sb.append(codeAndAccount + "\n");
            }
        } else {
            sb.append("======================================\nwhiteListForNonMainHedge\n======================================\n");
            for (CodeAndAccount codeAndAccount : whiteListForNonMainHedge) {
                sb.append(codeAndAccount + "\n");
            }
        }
        sb.append("======================================\n매수계좌->매도계좌\n======================================\n");
        longToShortMap.forEach((k, v) -> {
            sb.append(v.longAccount);
            sb.append(" -> ");
            sb.append(v.shortAccount);
            sb.append(" : ");
            for (String s : v.whiteList) {
                sb.append("(" + s + " " + AliasManager.Instance.getKoreanFromIsin(s) + "),");
            }
            sb.append("\n");
        });
        sb.append("======================================\n매도계좌->매수계좌\n======================================\n");
        longToShortMap.forEach((k, v) -> {
            sb.append(v.shortAccount);
            sb.append(" -> ");
            sb.append(v.longAccount);
            sb.append(" : ");
            for (String s : v.whiteList) {
                sb.append("(" + s + " " + AliasManager.Instance.getKoreanFromIsin(s) + "),");
            }
            sb.append("\n");
        });
        sb.append("======================================\nCodeAccServerAllocCenter\n======================================\n");
        DefaultLogger.logger.info("\n{}", sb);
    }
}
