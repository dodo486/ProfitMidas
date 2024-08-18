package bulls.tool.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatter {


    private static final SimpleDateFormat f1 = new SimpleDateFormat("yyyyMMddHHmmssSSS");
    private static final SimpleDateFormat f2 = new SimpleDateFormat("HH_mm_ss_SSS");
    private static final SimpleDateFormat f3 = new SimpleDateFormat("yyyyMMdd");
    private static final SimpleDateFormat f4 = new SimpleDateFormat("HHmmssSSS");

    private static final SimpleDateFormat f5 = new SimpleDateFormat("yyyy-MM-dd");

    public static Date parse(String dtString) throws ParseException {
        return f5.parse(dtString);
    }

    public static String yyyyMMddHHmmssSSS(Date d) {

        return f1.format(d);
    }

    public static String HH_mm_ss_SSS(Date d) {

        return f2.format(d);
    }

    public static String HHmmssSSS(Date d) {


        return f4.format(d);
    }

    public static String TODAY_yyyyMMdd;

    static {
        TODAY_yyyyMMdd = f3.format(new Date(System.currentTimeMillis()));
    }

}
