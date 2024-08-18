package bulls.staticData;

import bulls.feed.dc.CodeEventHashMap;
import bulls.feed.dc.CodeObserver;
import bulls.feed.dc.enums.장운영TS;

public enum 장운영TSCenter {
    Instance;

    private final CodeEventHashMap<장운영TS> CurrentTSMap;

    장운영TSCenter() {
        this.CurrentTSMap = new CodeEventHashMap<>();
    }

    public void monitorCurrentTSOf(String code, String obName, CodeObserver<장운영TS> ob) {
        CurrentTSMap.addObserver(code, ob, obName);
    }

    public void stopMonitorBidAskOf(String code, String obName) {
        CurrentTSMap.deleteObserver(code, obName);
    }

    public void updateTS(String code, 장운영TS ts) {
        if (ts == null) {
            throw new NullPointerException("ts");
        }

        if (CurrentTSMap.containsKey(code)) {
            CurrentTSMap.replace(code, ts);
        } else {
            CurrentTSMap.put(code, ts);
        }
    }

    public 장운영TS getTS(String code) throws NullPointerException {
        장운영TS result = this.CurrentTSMap.get(code);
        return result;
    }
}
