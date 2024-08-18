package bulls.hephaestus.collection;

import bulls.db.mongodb.DBCenter;
import bulls.db.mongodb.MongoDBCollectionName;
import bulls.db.mongodb.MongoDBDBName;
import bulls.hephaestus.document.AccountMasterDoc;
import bulls.json.DefaultMapper;
import bulls.log.DefaultLogger;
import bulls.order.enums.ProgramType;
import bulls.staticData.PredefinedString;
import bulls.staticData.TempConf;
import org.bson.Document;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

public enum AccountMaster {
    Instance;

    private final HashMap<String, AccountMasterDoc> accountMap = new HashMap<>();
    private final HashMap<String, ProgramType> pTypeMap = new HashMap<>();
    private final HashMap<String, ProgramType> expiryPTypeMap = new HashMap<>();
    private final HashMap<String, byte[]> ipMap = new HashMap<>();

    AccountMaster() {
        var col = DBCenter.Instance.findIterable(MongoDBDBName.MANAGE_DATA, MongoDBCollectionName.ACCOUNT_MASTER);
        for (Document d : col) {
            AccountMasterDoc doc = DefaultMapper.getGMapper().fromJson(d.toJson(), AccountMasterDoc.class);
            pTypeMap.put(doc.accountNumber, ProgramType.getProgramType(doc.programType));
            expiryPTypeMap.put(doc.accountNumber, ProgramType.getProgramType(doc.expiryProgramType));
            if (doc.ip != null) {
                if (doc.ip.matches("^[\\d]{12}")) {
                    ipMap.put(doc.accountNumber, doc.ip.getBytes());
                } else {
                    DefaultLogger.logger.error("계좌명: {} 계좌번호: {} IP: {} 오류", doc.accountName, doc.accountNumber, doc.ip);
                    ipMap.put(doc.accountNumber, TempConf.IP_FOR_KRX);
                }
            } else
                ipMap.put(doc.accountNumber, TempConf.IP_FOR_KRX);
            accountMap.put(doc.accountNumber, doc);
            if (doc.groupName == null || doc.groupName.isEmpty()) {
                if (doc.isDerivatives)
                    doc.groupName = "Deriv";
                else if (doc.accountNumber.equals(PredefinedString.ACCOUNT_EQUITY_MINI_HEDGE) || doc.accountNumber.equals(PredefinedString.ACCOUNT_EQUITY_MINI_ETF_SHORT))
                    doc.groupName = "Index";
                else
                    doc.groupName = "Basic";
            }
        }
    }

    // accountMap에 혜당 계좌가 등록되어 있지 않은 경우 default로 "00"을 리턴하도록.
    public ProgramType getProgramTypeFromAccount(String account) {
        return pTypeMap.getOrDefault(account, ProgramType.일반);
    }

    public ProgramType getExpiryProgramTypeFromAccount(String account) {
        return expiryPTypeMap.getOrDefault(account, ProgramType.일반);
    }

    public boolean isDerivAccount(String account) {
        AccountMasterDoc a = accountMap.get(account);
        if (a != null)
            return accountMap.get(account).isDerivatives;
        else
            return false;
    }

    public String getAccountName(String account) {
        AccountMasterDoc a = accountMap.get(account);
        if (a != null)
            return accountMap.get(account).accountName;
        else
            return account;
    }

    public String getGroupName(String account) {
        AccountMasterDoc a = accountMap.get(account);
        if (a != null)
            return accountMap.get(account).groupName;
        else
            return account;
    }


    public boolean isShortSellAvailable(String account) {
        //todo : 하드코딩 대신 account에 공매도가능여부 필드 추가
        return !account.trim().equals(PredefinedString.ACCOUNT_EQUITY_MINI_HEDGE);
    }

    public boolean checkAvailable(String account, String isinCode) {
        // default white, LP로 등록된 계좌가 아닌 종목은 모두 받아들인다.
        if (!accountMap.containsKey(account))
            return true;

        AccountMasterDoc doc = accountMap.get(account);

        for (String prefix : doc.allowedItem) {
            if (isinCode.indexOf(prefix) == 0) {
                return true;
            }
        }

        return false;
    }

    public boolean isLPAccount(String account) {
        AccountMasterDoc doc = accountMap.get(account);
        if (doc == null)
            return false;

        return doc.isLP;
    }

    public boolean isMarketPriceAvailable(String account) {
        AccountMasterDoc doc = accountMap.get(account);
        if (doc == null)
            return false;

        return doc.isMarketPriceAvailable;
    }

    public Set<String> getAllDerivAccount() {
        return accountMap.values().stream().filter(accountMasterDoc -> accountMasterDoc.isDerivatives).map(accountMasterDoc -> accountMasterDoc.accountNumber).collect(Collectors.toSet());
    }

    public Set<String> getAllEquityAccount() {
        return accountMap.values().stream().filter(accountMasterDoc -> !accountMasterDoc.isDerivatives).map(accountMasterDoc -> accountMasterDoc.accountNumber).collect(Collectors.toSet());
    }

    public Collection<AccountMasterDoc> getAllEquityAccountDoc() {
        return accountMap.values().stream().filter(accountMasterDoc -> !accountMasterDoc.isDerivatives).collect(Collectors.toList());
    }

    public byte[] getIP(String account) {
        return ipMap.getOrDefault(account, TempConf.IP_FOR_KRX);
    }

    public boolean shouldCheckUptickRule(String account) {
        var t = accountMap.get(account);
        //계좌 정보 없다면 일단 true
        if (t == null)
            return true;
        //파생은 미적용
        if (t.isDerivatives)
            return false;
        return t.shouldCheckUptickRule;
    }
}
