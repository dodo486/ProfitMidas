package bulls.dmaLog.shortSell;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import bulls.order.enums.FundType;
import bulls.staticData.AliasManager;
import bulls.tool.util.FileUtils;
import org.bson.Document;

import java.util.*;

public class BalanceMaster {
    private final Map<String, Set<String>> groupAccountMap;
    private final Map<String, String> accountGroupMap;

    // account, isinCode, balance
    private final Table<String, String, Balance> accountBalanceTable;
    // group, isinCode, balance
    private final Table<String, String, Balance> groupBalanceTable;

    private Set<String> groupSet;

    private static final Map<String, String> accountNameMap = new HashMap<>();

    static {
        accountNameMap.put("현물헷지일반1", "099136100008");
        accountNameMap.put("현물헷지일반2", "099136100009");
        accountNameMap.put("현물헷지일반3", "099136100010");
        accountNameMap.put("현물헷지일반4", "099136100011");
        accountNameMap.put("현물헷지일반5", "099136100012");
        accountNameMap.put("현물헷지일반9", "099136100016");
        accountNameMap.put("현물헷지일반10", "099136100017");
        accountNameMap.put("ETF2", "099136100019");
        accountNameMap.put("현물일반", "099136100020");
        accountNameMap.put("현물헷지일반11", "099136100021");
        accountNameMap.put("현물일반4", "099136100022");
        accountNameMap.put("현물일반5", "099136100025");
        accountNameMap.put("현물일반6", "099136100026");
        accountNameMap.put("현물헷지일반12", "099136100027");
        accountNameMap.put("현물헷지일반13", "099136100028");
        accountNameMap.put("현물헷징리반14", "099136100030");
        accountNameMap.put("현물헷지일반15", "099136100031");
        accountNameMap.put("현물헷지일반16", "099136100032");
        accountNameMap.put("현물헷지일반17", "099136100033");
        accountNameMap.put("현물일반7", "099136100034");
        accountNameMap.put("현물일반8", "099136100035");
        accountNameMap.put("현물일반9", "099136100036");
    }

    public BalanceMaster() {
        groupAccountMap = new HashMap<>();
        accountGroupMap = new HashMap<>();
        accountBalanceTable = HashBasedTable.create();
        groupBalanceTable = HashBasedTable.create();
    }

    /**
     * <h2>파일에서 잔고 정보 불러오기</h2>
     *
     * <p>원장의 잔고 정보 (이글프로 31111 화면)를 붙여넣기한 파일을 읽어서 Balance를 생성한다</p>
     *
     * @param filename 잔고 정보 파일의 이름
     * @return 파일을 제대로 읽었는지 여부
     */
    public boolean loadFromFile(String filename) {
        List<String> lines = FileUtils.readFile(filename);
        if (lines == null || lines.isEmpty()) {
            System.out.println(filename + " 파일이 없습니다.");
            return false;
        }

        //   1      2      3       4        5            6            7           8         9         10      11    12     13       14     15
        // 펀드명 종목코드 종목명 보유수량 매도가능수량 대차매도가능수량 대차매도수량 대차매도단가 대차매도금액 취득단가 취득금액 현재가 평가금액 평가손익 평가손익률
        // 3129800012 현물헷지일반2 A000080 하이트진로 21040 21040 0 30000 29937 898131550 36192 761483923 37300 784792000 23308077 3.0600000
        // 0          1           2        3        4     5     6 7     8     9         10    11        12    13        14       15
        for (String line : lines) {
            // 탭을 공백으로 변환
            line = line.replaceAll("\t", " ");
            // 여러 개의 공백을 하나의 공백으로 변환
            line = line.replaceAll(" +", " ");

            String[] split = line.split(" ");
            try {
                if (split.length < 16)
                    continue;

                int pad = split.length - 16;
                String accountName = split[1];
                String accountNumber = accountNameMap.get(accountName);
                // 종목명에 띄어쓰기가 있는 경우 처리
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i <= pad; i++)
                    sb.append(split[3 + i]).append(" ");
                sb.deleteCharAt(sb.length() - 1);

                String korean = sb.toString();

                String isinCode = AliasManager.Instance.getIsinFromKorean(korean);
                // 일반잔고 : 매도가능수량
                // 대차잔고 : 대차매도가능수량
                // 원대차수량 : 대차매도가능수량 + 대차매도수량
                int normalBalance = Integer.parseInt(split[5 + pad]);
                int borrowBalance = Integer.parseInt(split[6 + pad]);
                int shortSellAmount = Integer.parseInt(split[7 + pad]);
                int originalBorrowCount = borrowBalance + shortSellAmount;

                if (accountNumber == null || isinCode == null) {
                    System.out.println("계좌번호 혹은 종목코드를 알 수 없어서 잔고 정보를 처리하지 않습니다 : " + line);
                    continue;
                }

                setBalanceData(isinCode, accountNumber, normalBalance, borrowBalance, originalBorrowCount);

            } catch (NumberFormatException e) {
                // do nothing
            }
        }

        return true;
    }

    /**
     * <h3>독립거래단위 잔고 업데이트</h3>
     *
     * <p>계좌 잔고를 설정하면 독립거래단위 잔고를 업데이트하기 위해 내부적으로 사용하는 함수</p>
     *
     * @param group    독립거래단위
     * @param isinCode 종목코드
     */
    private void updateGroupBalance(String group, String isinCode) {
        Balance groupBalance = new Balance(group, isinCode);

        if (group == null) {
            System.out.println();
        }

        for (String account : groupAccountMap.get(group)) {
            if (!accountBalanceTable.contains(account, isinCode))
                continue;

            Balance accountBalance = accountBalanceTable.get(account, isinCode);
            groupBalance.addBalance(accountBalance);
        }

        groupBalanceTable.put(group, isinCode, groupBalance);
    }

    /**
     * 독립거래단위별 계좌 목록 설정
     *
     * @param groupAccountMap 독립거래단위별 계좌 목록
     */
    public void setGroupData(Map<String, Set<String>> groupAccountMap) {
        this.groupAccountMap.clear();
        this.accountGroupMap.clear();

        for (var entry : groupAccountMap.entrySet()) {
            String group = entry.getKey();

            for (String account : entry.getValue()) {
                this.groupAccountMap.computeIfAbsent(group, k -> new HashSet<>()).add(account);
                accountGroupMap.put(account, group);
            }
        }
    }

    /**
     * 잔고를 관리할 독립거래단위 설정
     *
     * @param groupSet 독립거래단위 목록
     */
    public void setGroupSet(Set<String> groupSet) {
        this.groupSet = groupSet;
    }

    /**
     * 계좌 잔고 설정
     *
     * @param isinCode            종목코드
     * @param account             계좌번호
     * @param normalBalance       일반잔고
     * @param borrowBalance       대차잔고
     * @param originalBorrowCount 원대차수량
     */
    public void setBalanceData(String isinCode, String account, int normalBalance, int borrowBalance, int originalBorrowCount) {
        Balance accountBalance = new Balance(account, isinCode);
        accountBalance.setBalance(normalBalance, borrowBalance, originalBorrowCount);
        accountBalanceTable.put(account, isinCode, accountBalance);
        String group = accountGroupMap.get(account);
        updateGroupBalance(group, isinCode);

        System.out.println(accountBalance);
    }

    public void addBalance(FundType fundType, String isinCode, String account, int balance) {
        if (fundType == FundType.일반잔고)
            addNormalBalance(isinCode, account, balance);
        else if (fundType == FundType.대차잔고)
            addBorrowBalance(isinCode, account, balance);
    }

    public void addNormalBalance(String isinCode, String account, int normalBalance) {
        if (!accountBalanceTable.contains(account, isinCode))
            setBalanceData(isinCode, account, 0, 0, 0);

        String group = accountGroupMap.get(account);
        Balance groupBalance = groupBalanceTable.get(group, isinCode);
        Balance accountBalance = accountBalanceTable.get(account, isinCode);
        groupBalance.addNormalBalance(normalBalance);
        accountBalance.addNormalBalance(normalBalance);
    }

    public void addBorrowBalance(String isinCode, String account, int borrowBalance) {
        if (!accountBalanceTable.contains(account, isinCode))
            setBalanceData(isinCode, account, 0, 0, 0);

        String group = accountGroupMap.get(account);
        Balance groupBalance = groupBalanceTable.get(group, isinCode);
        Balance accountBalance = accountBalanceTable.get(account, isinCode);
        groupBalance.addBorrowBalance(borrowBalance);
        accountBalance.addBorrowBalance(borrowBalance);
    }

    public void addOriginalBorrowCount(BorrowingData borrowingData) {
        addOriginalBorrowCount(borrowingData.isinCode, borrowingData.account, borrowingData.amount);
    }

    public void addOriginalBorrowCount(String isinCode, String account, int originalBorrowCount) {
        if (!accountBalanceTable.contains(account, isinCode))
            setBalanceData(isinCode, account, 0, 0, 0);

        String group = accountGroupMap.get(account);
        Balance groupBalance = groupBalanceTable.get(group, isinCode);
        Balance accountBalance = accountBalanceTable.get(account, isinCode);
        groupBalance.increaseOriginalBorrowCount(originalBorrowCount);
        accountBalance.increaseOriginalBorrowCount(originalBorrowCount);
    }

    /**
     * 해당 계좌가 속한 독립거래단위의 순 포지션 반환
     *
     * @param isinCode 종목코드
     * @param account  계좌
     * @return 해당 계좌가 속한 독립거래단위의 순 포지션
     */
    public int getNetPosition(String isinCode, String account) {
        if (!accountBalanceTable.contains(account, isinCode))
            setBalanceData(isinCode, account, 0, 0, 0);

        String group = accountGroupMap.get(account);
        Balance groupBalance = groupBalanceTable.get(group, isinCode);
        return groupBalance.getNetPosition();
    }

    public int getBorrowBalance(String isinCode, String account) {
        if (!accountBalanceTable.contains(account, isinCode)) {
            System.out.println(account + " " + isinCode + " 잔고 정보 없음");
            return 0;
        }

        return accountBalanceTable.get(account, isinCode).borrowBalance;
    }

    /**
     * <h2>BalanceMaster에서 처리 가능한 계좌인지 확인</h2>
     *
     * @param account 계좌번호
     * @return BalanceMaster에서 처리하는 group에 속한 계좌인지 여부
     */
    public boolean isValidAccount(String account) {
        String group = accountGroupMap.get(account);
        if (group == null)
            return false;

        return groupSet.contains(group);
    }

    public String getGroupName(String account) {
        return accountGroupMap.get(account);
    }

    public Balance getAccountBalance(String isinCode, String account) {
        return accountBalanceTable.get(account, isinCode);
    }

    public Balance getGroupBalance(String isinCode, String account) {
        String group = accountGroupMap.get(account);
        return groupBalanceTable.get(group, isinCode);
    }

    public List<Document> getDocumentList() {
        List<Document> documentList = new ArrayList<>();

        for (String account : accountBalanceTable.rowKeySet()) {
            for (String isinCode : accountBalanceTable.columnKeySet()) {
                String group = accountGroupMap.get(account);
                if (group == null)
                    continue;

                Balance accountBalance = accountBalanceTable.get(account, isinCode);
                Balance groupBalance = groupBalanceTable.get(group, isinCode);
                if (accountBalance == null)
                    continue;

                Document doc = new Document();
                doc.append("종목코드", isinCode)
                        .append("종목명", AliasManager.Instance.getKoreanFromIsin(isinCode))
                        .append("독립거래단위", group)
                        .append("계좌번호", account)
                        .append("계좌_일반잔고", accountBalance.normalBalance)
                        .append("계좌_대차잔고", accountBalance.borrowBalance)
                        .append("계좌_원대차수량", accountBalance.originalBorrowCount)
                        .append("매도가능잔고", accountBalance.normalBalance + accountBalance.borrowBalance);

                if (groupBalance != null) {
                    doc.append("독립거래단위_일반잔고", groupBalance.normalBalance)
                            .append("독립거래단위_대차잔고", groupBalance.borrowBalance)
                            .append("독립거래단위_원대차수량", groupBalance.originalBorrowCount)
                            .append("독립거래단위_순포지션", groupBalance.getNetPosition());
                }

                documentList.add(doc);
            }
        }

        return documentList;
    }
}
