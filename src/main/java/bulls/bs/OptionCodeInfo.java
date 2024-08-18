package bulls.bs;


@Deprecated
public final class OptionCodeInfo {

    public final int strike;
    public final char year;
    public final char month;
    public final CallPut callPut;


    public OptionCodeInfo(int strike, char year, char month, CallPut callPut) {
        this.strike = strike;
        this.year = year;
        this.month = month;
        this.callPut = callPut;
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 17 * result + Integer.hashCode(strike);
        result = 17 * result + Character.hashCode(year);
        result = 17 * result + Character.hashCode(month);
        result = 17 * result + callPut.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof OptionCodeInfo) {
            OptionCodeInfo info = (OptionCodeInfo) obj;
            return strike == info.strike && year == info.year && month == info.month && callPut == info.callPut;
        }
        return false;
    }
}
