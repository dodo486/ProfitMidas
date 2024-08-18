package bulls.staticData.tick.equityPriceTick;

import bulls.staticData.tick.VariableTickCalculator;

public class ElwTickCalculator extends VariableTickCalculator {
    private static final ElwTickCalculator instance = new ElwTickCalculator();

    public static ElwTickCalculator getInstance() {
        return instance;
    }

    protected ElwTickCalculator() {
        super(new int[][]{
                {5, 5},
                {5000, 10},
                {10000, 50},
                {50000, 100},
                {100000, 500},
                {500000, 1000},
                {Integer.MAX_VALUE, 1000},
        });
    }
}
