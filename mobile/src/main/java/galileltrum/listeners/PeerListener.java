package galileltrum.listeners;

import galileltrum.GalileltrumPeer;

public interface PeerListener {

    void onConnected(GalileltrumPeer galileltrumPeer);

    void onDisconnected(GalileltrumPeer galileltrumPeer);

    void onExceptionCaught(GalileltrumPeer galileltrumPeer, Exception e);
}
