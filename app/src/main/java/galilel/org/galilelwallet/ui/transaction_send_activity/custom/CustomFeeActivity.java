package galilel.org.galilelwallet.ui.transaction_send_activity.custom;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import galilel.org.galilelwallet.R;
import galilel.org.galilelwallet.ui.base.BaseActivity;
import galilel.org.galilelwallet.utils.DialogsUtil;

import static galilel.org.galilelwallet.ui.transaction_send_activity.custom.CustomFeeFragment.INTENT_EXTRA_CLEAR;
import static galilel.org.galilelwallet.ui.transaction_send_activity.custom.CustomFeeFragment.INTENT_EXTRA_FEE;
import static galilel.org.galilelwallet.ui.transaction_send_activity.custom.CustomFeeFragment.INTENT_EXTRA_IS_FEE_PER_KB;
import static galilel.org.galilelwallet.ui.transaction_send_activity.custom.CustomFeeFragment.INTENT_EXTRA_IS_MINIMUM_FEE;
import static galilel.org.galilelwallet.ui.transaction_send_activity.custom.CustomFeeFragment.INTENT_EXTRA_IS_TOTAL_FEE;

public class CustomFeeActivity extends BaseActivity {

    private View root;
    private CustomFeeFragment customFeeFragment;

    @Override
    protected void onCreateView(Bundle savedInstanceState, ViewGroup container) {
        root = getLayoutInflater().inflate(R.layout.custom_fee_main, container);
        setTitle(getString(R.string.option_custom_fee));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        customFeeFragment = (CustomFeeFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_custom_fee);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save_menu_default,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.option_ok){
            try {
                Intent intent = new Intent();
                CustomFeeFragment.FeeSelector feeSelector = customFeeFragment.getFee();
                intent.putExtra(INTENT_EXTRA_IS_FEE_PER_KB,feeSelector.isFeePerKbSelected());
                intent.putExtra(INTENT_EXTRA_IS_TOTAL_FEE,!feeSelector.isFeePerKbSelected());
                intent.putExtra(INTENT_EXTRA_IS_MINIMUM_FEE,feeSelector.isPayMinimum());
                intent.putExtra(INTENT_EXTRA_FEE,feeSelector.getAmount());
                setResult(RESULT_OK,intent);
                finish();
                return true;
            } catch (InvalidFeeException e) {
                e.printStackTrace();
                DialogsUtil.buildSimpleErrorTextDialog(this,getString(R.string.invalid_inputs),e.getMessage()).show(getFragmentManager(),"custom_fee_invalid_inputs");
            }
        }
        return super.onOptionsItemSelected(item);
    }


}
