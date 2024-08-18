package bulls.designTemplate;

import bulls.log.DefaultLogger;

// 어떤 구현체가 run time 중간에 호출되어 이니셜라이즈 되는 동안 생성자 등에서 DB 로부터
// 데이터를 가져오는 등의 헤비한 작업으로 인해 예상치 못한 딜레이를 일으키는것을 막기 위함
public interface EarlyInitialize {
    default void touch() {
        DefaultLogger.logger.info("[{}] enum has been touched!", this.getClass().toString());
    }
}
