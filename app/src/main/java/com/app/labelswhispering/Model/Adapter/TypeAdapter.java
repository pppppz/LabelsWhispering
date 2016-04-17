package com.app.labelswhispering.Model.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.app.labelswhispering.R;

public class TypeAdapter extends RecyclerView.Adapter<TypeAdapter.ViewHolder> {

    //private List<Schedule> schedules;
    ArrayAdapter<CharSequence> adapter;
    private Context mContext;
    private String TAG = TypeAdapter.class.getSimpleName();

    public TypeAdapter(Context context, ArrayAdapter<CharSequence> arrayAdapter) {
        adapter = arrayAdapter;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.row_type, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {

        viewHolder.type.setText(adapter.getItem(position));


    }

    @Override
    public int getItemCount() {
        return adapter.getCount();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView type;

        public ViewHolder(View view) {
            super(view);
            type = (TextView) view.findViewById(R.id.tv_type);
        }
    }
}