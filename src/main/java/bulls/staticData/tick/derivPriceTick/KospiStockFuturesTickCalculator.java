package bulls.staticData.tick.derivPriceTick;

import bulls.staticData.tick.VariableTickCalculator;

public class KospiStockFuturesTickCalculator extends VariableTickCalculator {
    private static final KospiStockFuturesTickCalculator instance = new KospiStockFuturesTickCalculator();

    public static KospiStockFuturesTickCalculator getInstance() {
        return instance;
    }

    protected KospiStockFuturesTickCalculator() {
        super(new int[][]{
                {10, 10},
                {10000, 50},
                {50000, 100},
                {100000, 500},
                {500000, 1000},
                {Integer.MAX_VALUE, 1000},
        });
    }
}
