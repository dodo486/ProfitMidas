package bulls.staticData.ELW;

import java.util.HashMap;

public enum ELWIssuer {
    미래("미래에셋증권 주식회사"),
    신영("신영증권(주)"),
    NH("엔에이치투자증권(주)"),
    한투("한국투자증권(주)"),
    신한("신한금융투자 주식회사"),
    KB("KB증권(주)"),
    UNKNOWN("미확인");
    private final String fullName;
    static HashMap<String, ELWIssuer> map;

    static {
        map = new HashMap<>();
        for (ELWIssuer issuer : ELWIssuer.values()) {
            map.put(issuer.getFullName(), issuer);
        }
    }

    ELWIssuer(String fullName) {
        this.fullName = fullName;
    }

    public static ELWIssuer parse(String fullName) {
        return map.getOrDefault(fullName, UNKNOWN);
    }

    public String getFullName() {
        return fullName;
    }
}
