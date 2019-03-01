package galileltrum;

public class GalileltrumPeerData {

    private String host;
    private int tcpPort;
    private int sslPort;
    private long prunningLimit;

    public GalileltrumPeerData(String host, int tcpPort, int sslPort) {
        this.host = host;
        this.tcpPort = tcpPort;
        this.sslPort = sslPort;
    }

    public String getHost() {
        return host;
    }

    public int getTcpPort() {
        return tcpPort;
    }

    public int getSslPort() {
        return sslPort;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof GalileltrumPeerData)) return false;
        GalileltrumPeerData other = (GalileltrumPeerData) o;
        if (!this.host.equals(other.getHost())){
            return false;
        }
        if (this.tcpPort != other.tcpPort){
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "GalileltrumPeerData{" +
                "host='" + host + '\'' +
                ", tcpPort=" + tcpPort +
                ", sslPort=" + sslPort +
                ", prunningLimit=" + prunningLimit +
                '}';
    }
}
