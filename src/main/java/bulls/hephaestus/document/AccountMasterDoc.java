package bulls.hephaestus.document;

import java.util.List;

public class AccountMasterDoc {
    public String accountNumber;
    public String accountName;
    public boolean isDerivatives;
    public boolean isLP;
    public boolean isMarketPriceAvailable;
    public String dealer;
    public List<String> allowedItem;
    public List<String> disallowedItem;
    public String programType;
    public String expiryProgramType;
    public String ip;
    public boolean shouldCheckUptickRule;
    public String groupName;
}
