package galilel.org.galilelwallet.module;

import android.os.Environment;
import android.text.format.DateUtils;

import org.galilelj.core.Context;
import org.galilelj.core.NetworkParameters;
import org.galilelj.params.MainNetParams;
import org.galilelj.params.TestNet3Params;

import java.io.File;

public class GalilelContext {

    public static final boolean IS_TEST = false;
    public static final NetworkParameters NETWORK_PARAMETERS = IS_TEST? TestNet3Params.get():MainNetParams.get();

    // galilelj global context.
    public static final Context CONTEXT = new Context(NETWORK_PARAMETERS);

    public static final String DEFAULT_RATE_COIN = "USD";

    // wallet update time of local currency rates (86400000 = every 24 hours).
    public static final long RATE_UPDATE_TIME = 86400000;

    public static final String ENABLE_BIP44_APP_VERSION = "1.03";

    // wallet released time.
    public static final long GALI_WALLET_APP_RELEASED_ON_PLAY_STORE_TIME = 1566381600;

    // Currency exchange rate.
    public static final String URL_FIAT_CURRENCIES_RATE = "https://bitpay.com/rates";

    // Report e-mail.
    public static final String REPORT_EMAIL = "maik.broemme@galilel.org";

    // Subject line for manually reported issues.
    public static final String REPORT_SUBJECT_ISSUE = "Reported issue";

    // Donation address.
    public static final String DONATE_ADDRESS = "UUr5nDmykhun1HWM7mJAqLVeLzoGtx19dX";

    // Filenames.
    public static final class Files {

        // Filename of network to use.
        private static final String FILENAME_NETWORK_SUFFIX = NETWORK_PARAMETERS.getId();

        // Filename of wordlist to use.
        public static final String BIP39_WORDLIST_FILENAME = "bip39-wordlist.txt";

        // Filename of the block store for storing the chain.
        public static final String BLOCKCHAIN_FILENAME = "blockchain" + FILENAME_NETWORK_SUFFIX;

        // Filename of the wallet.
        public static final String WALLET_FILENAME_PROTOBUF = "wallet-protobuf" + FILENAME_NETWORK_SUFFIX;

        // How often the wallet is autosaved.
        public static final long WALLET_AUTOSAVE_DELAY_MS = 5 * DateUtils.SECOND_IN_MILLIS;

        // Filename of the automatic wallet backup.
        public static final String WALLET_KEY_BACKUP_PROTOBUF = "key-backup-protobuf" + FILENAME_NETWORK_SUFFIX;

        // Path to external storage.
        public static final File EXTERNAL_STORAGE_DIR = Environment.getExternalStorageDirectory();

        // Manual backups go here.
        public static final File EXTERNAL_WALLET_BACKUP_DIR = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        // Filename of the manual wallet backup.
        public static final String getExternalWalletBackupFileName(String appName){
            return appName + "-" + FILENAME_NETWORK_SUFFIX;
        }

        // Checkpoint filename.
        public static final String CHECKPOINTS_FILENAME = "checkpoints";
    }

    // Minimum memory.
    public static final int MEMORY_CLASS_LOWEND = 48;

    // Peer timeouts.
    public static final int PEER_DISCOVERY_TIMEOUT_MS = 10 * (int) DateUtils.SECOND_IN_MILLIS;
    public static final int PEER_TIMEOUT_MS = 15 * (int) DateUtils.SECOND_IN_MILLIS;

    // Maximum size of backups. Files larger will be rejected.
    public static final long BACKUP_MAX_CHARS = 10000000;

    // MAX TIME WAITED TO SAY THAT THE APP IS NOT SYNCHED ANYMORE... in millis.
    public static final long OUT_OF_SYNC_TIME = 60000; // 1 minute
}
