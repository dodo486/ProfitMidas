package bulls.staticData.tick.derivPriceTick;

import bulls.staticData.tick.VariableTickCalculator;

public class KosdaqStockFuturesTickCalculator extends VariableTickCalculator {
    private static final KosdaqStockFuturesTickCalculator instance = new KosdaqStockFuturesTickCalculator();

    public static KosdaqStockFuturesTickCalculator getInstance() {
        return instance;
    }

    protected KosdaqStockFuturesTickCalculator() {
        super(new int[][]{
                {1, 1},
                {1000, 5},
                {5000, 10},
                {10000, 50},
                {50000, 100},
                {Integer.MAX_VALUE, 100},
        });
    }
}
