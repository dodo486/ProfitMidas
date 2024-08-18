package bulls.staticData.tick.derivPriceTick;

import bulls.staticData.tick.VariableTickCalculator;

public class WideKosdaq150OptionTickCalculator extends VariableTickCalculator {
    private static final WideKosdaq150OptionTickCalculator instance = new WideKosdaq150OptionTickCalculator();

    public static WideKosdaq150OptionTickCalculator getInstance() {
        return instance;
    }

    protected WideKosdaq150OptionTickCalculator() {
        super(new int[][]{
                {10, 10},
                {100, 20},
                {5000, 100},
                {Integer.MAX_VALUE, 100},
        });
    }
}
