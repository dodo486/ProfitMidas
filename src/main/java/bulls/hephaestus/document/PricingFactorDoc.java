package bulls.hephaestus.document;

import bulls.staticData.TempConf;

import java.util.Date;

public class PricingFactorDoc {
    public String isinCode;
    public String factorType;
    //rate
    public Date rateDate = new Date(0);
    public double riskFreeRate = TempConf.조달금리;
    public float borrowingRate;
    //div
    public double div;
    public Date divDate;
    public double divMax;
    public double divMin;
}
