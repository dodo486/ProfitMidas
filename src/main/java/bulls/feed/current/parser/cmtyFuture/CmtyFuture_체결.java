package bulls.feed.current.parser.cmtyFuture;

import bulls.feed.abstraction.FeedParser;
import bulls.packet.OffsetLength;
import bulls.packet.PacketType;

// 상품선물은 22년 3월에 가격이 8자리 (정수 6, 소수 2) -> 9자리 (정수 6, 소수 3)로 변경됨
// 그러나 실제로 바뀐 부분은 무위험지표금리선물만 사용하고, 나머지 상품선물은 그대로 2자리만 사용한다. (항상 마지막 숫자가 0)
// 또한 주문을 제출할 때도 무위험지표금리선물을 제외하면 소수점 2자리만 사용한다.
// 현재 무위험지표금리선물을 매매하고 있지 않기 때문에 해당 선물만 제외하고 앞의 8자리만 파싱해서
// 나머지 상품선물은 가격이 8자리인 것처럼 처리해준다.
// Todo: 무위험지표금리선물 매매가 필요한 경우 무위험지표금리선물 파싱 정보 추가
public enum CmtyFuture_체결 implements FeedParser {
    trCode(0, 5, PacketType.String),
    isinCode(5, 12, PacketType.String),
    boardId(20, 2, PacketType.String),

    priceSign(22, 1, PacketType.String),
    price(23, 8, PacketType.Integer),   // 원래 9자리이지만 8자리만 파싱
    amount(32, 6, PacketType.Integer),
    totalAmount(106, 7, PacketType.Integer),
    totalPrice(113, 15, PacketType.Integer),
    buySellSign(142, 1, PacketType.String);

    CmtyFuture_체결(int offSet, int length, PacketType type) {
        m = new OffsetLength(offSet, length, type);
    }

    private final OffsetLength m;

    public OffsetLength parser() {
        return m;
    }
}
