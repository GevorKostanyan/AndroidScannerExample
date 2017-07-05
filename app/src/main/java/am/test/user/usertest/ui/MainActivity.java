package am.test.user.usertest.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.arellomobile.mvp.presenter.InjectPresenter;

import am.test.user.usertest.R;
import am.test.user.usertest.databinding.ActivityMainBinding;
import am.test.user.usertest.listener.RecyclerItemClickListener;
import am.test.user.usertest.ui.adapter.UserAdapter;
import am.test.user.usertest.ui.base.BaseActivity;
import am.test.user.usertest.ui.presenter.MainActivityPresenter;

public class MainActivity extends BaseActivity {
    private ActivityMainBinding binding;
    @InjectPresenter
    MainActivityPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setContentView(binding.getRoot());

        binding.recycler.setLayoutManager(new LinearLayoutManager(this));
        binding.recycler.addOnItemTouchListener(new RecyclerItemClickListener(this,
                (view, position) ->
                        startActivity(AddUserActivity.newIntent(MainActivity.this,
                                binding.recycler.getAdapter().getItemId(position)))
        ));

        binding.setAdapter(new UserAdapter());

    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.getAdapter().notifyDataSetChanged();
    }

    public void addNewUser(View view) {
        startActivity(AddUserActivity.newIntent(MainActivity.this));
    }
}
