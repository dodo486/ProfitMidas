package bulls.staticData.tick;

import bulls.staticData.UpDown;

public interface TickFunction {
    int getTickSize(UpDown upDown, int currPrice);

    int getTickStartPrice(UpDown upDown, int currPrice);

    int getTickEndPrice(UpDown upDown, int currPrice);

    /**
     * <h2>기준 가격에서 틱 수만큼 움직인 가격을 반환</h2>
     *
     * @param upDown    방향
     * @param currPrice 기준 가격
     * @param tickCount 틱 수
     * @return <p>Price : currPrice 기준 주어진 방향으로 주어진 틱 수만큼 움직였을 때의 가격을 반환</p>
     * <p>Quote : Price와 동일. TickFunction을 다르게 설정한 경우 결과가 다를 수 있음 (e.g. KQ150 Option)</p>
     * <p>Strike : currPrice 기준 주어진 방향으로 주어진 틱 수 다음의 행사가를 반환</p>
     */
    int getPriceByTick(UpDown upDown, int currPrice, int tickCount);

    /**
     * <h2>기준 가격에서 다음 틱 가격을 반환</h2>
     *
     * @param upDown    방향
     * @param currPrice 기준 가격
     * @return <p>Price : currPrice 기준 주어진 방향으로 다음 틱 가격을 반환</p>
     * <p>Quote : Price와 동일. TickFunction을 다르게 설정한 경우 결과가 다를 수 있음 (e.g. KQ150 Option)</p>
     * <p>Strike : currPrice 기준 주어진 방향으로 다음 행사가를 반환</p>
     */
    default int getNextPrice(UpDown upDown, int currPrice) {
        return getPriceByTick(upDown, currPrice, 1);
    }

    /**
     * <h2>주어진 두 가격 사이에 몇 틱이 존재하는지 반환</h2>
     *
     * @param toPrice   가격의 상한
     * @param fromPrice 가격의 하한
     * @return <p>Price : toPrice ~ fromPrice 사이의 틱 수</p>
     * <p>Quote : Price와 동일. TickFunction을 다르게 설정한 경우 결과가 다를 수 있음 (e.g. KQ150 Option)</p>
     * <p>Strike : toPrice ~ fromPrice 사이의 행사가 수</p>
     */
    int getTickCountBetween(int toPrice, int fromPrice);

    /**
     * <h2>주어진 가격과 가장 가까운 normalize 가격 반환</h2>
     *
     * @param price 가격
     * @return <p>Price : 주어진 가격과 가장 가까운 normalize 가격</p>
     * <p>Quote : Price와 동일. TickFunction을 다르게 설정한 경우 결과가 다를 수 있음 (e.g. KQ150 Option)</p>
     * <p>Strike : 주어진 가격과 가장 가까운 행사가</p>
     */
    int getNearestNormalizedPrice(int price);

    int getNormalizedPrice(UpDown upDown, int price);
}
