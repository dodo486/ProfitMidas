package bulls.staticData.tick.equityPriceTick;

import bulls.staticData.tick.VariableTickCalculator;

public class KosdaqStockTickCalculator extends VariableTickCalculator {
    private static final KosdaqStockTickCalculator instance = new KosdaqStockTickCalculator();

    public static KosdaqStockTickCalculator getInstance() {
        return instance;
    }

    protected KosdaqStockTickCalculator() {
        super(new int[][]{
                {1, 1},
                {1000, 5},
                {5000, 10},
                {10000, 50},
                {50000, 100},
                {Integer.MAX_VALUE, 1000},
        });
    }
}
