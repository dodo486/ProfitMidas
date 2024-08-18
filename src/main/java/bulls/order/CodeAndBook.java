package bulls.order;

import com.google.common.collect.HashBasedTable;
import bulls.staticData.AliasManager;
import org.jetbrains.annotations.NotNull;

public final class CodeAndBook implements Comparable<CodeAndBook> {
    final String bookCode;

    final String code;
    final String key;
    String forToString = null;

    CodeAndBook(String code, String bookCode) {
        this.code = code;
        this.bookCode = bookCode;
        this.key = bookCode + " " + code;
    }

    static HashBasedTable<String, String, CodeAndBook> cbTable = HashBasedTable.create();

    public static CodeAndBook getOrCreate(String code, String bookCode) {
        CodeAndBook cb = cbTable.get(code, bookCode);
        if (cb != null)
            return cb;
        if (code == null || bookCode == null)
            return null;
        cb = new CodeAndBook(code, bookCode);
        cbTable.put(code, bookCode, cb);
        return cb;
    }

    public String getCode() {
        return code;
    }

    public String getBookCode() {
        return bookCode;
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CodeAndBook))
            return false;

        CodeAndBook another = (CodeAndBook) o;
        return another.bookCode.equals(bookCode) && another.code.equals(code);
    }

    @Override
    public String toString() {
        if (forToString != null)
            return forToString;
        String codeAndBookCode = String.format("%s %s", bookCode, code);
        forToString = String.format("%s %s", AliasManager.Instance.getKoreanFromIsin(code), codeAndBookCode);
        return forToString;
    }

    @Override
    public int compareTo(@NotNull CodeAndBook o) {
        int ret = bookCode.compareTo(o.bookCode);
        if (ret != 0)
            return ret;
        return code.compareTo(o.code);
    }
}