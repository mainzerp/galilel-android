package galilel.org.galilelwallet.ui.transaction_send_activity.custom.inputs;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.galilelj.core.Coin;

import galilel.org.galilelwallet.R;
import galilel.org.galilelwallet.ui.base.BaseActivity;

import static galilel.org.galilelwallet.ui.transaction_send_activity.SendActivity.INTENT_EXTRA_TOTAL_AMOUNT;

public class InputsActivity extends BaseActivity {

    public static final String INTENT_NO_TOTAL_AMOUNT = "no_total";

    private View root;
    private InputsFragment input_fragment;
    private TextView txt_amount;

    private Coin totalAmount;

    @Override
    protected void onCreateView(Bundle savedInstanceState, ViewGroup container) {
        setTitle("Coins selection");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        root = getLayoutInflater().inflate(R.layout.inputs_main,container);
        input_fragment = (InputsFragment) getSupportFragmentManager().findFragmentById(R.id.inputs_fragment);
        txt_amount = (TextView) root.findViewById(R.id.txt_amount);

        Intent intent = getIntent();
        if (intent!=null && intent.hasExtra(INTENT_NO_TOTAL_AMOUNT)){
            root.findViewById(R.id.container_header).setVisibility(View.GONE);
        }else {
            totalAmount = Coin.parseCoin(getIntent().getStringExtra(INTENT_EXTRA_TOTAL_AMOUNT));
            txt_amount.setText(totalAmount.toFriendlyString());
        }
    }




}
