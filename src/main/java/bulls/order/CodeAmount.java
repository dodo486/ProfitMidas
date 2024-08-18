package bulls.order;

import org.jetbrains.annotations.NotNull;

public class CodeAmount implements Comparable<CodeAmount> {

    public final String code;
    public final double amount;

    public CodeAmount(String code, double amount) {
        this.code = code;
        this.amount = amount;
    }


    @Override
    public int hashCode() {
        return code.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        CodeAmount ca = (CodeAmount) obj;
        return code.equals(ca.code);
    }

    @Override
    public int compareTo(@NotNull CodeAmount o) {
        return Double.compare(amount, o.amount);
    }
}
