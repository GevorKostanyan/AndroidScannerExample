package am.test.user.usertest.ui.adapter;

import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;

import am.test.user.usertest.R;
import am.test.user.usertest.data.UserData;
import am.test.user.usertest.databinding.InfoItemBinding;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Gevor
 */

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private RealmResults<UserData> data;

    public UserAdapter() {
        data = Realm.getDefaultInstance().where(UserData.class).findAll();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        InfoItemBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.info_item, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.binding.setUser(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }

    public long getItemId(int position){
        return data.get(position).getId();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        InfoItemBinding binding;

        ViewHolder(InfoItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @BindingAdapter("adapter")
    public static void setAdapter(RecyclerView recyclerView, UserAdapter adapter) {
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
