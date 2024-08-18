package bulls.dmaLog.transactiontracker;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import bulls.designTemplate.JsonConvertible;
import bulls.dmaLog.DMALog;

import java.time.LocalTime;
import java.util.List;

public class TransactionTrackerDataStruct implements JsonConvertible {
    private final String trCode;
    private final String action;
    private final String issueCode;
    private final String issueName;
    private final int totalLength;
    private final int timeStampLength;
    private final int timeStampInterval;
    private final List<LocalTime> timeStamp;
    private final int logListLength;
    private final List<DMALog> logList;

    public static class Builder {
        private final String trCode = "FEPServerResponse";
        private final String action = "TransactionTrackData";

        private int totalLength;
        private int timeStampLength;
        private int logListLength;

        private int timeStampInterval = 0;
        private String issueCode = "";
        private String issueName = "";
        private List<LocalTime> timeStamp = null;
        private List<DMALog> logList = null;

        public Builder issueCode(String val) {
            issueCode = val;
            return this;
        }

        public Builder issueName(String val) {
            issueName = val;
            return this;
        }

        public Builder totalLength(int val) {
            totalLength = val;
            return this;
        }

        public Builder timeStampLength(int val) {
            timeStampLength = val;
            return this;
        }

        public Builder timeStampInterval(int val) {
            timeStampInterval = val;
            return this;
        }

        public Builder timeStamp(List<LocalTime> val) {
            timeStamp = val;
            return this;
        }

        public Builder logListLength(int val) {
            logListLength = val;
            return this;
        }

        public Builder logList(List<DMALog> val) {
            logList = val;
            return this;
        }

        public TransactionTrackerDataStruct build() {
            return new TransactionTrackerDataStruct(this);
        }
    }

    private TransactionTrackerDataStruct(Builder builder) {
        trCode = builder.trCode;
        action = builder.action;
        issueCode = builder.issueCode;
        issueName = builder.issueName;
        totalLength = builder.totalLength;
        timeStampLength = builder.timeStampLength;
        timeStampInterval = builder.timeStampInterval;
        timeStamp = builder.timeStamp;
        logListLength = builder.logListLength;
        logList = builder.logList;
    }

    @Override
    public void fillObjectNode(ObjectNode node) {
        node.put("trCode", trCode);
        node.put("action", action);
        node.put("issueCode", issueCode);
        node.put("issueName", issueName);
        node.put("totalLength", totalLength);
        node.put("timeStampLength", timeStampLength);
        node.put("timeStampInterval", timeStampInterval);
        ArrayNode timeStampArray = node.putArray("timeStamp");
        for (LocalTime time : timeStamp) {
            timeStampArray.add(time.toString());
        }
        node.put("logListLength", logListLength);
        ArrayNode logArray = node.putArray("logList");
        for (DMALog log : logList) {
            logArray.add(log.toObjectNode());
        }
    }
}
