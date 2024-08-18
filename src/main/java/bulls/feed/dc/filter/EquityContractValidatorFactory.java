package bulls.feed.dc.filter;

import bulls.server.enums.MainTestSimul;
import bulls.staticData.TempConf;

public abstract class EquityContractValidatorFactory {

    public static EquityContractValidator create() {
        if (TempConf.MAIN_TEST_SIMUL == MainTestSimul.MAIN) {
            G2AccVolumeEquityContractValidator.Instance.touch();
            return G2AccVolumeEquityContractValidator.Instance;
        }
        return FreeEquityContractValidator.Instance;
    }
}
