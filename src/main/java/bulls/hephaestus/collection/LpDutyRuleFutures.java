package bulls.hephaestus.collection;

public class LpDutyRuleFutures {
    public String isinCode;
    public int dutyAmount;
    public RawLpDutyRuleFutures rawRule;

    public LpDutyRuleFutures(String isinCode, RawLpDutyRuleFutures rawRule) {
        this.isinCode = isinCode;
        this.dutyAmount = rawRule.의무수량;
        this.rawRule = rawRule;
    }
}
