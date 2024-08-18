package bulls.hephaestus.collection;

public class LpDutyRuleOptions {
    public String isinCode;
    public int dutyAmount;
    public int moneyness;
    public RawLpDutyRuleOptions rawRule;

    public LpDutyRuleOptions(String isinCode, int moneyness, int dutyAmount, RawLpDutyRuleOptions rawRule) {
        this.isinCode = isinCode;
        this.moneyness = moneyness;
        this.dutyAmount = dutyAmount;
        this.rawRule = rawRule;
    }
}
