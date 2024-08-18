package bulls.net;

import bulls.dateTime.TimeCenter;
import bulls.designTemplate.HasTime;
import bulls.designTemplate.TimeSortedMap;
import bulls.dmaLog.DMALog;
import bulls.dmaLog.loader.FEPDirectoryLoader;
import bulls.dmaLog.shortSell.*;
import bulls.hephaestus.collection.AccountMaster;
import bulls.hephaestus.document.AccountMasterDoc;
import bulls.staticData.AliasManager;

import java.io.IOException;
import java.io.PrintStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * <h2>DMALogShortSellChecker</h2>
 *
 * <p>DMA Log, 잔고 정보, 대차 정보를 읽어서 신규 매도 주문의 공매도 표시를 검증한다</p>
 * <p></p>
 *
 * <h3>입력</h3>
 * <p>DMA Log 파일 경로 : feplog/feplog_seoul_yyyyMMdd.zip</p>
 * <p>잔고 정보 파일 경로 : balanceData/yyyyMMdd</p>
 * <p>대차 정보 파일 경로 : borrowingData/yyyyMMdd</p>
 * <p></p>
 *
 * <h3>출력</h3>
 * <p>잔고 변동 내역 : yyyyMMdd.csv</p>
 */
public class DMALogShortSellChecker {
    public static void main(String[] args) {
        List<String> keyList = List.of(
                "주문시각", "체결시각", "독립단위", "주문번호", "원주문번호", "계좌번호", "매매구분", "종목코드", "종목명", "주문수량", "체결수량", "대차거래시각", "차입", "상환", "매도가능잔고",
                "계좌_일반잔고", "계좌_대차잔고", "계좌_원대차수량", "독립거래단위_일반잔고", "독립거래단위_대차잔고", "독립거래단위_원대차수량", "독립거래단위_순포지션"
        );
        LocalDate today = LocalDate.of(2022, 3, 17);
        TimeCenter.Instance.setDateAndTime(today);

        Set<String> groupSet = Set.of("전략파트");

        AliasManager.Instance.touch();

        Map<String, Set<String>> groupAccountMap = new HashMap<>();
        for (AccountMasterDoc doc : AccountMaster.Instance.getAllEquityAccountDoc()) {
            String group = doc.groupName;
            String account = doc.accountNumber;

            groupAccountMap.computeIfAbsent(group, k -> new HashSet<>()).add(account);
        }

        String logDirectory = "feplog";
        FEPDirectoryLoader loader = new FEPDirectoryLoader(logDirectory);
        if (!loader.init()) {
            System.out.println("FEPDirectoryLoader 실패");
            return;
        }

        var logDataList = loader.load();

        for (var logData : logDataList) {
            LocalDate date = logData.getDate();
            String dateString = date.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

            DocumentMaster documentMaster = new DocumentMaster(date);

            // BalanceMaster에 독립거래단위 정보, 잔고 정보 설정
            BalanceMaster balanceMaster = new BalanceMaster();
            balanceMaster.setGroupData(groupAccountMap);
            balanceMaster.setGroupSet(groupSet);
            if (!balanceMaster.loadFromFile("balanceData/" + dateString))
                continue;

            documentMaster.addAll(balanceMaster.getDocumentList());

            // 해석기에 BalanceMaster 넣어주기
            DMALogInterpreter interpreter = new DMALogInterpreter();
            interpreter.setBalanceMaster(balanceMaster);
            interpreter.setDocumentMaster(documentMaster);

            // 시간 순대로 처리하기 위해 모든 정보를 시간 순으로 정렬
            TimeSortedMap<HasTime> sortedDataMap = new TimeSortedMap<>();

            // DMALog 추가
            // 2022년 5월 1일 이전 FEP 서버 시간이 느렸기 때문에 시간을 보정해주어야 한다 (+8초)
            if (date.compareTo(LocalDate.of(2022, 5, 1)) < 0) {
                for (var logList : logData.getFullDMALogMap().values()) {
                    for (var log : logList) {
                        log.setTime(log.getTime().plusSeconds(8));
                        sortedDataMap.put(log);
                    }
                }
            } else {
                for (var logList : logData.getFullDMALogMap().values())
                    sortedDataMap.putAll(logList);
            }

            // 대차내역 추가
            BorrowingDataMaster borrowingDataMaster = new BorrowingDataMaster();
            borrowingDataMaster.loadFromFile("borrowingData/" + dateString);
            sortedDataMap.putAll(borrowingDataMaster.getBorrowingDataList());

            for (var sortedData : sortedDataMap.values()) {
                if (sortedData instanceof BorrowingData) {
                    balanceMaster.addOriginalBorrowCount((BorrowingData) sortedData);
                    documentMaster.add(((BorrowingData) sortedData).getDocument());
                } else if (sortedData instanceof DMALog) {
                    boolean success = interpreter.checkShortSellCode((DMALog) sortedData);
                    if (!success) {
                        System.out.println("공매도 태그 오류 발견!");
                    }
                }
            }

            try (PrintStream ps = new PrintStream(dateString + ".csv", "EUC-KR")) {
                documentMaster.print(ps, keyList);
            } catch (IOException e) {
                e.printStackTrace();
            }

            documentMaster.print(keyList);
        }
    }
}

