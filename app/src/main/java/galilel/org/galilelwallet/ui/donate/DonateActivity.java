package galilel.org.galilelwallet.ui.donate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.galilelj.core.Coin;
import org.galilelj.core.InsufficientMoneyException;
import org.galilelj.core.Transaction;

import galilel.org.galilelwallet.R;
import galilel.org.galilelwallet.module.GalilelContext;
import galilel.org.galilelwallet.service.GalilelWalletService;
import galilel.org.galilelwallet.ui.base.BaseDrawerActivity;
import galilel.org.galilelwallet.ui.base.dialogs.SimpleTextDialog;
import galilel.org.galilelwallet.ui.pincode_activity.PincodeActivity;
import galilel.org.galilelwallet.utils.DialogsUtil;
import galilel.org.galilelwallet.utils.NavigationUtils;

import static galilel.org.galilelwallet.service.IntentsConstants.ACTION_BROADCAST_TRANSACTION;
import static galilel.org.galilelwallet.service.IntentsConstants.DATA_TRANSACTION_HASH;

public class DonateActivity extends BaseDrawerActivity {

    private static final int PIN_RESULT = 121;

    private View root;
    private EditText edit_amount;
    private Button btn_donate;
    private SimpleTextDialog errorDialog;

    @Override
    protected void onCreateView(Bundle savedInstanceState, ViewGroup container) {
        root = getLayoutInflater().inflate(R.layout.donations_fragment,container);
        edit_amount = (EditText) root.findViewById(R.id.edit_amount);
        btn_donate = (Button) root.findViewById(R.id.btn_donate);
        btn_donate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start pin
                Intent intent = new Intent(DonateActivity.this, PincodeActivity.class);
                intent.putExtra(PincodeActivity.CHECK_PIN,true);
                startActivityForResult(intent,PIN_RESULT);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PIN_RESULT){
            if (resultCode == RESULT_OK){
                try{
                    send();
                }catch (Exception e){
                    e.printStackTrace();
                    showErrorDialog(e.getMessage());
                }
            }
        }
    }

    private void send() {
        try {
            // create the tx
            String addressStr = GalilelContext.DONATE_ADDRESS;
            if (!galilelModule.chechAddress(addressStr))
                throw new IllegalArgumentException(getString(R.string.invalid_input_address));
            String amountStr = edit_amount.getText().toString();
            if (amountStr.length() < 1) throw new IllegalArgumentException(getString(R.string.invalid_amount));
            if (amountStr.length()==1 && amountStr.equals(".")) throw new IllegalArgumentException(getString(R.string.invalid_amount));
            if (amountStr.charAt(0)=='.'){
                amountStr = getString(R.string.number_0) + amountStr;
            }
            Coin amount = Coin.parseCoin(amountStr);
            if (amount.isZero()) throw new IllegalArgumentException(getString(R.string.invalid_amount));
            if (amount.isLessThan(Transaction.MIN_NONDUST_OUTPUT)) throw new IllegalArgumentException(getString(R.string.invalid_amount_small) + " " + Transaction.MIN_NONDUST_OUTPUT.toFriendlyString());
            if (amount.isGreaterThan(Coin.valueOf(galilelModule.getAvailableBalance())))
                throw new IllegalArgumentException(getString(R.string.invalid_balance));
            String memo = "Donation!";
            // build a tx with the default fee
            Transaction transaction = galilelModule.buildSendTx(addressStr, amount, memo,galilelModule.getReceiveAddress());
            // send it
            galilelModule.commitTx(transaction);
            Intent intent = new Intent(DonateActivity.this, GalilelWalletService.class);
            intent.setAction(ACTION_BROADCAST_TRANSACTION);
            intent.putExtra(DATA_TRANSACTION_HASH,transaction.getHash().getBytes());
            startService(intent);

            Toast.makeText(this,getString(R.string.donation_thanks),Toast.LENGTH_LONG).show();
            onBackPressed();

        } catch (InsufficientMoneyException e) {
            e.printStackTrace();
            throw new IllegalArgumentException(getString(R.string.invalid_balance));
        }
    }

    private void showErrorDialog(String message) {
        if (errorDialog==null){
            errorDialog = DialogsUtil.buildSimpleErrorTextDialog(this,getResources().getString(R.string.invalid_inputs),message);
        }else {
            errorDialog.setBody(message);
        }
        errorDialog.show(getFragmentManager(),getResources().getString(R.string.send_error_dialog_tag));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        NavigationUtils.goBackToHome(this);
    }
}
