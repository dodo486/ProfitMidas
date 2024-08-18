package bulls.feed.next.parser.option;

import bulls.feed.abstraction.FeedParser;
import bulls.packet.OffsetLength;
import bulls.packet.PacketType;

public enum Option_민감도 implements FeedParser {

    trCode(0, 5, PacketType.String),  //데이터구분값(0, 2, PacketType.String), + 정보구분값(2, 3, PacketType.String),
    isinCode(5, 12, PacketType.String),
    생성일자(23, 8, PacketType.Integer), //산출일자(23, 8, PacketType.String),
    생성시각(31, 9, PacketType.Integer),  //산출시각(31, 9, PacketType.String),
    구분코드(40, 1, PacketType.String),     // 1:전일확정, 2:장중산출, E:장중완료, 3:당일확정   내재변동성산출구분코드(40, 1, PacketType.String),
    기초자산ID(41, 3, PacketType.String),  //기초자산ID(41, 3, PacketType.String),
    DeltaSign(44, 1, PacketType.String),  //민감도델타(44, 1, PacketType.Float),
    Delta(45, 19, PacketType.Float), //민감도델타(45, 19, PacketType.Float),
    ThetaSign(64, 1, PacketType.String), //민감도쎄타(64, 1, PacketType.Float),
    Theta(65, 19, PacketType.Float), //민감도쎄타(65, 19, PacketType.Float),
    VegaSign(84, 1, PacketType.String), //민감도베가(84, 1, PacketType.Float),
    Vega(85, 19, PacketType.Float), //민감도베가(85, 19, PacketType.Float),
    GammaSign(104, 1, PacketType.String), //민감도감마(104, 1, PacketType.Float),
    Gamma(105, 19, PacketType.Float),  //민감도감마(105, 19, PacketType.Float),
    RhoSign(124, 1, PacketType.String), //민감도로(124, 1, PacketType.Float),
    Rho(125, 19, PacketType.Float); //민감도로(125, 19, PacketType.Float),


    Option_민감도(int offSet, int length, PacketType type) {
        m = new OffsetLength(offSet, length, type);
    }

    private final OffsetLength m;

    @Override
    public OffsetLength parser() {
        return m;
    }
}
