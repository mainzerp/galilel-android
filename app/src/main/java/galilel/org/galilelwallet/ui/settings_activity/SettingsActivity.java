package galilel.org.galilelwallet.ui.settings_activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import chain.BlockchainState;
import global.GalilelModuleImp;
import galilel.org.galilelwallet.BuildConfig;
import galilel.org.galilelwallet.R;
import galilel.org.galilelwallet.module.GalilelContext;
import galilel.org.galilelwallet.ui.base.BaseDrawerActivity;
import galilel.org.galilelwallet.ui.base.dialogs.SimpleTwoButtonsDialog;
import galilel.org.galilelwallet.ui.export_account.ExportKeyActivity;
import galilel.org.galilelwallet.ui.import_watch_only.SettingsWatchOnly;
import galilel.org.galilelwallet.ui.restore_activity.RestoreActivity;
import galilel.org.galilelwallet.ui.settings_backup_activity.SettingsBackupActivity;
import galilel.org.galilelwallet.ui.settings_network_activity.SettingsNetworkActivity;
import galilel.org.galilelwallet.ui.settings_pincode_activity.SettingsPincodeActivity;
import galilel.org.galilelwallet.ui.settings_rates.SettingsRatesActivity;
import galilel.org.galilelwallet.ui.start_node_activity.StartNodeActivity;
import galilel.org.galilelwallet.ui.tutorial_activity.TutorialActivity;
import galilel.org.galilelwallet.utils.CrashReporter;
import galilel.org.galilelwallet.utils.DialogsUtil;
import galilel.org.galilelwallet.utils.IntentsUtils;
import galilel.org.galilelwallet.utils.NavigationUtils;
import galilel.org.galilelwallet.utils.ReportIssueDialogBuilder;

import static galilel.org.galilelwallet.service.IntentsConstants.INTENT_BROADCAST_DATA_BLOCKCHAIN_STATE;
import static galilel.org.galilelwallet.service.IntentsConstants.INTENT_BROADCAST_DATA_PEER_CONNECTED;
import static galilel.org.galilelwallet.service.IntentsConstants.INTENT_BROADCAST_DATA_TYPE;
import static galilel.org.galilelwallet.service.IntentsConstants.INTENT_EXTRA_BLOCKCHAIN_STATE;
import static galilel.org.galilelwallet.ui.tutorial_activity.TutorialActivity.INTENT_EXTRA_INFO_TUTORIAL;

public class SettingsActivity extends BaseDrawerActivity implements View.OnClickListener {
    private Button buttonBackup;
    private Button buttonRestore;
    private Button btn_export_pub_key;
    private Button btn_import_xpub;
    private Button buttonChange;
    private Button btn_change_node;
    private Button btn_reset_blockchain;
    private Button btn_report;
    private Button btn_support;
    private Button buttonTutorial;
    private TextView textAbout, text_rates;
    private TextView txt_network_info;

    @Override
    protected void onCreateView(Bundle savedInstanceState, ViewGroup container) {
        getLayoutInflater().inflate(R.layout.fragment_settings, container);
        setTitle(getString(R.string.settings_screen_title));

        TextView app_version = (TextView) findViewById(R.id.app_version);
        app_version.setText(BuildConfig.VERSION_NAME);

        txt_network_info = (TextView) findViewById(R.id.txt_network_info);

        textAbout = (TextView)findViewById(R.id.text_about);
        String text = getString(R.string.made_by) + "<br> <font color=#795548>Maik Broemme</font> <br>(c) Galilel UG (haftungsbeschränkt)";
        textAbout.setText(Html.fromHtml(text));
        // Open Backup Wallet
        buttonBackup = (Button) findViewById(R.id.btn_backup_wallet);
        buttonBackup.setOnClickListener(this);

        // Open Restore Wallet
        buttonRestore = (Button) findViewById(R.id.btn_restore_wallet);
        buttonRestore.setOnClickListener(this);

        btn_export_pub_key = (Button) findViewById(R.id.btn_export_pub_key);
        btn_export_pub_key.setOnClickListener(this);

        btn_import_xpub = (Button) findViewById(R.id.btn_import_xpub);
        btn_import_xpub.setOnClickListener(this);

        // Open Change Pincode
        buttonChange = (Button) findViewById(R.id.btn_change_pincode);
        buttonChange.setOnClickListener(this);

        btn_change_node = (Button) findViewById(R.id.btn_change_node);
        btn_change_node.setOnClickListener(this);

        btn_reset_blockchain = (Button) findViewById(R.id.btn_reset_blockchain);
        btn_reset_blockchain.setOnClickListener(this);

        // rates
        findViewById(R.id.btn_rates).setOnClickListener(this);
        text_rates = (TextView) findViewById(R.id.text_rates);
        text_rates.setText(galilelApplication.getAppConf().getSelectedRateCoin());

        // Open Network Monitor
        buttonChange = (Button) findViewById(R.id.btn_network);
        buttonChange.setOnClickListener(this);

        btn_report = (Button) findViewById(R.id.btn_report);
        btn_report.setOnClickListener(this);

        btn_support = (Button) findViewById(R.id.btn_support);
        btn_support.setOnClickListener(this);

        // Open Tutorial
        buttonTutorial = (Button) findViewById(R.id.btn_tutorial);
        buttonTutorial.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // to check current activity in the navigation drawer
        setNavigationMenuItemChecked(2);
        updateNetworkStatus();
        text_rates.setText(galilelApplication.getAppConf().getSelectedRateCoin());
    }

    private void updateNetworkStatus() {
        // Check if the activity is on foreground
        if (!isOnForeground)return;
        txt_network_info.setText(
                Html.fromHtml(
                        getString(R.string.about_network) +
                        "<br><font color=#795548>" +
                        galilelModule.getConf().getNetworkParams().getId() +
                        "</font><br>" +
                        getString(R.string.about_height) +
                        "<br><font color=#795548>" + 
                        galilelModule.getChainHeight() +
                        "</font><br>" +
                        getString(R.string.about_protocol) +
                        "<br><font color=#795548>" +
                        galilelModule.getProtocolVersion() +
                        "</font>"
                )
        );
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_backup_wallet){
            Intent myIntent = new Intent(v.getContext(), SettingsBackupActivity.class);
            startActivity(myIntent);
        }else if (id == R.id.btn_tutorial){
            Intent myIntent = new Intent(v.getContext(), TutorialActivity.class);
            myIntent.putExtra(INTENT_EXTRA_INFO_TUTORIAL,true);
            startActivity(myIntent);
        }else if (id == R.id.btn_restore_wallet){
            Intent myIntent = new Intent(v.getContext(), RestoreActivity.class);
            startActivity(myIntent);
        }else if (id == R.id.btn_change_pincode){
            Intent myIntent = new Intent(v.getContext(), SettingsPincodeActivity.class);
            startActivity(myIntent);
        }else if (id == R.id.btn_network){
            startActivity(new Intent(v.getContext(),SettingsNetworkActivity.class));
        }else if(id == R.id.btn_change_node) {
            startActivity(new Intent(v.getContext(), StartNodeActivity.class));
        }else if(id == R.id.btn_reset_blockchain){
            launchResetBlockchainDialog();
        }else if (id == R.id.btn_report){
            launchReportDialog();
        }else if(id == R.id.btn_support){
            IntentsUtils.startSend(
                    this,
                    getString(R.string.support_subject),
                    getString(R.string.report_issue_dialog_message_issue),
                    new ArrayList<Uri>()
            );
        }else if (id == R.id.btn_export_pub_key){
            startActivity(new Intent(v.getContext(), ExportKeyActivity.class));
        }else if (id == R.id.btn_import_xpub){
            startActivity(new Intent(v.getContext(), SettingsWatchOnly.class));
        }else if (id == R.id.btn_rates){
            startActivity(new Intent(v.getContext(), SettingsRatesActivity.class));
        }
    }

    private void launchResetBlockchainDialog() {
        SimpleTwoButtonsDialog dialog = DialogsUtil.buildSimpleTwoBtnsDialog(
                this,
                getString(R.string.dialog_reset_blockchain_title),
                getString(R.string.dialog_reset_blockchain_body),
                new SimpleTwoButtonsDialog.SimpleTwoBtnsDialogListener() {
                    @Override
                    public void onRightBtnClicked(SimpleTwoButtonsDialog dialog) {
                        galilelApplication.stopBlockchain();
                        Toast.makeText(SettingsActivity.this,getString(R.string.reseting_blockchain),Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }

                    @Override
                    public void onLeftBtnClicked(SimpleTwoButtonsDialog dialog) {
                        dialog.dismiss();
                    }
                }
        );
        dialog.setLeftBtnText(getString(R.string.button_cancel))
                .setRightBtnText(getString(R.string.button_ok));
        dialog.show();
    }

    private void launchReportDialog() {
        ReportIssueDialogBuilder dialog = new ReportIssueDialogBuilder(
                this,
                "galilel.org.galilelwallet.myfileprovider",
                R.string.report_issuea_dialog_title,
                R.string.report_issue_dialog_message_issue)
        {
            @Nullable
            @Override
            protected CharSequence subject() {
                return GalilelContext.REPORT_SUBJECT_ISSUE+" "+galilelApplication.getVersionName();
            }

            @Nullable
            @Override
            protected CharSequence collectApplicationInfo() throws IOException {
                final StringBuilder applicationInfo = new StringBuilder();
                CrashReporter.appendApplicationInfo(applicationInfo, galilelApplication);
                return applicationInfo;
            }

            @Nullable
            @Override
            protected CharSequence collectStackTrace() throws IOException {
                return null;
            }

            @Nullable
            @Override
            protected CharSequence collectDeviceInfo() throws IOException {
                final StringBuilder deviceInfo = new StringBuilder();
                CrashReporter.appendDeviceInfo(deviceInfo, SettingsActivity.this);
                return deviceInfo;
            }

            @Nullable
            @Override
            protected CharSequence collectWalletDump() throws IOException {
                return ((GalilelModuleImp)galilelModule).getWallet().toString(false,true,true,null);
            }
        };
        dialog.show();
    }
    @Override
    protected void onBlockchainStateChange() {
        updateNetworkStatus();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        NavigationUtils.goBackToHome(this);
    }
}
