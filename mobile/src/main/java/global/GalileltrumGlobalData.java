package global;

import java.util.ArrayList;
import java.util.List;

import galileltrum.GalileltrumPeerData;

public class GalileltrumGlobalData {

    public static final String[] TRUSTED_NODES = new String[] {
        "seed1.galilel.cloud",
        "seed2.galilel.cloud",
        "seed3.galilel.cloud",
        "seed4.galilel.cloud",
        "seed5.galilel.cloud",
        "seed6.galilel.cloud",
        "seed7.galilel.cloud",
        "seed8.galilel.cloud"
    };

    public static final List<GalileltrumPeerData> listTrustedHosts() {
        List<GalileltrumPeerData> list = new ArrayList<>();
        for (String trustedNode : TRUSTED_NODES) {
            list.add(new GalileltrumPeerData(trustedNode,36001,55552));
        }
        return list;
    }
}
