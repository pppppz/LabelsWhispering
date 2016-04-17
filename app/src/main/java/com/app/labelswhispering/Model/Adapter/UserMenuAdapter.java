package com.app.labelswhispering.Model.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.labelswhispering.Model.UserMenu;
import com.app.labelswhispering.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserMenuAdapter extends RecyclerView.Adapter<UserMenuAdapter.ViewHolder> {

    private List<UserMenu> menus;
    private Context mContext;


    public UserMenuAdapter(Context context, List<UserMenu> dataset) {
        menus = dataset;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.row_user_menu, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        UserMenu menu = menus.get(position);
        viewHolder.title.setText(menu.getTitle());
        viewHolder.icon.setImageResource(menu.getIcon());
    }

    @Override
    public int getItemCount() {
        return menus.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public CircleImageView icon;

        public ViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.menu_title);
            icon = (CircleImageView) view.findViewById(R.id.menu_icon);
        }
    }
}