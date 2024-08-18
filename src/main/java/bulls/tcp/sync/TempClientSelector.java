package bulls.tcp.sync;

import bulls.websocket.WebSocketGlobalMessageBroadcaster;


// 나중에 NB 안정성이 검증 되면 지우자
public abstract class TempClientSelector {


    public static final boolean isNB = true;

    public static void notifyLazy(String jsonPacket) {
        NBPeriodicUpdater.Instance.notifyLazy(jsonPacket);

//        if (isNB) {
//            NBPeriodicUpdater.Instance.notifyLazy(jsonPacket);
//        } else {
//            ClientConnCenter.notifyLazy(jsonPacket);
//        }
    }


    public static void notifyLazy(String trCode, String key, String value) {
        NBPeriodicUpdater.Instance.notifyLazy(trCode, key, value);

//        if (isNB) {
//            NBPeriodicUpdater.Instance.notifyLazy(trCode, key, value);
//        } else {
//            ClientConnCenter.notifyLazy(trCode, key, value);
//        }
    }


    // 새로 보낼테니 이전 데이터 지워라
    public static void notifyReset(String trToReset) {
        NBPeriodicUpdater.Instance.notifyReset(trToReset);

//        if (isNB) {
//            NBPeriodicUpdater.Instance.notifyReset(trToReset);
//        } else {
//            ClientConnCenter.notifyReset(trToReset);
//        }
    }

    public static void notifyEmergency(String category, Object o) {
        NBPeriodicUpdater.Instance.notifyEmergency(category, o);

//        if (isNB) {
//            NBPeriodicUpdater.Instance.notifyEmergency(category, o);
//        } else {
//            ClientConnCenter.notifyEmergency(category, o);
//        }
    }

    public static void notifyEmergency(String category, String msg) {
        NBPeriodicUpdater.Instance.notifyEmergency(category, msg);

//        if (isNB) {
//            NBPeriodicUpdater.Instance.notifyEmergency(category, msg);
//        } else {
//            ClientConnCenter.notifyEmergency(category, msg);
//        }
    }


    public static void notifyObjectLazy(String trCode, Object o) {
        NBPeriodicUpdater.Instance.notifyObjectLazy(trCode, o);

//        if (isNB) {
//            NBPeriodicUpdater.Instance.notifyObjectLazy(trCode, o);
//        } else {
//            ClientConnCenter.notifyObjectLazy(trCode, o);
//        }
    }

    public static void sendLogMsg(String category, Object o) {
        NBPeriodicUpdater.Instance.sendLogMsg(category, o);
        WebSocketGlobalMessageBroadcaster.Instance.broadcastLogMessage(category, o);

//        if (isNB) {
//            NBPeriodicUpdater.Instance.sendLogMsg(category, o);
//            WebSocketGlobalMessageBroadcaster.Instance.broadcastLogMessage(category, o);
//        } else {
//            ClientConnCenter.sendLogMsg(category, o);
//        }
    }


    public static void sendLogMsg(String category, String msg) {
        NBPeriodicUpdater.Instance.sendLogMsg(category, msg);
        WebSocketGlobalMessageBroadcaster.Instance.broadcastLogMessage(category, msg);

//        if (isNB) {
//            NBPeriodicUpdater.Instance.sendLogMsg(category, msg);
//            WebSocketGlobalMessageBroadcaster.Instance.broadcastLogMessage(category, msg);
//        } else {
//            ClientConnCenter.sendLogMsg(category, msg);
//        }
    }


    public static void sendLogMsg(String msg) {
        NBPeriodicUpdater.Instance.sendLogMsg(msg);

//        if (isNB) {
//            NBPeriodicUpdater.Instance.sendLogMsg(msg);
//        } else {
//            ClientConnCenter.sendLogMsg(msg);
//        }
    }


}
