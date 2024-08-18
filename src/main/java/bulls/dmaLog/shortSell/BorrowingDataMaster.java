package bulls.dmaLog.shortSell;

import bulls.tool.util.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class BorrowingDataMaster {
    private final List<BorrowingData> borrowingDataList;

    public BorrowingDataMaster() {
        this.borrowingDataList = new ArrayList<>();
    }

    /**
     * <h2>파일에서 대차 정보를 불러오기</h2>
     *
     * <p>Hephaestus의 주식 대차/대여 페이지 정보를 붙여넣기한 파일을 읽어서 BorrowingData를 생성한다</p>
     * @param filename 대차 정보 파일의 이름
     */
    public void loadFromFile(String filename) {
        List<String> lines = FileUtils.readFile(filename);
        if (lines == null || lines.isEmpty()) {
            System.out.println(filename + " 파일이 없습니다.");
            return;
        }

        //  0    1     2       3       4           5      6      7   8      9          10
        // 날짜	Seq	계좌번호	계좌이름	IsinCode	Korean	SlbType	요율	수량	processedBy	lastUpdate
        for (String line : lines) {
            // 탭을 공백으로 변환
            line = line.replaceAll("\t", " ");
            // 여러 개의 공백을 하나의 공백으로 변환
            line = line.replaceAll(" +", " ");

            String[] split = line.split(" ");

            try {
                String account = split[2];
                String isinCode = split[4];
                // 21 : 대차, AA : 설정으로 인한 잔고 감소 (바스켓), BB : 설정으로 인한 잔고 증가 (ETF)
                String type = split[6];
                int amount = Integer.parseInt(split[8]);
                // 서버에서 처리가 되지 않은 경우 processedBy가 공백이므로 길이가 하나 줄어든다
                // lastUpdate는 데이터 개수와 무관하게 마지막 것을 가져오면 된다
                String timeString = split[split.length - 1].replaceAll("\"", "");
                // 주식 대차/대여 처리 페이지에서 대차 내역 정보를 Copy하면 시간은 UTC 기준으로 표시되기 때문에 한국 시간으로 변환하기 위해 9시간을 더해준다
                LocalTime t = LocalDateTime.parse(timeString, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX")).toLocalTime().plusHours(9);

                if (type.equals("AA") || type.equals("상환"))
                    amount = -amount;

                BorrowingData borrowingData = new BorrowingData(t, isinCode, account, type, amount);
                borrowingDataList.add(borrowingData);
                System.out.println(borrowingData);
            } catch (NumberFormatException e) {
                // do nothing
            }
        }
    }

    @NotNull
    public List<BorrowingData> getBorrowingDataList() {
        return borrowingDataList;
    }

    @Override
    public String toString() {
        return "BorrowingDataMaster{" +
                "borrowingDataList=" + borrowingDataList +
                '}';
    }
}
