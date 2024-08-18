package bulls.staticData.ProdType;

public enum ProdType {
    K200Futures(ProdClass.Derivatives, DerivativesType.Futures, DerivativesUnderlyingMarketType.STK, DerivativesProdClassType.지수, DerivativesUnderlyingType.K2I),
    K200CallOption(ProdClass.Derivatives, DerivativesType.CallOption, DerivativesUnderlyingMarketType.STK, DerivativesProdClassType.지수, DerivativesUnderlyingType.K2I),
    K200PutOption(ProdClass.Derivatives, DerivativesType.PutOption, DerivativesUnderlyingMarketType.STK, DerivativesProdClassType.지수, DerivativesUnderlyingType.K2I),
    K200FuturesSpread(ProdClass.Derivatives, DerivativesType.Spread, DerivativesUnderlyingMarketType.STK, DerivativesProdClassType.지수, DerivativesUnderlyingType.K2I),

    K200MiniFutures(ProdClass.Derivatives, DerivativesType.Futures, DerivativesUnderlyingMarketType.STK, DerivativesProdClassType.지수, DerivativesUnderlyingType.MKI),
    K200MiniCallOption(ProdClass.Derivatives, DerivativesType.CallOption, DerivativesUnderlyingMarketType.STK, DerivativesProdClassType.지수, DerivativesUnderlyingType.MKI),
    K200MiniPutOption(ProdClass.Derivatives, DerivativesType.PutOption, DerivativesUnderlyingMarketType.STK, DerivativesProdClassType.지수, DerivativesUnderlyingType.MKI),
    K200MiniFuturesSpread(ProdClass.Derivatives, DerivativesType.Spread, DerivativesUnderlyingMarketType.STK, DerivativesProdClassType.지수, DerivativesUnderlyingType.MKI),

    K200WeeklyCallOption(ProdClass.Derivatives, DerivativesType.CallOption, DerivativesUnderlyingMarketType.STK, DerivativesProdClassType.지수, DerivativesUnderlyingType.WKI),
    K200WeeklyPutOption(ProdClass.Derivatives, DerivativesType.PutOption, DerivativesUnderlyingMarketType.STK, DerivativesProdClassType.지수, DerivativesUnderlyingType.WKI),

    KQ150Futures(ProdClass.Derivatives, DerivativesType.Futures, DerivativesUnderlyingMarketType.KSQ, DerivativesProdClassType.지수, DerivativesUnderlyingType.KQI),
    KQ150CallOption(ProdClass.Derivatives, DerivativesType.CallOption, DerivativesUnderlyingMarketType.KSQ, DerivativesProdClassType.지수, DerivativesUnderlyingType.KQI),
    KQ150PutOption(ProdClass.Derivatives, DerivativesType.PutOption, DerivativesUnderlyingMarketType.KSQ, DerivativesProdClassType.지수, DerivativesUnderlyingType.KQI),
    KQ150FuturesSpread(ProdClass.Derivatives, DerivativesType.Spread, DerivativesUnderlyingMarketType.KSQ, DerivativesProdClassType.지수, DerivativesUnderlyingType.KQI),

    KRX300Futures(ProdClass.Derivatives, DerivativesType.Futures, DerivativesUnderlyingMarketType.STK, DerivativesProdClassType.지수, DerivativesUnderlyingType.XI3),
    KRX300FuturesSpread(ProdClass.Derivatives, DerivativesType.Spread, DerivativesUnderlyingMarketType.STK, DerivativesProdClassType.지수, DerivativesUnderlyingType.XI3),

    IndexFutures(ProdClass.Derivatives, DerivativesType.Futures, DerivativesUnderlyingMarketType.STK, DerivativesProdClassType.지수, DerivativesUnderlyingType.UNKNOWN),
    IndexFuturesSpread(ProdClass.Derivatives, DerivativesType.Spread, DerivativesUnderlyingMarketType.STK, DerivativesProdClassType.지수, DerivativesUnderlyingType.UNKNOWN),

    SectorITFutures(ProdClass.Derivatives, DerivativesType.Futures, DerivativesUnderlyingMarketType.STK, DerivativesProdClassType.섹터, DerivativesUnderlyingType.XA1),
    SectorITFuturesSpread(ProdClass.Derivatives, DerivativesType.Spread, DerivativesUnderlyingMarketType.STK, DerivativesProdClassType.섹터, DerivativesUnderlyingType.XA1),
    SectorHEALTHFutures(ProdClass.Derivatives, DerivativesType.Futures, DerivativesUnderlyingMarketType.STK, DerivativesProdClassType.섹터, DerivativesUnderlyingType.XA8),
    SectorHEALTHFuturesSpread(ProdClass.Derivatives, DerivativesType.Spread, DerivativesUnderlyingMarketType.STK, DerivativesProdClassType.섹터, DerivativesUnderlyingType.XA8),
    SectorHEAVYINDFutures(ProdClass.Derivatives, DerivativesType.Futures, DerivativesUnderlyingMarketType.STK, DerivativesProdClassType.섹터, DerivativesUnderlyingType.XA7),
    SectorHEAVYINDFuturesSpread(ProdClass.Derivatives, DerivativesType.Spread, DerivativesUnderlyingMarketType.STK, DerivativesProdClassType.섹터, DerivativesUnderlyingType.XA7),
    SectorConstructFutures(ProdClass.Derivatives, DerivativesType.Futures, DerivativesUnderlyingMarketType.STK, DerivativesProdClassType.섹터, DerivativesUnderlyingType.XA6),
    SectorConstructFuturesSpread(ProdClass.Derivatives, DerivativesType.Spread, DerivativesUnderlyingMarketType.STK, DerivativesProdClassType.섹터, DerivativesUnderlyingType.XA6),
    SectorGrowthFutures(ProdClass.Derivatives, DerivativesType.Futures, DerivativesUnderlyingMarketType.STK, DerivativesProdClassType.섹터, DerivativesUnderlyingType.XA5),
    SectorGrowthFuturesSpread(ProdClass.Derivatives, DerivativesType.Spread, DerivativesUnderlyingMarketType.STK, DerivativesProdClassType.섹터, DerivativesUnderlyingType.XA5),

    SectorChemistryFutures(ProdClass.Derivatives, DerivativesType.Futures, DerivativesUnderlyingMarketType.STK, DerivativesProdClassType.섹터, DerivativesUnderlyingType.XA0),
    SectorChemistryFuturesSpread(ProdClass.Derivatives, DerivativesType.Spread, DerivativesUnderlyingMarketType.STK, DerivativesProdClassType.섹터, DerivativesUnderlyingType.XA0),
    SectorFinanceFutures(ProdClass.Derivatives, DerivativesType.Futures, DerivativesUnderlyingMarketType.STK, DerivativesProdClassType.섹터, DerivativesUnderlyingType.XA2),
    SectorFinanceFuturesSpread(ProdClass.Derivatives, DerivativesType.Spread, DerivativesUnderlyingMarketType.STK, DerivativesProdClassType.섹터, DerivativesUnderlyingType.XA2),
    SectorConsuDiscretFutures(ProdClass.Derivatives, DerivativesType.Futures, DerivativesUnderlyingMarketType.STK, DerivativesProdClassType.섹터, DerivativesUnderlyingType.XA3),
    SectorConsuDiscretFuturesSpread(ProdClass.Derivatives, DerivativesType.Spread, DerivativesUnderlyingMarketType.STK, DerivativesProdClassType.섹터, DerivativesUnderlyingType.XA3),
    SectorDividendFutures(ProdClass.Derivatives, DerivativesType.Futures, DerivativesUnderlyingMarketType.STK, DerivativesProdClassType.섹터, DerivativesUnderlyingType.XA4),
    SectorDividendFuturesSpread(ProdClass.Derivatives, DerivativesType.Spread, DerivativesUnderlyingMarketType.STK, DerivativesProdClassType.섹터, DerivativesUnderlyingType.XA4),
    SectorSteelFutures(ProdClass.Derivatives, DerivativesType.Futures, DerivativesUnderlyingMarketType.STK, DerivativesProdClassType.섹터, DerivativesUnderlyingType.XA9),
    SectorSteelFuturesSpread(ProdClass.Derivatives, DerivativesType.Spread, DerivativesUnderlyingMarketType.STK, DerivativesProdClassType.섹터, DerivativesUnderlyingType.XA9),
    SectorConsuStapleFutures(ProdClass.Derivatives, DerivativesType.Futures, DerivativesUnderlyingMarketType.STK, DerivativesProdClassType.섹터, DerivativesUnderlyingType.XAA),
    SectorConsuStapleFuturesSpread(ProdClass.Derivatives, DerivativesType.Spread, DerivativesUnderlyingMarketType.STK, DerivativesProdClassType.섹터, DerivativesUnderlyingType.XAA),
    SectorIndustrialFutures(ProdClass.Derivatives, DerivativesType.Futures, DerivativesUnderlyingMarketType.STK, DerivativesProdClassType.섹터, DerivativesUnderlyingType.XAB),
    SectorIndustrialFuturesSpread(ProdClass.Derivatives, DerivativesType.Spread, DerivativesUnderlyingMarketType.STK, DerivativesProdClassType.섹터, DerivativesUnderlyingType.XAB),

    SectorKNewDealBBIGFutures(ProdClass.Derivatives, DerivativesType.Futures, DerivativesUnderlyingMarketType.STK, DerivativesProdClassType.섹터, DerivativesUnderlyingType.XAC),
    SectorKNewDealBBIGFuturesSpread(ProdClass.Derivatives, DerivativesType.Spread, DerivativesUnderlyingMarketType.STK, DerivativesProdClassType.섹터, DerivativesUnderlyingType.XAC),

    SectorKNewDealBatteryFutures(ProdClass.Derivatives, DerivativesType.Futures, DerivativesUnderlyingMarketType.STK, DerivativesProdClassType.섹터, DerivativesUnderlyingType.XAD),
    SectorKNewDealBatteryFuturesSpread(ProdClass.Derivatives, DerivativesType.Spread, DerivativesUnderlyingMarketType.STK, DerivativesProdClassType.섹터, DerivativesUnderlyingType.XAD),

    SectorKNewDealBioFutures(ProdClass.Derivatives, DerivativesType.Futures, DerivativesUnderlyingMarketType.STK, DerivativesProdClassType.섹터, DerivativesUnderlyingType.XAE),
    SectorKNewDealBioFuturesSpread(ProdClass.Derivatives, DerivativesType.Spread, DerivativesUnderlyingMarketType.STK, DerivativesProdClassType.섹터, DerivativesUnderlyingType.XAE),

    SectorFutures(ProdClass.Derivatives, DerivativesType.Futures, DerivativesUnderlyingMarketType.STK, DerivativesProdClassType.섹터, DerivativesUnderlyingType.UNKNOWN),
    SectorFuturesSpread(ProdClass.Derivatives, DerivativesType.Spread, DerivativesUnderlyingMarketType.STK, DerivativesProdClassType.섹터, DerivativesUnderlyingType.UNKNOWN),

    KOSPIStockFutures(ProdClass.Derivatives, DerivativesType.Futures, DerivativesUnderlyingMarketType.STK, DerivativesProdClassType.개별주식, DerivativesUnderlyingType.UNKNOWN),
    KOSPIStockCallOptions(ProdClass.Derivatives, DerivativesType.CallOption, DerivativesUnderlyingMarketType.STK, DerivativesProdClassType.개별주식, DerivativesUnderlyingType.UNKNOWN),
    KOSPIStockPutOptions(ProdClass.Derivatives, DerivativesType.PutOption, DerivativesUnderlyingMarketType.STK, DerivativesProdClassType.개별주식, DerivativesUnderlyingType.UNKNOWN),
    KOSPIStockFuturesSpread(ProdClass.Derivatives, DerivativesType.Spread, DerivativesUnderlyingMarketType.STK, DerivativesProdClassType.개별주식, DerivativesUnderlyingType.UNKNOWN),

    KOSDAQStockFutures(ProdClass.Derivatives, DerivativesType.Futures, DerivativesUnderlyingMarketType.KSQ, DerivativesProdClassType.개별주식, DerivativesUnderlyingType.UNKNOWN),
    KOSDAQStockCallOptions(ProdClass.Derivatives, DerivativesType.CallOption, DerivativesUnderlyingMarketType.KSQ, DerivativesProdClassType.개별주식, DerivativesUnderlyingType.UNKNOWN),
    KOSDAQStockPutOptions(ProdClass.Derivatives, DerivativesType.PutOption, DerivativesUnderlyingMarketType.KSQ, DerivativesProdClassType.개별주식, DerivativesUnderlyingType.UNKNOWN),
    KOSDAQStockFuturesSpread(ProdClass.Derivatives, DerivativesType.Spread, DerivativesUnderlyingMarketType.KSQ, DerivativesProdClassType.개별주식, DerivativesUnderlyingType.UNKNOWN),

    EuroStoxxFutures(ProdClass.Derivatives, DerivativesType.Futures, DerivativesUnderlyingMarketType.GBL, DerivativesProdClassType.유로스톡스, DerivativesUnderlyingType.UNKNOWN),
    EuroStoxxFuturesSpread(ProdClass.Derivatives, DerivativesType.Spread, DerivativesUnderlyingMarketType.GBL, DerivativesProdClassType.유로스톡스, DerivativesUnderlyingType.UNKNOWN),

    VkospiFutures(ProdClass.Derivatives, DerivativesType.Futures, DerivativesUnderlyingMarketType.STK, DerivativesProdClassType.변동성, DerivativesUnderlyingType.VKI),
    VkospiFuturesSpread(ProdClass.Derivatives, DerivativesType.Spread, DerivativesUnderlyingMarketType.STK, DerivativesProdClassType.변동성, DerivativesUnderlyingType.VKI),

    BondFutures(ProdClass.Derivatives, DerivativesType.Futures, DerivativesUnderlyingMarketType.UNDEF, DerivativesProdClassType.금리, DerivativesUnderlyingType.UNKNOWN),
    BondFuturesSpread(ProdClass.Derivatives, DerivativesType.Spread, DerivativesUnderlyingMarketType.UNDEF, DerivativesProdClassType.금리, DerivativesUnderlyingType.UNKNOWN),

    FXFutures(ProdClass.Derivatives, DerivativesType.Futures, DerivativesUnderlyingMarketType.UNDEF, DerivativesProdClassType.환율, DerivativesUnderlyingType.UNKNOWN),
    FXFuturesSpread(ProdClass.Derivatives, DerivativesType.Spread, DerivativesUnderlyingMarketType.UNDEF, DerivativesProdClassType.환율, DerivativesUnderlyingType.UNKNOWN),

    FlexFutures(ProdClass.Derivatives, DerivativesType.Flex, DerivativesUnderlyingMarketType.UNDEF, DerivativesProdClassType.환율, DerivativesUnderlyingType.UNKNOWN),
    FlexFuturesSpread(ProdClass.Derivatives, DerivativesType.Flex, DerivativesUnderlyingMarketType.UNDEF, DerivativesProdClassType.환율, DerivativesUnderlyingType.UNKNOWN),

    //commodity
    LeanHogFutures(ProdClass.Derivatives, DerivativesType.Futures, DerivativesUnderlyingMarketType.UNDEF, DerivativesProdClassType.돈육, DerivativesUnderlyingType.UNKNOWN),
    LeanHogFuturesSpread(ProdClass.Derivatives, DerivativesType.Spread, DerivativesUnderlyingMarketType.UNDEF, DerivativesProdClassType.돈육, DerivativesUnderlyingType.UNKNOWN),

    GoldFutures(ProdClass.Derivatives, DerivativesType.Futures, DerivativesUnderlyingMarketType.CMD, DerivativesProdClassType.금, DerivativesUnderlyingType.UNKNOWN),
    GoldFuturesSpread(ProdClass.Derivatives, DerivativesType.Spread, DerivativesUnderlyingMarketType.CMD, DerivativesProdClassType.금, DerivativesUnderlyingType.UNKNOWN),

    EquityKOSPI(ProdClass.Equity, EquitySecurityGroupType.ST, true),
    EquityKOSDAQ(ProdClass.Equity, EquitySecurityGroupType.ST, false),
    EquityELW(ProdClass.Equity, EquitySecurityGroupType.EW, true),
    EquityETF(ProdClass.Equity, EquitySecurityGroupType.EF, true),
    EquityETN(ProdClass.Equity, EquitySecurityGroupType.EN, true),
    EquityKOSPI신주인수권증권(ProdClass.Equity, EquitySecurityGroupType.SW, true),
    EquityKOSPI신주인수권증서(ProdClass.Equity, EquitySecurityGroupType.SR, true),
    EquityKOSDAQ신주인수권증권(ProdClass.Equity, EquitySecurityGroupType.SW, false),
    EquityKOSDAQ신주인수권증서(ProdClass.Equity, EquitySecurityGroupType.SR, false),
    EquityKOSPIDR(ProdClass.Equity, EquitySecurityGroupType.DR, true),
    EquityKOSDAQDR(ProdClass.Equity, EquitySecurityGroupType.DR, false),
    EquityBC(ProdClass.Equity, EquitySecurityGroupType.BC, true),
    EquityInfraFund(ProdClass.Equity, EquitySecurityGroupType.IF, true),
    EquityFE(ProdClass.Equity, EquitySecurityGroupType.FE, true),
    EquityKOSPIFS(ProdClass.Equity, EquitySecurityGroupType.FS, true),
    EquityKOSDAQFS(ProdClass.Equity, EquitySecurityGroupType.FS, false),
    EquityMF(ProdClass.Equity, EquitySecurityGroupType.MF, true),
    EquityReits(ProdClass.Equity, EquitySecurityGroupType.RT, true),
    EquitySC(ProdClass.Equity, EquitySecurityGroupType.SC, true),

    DerivativesUnknown(ProdClass.Derivatives, DerivativesType.UNKNOWN, DerivativesUnderlyingMarketType.UNKNOWN, DerivativesProdClassType.UNKNOWN, DerivativesUnderlyingType.UNKNOWN),
    EquityUnknown(ProdClass.Equity, EquitySecurityGroupType.UNKNOWN, false),
    Index(ProdClass.Index),
    Unknown(ProdClass.Unknown);

    final ProdClass prodClass;
    final DerivativesType dt;
    final DerivativesUnderlyingMarketType dumt;
    final DerivativesProdClassType dpct;
    final DerivativesUnderlyingType dut;
    final EquitySecurityGroupType esgt;
    final boolean KOSPIYN;

    ProdType(ProdClass prodClass) {
        this.prodClass = prodClass;
        dt = DerivativesType.NA;
        dumt = DerivativesUnderlyingMarketType.NA;
        dpct = DerivativesProdClassType.NA;
        dut = DerivativesUnderlyingType.NA;
        esgt = EquitySecurityGroupType.NA;
        KOSPIYN = false;
    }

    ProdType(ProdClass prodClass, EquitySecurityGroupType esgt, boolean KOSPIYN) {
        this.prodClass = prodClass;
        this.dt = DerivativesType.NA;
        this.dumt = DerivativesUnderlyingMarketType.NA;
        this.dpct = DerivativesProdClassType.NA;
        this.dut = DerivativesUnderlyingType.NA;
        this.esgt = esgt;
        this.KOSPIYN = KOSPIYN;
    }

    ProdType(ProdClass prodClass, DerivativesType dt, DerivativesUnderlyingMarketType dumt, DerivativesProdClassType dpct, DerivativesUnderlyingType dut) {
        this.prodClass = prodClass;
        this.dt = dt;
        this.dumt = dumt;
        this.dpct = dpct;
        this.dut = dut;
        this.esgt = EquitySecurityGroupType.NA;
        this.KOSPIYN = false;
    }

    public boolean isEquity() {
        return prodClass == ProdClass.Equity;
    }

    public boolean isDerivative() {
        return prodClass == ProdClass.Derivatives;
    }

    public boolean isIndex() {
        return prodClass == ProdClass.Index;
    }

    public boolean isUnknown() {
        return prodClass == ProdClass.Unknown;
    }

    public boolean isEquityStock() {
        return prodClass == ProdClass.Equity && esgt == EquitySecurityGroupType.ST;
    }

    public boolean isEquityStockKOSPI() {
        return prodClass == ProdClass.Equity && esgt == EquitySecurityGroupType.ST && KOSPIYN;
    }

    public boolean isEquityStockKOSDAQ() {
        return prodClass == ProdClass.Equity && esgt == EquitySecurityGroupType.ST && !KOSPIYN;
    }

    public boolean isEquityELW() {
        return prodClass == ProdClass.Equity && esgt == EquitySecurityGroupType.EW;
    }

    public boolean isEquityETN() {
        return prodClass == ProdClass.Equity && esgt == EquitySecurityGroupType.EN;
    }

    public boolean isEquityKospiStockWarrant() {
        return prodClass == ProdClass.Equity && esgt == EquitySecurityGroupType.SW;
    }

    public boolean isEquityKosdaqStockWarrant() {
        return prodClass == ProdClass.Equity && esgt == EquitySecurityGroupType.SR;
    }

    public boolean isEquityETF() {
        return prodClass == ProdClass.Equity && esgt == EquitySecurityGroupType.EF;
    }

    public boolean isEquityMF() {
        return prodClass == ProdClass.Equity && esgt == EquitySecurityGroupType.MF;
    }

    public boolean isEquityReits() {
        return prodClass == ProdClass.Equity && esgt == EquitySecurityGroupType.RT;
    }

    public boolean isEquityFS() {
        return prodClass == ProdClass.Equity && esgt == EquitySecurityGroupType.FS;
    }

    public boolean isEquityDR() {
        return prodClass == ProdClass.Equity && esgt == EquitySecurityGroupType.DR;
    }

    public boolean isEquityBC() {
        return prodClass == ProdClass.Equity && esgt == EquitySecurityGroupType.BC;
    }

    public boolean isFloatingPointDeriv() {
        return dpct == DerivativesProdClassType.지수 || dpct == DerivativesProdClassType.섹터 || dpct == DerivativesProdClassType.변동성 || dpct == DerivativesProdClassType.금리 || dpct == DerivativesProdClassType.환율;
    }

    public boolean isK200Deriv() {
        return dumt == DerivativesUnderlyingMarketType.STK && dpct == DerivativesProdClassType.지수 && (dut == DerivativesUnderlyingType.K2I || dut == DerivativesUnderlyingType.MKI || dut == DerivativesUnderlyingType.WKI);
    }

    public boolean isK200BigDeriv() {
        return dumt == DerivativesUnderlyingMarketType.STK && dpct == DerivativesProdClassType.지수 && dut == DerivativesUnderlyingType.K2I;
    }

    public boolean isCallOption() {
        return dt == DerivativesType.CallOption;
    }

    public boolean isPutOption() {
        return dt == DerivativesType.PutOption;
    }

    public boolean isK200Fut() {
        return this == ProdType.K200Futures;
    }

    public boolean isK200Call() {
        return this == ProdType.K200CallOption;
    }

    public boolean isK200Put() {
        return this == ProdType.K200PutOption;
    }

    public boolean isK200Opt() {
        return this == ProdType.K200CallOption || this == ProdType.K200PutOption;
    }

    public boolean isK200FutSP() {
        return this == ProdType.K200FuturesSpread;
    }

    public boolean isK200MiniDeriv() {
        return dumt == DerivativesUnderlyingMarketType.STK && dpct == DerivativesProdClassType.지수 && dut == DerivativesUnderlyingType.MKI;
    }

    public boolean isK200WeeklyDeriv() {
        return dumt == DerivativesUnderlyingMarketType.STK && dpct == DerivativesProdClassType.지수 && dut == DerivativesUnderlyingType.WKI;
    }

    public boolean isK200MiniFut() {
        return this == ProdType.K200MiniFutures;
    }

    public boolean isVKospiFut() {
        return this == ProdType.VkospiFutures;
    }

    public boolean isVKospiFutSP() {
        return this == ProdType.VkospiFuturesSpread;
    }

    public boolean isK200MiniCall() {
        return this == ProdType.K200MiniCallOption;
    }

    public boolean isK200MiniPut() {
        return this == ProdType.K200MiniPutOption;
    }

    public boolean isK200MiniOpt() {
        return this == ProdType.K200MiniCallOption || this == ProdType.K200MiniPutOption;
    }

    public boolean isK200MiniFutSP() {
        return this == ProdType.K200MiniFuturesSpread;
    }

    public boolean isK200WeeklyCall() {
        return this == ProdType.K200WeeklyCallOption;
    }

    public boolean isK200WeeklyiPut() {
        return this == ProdType.K200WeeklyPutOption;
    }

    public boolean isK200WeeklyOpt() {
        return this == ProdType.K200WeeklyCallOption || this == ProdType.K200WeeklyPutOption;
    }

    public boolean isKQ150Deriv() {
        return dumt == DerivativesUnderlyingMarketType.STK && dpct == DerivativesProdClassType.지수 && dut == DerivativesUnderlyingType.KQI;
    }

    public boolean isKQ150Fut() {
        return this == ProdType.KQ150Futures;
    }

    public boolean isKQ150Call() {
        return this == ProdType.KQ150CallOption;
    }

    public boolean isKQ150Put() {
        return this == ProdType.KQ150PutOption;
    }

    public boolean isKQ150Opt() {
        return this == ProdType.KQ150CallOption || this == ProdType.KQ150PutOption;
    }

    public boolean isKQ150FutSP() {
        return this == ProdType.KQ150FuturesSpread;
    }

    public boolean isKRX300Fut() {
        return this == ProdType.KRX300Futures;
    }

    public boolean isKRX300FutSP() {
        return this == ProdType.KRX300FuturesSpread;
    }

    //KOSDAQStockDeriv, KOSPIStockDeriv 모두 포함
    public boolean isStockFut() {
        return this == ProdType.KOSPIStockFutures || this == ProdType.KOSDAQStockFutures;
    }

    public boolean isStockFutKOSPI() {
        return this == ProdType.KOSPIStockFutures;
    }

    public boolean isStockFutSPKOSPI() {
        return this == ProdType.KOSPIStockFuturesSpread;
    }

    public boolean isStockFutKOSDAQ() {
        return this == ProdType.KOSDAQStockFutures;
    }

    public boolean isStockFutSP() {
        return this == ProdType.KOSPIStockFuturesSpread || this == ProdType.KOSDAQStockFuturesSpread;
    }

    public boolean isStockCall() {
        return this == ProdType.KOSPIStockCallOptions || this == ProdType.KOSDAQStockCallOptions;
    }

    public boolean isStockPut() {
        return this == ProdType.KOSPIStockPutOptions || this == ProdType.KOSDAQStockPutOptions;
    }

    public boolean isStockOpt() {
        return this == ProdType.KOSPIStockCallOptions || this == ProdType.KOSDAQStockCallOptions || this == ProdType.KOSPIStockPutOptions || this == ProdType.KOSDAQStockPutOptions;
    }

    public boolean isSectorFut() {
        return dpct == DerivativesProdClassType.섹터;
    }

    public boolean isSectorFutSP() {
        return dpct == DerivativesProdClassType.섹터 && dt == DerivativesType.Spread;
    }

    public boolean isSectorFut50() {
        return this == ProdType.SectorGrowthFutures || this == ProdType.SectorDividendFutures;
    }

    public boolean isSectorFut50SP() {
        return this == ProdType.SectorGrowthFuturesSpread || this == ProdType.SectorDividendFuturesSpread;
    }

    public boolean isFutures() {
        return dt == DerivativesType.Futures;
    }

    public boolean isSpread() {
        return dt == DerivativesType.Spread;
    }

    public boolean isOption() {
        return dt == DerivativesType.CallOption || dt == DerivativesType.PutOption;
    }

    public static ProdType fromDerivativesValues(DerivativesType dt, DerivativesUnderlyingMarketType dumt, DerivativesProdClassType dpct, DerivativesUnderlyingType dut) {
        ProdType candidate = ProdType.DerivativesUnknown;
        for (ProdType pt : values()) {
            if (pt.prodClass == ProdClass.Derivatives && pt.dt == dt && pt.dumt == dumt && pt.dpct == dpct && pt.dut == dut) {
                return pt;
            } else if (pt.prodClass == ProdClass.Derivatives && pt.dt == dt && pt.dumt == dumt && pt.dpct == dpct && pt.dut == DerivativesUnderlyingType.UNKNOWN) {
                candidate = pt;
            }
        }
        return candidate;
    }

    public static ProdType fromEquityValues(EquitySecurityGroupType esgt, boolean KOSPIYN) {
        ProdType candidate = ProdType.EquityUnknown;
        for (ProdType pt : values()) {
            if (pt.prodClass == ProdClass.Equity && pt.esgt == esgt && pt.KOSPIYN == KOSPIYN) {
                return pt;
            } else if (pt.prodClass == ProdClass.Equity && pt.esgt == EquitySecurityGroupType.UNKNOWN) {
                candidate = pt;
            }
        }
        return candidate;
    }

    public static ProdType fromString(String pTypeStr) {
        if (pTypeStr == null)
            return Unknown;

        for (ProdType pType : values()) {
            if (pType.toString().equals(pTypeStr))
                return pType;
        }

        return Unknown;
    }
}