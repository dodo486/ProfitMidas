package bulls.staticData.tick.equityPriceTick;

import bulls.staticData.tick.VariableTickCalculator;

public class EtfTickCalculator extends VariableTickCalculator {
    private static final EtfTickCalculator instance = new EtfTickCalculator();

    public static EtfTickCalculator getInstance() {
        return instance;
    }

    protected EtfTickCalculator() {
        super(new int[][]{
                {5, 5},
                {Integer.MAX_VALUE, 1000},
        });
    }
}
