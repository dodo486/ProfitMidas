package bulls.dmaLog.transactiontracker;

import com.fasterxml.jackson.databind.node.ObjectNode;
import bulls.dmaLog.DMALog;
import bulls.dmaLog.DMALogUtil;
import bulls.dmaLog.FEPConstantValue;
import bulls.dmaLog.loader.DMALogDataCenter;
import bulls.staticData.AliasManager;
import bulls.websocket.asyncHandling.AsyncResponse;
import bulls.websocket.asyncHandling.AsyncWork;

import java.time.LocalTime;
import java.util.List;

public class TransactionTrackDataProvider implements AsyncWork {
    @Override
    public AsyncResponse asyncWork(AsyncResponse obj) {
        ObjectNode inputNode = obj.getInputNode();
        ObjectNode outputNode = obj.getOutputNode();

        String orderId = inputNode.get("orderId").asText();
        String startTimeString = inputNode.get("startTime").asText();
        String endTimeString = inputNode.get("endTime").asText();
        String countString = inputNode.get("count").asText();

        LocalTime startTime, endTime;
        int count, totalLength;

        startTime = DMALogUtil.parseTimeInTracker(startTimeString);
        endTime = DMALogUtil.parseTimeInTracker(endTimeString);

        try {
            if (countString == null)
                count = FEPConstantValue.DEFAULT_COUNT_SIZE;
            else {
                count = Integer.parseInt(countString);

                if (count <= 0)
                    count = FEPConstantValue.DEFAULT_COUNT_SIZE;
            }

        } catch (NumberFormatException e) {
            count = FEPConstantValue.DEFAULT_COUNT_SIZE;
        }

        DMALogDataCenter.Instance.loadDataAndNotify();
        TransactionTracker tracker = TransactionTracker.Instance;

        List<DMALog> logList = tracker.track(orderId);
        totalLength = logList.size();

        TransactionTrackerDataStruct trackData;

        if (totalLength == 0) {
            trackData = new TransactionTrackerDataStruct.Builder()
                    .totalLength(0)
                    .timeStampLength(0)
                    .logListLength(0)
                    .build();

            trackData.fillObjectNode(outputNode);
            return obj;
        }

        String issueCode = logList.get(0).getIsinCode();
        String issueName = AliasManager.Instance.getKoreanFromIsin(issueCode);
        List<LocalTime> timeStamp = DMALogUtil.getTimeStampFromLogList(logList, FEPConstantValue.DEFAULT_COUNT_SIZE);
        logList = DMALogUtil.timeFilter(logList, startTime, endTime, count);
        trackData = new TransactionTrackerDataStruct.Builder()
                .issueCode(issueCode)
                .issueName(issueName)
                .totalLength(totalLength)
                .timeStampLength(timeStamp.size())
                .timeStampInterval(FEPConstantValue.DEFAULT_COUNT_SIZE)
                .timeStamp(timeStamp)
                .logListLength(logList.size())
                .logList(logList)
                .build();
        System.out.println("Send Track Data");

        trackData.fillObjectNode(outputNode);
        return obj;
    }
}
