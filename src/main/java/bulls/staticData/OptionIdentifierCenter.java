package bulls.staticData;

import bulls.bs.CallPut;
import bulls.designTemplate.EarlyInitialize;
import bulls.exception.CodeNotFoundException;
import bulls.exception.NoClosingPriceException;
import bulls.log.DefaultLogger;
import bulls.staticData.ProdType.DerivativesType;
import bulls.staticData.ProdType.DerivativesUnderlyingType;
import bulls.staticData.ProdType.ProdType;
import bulls.staticData.ProdType.ProdTypeCenter;
import bulls.staticData.tick.TickCalculatorCenter;
import org.apache.commons.math3.util.FastMath;

import java.util.*;
import java.util.stream.Collectors;

public enum OptionIdentifierCenter implements EarlyInitialize {
    Instance;


    final HashSet<OptionIdentifier> optionSet = new HashSet<>();
//    HashMap<OptionIdentifier, OptionIdentifier> parityMap = new HashMap<>();

    // key 가 종목코드가 아니라 OptionIdentifier 의 getKey()
    final HashMap<String, OptionIdentifier> keyToIdMap = new HashMap<>();
    // <종목 코드, OptionIdentifier>
    final HashMap<String, OptionIdentifier> codeToIdMap = new HashMap<>();
    final HashSet<String> soUnderlyingSet = new HashSet<>();
    //주식옵션 종목코드
    final HashSet<String> soCodeSet = new HashSet<>();
    //K200 지수옵션(미니포함) 종목코드
    final HashSet<String> k200OptionSet = new HashSet<>();
    final HashSet<String> miniOptionSet = new HashSet<>();
    final HashSet<String> weeklyOptionSet = new HashSet<>();
    final HashSet<String> kq150OptionSet = new HashSet<>();


    // <옵션코드, 언더라잉>
    final HashMap<String, String> optionCodeUnderlyingMap = new HashMap<>();

    // Dut,Date,strike
    final HashMap<DerivativesUnderlyingType, HashMap<Date, HashMap<Integer, OptionIdentifier>>> callDutMatDateStrikeMap = new HashMap<>();
    final HashMap<DerivativesUnderlyingType, HashMap<Date, HashMap<Integer, OptionIdentifier>>> putDutMatDateStrikeMap = new HashMap<>();

    final HashMap<DerivativesUnderlyingType, HashMap<Integer, HashMap<Integer, OptionIdentifier>>> callDutMatOrderStrikeMap = new HashMap<>();
    final HashMap<DerivativesUnderlyingType, HashMap<Integer, HashMap<Integer, OptionIdentifier>>> putDutMatOrderStrikeMap = new HashMap<>();

    OptionIdentifierCenter() {
        Collection<FuturesInfo> infos = FuturesInfoCenter.Instance.getAllFuturesInfo();
        for (FuturesInfo info : infos) {
            String isinCode = info.isinCode;
            ProdType pType = ProdTypeCenter.Instance.getProdType(isinCode);
            if (!pType.isOption())
                continue;
            String underlyingIsinCode = info.underlyingIsinCode;
            String maturityDate = info.matDate;
            Double strikePrice = info.strikePrice;
            Double multiplier = info.multiplier;
            String type = info.type;
            String productName = info.productName;

            // batch 종료 패킷
            if (underlyingIsinCode.equals("000000000000"))
                continue;

            // K200 만 특수처리
            if (underlyingIsinCode.equals(PredefinedIsinCode.KOSPI_200_ALT))
                underlyingIsinCode = PredefinedIsinCode.KOSPI_200;


            OptionIdentifier id = null;
            // 주식 옵션 초기화
            if (pType.isStockOpt()) {
//                if (FeedTRCode.A0025.getTrCodeStr().equals(type)) {
                int closingPrice;
                try {
                    closingPrice = ClosingPriceCenter.Instance.getClosingPrice(underlyingIsinCode);
                } catch (NoClosingPriceException e) {
                    DefaultLogger.logger.error("error found", e);
                    DefaultLogger.logger.error("{} {} {} {}", underlyingIsinCode, isinCode, type, maturityDate);
                    continue;
                }

                var strikeTickFunc = TickCalculatorCenter.Instance.getStrikeTickFunction(ProdType.KOSPIStockCallOptions);
                int atm = strikeTickFunc.getNearestNormalizedPrice(closingPrice);
                int strike = (int) (Math.floor(strikePrice)); //소수점으로 행사가 들어오는 종목 문제 임시 수정
                id = OptionIdentifier.createStockOption(underlyingIsinCode, strike, isinCode, atm, productName, multiplier, maturityDate, info.기초자산ID);
                optionSet.add(id);
                keyToIdMap.put(id.getKey(), id);
                optionCodeUnderlyingMap.put(isinCode, underlyingIsinCode);
                codeToIdMap.put(isinCode, id);

                soCodeSet.add(isinCode);
                soUnderlyingSet.add(underlyingIsinCode);
//            } else if (isIndexOptionType(type)) {
            } else if (pType.isK200Opt()) {
                int closingPrice;
                try {
                    closingPrice = ClosingPriceCenter.Instance.getClosingPrice(underlyingIsinCode);
                } catch (NoClosingPriceException e) {
                    DefaultLogger.logger.error("error found", e);
                    DefaultLogger.logger.error("{} {} {} {}", underlyingIsinCode, isinCode, type, maturityDate);
                    continue;
                }

                var strikeFunc = TickCalculatorCenter.Instance.getStrikeTickFunction(ProdType.K200CallOption);
                int atm = strikeFunc.getNearestNormalizedPrice(closingPrice);
                int strike = (int) FastMath.round(strikePrice * 100);
                id = OptionIdentifier.createK200Option(underlyingIsinCode, strike, isinCode, atm, productName, multiplier, maturityDate);
                optionSet.add(id);
                k200OptionSet.add(isinCode);
                keyToIdMap.put(id.getKey(), id);
                optionCodeUnderlyingMap.put(isinCode, underlyingIsinCode);
                codeToIdMap.put(isinCode, id);
            } else if (pType.isKQ150Opt()) {
                int closingPrice;
                try {
                    closingPrice = ClosingPriceCenter.Instance.getClosingPrice(underlyingIsinCode);
                } catch (NoClosingPriceException e) {
                    DefaultLogger.logger.error("error found", e);
                    DefaultLogger.logger.error("{} {} {} {}", underlyingIsinCode, isinCode, type, maturityDate);
                    continue;
                }

                var strikeFunc = TickCalculatorCenter.Instance.getStrikeTickFunction(ProdType.KQ150CallOption);
                int atm = strikeFunc.getNearestNormalizedPrice(closingPrice);
                int strike = (int) FastMath.round(strikePrice * 100);
                id = OptionIdentifier.createKosdaq150Option(underlyingIsinCode, strike, isinCode, atm, productName, multiplier, maturityDate);
                optionSet.add(id);
                kq150OptionSet.add(isinCode);
                keyToIdMap.put(id.getKey(), id);
                optionCodeUnderlyingMap.put(isinCode, underlyingIsinCode);
                codeToIdMap.put(isinCode, id);
            } else if (pType.isK200MiniOpt()) {
                int closingPrice;
                try {
                    closingPrice = ClosingPriceCenter.Instance.getClosingPrice(underlyingIsinCode);
                } catch (NoClosingPriceException e) {
                    DefaultLogger.logger.error("error found", e);
                    DefaultLogger.logger.error("{} {} {} {}", underlyingIsinCode, isinCode, type, maturityDate);
                    continue;
                }

                var strikeFunc = TickCalculatorCenter.Instance.getStrikeTickFunction(ProdType.K200MiniCallOption);
                int atm = strikeFunc.getNearestNormalizedPrice(closingPrice);
                int strike = (int) FastMath.round(strikePrice * 100);
                id = OptionIdentifier.createMiniOption(underlyingIsinCode, strike, isinCode, atm, productName, multiplier, maturityDate);
                optionSet.add(id);
                miniOptionSet.add(isinCode);
                keyToIdMap.put(id.getKey(), id);
                optionCodeUnderlyingMap.put(isinCode, underlyingIsinCode);
                codeToIdMap.put(isinCode, id);
            } else if (pType.isK200WeeklyOpt()) {
                int closingPrice;
                try {
                    closingPrice = ClosingPriceCenter.Instance.getClosingPrice(underlyingIsinCode);
                } catch (NoClosingPriceException e) {
                    DefaultLogger.logger.error("error found", e);
                    DefaultLogger.logger.error("{} {} {} {}", underlyingIsinCode, isinCode, type, maturityDate);
                    continue;
                }

                var strikeFunc = TickCalculatorCenter.Instance.getStrikeTickFunction(ProdType.K200CallOption);
                int atm = strikeFunc.getNearestNormalizedPrice(closingPrice);
                int strike = (int) FastMath.round(strikePrice * 100);
                id = OptionIdentifier.createWeeklyOption(underlyingIsinCode, strike, isinCode, atm, productName, multiplier, maturityDate);
                optionSet.add(id);
                weeklyOptionSet.add(isinCode);
                keyToIdMap.put(id.getKey(), id);
                optionCodeUnderlyingMap.put(isinCode, underlyingIsinCode);
                codeToIdMap.put(isinCode, id);
            } else {
                DefaultLogger.logger.error("Unknown type {}", isinCode);
            }

            //////////////////
            // Dut,MatDate,strike
            if (id == null)
                continue;
            HashMap<DerivativesUnderlyingType, HashMap<Date, HashMap<Integer, OptionIdentifier>>> dutMatDateStrikeMap;
            if (DerivativesType.getTypeFromCode(info.isinCode) == DerivativesType.CallOption) {
                dutMatDateStrikeMap = callDutMatDateStrikeMap;
            } else if (DerivativesType.getTypeFromCode(info.isinCode) == DerivativesType.PutOption) {
                dutMatDateStrikeMap = putDutMatDateStrikeMap;
            } else {
                continue;
            }
            int strikePriceInt = (int) FastMath.floor(info.priceDivider * info.strikePrice);
            dutMatDateStrikeMap.computeIfAbsent(info.기초자산ID, (k1) -> new HashMap<>())
                    .computeIfAbsent(info.만기, (k2) -> new HashMap<>())
                    .putIfAbsent(strikePriceInt, id);

            HashMap<DerivativesUnderlyingType, HashMap<Integer, HashMap<Integer, OptionIdentifier>>> dutMatOrderStrikeMap;
            if (DerivativesType.getTypeFromCode(info.isinCode) == DerivativesType.CallOption) {
                dutMatOrderStrikeMap = callDutMatOrderStrikeMap;
            } else if (DerivativesType.getTypeFromCode(info.isinCode) == DerivativesType.PutOption) {
                dutMatOrderStrikeMap = putDutMatOrderStrikeMap;
            } else {
                continue;
            }
//            Integer matOrder = DerivativesMaturityCenter.Instance.getOptionsMatOrderByMatDate(info.기초자산ID, info.만기);
            long expiryId = ExpiryCenter.Instance.getExpiryId(info.isinCode);
            Integer matOrder = DerivativesMaturityCenter.Instance.getOptionsMatOrderByExpiryId(info.기초자산ID, expiryId);
            if (matOrder == null) {
                DefaultLogger.logger.error("MatOrder not exists for {},{}", info.기초자산ID, info.만기);
                continue;
            }
            dutMatOrderStrikeMap.computeIfAbsent(info.기초자산ID, (k1) -> new HashMap<>())
                    .computeIfAbsent(matOrder, (k2) -> new HashMap<>())
                    .putIfAbsent(strikePriceInt, id);
            //////////////////
        }
        fillAdditionalData();
    }

    private void fillAdditionalData() {
        for (OptionIdentifier id : optionSet) {
            id.parityOptId = getOptions(id.callPut.opposite(), id.uType, id.만기date, id.strikePrice);
            if (id.prodType.isK200Opt()) {
                id.representOptId = id;
                id.hasMini = true;
                id.isMini = false;
                id.miniOptId = getOptions(id.callPut, DerivativesUnderlyingType.MKI, id.만기date, id.strikePrice);
            } else if (id.prodType.isK200MiniOpt()) {
                id.representOptId = getOptions(id.callPut, DerivativesUnderlyingType.K2I, id.만기date, id.strikePrice);
                id.hasMini = true;
                id.isMini = true;
                id.miniOptId = id;
            } else {
                id.representOptId = id;
                id.hasMini = false;
                id.isMini = false;
                id.miniOptId = null;
            }
//            DefaultLogger.logger.info("{} -> {} {} {} {} {}", id, id.parityOptId, id.representOptId, id.hasMini, id.isMini, id.miniOptId);
        }
    }

    public Set<OptionIdentifier> getAllOptionOnDutyOf(DerivativesUnderlyingType dut, long expiryId) {
        return optionSet.stream()
                .filter(id -> id.uType == dut)
                .filter(id -> id.isOnDutyToday)
                .filter(id -> id.expiryId == expiryId)
                .collect(Collectors.toSet());
    }

    public Set<OptionIdentifier> getAllOptionOf(DerivativesUnderlyingType dut, long expiryId) {
        return optionSet.stream()
                .filter(id -> id.uType == dut)
                .filter(id -> id.expiryId == expiryId)
                .collect(Collectors.toSet());
    }

    public boolean isSOUnderlying(String stockIsinCode) {
        return soUnderlyingSet.contains(stockIsinCode);
    }

    public Set<String> getAllSOUnderlying() {
        return soUnderlyingSet;
    }

    /**
     * 콜/풋, DerivativesUnderlyingType, 만기일, 행사가에 부합하는 OptionIdentifier를 찾아서 리턴한다.
     *
     * @param callPut
     * @param dut
     * @param matDate
     * @param strike
     * @return
     */
    public OptionIdentifier getOptions(CallPut callPut, DerivativesUnderlyingType dut, Date matDate, int strike) {
        HashMap<DerivativesUnderlyingType, HashMap<Date, HashMap<Integer, OptionIdentifier>>> dutMatStrikeMap;
        if (callPut == CallPut.CALL) {
            dutMatStrikeMap = callDutMatDateStrikeMap;
        } else if (callPut == CallPut.PUT) {
            dutMatStrikeMap = putDutMatDateStrikeMap;
        } else {
            return null;
        }
        HashMap<Date, HashMap<Integer, OptionIdentifier>> map1 = dutMatStrikeMap.get(dut);
        if (map1 == null)
            return null;
        HashMap<Integer, OptionIdentifier> map2 = map1.get(matDate);
        if (map2 == null)
            return null;
        return map2.get(strike);
    }

    /**
     * 콜/풋, DerivativesUnderlyingType, 만기순번, 행사가에 부합하는 OptionIdentifier를 찾아서 리턴한다.
     *
     * @param callPut
     * @param dut
     * @param matOrder
     * @param strike
     * @return
     */
    public OptionIdentifier getOptions(CallPut callPut, DerivativesUnderlyingType dut, int matOrder, int strike) {
        HashMap<DerivativesUnderlyingType, HashMap<Integer, HashMap<Integer, OptionIdentifier>>> dutMatOrderStrikeMap;
        if (callPut == CallPut.CALL) {
            dutMatOrderStrikeMap = callDutMatOrderStrikeMap;
        } else if (callPut == CallPut.PUT) {
            dutMatOrderStrikeMap = putDutMatOrderStrikeMap;
        } else {
            return null;
        }
        HashMap<Integer, HashMap<Integer, OptionIdentifier>> map1 = dutMatOrderStrikeMap.get(dut);
        if (map1 == null)
            return null;
        HashMap<Integer, OptionIdentifier> map2 = map1.get(matOrder);
        if (map2 == null)
            return null;
        return map2.get(strike);
    }

    /**
     * 기초자산ID와 월물번호로 옵션의 FuturesInfo를 조회
     *
     * @param dut     기초자산ID
     * @param matDate 0부터 시작하는 월물번호(근월물=0). DB에 입력되어 있는 종목 정보 기준임. 월물이 1,2,3,6,9월 존재하고 현재 근월물이 1월이라면 maturityNumber 4는 5월이 아니라 9월을 의미.
     * @param callPut 콜/풋
     * @return 해당되는 FuturesInfo 리스트
     */
    public List<OptionIdentifier> getOptionsList(CallPut callPut, DerivativesUnderlyingType dut, Date matDate) {
        ArrayList<OptionIdentifier> list = new ArrayList<>();
        HashMap<DerivativesUnderlyingType, HashMap<Date, HashMap<Integer, OptionIdentifier>>> dutMatStrikeMap;
        if (callPut == CallPut.CALL) {
            dutMatStrikeMap = callDutMatDateStrikeMap;
        } else if (callPut == CallPut.PUT) {
            dutMatStrikeMap = putDutMatDateStrikeMap;
        } else {
            return list;
        }
        HashMap<Date, HashMap<Integer, OptionIdentifier>> map1 = dutMatStrikeMap.get(dut);
        if (map1 == null)
            return list;
        HashMap<Integer, OptionIdentifier> map2 = map1.get(matDate);
        if (map2 == null)
            return list;
        list.addAll(map2.values());
        Collections.sort(list, Comparator.comparingInt(OptionIdentifier::getStrikePrice));
        return list;
    }

    /**
     * 기초자산ID와 월물번호로 옵션의 FuturesInfo를 조회
     *
     * @param dut      기초자산ID
     * @param matOrder 0부터 시작하는 월물번호(근월물=0). DB에 입력되어 있는 종목 정보 기준임. 월물이 1,2,3,6,9월 존재하고 현재 근월물이 1월이라면 maturityNumber 4는 5월이 아니라 9월을 의미.
     * @param callPut  콜/풋
     * @return 해당되는 FuturesInfo 리스트
     */
    public List<OptionIdentifier> getOptionsList(CallPut callPut, DerivativesUnderlyingType dut, int matOrder) {
        ArrayList<OptionIdentifier> list = new ArrayList<>();
        HashMap<DerivativesUnderlyingType, HashMap<Integer, HashMap<Integer, OptionIdentifier>>> dutMatOrderStrikeMap;
        if (callPut == CallPut.CALL) {
            dutMatOrderStrikeMap = callDutMatOrderStrikeMap;
        } else if (callPut == CallPut.PUT) {
            dutMatOrderStrikeMap = putDutMatOrderStrikeMap;
        } else {
            return list;
        }
        HashMap<Integer, HashMap<Integer, OptionIdentifier>> map1 = dutMatOrderStrikeMap.get(dut);
        if (map1 == null)
            return list;
        HashMap<Integer, OptionIdentifier> map2 = map1.get(matOrder);
        if (map2 == null)
            return list;
        list.addAll(map2.values());
        list.sort(Comparator.comparingInt(OptionIdentifier::getStrikePrice));
        return list;
    }

    /**
     * 기초자산ID와 월물번호로 옵션의 FuturesInfo를 조회
     *
     * @param dut           기초자산ID
     * @param matOrder      0부터 시작하는 월물번호(근월물=0). DB에 입력되어 있는 종목 정보 기준임. 월물이 1,2,3,6,9월 존재하고 현재 근월물이 1월이라면 maturityNumber 4는 5월이 아니라 9월을 의미.
     * @param callPut       콜/풋
     * @param moneynessFrom 조회 대상 행사가 시작 moneyness(콜 풋 관계 없이 양수의 경우 ATM으로부터 증가하는 방향 행사가, 음수의 경우 감소하는 방향 행사가, 코스피200 ATM이 280일 때 +1은 282.5 -2는 275 의미.
     *                      행사가 단위를 이용해서 계산하는게 아닌 DB에 입력되어 있는 종목 정보의 순서 기준임. 원월물의 행사가가 260 265 270 276 존재하고 ATM이 265일 때 -1은 262.5가 아닌 260임)
     * @param moneynessTo   조회 대상 행사가 시작 moneyness(콜 풋 관계 없이 양수의 경우 ATM으로부터 증가하는 방향 행사가, 음수의 경우 감소하는 방향 행사가, 코스피200 ATM이 280일 때 +1은 282.5 -2는 275 의미.
     *                      *                      행사가 단위를 이용해서 계산하는게 아닌 DB에 입력되어 있는 종목 정보의 순서 기준임. 원월물의 행사가가 260 265 270 276 존재하고 ATM이 265일 때 -1은 262.5가 아닌 260임)
     * @return 해당되는 FuturesInfo 리스트
     */
    public List<OptionIdentifier> getOptionsList(CallPut callPut, DerivativesUnderlyingType dut, int matOrder, int moneynessFrom, int moneynessTo) {
        ArrayList<OptionIdentifier> resultList = new ArrayList<>();
        HashMap<DerivativesUnderlyingType, HashMap<Integer, HashMap<Integer, OptionIdentifier>>> dutMatOrderStrikeMap;
        if (callPut == CallPut.CALL) {
            dutMatOrderStrikeMap = callDutMatOrderStrikeMap;
        } else if (callPut == CallPut.PUT) {
            dutMatOrderStrikeMap = putDutMatOrderStrikeMap;
        } else {
            return resultList;
        }
        HashMap<Integer, HashMap<Integer, OptionIdentifier>> map1 = dutMatOrderStrikeMap.get(dut);
        if (map1 == null)
            return resultList;
        HashMap<Integer, OptionIdentifier> map2 = map1.get(matOrder);
        if (map2 == null)
            return resultList;
        ArrayList<OptionIdentifier> list = new ArrayList<>(map2.values());
        list.sort(Comparator.comparingInt(OptionIdentifier::getStrikePrice));

        int atmIdx = -1;
        for (int i = 0; i < list.size(); ++i) {
            FuturesInfo info = list.get(i).getFuturesInfo();
            if (info == null)
                continue;
            if (info.ATM구분 == 1) {
                atmIdx = i;
                break;
            }
        }
        if (atmIdx >= 0) {
            for (int i = Math.max(0, atmIdx + moneynessFrom); i <= Math.min(list.size() - 1, atmIdx + moneynessTo); ++i) {
                resultList.add(list.get(i));
            }
        }
        return resultList;
    }

    /**
     * 기초자산ID와 월물번호로 옵션의 FuturesInfo를 조회
     *
     * @param dut           기초자산ID
     * @param matDate       만기일
     * @param callPut       콜/풋
     * @param moneynessFrom 조회 대상 행사가 시작 moneyness(콜 풋 관계 없이 양수의 경우 ATM으로부터 증가하는 방향 행사가, 음수의 경우 감소하는 방향 행사가, 코스피200 ATM이 280일 때 +1은 282.5 -2는 275 의미.
     *                      행사가 단위를 이용해서 계산하는게 아닌 DB에 입력되어 있는 종목 정보의 순서 기준임. 원월물의 행사가가 260 265 270 276 존재하고 ATM이 265일 때 -1은 262.5가 아닌 260임)
     * @param moneynessTo   조회 대상 행사가 시작 moneyness(콜 풋 관계 없이 양수의 경우 ATM으로부터 증가하는 방향 행사가, 음수의 경우 감소하는 방향 행사가, 코스피200 ATM이 280일 때 +1은 282.5 -2는 275 의미.
     *                      *                      행사가 단위를 이용해서 계산하는게 아닌 DB에 입력되어 있는 종목 정보의 순서 기준임. 원월물의 행사가가 260 265 270 276 존재하고 ATM이 265일 때 -1은 262.5가 아닌 260임)
     * @return 해당되는 FuturesInfo 리스트
     */
    public List<OptionIdentifier> getOptionsList(CallPut callPut, DerivativesUnderlyingType dut, Date matDate, int moneynessFrom, int moneynessTo) {
        ArrayList<OptionIdentifier> list = new ArrayList<>();
        HashMap<DerivativesUnderlyingType, HashMap<Date, HashMap<Integer, OptionIdentifier>>> dutMatDateStrikeMap;
        if (callPut == CallPut.CALL) {
            dutMatDateStrikeMap = callDutMatDateStrikeMap;
        } else if (callPut == CallPut.PUT) {
            dutMatDateStrikeMap = putDutMatDateStrikeMap;
        } else {
            return list;
        }
        HashMap<Date, HashMap<Integer, OptionIdentifier>> map1 = dutMatDateStrikeMap.get(dut);
        if (map1 == null)
            return list;
        HashMap<Integer, OptionIdentifier> map2 = map1.get(matDate);
        if (map2 == null)
            return list;
        list.addAll(map2.values());
        list.sort(Comparator.comparingInt(OptionIdentifier::getStrikePrice));

        int atmIdx = -1;
        for (int i = 0; i < list.size(); ++i) {
            FuturesInfo info = list.get(i).getFuturesInfo();
            if (info == null)
                continue;
            if (info.ATM구분 == 1) {
                atmIdx = i;
                break;
            }
        }
        ArrayList<OptionIdentifier> resultList = new ArrayList<>();
        if (atmIdx >= 0) {
            for (int i = Math.max(0, atmIdx + moneynessFrom); i <= Math.min(list.size() - 1, atmIdx + moneynessTo); ++i) {
                resultList.add(list.get(i));
            }
        }
        return resultList;
    }

    public OptionIdentifier getOptionCode(String underlyingIsinCode, String expiryString, int strike, CallPut callPut, DerivativesUnderlyingType duType) throws CodeNotFoundException {
        String key = OptionIdentifier.getKey(underlyingIsinCode, expiryString, strike, callPut, duType);
        OptionIdentifier id = keyToIdMap.get(key);
        if (id == null) {
            throw new CodeNotFoundException(key + " 에 해당하는 옵션코드가 없습니다.");
        }
        return id;
    }


    public boolean isSO(String code) {
        return soCodeSet.contains(code);
    }

    public boolean isIndexOption(String code) {
        return (k200OptionSet.contains(code) || kq150OptionSet.contains(code) || miniOptionSet.contains(code) || weeklyOptionSet.contains(code));
    }

    public boolean isApptUnderlying(String optionCode, String stockCode) {
        String underLyingCode = optionCodeUnderlyingMap.get(optionCode);

        if (underLyingCode == null)
            return false;

        return underLyingCode.equals(stockCode);
    }

    public OptionIdentifier getOptionId(String optionCode) {
        return codeToIdMap.get(optionCode);
    }


    public Set<Map.Entry<String, OptionIdentifier>> getAllOptionOf() {
        return codeToIdMap.entrySet();
    }
}
