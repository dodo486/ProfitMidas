package bulls.hephaestus.collection;

import bulls.db.mongodb.DBCenter;
import bulls.db.mongodb.MongoDBCollectionName;
import bulls.db.mongodb.MongoDBDBName;
import bulls.designTemplate.EarlyInitialize;
import bulls.hephaestus.document.ETFLpDoc;
import bulls.staticData.ProdType.DerivativesUnderlyingType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public enum ETFLpMaster implements EarlyInitialize {
    Instance;

    private final HashMap<String, ETFLpDoc> lpDocMap;
    private final Set<String> etfLPDutyIsinCodeSet;
    private final Set<String> etfIsinCodeWithoutKospiKosdaqSet;
    private final Set<String> transferAvailableEtfSet;

    private final HashMap<String, String> lpAccountMap;
    private final HashMap<String, String> hedgeAccountMap;
    private final HashMap<String, Set<String>> lpAccountIsinCodeMap;
    private final HashMap<String, Set<String>> hedgeAccountIsinCodeMap;

    ETFLpMaster() {
        lpDocMap = new HashMap<>();
        etfLPDutyIsinCodeSet = new HashSet<>();
        etfIsinCodeWithoutKospiKosdaqSet = new HashSet<>();
        transferAvailableEtfSet = new HashSet<>();
        lpAccountMap = new HashMap<>();
        hedgeAccountMap = new HashMap<>();
        lpAccountIsinCodeMap = new HashMap<>();
        hedgeAccountIsinCodeMap = new HashMap<>();

        var col = DBCenter.Instance.findIterable(MongoDBDBName.LP, MongoDBCollectionName.ETF_LP, ETFLpDoc.class);
        for (ETFLpDoc doc : col) {
            doc.duType = DerivativesUnderlyingType.getTypeFromDutCode(doc.underlying);
            lpDocMap.put(doc.isinCode, doc);

            if (doc.duType != DerivativesUnderlyingType.K2I &&
                    doc.duType != DerivativesUnderlyingType.MKI &&
                    doc.duType != DerivativesUnderlyingType.KQI)
                etfIsinCodeWithoutKospiKosdaqSet.add(doc.isinCode);

            if (doc.LP계약여부) {
                etfLPDutyIsinCodeSet.add(doc.isinCode);
                lpAccountMap.put(doc.isinCode, doc.lpAccount);
                hedgeAccountMap.put(doc.isinCode, doc.hedgeAccount);
                lpAccountIsinCodeMap.computeIfAbsent(doc.lpAccount, k -> new HashSet<>()).add(doc.isinCode);
                hedgeAccountIsinCodeMap.computeIfAbsent(doc.hedgeAccount, k -> new HashSet<>()).add(doc.isinCode);

                if (doc.transferAvailable)
                    transferAvailableEtfSet.add(doc.isinCode);
            }
        }
    }

    public DerivativesUnderlyingType getDUTypeOf(String etfCode) {
        ETFLpDoc lpDoc = lpDocMap.get(etfCode);
        if (lpDoc == null)
            return DerivativesUnderlyingType.UNKNOWN;
        return lpDoc.duType;
    }

    public Set<String> getTransferAvailableEtfSet() {
        return transferAvailableEtfSet;
    }

    public Set<String> getEtfLPDutyIsinCodeSet() {
        return etfLPDutyIsinCodeSet;
    }

    public Set<String> getEtfLpIsinCodeSet() {
        return lpDocMap.keySet();
    }

    public Set<String> getEtfLpIsinCodeSetByGroup(String group) {
        if (group == null)
            return null;

        return lpDocMap.values().stream()
                .filter(doc -> doc.LP계약여부 && doc.group.equals(group))
                .map(doc -> doc.isinCode)
                .collect(Collectors.toSet());
    }

    public Set<String> getEtfIsinCodeWithoutKospiKosdaqSet() {
        return etfIsinCodeWithoutKospiKosdaqSet;
    }

    public String getLpAccount(String isinCode) {
        return lpAccountMap.get(isinCode);
    }

    public String getHedgeAccount(String isinCode) {
        return hedgeAccountMap.get(isinCode);
    }

    public boolean isLPCodeAccountPair(String isinCode, String account) {
        String lpAccount = lpAccountMap.get(isinCode);
        if (lpAccount == null)
            return false;

        return lpAccount.equals(account);
    }

    public HashMap<String, ETFLpDoc> getLpDocMap() {
        return lpDocMap;
    }

    public Set<String> getIsinCodeSetByLpAccount(String lpAccount) {
        return lpAccountIsinCodeMap.get(lpAccount);
    }

    public Set<String> getIsinCodeSetByHedgeAccount(String hedgeAccount) {
        return hedgeAccountIsinCodeMap.get(hedgeAccount);
    }

    public ETFLpDoc getETFLpDoc(String isinCode) {
        return lpDocMap.get(isinCode);
    }
}
