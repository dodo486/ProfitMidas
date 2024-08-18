package bulls.tcp;

import bulls.staticData.TempConf;
import bulls.tcp.sync.NBPeriodicUpdater;

import java.util.List;

/**
 * 클라이언트와 공유되기 위한 컨텐츠
 */
public interface ClientContents {

    String getTrCode();

    List<?> getPeriodicUpdateContents();

    // 현재까지 체결 결과와 같은 컨텐츠는 키 없이 접속시 한번만 일괄 업데이트..
    // 이런 경우 default method 를 override 하면 되겠다...
    // PosManager 참고
    default List<?> getInitialUpdateContents() {
        return getPeriodicUpdateContents();
    }

    default void notifyClientAllPeriodically() {
        List<?> contentsList = getPeriodicUpdateContents();
        contentsList.forEach(this::notifyLazy);
    }

    default void notifyLazy(Object o) {
        NBPeriodicUpdater.Instance.notifyObjectLazy(getTrCode(), o);
    }

    default void notifyAllImmediately(Object o) {
        NBPeriodicUpdater.Instance.notifyObjectImmediately(getTrCode(), o);
    }

    default int getNotifyInterval() {
        return TempConf.CLIENT_NOTIFY_INTERVAL;
    }
}
