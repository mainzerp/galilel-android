package galileltrum.listeners;

import java.util.List;

import galileltrum.GalileltrumPeer;
import galileltrum.messages.responses.StatusHistory;
import galileltrum.messages.responses.Unspent;
import galileltrum.utility.TxHashHeightWrapper;

public interface PeerDataListener {

    void onSubscribedAddressChange(GalileltrumPeer galileltrumPeer, String address, String status);

    void onListUnpent(GalileltrumPeer galileltrumPeer,String address, List<Unspent> unspent);

    void onBalanceReceive(GalileltrumPeer galileltrumPeer, String address, long confirmed, long unconfirmed);

    void onGetHistory(GalileltrumPeer galileltrumPeer, StatusHistory statusHistory);
}
