package bulls.dmaLog.shortSell;

import org.bson.Document;

import java.io.PrintStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DocumentMaster {
    private final LocalDate date;
    private final List<Document> docList;
    private final Set<String> keySet;

    public DocumentMaster(LocalDate date) {
        this.date = date;
        docList = new ArrayList<>();
        keySet = new HashSet<>();
    }

    public void add(Document doc) {
        docList.add(doc);
        keySet.addAll(doc.keySet());
    }

    public void addAll(Collection<Document> docCollection) {
        for (Document doc : docCollection)
            add(doc);
    }

    public void print() {
        print(System.out);
    }

    public void print(PrintStream ps) {
        List<String> keyList = new ArrayList<>(keySet);
        Collections.sort(keyList);
        print(ps, keyList);
    }

    public void print(List<String> keyList) {
        print(System.out, keyList);
    }

    public void print(PrintStream ps, List<String> keyList) {
        StringBuilder sb = new StringBuilder();
        sb.append("날짜").append(",");
        for (String key : keyList)
            sb.append(key).append(",");
        sb.deleteCharAt(sb.length() - 1);
        ps.println(sb);
        sb.setLength(0);

        String dateString = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        for (Document doc : docList) {
            sb.append(dateString).append(",");
            for (String key : keyList) {
                String value = "";
                if (doc.containsKey(key)) {
                    Object o = doc.get(key);
                    if (o instanceof LocalTime) {
                        value = ((LocalTime) o).format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
                    } else {
                        value = o.toString();
                    }
                }

                sb.append(value).append(",");
            }
            sb.deleteCharAt(sb.length() - 1);
            ps.println(sb);
            sb.setLength(0);
        }
    }
}
