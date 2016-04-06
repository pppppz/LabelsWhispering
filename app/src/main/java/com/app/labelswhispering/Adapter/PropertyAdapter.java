package com.app.labelswhispering.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import com.app.labelswhispering.Model.Medicine;
import com.app.labelswhispering.R;

import java.util.List;
import java.util.Locale;

public class PropertyAdapter extends RecyclerView.Adapter<PropertyAdapter.ViewHolder> {

    private List<Medicine> currentList;
    private Context mContext;


    public PropertyAdapter(Context context, List<Medicine> dataset) {
        currentList = dataset;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.view_property, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {

        Medicine medicine = currentList.get(position);
        // set data in property
        int type_number = medicine.getType();
        String locale_lang = Locale.getDefault().getDisplayLanguage();
        String[] type_items;
        if (locale_lang.equals("th") || locale_lang.equals(mContext.getString(R.string.thai))) {
            type_items = mContext.getResources().getStringArray(R.array.medicine_type_th);
        } else {
            type_items = mContext.getResources().getStringArray(R.array.medicine_type);
        }
        String Type = type_items[type_number];
        viewHolder.tvType.setText(Type);

        String property = medicine.getUseFor();
        viewHolder.tvProperty.setText(property);
        viewHolder.cbBefore.setChecked(medicine.isBeforeMeal());
        viewHolder.cbAfter.setChecked(medicine.isAfterMeal());
        viewHolder.cbMorning.setChecked(medicine.isMorning());
        viewHolder.cbNoon.setChecked(medicine.isAfterNoon());
        viewHolder.cbEvening.setChecked(medicine.isEvening());
        viewHolder.cbBedtime.setChecked(medicine.isBedtime());


    }

    @Override
    public int getItemCount() {
        return currentList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvType, tvProperty;
        public CheckBox cbBefore, cbAfter, cbMorning, cbNoon, cbEvening, cbBedtime;
        public ImageButton btnTTS;


        public ViewHolder(View view) {
            super(view);

            tvType = (TextView) view.findViewById(R.id.tvType);
            tvProperty = (TextView) view.findViewById(R.id.tvProperty);
            cbBefore = (CheckBox) view.findViewById(R.id.cb_beforeMeal_pf);
            cbAfter = (CheckBox) view.findViewById(R.id.cb_afterMeal_pf);
            cbMorning = (CheckBox) view.findViewById(R.id.cb_Morning_pf);
            cbNoon = (CheckBox) view.findViewById(R.id.cb_Noon_pf);
            cbEvening = (CheckBox) view.findViewById(R.id.cb_Evening_pf);
            cbBedtime = (CheckBox) view.findViewById(R.id.cb_Bedtime_pf);
            btnTTS = (ImageButton) view.findViewById(R.id.btnTTS);
        }
    }


}