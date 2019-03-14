package galilel.org.galilelwallet.ui.initial;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import galilel.org.galilelwallet.GalilelApplication;
import galilel.org.galilelwallet.ui.splash_activity.SplashActivity;
import galilel.org.galilelwallet.ui.wallet_activity.WalletActivity;
import galilel.org.galilelwallet.utils.AppConf;

public class InitialActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GalilelApplication galilelApplication = GalilelApplication.getInstance();
        AppConf appConf = galilelApplication.getAppConf();

        // show report dialog if something happen with the previous process.
        Intent intent;

        if (!appConf.isAppInit() || appConf.hasSplashVideo())
            intent = new Intent(this, SplashActivity.class);
        else
            intent = new Intent(this, WalletActivity.class);

        appConf.setHasSplashVideo(false);

        startActivity(intent);
        finish();
    }
}
