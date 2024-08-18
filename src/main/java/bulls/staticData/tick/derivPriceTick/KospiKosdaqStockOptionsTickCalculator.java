package bulls.staticData.tick.derivPriceTick;

import bulls.staticData.tick.VariableTickCalculator;

public class KospiKosdaqStockOptionsTickCalculator extends VariableTickCalculator {
    private static final KospiKosdaqStockOptionsTickCalculator instance = new KospiKosdaqStockOptionsTickCalculator();

    public static KospiKosdaqStockOptionsTickCalculator getInstance() {
        return instance;
    }

    protected KospiKosdaqStockOptionsTickCalculator() {
        super(new int[][]{
                {10, 10},
                {1000, 20},
                {2000, 50},
                {5000, 100},
                {10000, 200},
                {Integer.MAX_VALUE, 200},
        });
    }
}
