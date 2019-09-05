package galilel.org.galilelwallet.module;

import android.content.SharedPreferences;

import org.galilelj.core.Context;
import org.galilelj.core.NetworkParameters;

import global.WalletConfiguration;
import galilel.org.galilelwallet.utils.Configurations;

import static galilel.org.galilelwallet.module.GalilelContext.CONTEXT;
import static galilel.org.galilelwallet.module.GalilelContext.Files.BLOCKCHAIN_FILENAME;
import static galilel.org.galilelwallet.module.GalilelContext.Files.CHECKPOINTS_FILENAME;
import static galilel.org.galilelwallet.module.GalilelContext.Files.WALLET_FILENAME_PROTOBUF;
import static galilel.org.galilelwallet.module.GalilelContext.Files.WALLET_KEY_BACKUP_PROTOBUF;
import static galilel.org.galilelwallet.module.GalilelContext.NETWORK_PARAMETERS;
import static galilel.org.galilelwallet.module.GalilelContext.PEER_DISCOVERY_TIMEOUT_MS;
import static galilel.org.galilelwallet.module.GalilelContext.PEER_TIMEOUT_MS;

public class WalletConfImp extends Configurations implements WalletConfiguration {

    private static final String PREF_TRUSTED_NODE = "trusted_node";
    private static final String PREF_TRUSTED_NODE_PORT = "trusted_node_port";
    private static final String PREFS_KEY_SCHEDULE_BLOCKCHAIN_SERVICE = "sch_block_serv";
    private static final String PREF_CURRENCY_RATE = "currency_code";


    public WalletConfImp(SharedPreferences prefs) {
        super(prefs);
    }

    @Override
    public String getTrustedNodeHost() {
        return getString(PREF_TRUSTED_NODE,null);
    }

    @Override
    public void saveTrustedNode(String host, int port) {
        save(PREF_TRUSTED_NODE,host);
        save(PREF_TRUSTED_NODE_PORT,port);
    }

    @Override
    public void saveScheduleBlockchainService(long time){
        save(PREFS_KEY_SCHEDULE_BLOCKCHAIN_SERVICE,time);
    }

    @Override
    public long getScheduledBLockchainService(){
        return getLong(PREFS_KEY_SCHEDULE_BLOCKCHAIN_SERVICE,0);
    }

    @Override
    public int getTrustedNodePort() {
        return getInt(PREF_TRUSTED_NODE_PORT,GalilelContext.NETWORK_PARAMETERS.getPort());
    }

    @Override
    public String getMnemonicFilename() {
        return GalilelContext.Files.BIP39_WORDLIST_FILENAME;
    }

    @Override
    public String getWalletProtobufFilename() {
        return WALLET_FILENAME_PROTOBUF;
    }

    @Override
    public NetworkParameters getNetworkParams() {
        return GalilelContext.NETWORK_PARAMETERS;
    }

    @Override
    public String getKeyBackupProtobuf() {
        return WALLET_KEY_BACKUP_PROTOBUF;
    }

    @Override
    public long getWalletAutosaveDelayMs() {
        return GalilelContext.Files.WALLET_AUTOSAVE_DELAY_MS;
    }

    @Override
    public Context getWalletContext() {
        return CONTEXT;
    }

    @Override
    public String getBlockchainFilename() {
        return BLOCKCHAIN_FILENAME;
    }

    @Override
    public String getCheckpointFilename() {
        return CHECKPOINTS_FILENAME;
    }

    @Override
    public int getPeerTimeoutMs() {
        return PEER_TIMEOUT_MS;
    }

    @Override
    public long getPeerDiscoveryTimeoutMs() {
        return PEER_DISCOVERY_TIMEOUT_MS;
    }

    @Override
    public int getMinMemoryNeeded() {
        return GalilelContext.MEMORY_CLASS_LOWEND;
    }

    @Override
    public long getBackupMaxChars() {
        return GalilelContext.BACKUP_MAX_CHARS;
    }

    @Override
    public boolean isTest() {
        return GalilelContext.IS_TEST;
    }

    @Override
    public int getProtocolVersion() {
        return NETWORK_PARAMETERS.getProtocolVersionNum(NetworkParameters.ProtocolVersion.CURRENT);
    }

}
