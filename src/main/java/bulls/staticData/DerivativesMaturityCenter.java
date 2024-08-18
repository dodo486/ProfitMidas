package bulls.staticData;

import bulls.designTemplate.EarlyInitialize;
import bulls.staticData.ProdType.DerivativesType;
import bulls.staticData.ProdType.DerivativesUnderlyingType;

import java.util.Date;
import java.util.HashMap;
import java.util.TreeSet;

public enum DerivativesMaturityCenter implements EarlyInitialize {
    Instance;

    final HashMap<DerivativesUnderlyingType, HashMap<Integer, Date>> futOrderToDateMap = new HashMap<>();
    final HashMap<DerivativesUnderlyingType, HashMap<Integer, Date>> optOrderToDateMap = new HashMap<>();
    final HashMap<DerivativesUnderlyingType, HashMap<Date, Integer>> futDateToOrderMap = new HashMap<>();
    final HashMap<DerivativesUnderlyingType, HashMap<Date, Integer>> optDateToOrderMap = new HashMap<>();
    final HashMap<DerivativesUnderlyingType, HashMap<Integer, Long>> optOrderToExpiryIdMap = new HashMap<>();
    final HashMap<DerivativesUnderlyingType, HashMap<Long, Integer>> optExpiryIdToOrderMap = new HashMap<>();

    DerivativesMaturityCenter() {
        HashMap<DerivativesUnderlyingType, TreeSet<Date>> futMaturitySetMap = new HashMap<>();
        HashMap<DerivativesUnderlyingType, TreeSet<Date>> optMaturitySetMap = new HashMap<>();
        HashMap<DerivativesUnderlyingType, TreeSet<Long>> optExpiryIdSetMap = new HashMap<>();

        for (FuturesInfo info : FuturesInfoCenter.Instance.getAllFuturesInfo()) {
            DerivativesType dt = DerivativesType.getTypeFromCode(info.isinCode);
            if (dt == DerivativesType.Futures || dt == DerivativesType.Spread) {
                TreeSet<Date> tmpMatSet = futMaturitySetMap.computeIfAbsent(info.기초자산ID, (k) -> new TreeSet<>());
                tmpMatSet.add(info.만기);
            } else if (dt == DerivativesType.CallOption || dt == DerivativesType.PutOption) {
                TreeSet<Date> tmpMatSet = optMaturitySetMap.computeIfAbsent(info.기초자산ID, (k) -> new TreeSet<>());
                tmpMatSet.add(info.만기);

                TreeSet<Long> tmpExpiryIdSet = optExpiryIdSetMap.computeIfAbsent(info.기초자산ID, (k) -> new TreeSet<>());
                long expiryId = ExpiryCenter.Instance.getExpiryId(info.isinCode);
                if (expiryId < 0)
                    continue;

                tmpExpiryIdSet.add(expiryId);
            }
        }
        futMaturitySetMap.forEach((dut, set) -> {
            HashMap<Integer, Date> map1 = futOrderToDateMap.computeIfAbsent(dut, (k) -> new HashMap<>());
            HashMap<Date, Integer> map2 = futDateToOrderMap.computeIfAbsent(dut, (k) -> new HashMap<>());
            int i = 0;
            for (Date date : set) {
                map1.put(i, date);
                map2.put(date, i);
                i++;
            }
        });
        optMaturitySetMap.forEach((dut, set) -> {
            HashMap<Integer, Date> map1 = optOrderToDateMap.computeIfAbsent(dut, (k) -> new HashMap<>());
            HashMap<Date, Integer> map2 = optDateToOrderMap.computeIfAbsent(dut, (k) -> new HashMap<>());
            int i = 0;
            for (Date date : set) {
                map1.put(i, date);
                map2.put(date, i);
                i++;
            }
        });
        optExpiryIdSetMap.forEach((dut, set) -> {
            HashMap<Integer, Long> map1 = optOrderToExpiryIdMap.computeIfAbsent(dut, (k) -> new HashMap<>());
            HashMap<Long, Integer> map2 = optExpiryIdToOrderMap.computeIfAbsent(dut, (k) -> new HashMap<>());
            int i = 0;
            for (long expiryId : set) {
                map1.put(i, expiryId);
                map2.put(expiryId, i);
                i++;
            }
        });
    }


    public Date getFuturesMatDateByMatOrder(DerivativesUnderlyingType dut, int maturityOrder) {
        HashMap<Integer, Date> map = futOrderToDateMap.get(dut);
        if (map == null)
            return null;
        return map.get(maturityOrder);
    }

    public Integer getFuturesMatOrderByMatDate(DerivativesUnderlyingType dut, Date date) {
        HashMap<Date, Integer> map = futDateToOrderMap.get(dut);
        if (map == null)
            return null;
        return map.get(date);
    }

    public Date getOptionsMatDateByMatOrder(DerivativesUnderlyingType dut, int maturityOrder) {
        HashMap<Integer, Date> map = optOrderToDateMap.get(dut);
        if (map == null)
            return null;
        return map.get(maturityOrder);
    }

    public Integer getOptionsMatOrderByMatDate(DerivativesUnderlyingType dut, Date date) {
        HashMap<Date, Integer> map = optDateToOrderMap.get(dut);
        if (map == null)
            return null;
        return map.get(date);
    }

    public Long getOptionsExpiryIdByMatOrder(DerivativesUnderlyingType dut, int maturityOrder) {
        HashMap<Integer, Long> map = optOrderToExpiryIdMap.get(dut);
        if (map == null)
            return null;
        return map.get(maturityOrder);
    }

    public Integer getOptionsMatOrderByExpiryId(DerivativesUnderlyingType dut, Long expiryId) {
        HashMap<Long, Integer> map = optExpiryIdToOrderMap.get(dut);
        if (map == null)
            return null;
        return map.get(expiryId);
    }
}
