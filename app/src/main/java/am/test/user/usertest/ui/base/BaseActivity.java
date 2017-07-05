package am.test.user.usertest.ui.base;

import android.app.ProgressDialog;
import android.os.Bundle;

import com.arellomobile.mvp.MvpAppCompatActivity;

import am.test.user.usertest.R;
import am.test.user.usertest.ui.view.BaseView;
import io.realm.Realm;

/**
 * Created by Gevor
 */

public abstract class BaseActivity extends MvpAppCompatActivity implements BaseView {
    private ProgressDialog progressDialog;
    protected Realm realmDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Realm.init(this);
        realmDB = Realm.getDefaultInstance();
    }

    @Override
    public void showProgressDialog() {
        progressDialog = ProgressDialog.show(this, getString(R.string.info), getString(R.string.loading), true, false);
        runOnUiThread(() -> progressDialog.show());
    }

    @Override
    public void hideProgressDialog() {
        if (progressDialog != null) {
            runOnUiThread(() -> progressDialog.dismiss());
        }
    }

    @Override
    protected void onDestroy() {
        realmDB.close();
        super.onDestroy();
    }
}
