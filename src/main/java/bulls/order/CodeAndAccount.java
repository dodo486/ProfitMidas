package bulls.order;

import com.google.common.collect.HashBasedTable;
import bulls.staticData.AliasManager;

/**
 * for hash key
 */
public final class CodeAndAccount {

    final String code;
    final String account;
    final String key;
    String forToString = null;

    CodeAndAccount(String code, String account) {
        this.code = code;
        this.account = account;
        this.key = account + " " + code;
    }

    static HashBasedTable<String, String, CodeAndAccount> caTable = HashBasedTable.create();

    public static CodeAndAccount getOrCreate(String code, String account) {
        CodeAndAccount ca = caTable.get(code, account);
        if (ca != null)
            return ca;
        if (code == null || account == null)
            return null;
        ca = new CodeAndAccount(code, account);
        caTable.put(code, account, ca);
        return ca;
    }

    public String getCode() {
        return code;
    }

    public String getAccount() {
        return account;
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        CodeAndAccount another = (CodeAndAccount) o;
        return another.account.equals(account) && another.code.equals((code));
    }

    @Override
    public String toString() {
        if (forToString != null)
            return forToString;
        String codeAccount = String.format("%s %s", account, code);
        forToString = String.format("%s %s", AliasManager.Instance.getKoreanFromIsin(code), codeAccount);
        return forToString;
    }
}