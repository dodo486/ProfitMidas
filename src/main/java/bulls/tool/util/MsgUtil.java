package bulls.tool.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;

public abstract class MsgUtil {
    public static String getFormattedString(String msg, Object... arguments) {
        // msg 맨 마지막에 {}가 들어가는 경우를 위해 공백 하나를 추가해준다
        msg = msg + " ";

        String[] split = msg.split("\\{}");
        if (split.length != arguments.length + 1)
            System.out.println("Formatting 실패 경고 : 메시지의 {} 개수와 매개변수의 개수가 일치하지 않습니다. msg=" + msg + ", arguments=" + Arrays.toString(arguments));

        int msgIdx = 0, argIdx = 0;
        StringBuilder sb = new StringBuilder();
        while (msgIdx < split.length) {
            sb.append(split[msgIdx++]);
            if (msgIdx == split.length)
                break;

            if (argIdx < arguments.length)
                sb.append(arguments[argIdx++]);
        }

        // 처음에 추가한 공백 제거
        sb.deleteCharAt(sb.length() - 1);

        return sb.toString();
    }

    public static String getTimeString(Collection<LocalDateTime> timeCollection) {
        StringBuilder sb = new StringBuilder();
        for (var t : timeCollection) {
            if (t.getSecond() == 0)
                sb.append(t.format(DateTimeFormatter.ofPattern("HH:mm"))).append(" ");
            else
                sb.append(t.format(DateTimeFormatter.ofPattern("HH:mm:ss"))).append(" ");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
}
