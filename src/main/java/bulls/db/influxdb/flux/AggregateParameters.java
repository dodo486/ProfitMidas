package bulls.db.influxdb.flux;

public final class AggregateParameters {
    public final String period;
    public final String func;
    public final boolean create_empty;

    public AggregateParameters(String period, String func, boolean create_empty) {
        this.period = period;
        this.func = func;
        this.create_empty = create_empty;
    }

    public AggregateParameters() {
        this.period = "1d";
        this.func = "last";
        this.create_empty = false;
    }

    @Override
    public String toString() {
        return "|> aggregateWindow(every: " + period + ", fn:" + func + ", createEmpty:" + create_empty + ")\n";
    }
}
