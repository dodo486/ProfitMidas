package bulls.feed.current.parser.kospiOption;

import bulls.feed.abstraction.FeedParser;
import bulls.packet.OffsetLength;
import bulls.packet.PacketType;

public enum Option_민감도 implements FeedParser {
    trCode(0, 5, PacketType.String),
    isinCode(5, 12, PacketType.String),
    생성일자(24, 8, PacketType.Integer),
    생성시각(32, 8, PacketType.Integer),
    구분코드(40, 1, PacketType.String),     // 1:전일확정, 2:장중산출, E:장중완료, 3:당일확정
    기초자산ID(41, 3, PacketType.String),
    DeltaSign(44, 1, PacketType.String),
    Delta(45, 18, PacketType.Float),
    ThetaSign(63, 1, PacketType.String),
    Theta(64, 18, PacketType.Float),
    VegaSign(82, 1, PacketType.String),
    Vega(83, 18, PacketType.Float),
    GammaSign(101, 1, PacketType.String),
    Gamma(102, 18, PacketType.Float),
    RhoSign(120, 1, PacketType.String),
    Rho(121, 18, PacketType.Float);

    Option_민감도(int offSet, int length, PacketType type) {
        m = new OffsetLength(offSet, length, type);
    }

    private final OffsetLength m;

    @Override
    public OffsetLength parser() {
        return m;
    }
}
