package bulls.staticData.tick.derivStrikeTick;

import bulls.staticData.tick.VariableTickCalculator;

public class KospiKosdaqStockOptionStrikeTickCalculator extends VariableTickCalculator {
    private static final KospiKosdaqStockOptionStrikeTickCalculator instance = new KospiKosdaqStockOptionStrikeTickCalculator();

    public static KospiKosdaqStockOptionStrikeTickCalculator getInstance() {
        return instance;
    }

    protected KospiKosdaqStockOptionStrikeTickCalculator() {
        super(new int[][]{
                {100, 100},
                {5000, 200},
                {10000, 500},
                {20000, 1000},
                {50000, 2000},
                {100000, 5000},
                {200000, 10000},
                {500000, 20000},
                {1000000, 50000},
                {Integer.MAX_VALUE, 50000},
        });
    }
}
