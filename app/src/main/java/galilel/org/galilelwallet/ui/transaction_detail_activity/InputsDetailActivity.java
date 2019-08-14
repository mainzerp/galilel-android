package galilel.org.galilelwallet.ui.transaction_detail_activity;

import android.os.Bundle;
import android.view.ViewGroup;

import galilel.org.galilelwallet.R;
import galilel.org.galilelwallet.ui.base.BaseActivity;

public class InputsDetailActivity extends BaseActivity {

    @Override
    protected void onCreateView(Bundle savedInstanceState, ViewGroup container) {
        setTitle(getString(R.string.tx_input_title));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getLayoutInflater().inflate(R.layout.inputs_tx_detail_main,container);
    }
}
