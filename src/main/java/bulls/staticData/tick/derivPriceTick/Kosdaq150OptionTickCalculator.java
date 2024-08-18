package bulls.staticData.tick.derivPriceTick;

import bulls.staticData.tick.VariableTickCalculator;

public class Kosdaq150OptionTickCalculator extends VariableTickCalculator {
    private static final Kosdaq150OptionTickCalculator instance = new Kosdaq150OptionTickCalculator();

    public static Kosdaq150OptionTickCalculator getInstance() {
        return instance;
    }

    protected Kosdaq150OptionTickCalculator() {
        super(new int[][]{
                {10, 10},
                {5000, 50},
                {Integer.MAX_VALUE, 50},
        });
    }
}
