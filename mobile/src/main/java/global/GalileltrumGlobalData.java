package global;

import java.util.ArrayList;
import java.util.List;

import galileltrum.GalileltrumPeerData;

public class GalileltrumGlobalData {

    public static final String[] TRUSTED_NODES = new String[] {
        // None for now, we don't use trusted nodes anymore, too complicated
        // for users and >95% of users never touch it. -> useless setting.
    };

    public static final List<GalileltrumPeerData> listTrustedHosts(int paramsPort) {
        List<GalileltrumPeerData> list = new ArrayList<>();
        for (String trustedNode : TRUSTED_NODES) {
            list.add(new GalileltrumPeerData(trustedNode,paramsPort,55552));
        }
        return list;
    }
}
