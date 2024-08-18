package bulls.json.index;

public class CodeAndRatio {
    public String code;
    public double ratio;

    public CodeAndRatio(String code, double ratio) {
        this.code = code;
        this.ratio = ratio;
    }

    @Override
    public String toString() {
        return "CodeAndRatio{" +
                "code='" + code + '\'' +
                ", ratio=" + ratio +
                '}';
    }
}
