package galilel.org.galilelwallet.ui.transaction_detail_activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import global.wrappers.TransactionWrapper;
import galilel.org.galilelwallet.R;
import galilel.org.galilelwallet.ui.base.BaseActivity;
import galilel.org.galilelwallet.ui.wallet_activity.WalletActivity;
import galilel.org.galilelwallet.utils.NavigationUtils;

import static galilel.org.galilelwallet.ui.transaction_detail_activity.FragmentTxDetail.IS_DETAIL;
import static galilel.org.galilelwallet.ui.transaction_detail_activity.FragmentTxDetail.TX_WRAPPER;

public class TransactionDetailActivity extends BaseActivity {

    private TransactionWrapper transactionWrapper;

    @Override
    protected void onCreateView(Bundle savedInstanceState, ViewGroup container) {
        getLayoutInflater().inflate(R.layout.transaction_detail_main, container);
        setTitle(R.string.tx_detail_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent intent = getIntent();
        if (intent != null){
            transactionWrapper = (TransactionWrapper) intent.getSerializableExtra(TX_WRAPPER);
            if (intent.hasExtra(IS_DETAIL)){
                transactionWrapper.setTransaction(galilelModule.getTx(transactionWrapper.getTxId()));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*MenuItem menuItem = menu.add(0,0,0,R.string.explorer);
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);*/
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*switch (item.getItemId()) {
            case 0:
                //Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com"));
                //startActivity(browserIntent);
                return true;
        }*/
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = getIntent();
        setResult(RESULT_OK,intent);
        finish();
    }
}
