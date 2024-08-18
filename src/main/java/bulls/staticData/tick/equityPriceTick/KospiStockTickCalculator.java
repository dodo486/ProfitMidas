package bulls.staticData.tick.equityPriceTick;

import bulls.staticData.tick.VariableTickCalculator;

public class KospiStockTickCalculator extends VariableTickCalculator {
    private static final KospiStockTickCalculator instance = new KospiStockTickCalculator();

    public static KospiStockTickCalculator getInstance() {
        return instance;
    }

    protected KospiStockTickCalculator() {
        super(new int[][]{
                {1, 1},
                {1000, 5},
                {5000, 10},
                {10000, 50},
                {50000, 100},
                {100000, 500},
                {500000, 1000},
                {Integer.MAX_VALUE, 1000},
        });
    }
}
