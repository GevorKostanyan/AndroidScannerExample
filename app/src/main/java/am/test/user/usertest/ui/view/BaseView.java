package am.test.user.usertest.ui.view;

import com.arellomobile.mvp.MvpView;

/**
 * Created by Gevor
 */

public interface BaseView extends MvpView {
    void showProgressDialog();

    void hideProgressDialog();
}
