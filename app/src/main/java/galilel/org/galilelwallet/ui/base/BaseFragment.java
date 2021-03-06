package galilel.org.galilelwallet.ui.base;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import galilel.org.galilelwallet.GalilelApplication;
import global.GalilelModule;

public class BaseFragment extends Fragment {

    protected GalilelApplication galilelApplication;
    protected GalilelModule galilelModule;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        galilelApplication = GalilelApplication.getInstance();
        galilelModule = galilelApplication.getModule();
    }

    protected boolean checkPermission(String permission) {
        int result = ContextCompat.checkSelfPermission(getActivity(),permission);
        return result == PackageManager.PERMISSION_GRANTED;
    }
}
