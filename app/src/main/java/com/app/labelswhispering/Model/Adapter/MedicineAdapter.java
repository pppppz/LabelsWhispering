package com.app.labelswhispering.model.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.labelswhispering.R;
import com.app.labelswhispering.model.Medicine;

import java.util.List;

public class MedicineAdapter extends RecyclerView.Adapter<MedicineAdapter.ViewHolder> {

    private List<Medicine> currentList;
    private Context mContext;


    public MedicineAdapter(Context context, List<Medicine> dataset) {
        currentList = dataset;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.row_medicine, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {

        Medicine medicine = currentList.get(position);
        viewHolder.Name.setText(medicine.getName());

    }

    @Override
    public int getItemCount() {
        return currentList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView Name;

        public ViewHolder(View view) {
            super(view);
            Name = (TextView) view.findViewById(R.id.medicineName);
        }
    }


}