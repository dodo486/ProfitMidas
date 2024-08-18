package bulls.dmaLog.shortSell;

import bulls.staticData.AliasManager;

public class Balance {
    public final String tag;
    public final String isinCode;

    public int normalBalance;
    public int borrowBalance;
    public int originalBorrowCount;

    public Balance(String tag, String isinCode) {
        this.tag = tag;
        this.isinCode = isinCode;
    }

    public void setBalance(int normalBalance, int borrowBalance, int originalBorrowCount) {
        this.normalBalance = normalBalance;
        this.borrowBalance = borrowBalance;
        this.originalBorrowCount = originalBorrowCount;
    }

    public void setBalance(Balance balance) {
        this.normalBalance = balance.normalBalance;
        this.borrowBalance = balance.borrowBalance;
        this.originalBorrowCount = balance.originalBorrowCount;
    }

    public void addBalance(int normalBalance, int borrowBalance, int originalBorrowCount) {
        addNormalBalance(normalBalance);
        addBorrowBalance(borrowBalance);
        this.originalBorrowCount += originalBorrowCount;
    }

    public void addBalance(Balance balance) {
        addNormalBalance(balance.normalBalance);
        addBorrowBalance(balance.borrowBalance);
        this.originalBorrowCount += balance.originalBorrowCount;
    }

    public void increaseOriginalBorrowCount(int originalBorrowCount) {
        this.originalBorrowCount += originalBorrowCount;
        this.borrowBalance += originalBorrowCount;
    }

    public int getNetPosition() {
        return normalBalance + borrowBalance - originalBorrowCount;
    }

    public void addNormalBalance(int normalBalance) {
        this.normalBalance += normalBalance;
        if (this.normalBalance < 0)
            System.out.println("잔고 처리 확인 필요 : " + tag + " " + AliasManager.Instance.getKoreanFromIsin(isinCode) + " (" + isinCode + ") 일반 잔고가 0보다 작아졌습니다. 일반잔고=" + this.normalBalance);
    }

    public void addBorrowBalance(int borrowBalance) {
        this.borrowBalance += borrowBalance;
        if (this.borrowBalance < 0)
            System.out.println("잔고 처리 확인 필요 : " + AliasManager.Instance.getKoreanFromIsin(isinCode) + " (" + isinCode + ") 대차 잔고가 0보다 작아졌습니다. 대차잔고=" + this.borrowBalance);
    }

    @Override
    public String toString() {
        return "Balance{" +
                "tag='" + tag + '\'' +
                ", isinCode='" + isinCode + '\'' +
                ", normalBalance=" + normalBalance +
                ", borrowBalance=" + borrowBalance +
                ", originalBorrowCount=" + originalBorrowCount +
                ", netPosition=" + getNetPosition() +
                '}';
    }
}
