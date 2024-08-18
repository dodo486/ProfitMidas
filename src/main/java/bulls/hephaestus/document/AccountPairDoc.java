package bulls.hephaestus.document;

import java.util.HashSet;
import java.util.Set;

public class AccountPairDoc {
    public String longAccount;
    public String shortAccount;
    public Set<String> whiteList = new HashSet<>();
}
