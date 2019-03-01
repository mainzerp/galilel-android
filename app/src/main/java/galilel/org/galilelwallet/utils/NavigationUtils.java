package galilel.org.galilelwallet.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import galilel.org.galilelwallet.ui.wallet_activity.WalletActivity;

public class NavigationUtils {

    public static void goBackToHome(Activity activity){
        Intent upIntent = new Intent(activity,WalletActivity.class);
        upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(upIntent);
        activity.finish();
    }

}
