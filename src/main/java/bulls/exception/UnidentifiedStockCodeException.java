package bulls.exception;

import bulls.staticData.AliasManager;

public class UnidentifiedStockCodeException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 6686069315588937457L;

    String stockCode;


    public UnidentifiedStockCodeException(String stockCode, String errMsg) {
        super(String.format("%s(%s), %s", AliasManager.Instance.getKoreanFromIsin(stockCode), stockCode, errMsg));
        this.stockCode = stockCode;
    }

}
