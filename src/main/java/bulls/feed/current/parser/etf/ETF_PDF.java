package bulls.feed.current.parser.etf;

import bulls.feed.abstraction.FeedParser;
import bulls.packet.OffsetLength;
import bulls.packet.PacketType;

public enum ETF_PDF implements FeedParser {
    trCode(0, 5, PacketType.String),
    isinCodeEtf(5, 12, PacketType.String),
    codeEtf(8, 6, PacketType.String),

    isinCodePdfMember(40, 12, PacketType.String),
    codePdfMember(43, 6, PacketType.String),
    구성종목수량(52, 18, PacketType.Integer),
    구성종목시장구분(70, 1, PacketType.String);

    ETF_PDF(int offSet, int length, PacketType type) {
        m = new OffsetLength(offSet, length, type);
    }

    private final OffsetLength m;

    public OffsetLength parser() {
        return m;
    }

}
