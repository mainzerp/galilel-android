package galilel.org.galilelwallet.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;

import org.galilelj.core.Block;
import org.galilelj.core.Coin;
import org.galilelj.core.FilteredBlock;
import org.galilelj.core.Peer;
import org.galilelj.core.Transaction;
import org.galilelj.core.TransactionConfidence;
import org.galilelj.core.listeners.AbstractPeerDataEventListener;
import org.galilelj.core.listeners.PeerConnectedEventListener;
import org.galilelj.core.listeners.PeerDataEventListener;
import org.galilelj.core.listeners.PeerDisconnectedEventListener;
import org.galilelj.core.listeners.TransactionConfidenceEventListener;
import org.galilelj.wallet.Wallet;
import org.galilelj.wallet.listeners.WalletCoinsReceivedEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import chain.BlockchainManager;
import chain.BlockchainState;
import chain.Impediment;
import galileltrum.listeners.AddressListener;
import galilel.org.galilelwallet.GalilelApplication;
import galilel.org.galilelwallet.R;
import galilel.org.galilelwallet.module.GalilelContext;
import global.GalilelModuleImp;
import galilel.org.galilelwallet.module.store.SnappyBlockchainStore;
import galilel.org.galilelwallet.rate.CoinMarketCapApiClient;
import galilel.org.galilelwallet.rate.RequestGalilelRateException;
import global.GalilelRate;
import galilel.org.galilelwallet.ui.wallet_activity.WalletActivity;
import galilel.org.galilelwallet.utils.AppConf;
import galilel.org.galilelwallet.utils.CrashReporter;

import static galilel.org.galilelwallet.module.GalilelContext.CONTEXT;
import static galilel.org.galilelwallet.module.GalilelContext.DEFAULT_RATE_COIN;
import static galilel.org.galilelwallet.service.IntentsConstants.ACTION_ADDRESS_BALANCE_CHANGE;
import static galilel.org.galilelwallet.service.IntentsConstants.ACTION_BROADCAST_TRANSACTION;
import static galilel.org.galilelwallet.service.IntentsConstants.ACTION_CANCEL_COINS_RECEIVED;
import static galilel.org.galilelwallet.service.IntentsConstants.ACTION_NOTIFICATION;
import static galilel.org.galilelwallet.service.IntentsConstants.ACTION_RESET_BLOCKCHAIN;
import static galilel.org.galilelwallet.service.IntentsConstants.ACTION_SCHEDULE_SERVICE;
import static galilel.org.galilelwallet.service.IntentsConstants.DATA_TRANSACTION_HASH;
import static galilel.org.galilelwallet.service.IntentsConstants.INTENT_BROADCAST_DATA_BLOCKCHAIN_STATE;
import static galilel.org.galilelwallet.service.IntentsConstants.INTENT_BROADCAST_DATA_ON_COIN_RECEIVED;
import static galilel.org.galilelwallet.service.IntentsConstants.INTENT_BROADCAST_DATA_PEER_CONNECTED;
import static galilel.org.galilelwallet.service.IntentsConstants.INTENT_BROADCAST_DATA_TYPE;
import static galilel.org.galilelwallet.service.IntentsConstants.INTENT_EXTRA_BLOCKCHAIN_STATE;
import static galilel.org.galilelwallet.service.IntentsConstants.NOT_BLOCKCHAIN_ALERT;
import static galilel.org.galilelwallet.service.IntentsConstants.NOT_COINS_RECEIVED;

public class GalilelWalletService extends Service{

    private Logger log = LoggerFactory.getLogger(GalilelWalletService.class);

    private GalilelApplication galilelApplication;
    private GalilelModuleImp module;
    private BlockchainManager blockchainManager;

    private PeerConnectivityListener peerConnectivityListener;

    private PowerManager.WakeLock wakeLock;
    private NotificationManager nm;
    private LocalBroadcastManager broadcastManager;

    private SnappyBlockchainStore blockchainStore;
    private boolean resetBlockchainOnShutdown = false;
    /** Created service time (just for checks) */
    private long serviceCreatedAt;
    /** Cached amount to notify balance */
    private Coin notificationAccumulatedAmount = Coin.ZERO;
    /**  */
    private final Set<Impediment> impediments = EnumSet.noneOf(Impediment.class);

    private BlockchainState blockchainState = BlockchainState.NOT_CONNECTION;

    private volatile long lastUpdateTime = System.currentTimeMillis();
    private volatile long lastMessageTime = System.currentTimeMillis();

    private static final String NOTIFICATION_CHANNEL_ID = "notification_channel";
    private static final String NOTIFICATION_CHANNEL_NAME = "notification_wallet";

    public class GalilelBinder extends Binder {
        public GalilelWalletService getService() {
            return GalilelWalletService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new GalilelBinder();
    }

    private AddressListener addressListener = new AddressListener() {
        @Override
        public void onBalanceChange(String address, long confirmed, long unconfirmed,int numConfirmations) {
            Intent intent = new Intent(ACTION_ADDRESS_BALANCE_CHANGE);
            broadcastManager.sendBroadcast(intent);
        }
    };

    private final class PeerConnectivityListener implements PeerConnectedEventListener, PeerDisconnectedEventListener{

        @Override
        public void onPeerConnected(Peer peer, int i) {
            //todo: notify peer connected
            log.info("Peer connected: "+peer.getAddress());
            broadcastPeerConnected();
        }

        @Override
        public void onPeerDisconnected(Peer peer, int i) {
            //todo: notify peer disconnected
            log.info("Peer disconnected: "+peer.getAddress());
        }
    }

    private final PeerDataEventListener blockchainDownloadListener = new AbstractPeerDataEventListener() {

        @Override
        public void onBlocksDownloaded(final Peer peer, final Block block, final FilteredBlock filteredBlock, final int blocksLeft) {
            try {
                //log.info("Block received , left: " + blocksLeft);

            /*log.info("############# on Blockcs downloaded ###########");
            log.info("Peer: " + peer + ", Block: " + block + ", left: " + blocksLeft);*/


            /*if (GalilelContext.IS_TEST)
                showBlockchainSyncNotification(blocksLeft);*/

                //delayHandler.removeCallbacksAndMessages(null);


                final long now = System.currentTimeMillis();
                if (now - lastMessageTime > TimeUnit.SECONDS.toMillis(6)) {
                    if (blocksLeft < 6) {
                        blockchainState = BlockchainState.SYNC;
                    } else {
                        blockchainState = BlockchainState.SYNCING;
                    }
                    galilelApplication.getAppConf().setLastBestChainBlockTime(block.getTime().getTime());
                    broadcastBlockchainState(true);
                }
            }catch (Exception e){
                e.printStackTrace();
                CrashReporter.saveBackgroundTrace(e,galilelApplication.getPackageInfo());
            }
        }
    };

    private class RunnableBlockChecker implements Runnable{

        private Block block;

        public RunnableBlockChecker(Block block) {
            this.block = block;
        }

        @Override
        public void run() {
            org.galilelj.core.Context.propagate(GalilelContext.CONTEXT);
            lastMessageTime = System.currentTimeMillis();
            broadcastBlockchainState(false);
        }
    }

    private final BroadcastReceiver connectivityReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                final String action = intent.getAction();
                if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
                    final NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
                    final boolean hasConnectivity = networkInfo.isConnected();
                    log.info("network is {}, state {}/{}", hasConnectivity ? "up" : "down", networkInfo.getState(), networkInfo.getDetailedState());
                    if (hasConnectivity)
                        impediments.remove(Impediment.NETWORK);
                    else
                        impediments.add(Impediment.NETWORK);
                    check();
                    // try to request coin rate
                    requestRateCoin();
                } else if (Intent.ACTION_DEVICE_STORAGE_LOW.equals(action)) {
                    log.info("device storage low");

                    impediments.add(Impediment.STORAGE);
                    check();
                } else if (Intent.ACTION_DEVICE_STORAGE_OK.equals(action)) {
                    log.info("device storage ok");
                    impediments.remove(Impediment.STORAGE);
                    check();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    private WalletCoinsReceivedEventListener coinReceiverListener = new WalletCoinsReceivedEventListener() {

        android.support.v4.app.NotificationCompat.Builder mBuilder;
        PendingIntent deleteIntent;
        PendingIntent openPendingIntent;

        @Override
        public void onCoinsReceived(Wallet wallet, Transaction transaction, Coin coin, Coin coin1) {
            //todo: acá falta una validación para saber si la transaccion es mia.
            org.galilelj.core.Context.propagate(CONTEXT);

            try {

                int depthInBlocks = transaction.getConfidence().getDepthInBlocks();

                long now = System.currentTimeMillis();
                if (lastUpdateTime + 5000 < now) {
                    lastUpdateTime = now;
                    Intent intent = new Intent(ACTION_NOTIFICATION);
                    intent.putExtra(INTENT_BROADCAST_DATA_TYPE, INTENT_BROADCAST_DATA_ON_COIN_RECEIVED);
                    broadcastManager.sendBroadcast(intent);
                }

                //final Address address = WalletUtils.getWalletAddressOfReceived(WalletConstants.NETWORK_PARAMETERS,transaction, wallet);
                final Coin amount = transaction.getValue(wallet);
                final TransactionConfidence.ConfidenceType confidenceType = transaction.getConfidence().getConfidenceType();

                if (amount.isGreaterThan(Coin.ZERO)) {
                    //notificationCount++;
                    notificationAccumulatedAmount = notificationAccumulatedAmount.add(amount);
                    Intent openIntent = new Intent(getApplicationContext(), WalletActivity.class);
                    openIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                            | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    openPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, openIntent, 0);
                    Intent resultIntent = new Intent(getApplicationContext(), GalilelWalletService.this.getClass());
                    resultIntent.setAction(ACTION_CANCEL_COINS_RECEIVED);
                    deleteIntent = PendingIntent.getService(GalilelWalletService.this, 0, resultIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                    mBuilder = new NotificationCompat.Builder(getApplicationContext(), NOTIFICATION_CHANNEL_ID)
                            .setContentTitle(getString(R.string.notification_received_title))
                            .setContentText(getString(R.string.notification_received_text) + " " + notificationAccumulatedAmount.toFriendlyString())
                            .setAutoCancel(true)
                            .setSmallIcon(R.mipmap.ic_notification)
                            .setColor(ContextCompat.getColor(getApplicationContext(), R.color.darkBrown1))
                            .setDeleteIntent(deleteIntent)
                            .setContentIntent(openPendingIntent);
                    nm.notify(NOT_COINS_RECEIVED, mBuilder.build());
                } else {
                    log.error("transaction with a value lesser than zero arrives..");
                }

            }catch (Exception e){
                log.error("Something happen on coin receive ", e);
            }

        }
    };

    private TransactionConfidenceEventListener transactionConfidenceEventListener = new TransactionConfidenceEventListener() {
        @Override
        public void onTransactionConfidenceChanged(Wallet wallet, Transaction transaction) {
            org.galilelj.core.Context.propagate(CONTEXT);
            try {
                if (transaction != null) {
                    if (transaction.getConfidence().getDepthInBlocks() > 1) {
                        long now = System.currentTimeMillis();
                        if (lastUpdateTime + 5000 < now) {
                            lastUpdateTime = now;
                            // update balance state
                            Intent intent = new Intent(ACTION_NOTIFICATION);
                            intent.putExtra(INTENT_BROADCAST_DATA_TYPE, INTENT_BROADCAST_DATA_ON_COIN_RECEIVED);
                            broadcastManager.sendBroadcast(intent);
                        }
                    }
                }
            }catch (Exception e){
                log.error("onTransactionConfidenceChanged exception",e);
            }
        }
    };

    @Override
    public void onCreate() {
        serviceCreatedAt = System.currentTimeMillis();
        super.onCreate();
        try {
            log.info("Galilel service started");
            // Android stuff
            final String lockName = getPackageName() + " blockchain sync";
            final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, lockName);

            nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            broadcastManager = LocalBroadcastManager.getInstance(this);
            // Galilel
            galilelApplication = GalilelApplication.getInstance();
            module = (GalilelModuleImp) galilelApplication.getModule();
            blockchainManager = module.getBlockchainManager();

            // Schedule service
            tryScheduleService();

            peerConnectivityListener = new PeerConnectivityListener();

            File file = getDir("blockstore_v2",MODE_PRIVATE);
            String filename = GalilelContext.Files.BLOCKCHAIN_FILENAME;
            boolean fileExists = new File(file,filename).exists();
            blockchainStore = new SnappyBlockchainStore(GalilelContext.CONTEXT,file,filename);
            blockchainManager.init(
                    blockchainStore,
                    file,
                    filename,
                    fileExists
            );

            module.addCoinsReceivedEventListener(coinReceiverListener);
            module.addOnTransactionConfidenceChange(transactionConfidenceEventListener);

            final IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            intentFilter.addAction(Intent.ACTION_DEVICE_STORAGE_LOW);
            intentFilter.addAction(Intent.ACTION_DEVICE_STORAGE_OK);
            registerReceiver(connectivityReceiver, intentFilter); // implicitly init PeerGroup


        } catch (Error e){
            e.printStackTrace();
            CrashReporter.appendSavedBackgroundTraces(e);
            Intent intent = new Intent(IntentsConstants.ACTION_STORED_BLOCKCHAIN_ERROR);
            broadcastManager.sendBroadcast(intent);
            throw e;
        } catch (Exception e){
            // todo: I have to handle the connection refused..
            e.printStackTrace();
            CrashReporter.appendSavedBackgroundTraces(e);
            // for now i just launch a notification
            Intent intent = new Intent(IntentsConstants.ACTION_TRUSTED_PEER_CONNECTION_FAIL);
            broadcastManager.sendBroadcast(intent);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        log.info("Galilel service onStartCommand");

        // create initial configuration for foreground service.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);

            Bitmap largeIconBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
            Notification.Builder builder = new Notification.Builder(this, NOTIFICATION_CHANNEL_ID)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText("MAIK wallet is syncing.")
                    .setLargeIcon(largeIconBitmap)
                    .setAutoCancel(true);

            Notification notification = builder.build();
            startForeground(1, notification);
        }

        try {
            if (intent != null) {
                try {
                    log.info("service init command: " + intent
                            + (intent.hasExtra(Intent.EXTRA_ALARM_COUNT) ? " (alarm count: " + intent.getIntExtra(Intent.EXTRA_ALARM_COUNT, 0) + ")" : ""));
                } catch (Exception e) {
                    e.printStackTrace();
                    log.info("service init command: " + intent
                            + (intent.hasExtra(Intent.EXTRA_ALARM_COUNT) ? " (alarm count: " + intent.getLongArrayExtra(Intent.EXTRA_ALARM_COUNT) + ")" : ""));
                }
                final String action = intent.getAction();
                if (ACTION_SCHEDULE_SERVICE.equals(action)) {
                    check();
                } else if (ACTION_CANCEL_COINS_RECEIVED.equals(action)) {
                    notificationAccumulatedAmount = Coin.ZERO;
                    nm.cancel(NOT_COINS_RECEIVED);
                } else if (ACTION_RESET_BLOCKCHAIN.equals(action)) {
                    log.info("will remove blockchain on service shutdown");
                    resetBlockchainOnShutdown = true;

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        stopForeground(true);
                    } else {
                        stopSelf();
                    }
                } else if (ACTION_BROADCAST_TRANSACTION.equals(action)) {
                    blockchainManager.broadcastTransaction(intent.getByteArrayExtra(DATA_TRANSACTION_HASH));
                }
            } else {
                log.warn("service restart, although it was started as non-sticky");
            }
        }catch (Exception e){
            log.error("onStartCommand exception",e);
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        log.info(".onDestroy()");
        try {
            // todo: notify module about this shutdown...
            try {
                unregisterReceiver(connectivityReceiver);
            }catch (Exception e){
                // swallow
            }

            if (module.isStarted()) {

                // remove listeners
                module.removeCoinsReceivedEventListener(coinReceiverListener);
                module.removeTransactionsConfidenceChange(transactionConfidenceEventListener);
                blockchainManager.removeBlockchainDownloadListener(blockchainDownloadListener);
                blockchainManager.destroy(resetBlockchainOnShutdown);
            } else {
                tryScheduleServiceNow();
            }

            if (wakeLock.isHeld()) {
                log.debug("wakelock still held, releasing");
                wakeLock.release();
            }

            log.info("service was up for " + ((System.currentTimeMillis() - serviceCreatedAt) / 1000 / 60) + " minutes");
            // schedule service it is not scheduled yet
            tryScheduleService();
        }catch (Exception e){
            log.error("onDestroy exception",e);
        }
    }

    /**
     * Schedule service for later
     */
    private void tryScheduleService() {
        boolean isSchedule = System.currentTimeMillis() < module.getConf().getScheduledBLockchainService();

        if (!isSchedule){
            log.info("scheduling service");
            AlarmManager alarm = (AlarmManager)getSystemService(ALARM_SERVICE);
            long scheduleTime = System.currentTimeMillis() + 1000 * 60 * 15; // (1000 * 60 * 15); // 15 minutes from now

            Intent intent = new Intent(this, GalilelWalletService.class);
            intent.setAction(ACTION_SCHEDULE_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                alarm.set(
                        AlarmManager.RTC_WAKEUP,
                        scheduleTime,
                        PendingIntent.getForegroundService(this, 0, intent, 0)
                );
            } else {
                alarm.set(
                        AlarmManager.RTC_WAKEUP,
                        scheduleTime,
                        PendingIntent.getService(this, 0, intent, 0)
                );
            }

            // save
            module.getConf().saveScheduleBlockchainService(scheduleTime);
        }
    }

    private void tryScheduleServiceNow() {
        log.info("scheduling service now");
        AlarmManager alarm = (AlarmManager)getSystemService(ALARM_SERVICE);
        long scheduleTime = System.currentTimeMillis() + 1000 * 60 * 5; // 5 minutes

        Intent intent = new Intent(this, GalilelWalletService.class);
        intent.setAction(ACTION_SCHEDULE_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            alarm.set(
                    AlarmManager.RTC_WAKEUP,
                    scheduleTime,
                    PendingIntent.getForegroundService(this, 0, intent, 0)
            );
        } else {
            alarm.set(
                    AlarmManager.RTC_WAKEUP,
                    scheduleTime,
                    PendingIntent.getService(this, 0, intent, 0)
            );
        }
    }

    private void requestRateCoin(){
        final AppConf appConf = galilelApplication.getAppConf();
        GalilelRate galilelRate = module.getRate(appConf.getSelectedRateCoin());
        if (galilelRate == null || galilelRate.getTimestamp() + GalilelContext.RATE_UPDATE_TIME < System.currentTimeMillis()){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        CoinMarketCapApiClient c = new CoinMarketCapApiClient();
                        CoinMarketCapApiClient.GalilelMarket galilelMarket = c.getGalilelPxrice();
                        GalilelRate galilelRate = new GalilelRate(DEFAULT_RATE_COIN,galilelMarket.priceUsd,System.currentTimeMillis());
                        module.saveRate(galilelRate);
                        final GalilelRate galilelBtcRate = new GalilelRate("BTC",galilelMarket.priceBtc,System.currentTimeMillis());
                        module.saveRate(galilelBtcRate);

                        // Get the rest of the rates:
                        List<GalilelRate> rates = new CoinMarketCapApiClient.BitPayApi().getRates(new CoinMarketCapApiClient.BitPayApi.RatesConvertor<GalilelRate>() {
                            @Override
                            public GalilelRate convertRate(String code, String name, BigDecimal bitcoinRate) {
                                BigDecimal rate = bitcoinRate.multiply(galilelBtcRate.getRate());
                                return new GalilelRate(code,rate,System.currentTimeMillis());
                            }
                        });

                        for (GalilelRate rate : rates) {
                            module.saveRate(rate);
                        }

                    } catch (RequestGalilelRateException e) {
                        e.printStackTrace();
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    private AtomicBoolean isChecking = new AtomicBoolean(false);

    /**
     * Check and download the blockchain if it needed
     */
    private void check() {
        log.info("check");
        try {
            if (!isChecking.getAndSet(true)) {
                if (module.isStarted()) {
                    blockchainManager.check(
                            impediments,
                            peerConnectivityListener,
                            peerConnectivityListener,
                            blockchainDownloadListener,
                            null
                    );
                } else {
                    tryScheduleServiceNow();
                }
                //todo: ver si conviene esto..
                broadcastBlockchainState(true);
                isChecking.set(false);
            }
        }catch (Exception e){
            log.error("Exception on blockchainManager check", e);
            isChecking.set(false);
            broadcastBlockchainState(false);
            // Try to schedule the service again
            tryScheduleServiceNow();
        }
    }

    private void broadcastBlockchainState(boolean isCheckOk) {
        boolean showNotif = false;
        if (!impediments.isEmpty()) {

            StringBuilder stringBuilder = new StringBuilder();
            for (Impediment impediment : impediments) {
                if (stringBuilder.length() != 0){
                    stringBuilder.append("\n");
                }
                if (impediment == Impediment.NETWORK){
                    blockchainState = BlockchainState.NOT_CONNECTION;
                    stringBuilder.append("No peer connection");
                }else if(impediment == Impediment.STORAGE){
                    stringBuilder.append("No available storage");
                    showNotif = true;
                }
            }

            if(showNotif) {
                android.support.v4.app.NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(getApplicationContext(), NOTIFICATION_CHANNEL_ID)
                                .setSmallIcon(R.mipmap.ic_notification)
                                .setContentTitle(getString(R.string.notification_received_title))
                                .setContentText(stringBuilder.toString())
                                .setAutoCancel(true)
                                .setColor(ContextCompat.getColor(getApplicationContext(), R.color.darkBrown1))
                        ;

                nm.notify(NOT_BLOCKCHAIN_ALERT, mBuilder.build());
            }
        }

        if (isCheckOk){
            broadcastBlockchainStateIntent();
        }
    }

    private void broadcastBlockchainStateIntent(){
        final long now = System.currentTimeMillis();
        if (now-lastMessageTime> TimeUnit.SECONDS.toMillis(6)) {
            lastMessageTime = System.currentTimeMillis();
            Intent intent = new Intent(ACTION_NOTIFICATION);
            intent.putExtra(INTENT_BROADCAST_DATA_TYPE, INTENT_BROADCAST_DATA_BLOCKCHAIN_STATE);
            intent.putExtra(INTENT_EXTRA_BLOCKCHAIN_STATE,blockchainState);
            broadcastManager.sendBroadcast(intent);
        }

        if (blockchainState == BlockchainState.SYNC){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                stopForeground(true);
            } else {
                stopSelf();
            }
        }
    }

    private void broadcastPeerConnected() {
        Intent intent = new Intent(ACTION_NOTIFICATION);
        intent.putExtra(INTENT_BROADCAST_DATA_TYPE, INTENT_BROADCAST_DATA_PEER_CONNECTED);
        broadcastManager.sendBroadcast(intent);
    }

}
