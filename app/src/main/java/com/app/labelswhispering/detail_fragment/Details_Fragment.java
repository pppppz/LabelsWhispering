package com.app.labelswhispering.detail_fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.andexert.expandablelayout.library.ExpandableLayoutListView;
import com.app.labelswhispering.R;

import java.util.Locale;


public class Details_Fragment extends Fragment {
    private final String[] array = {"Hello", "World", "Android", "is", "Awesome", "World", "Android", "is", "Awesome", "World", "Android", "is", "Awesome", "World", "Android", "is", "Awesome"};
    private TextView tvTradeName, tvType, tvProperty;
    private String TAG = Details_Fragment.class.getSimpleName();
    private CheckBox cbBefore, cbAfter, cbMorning, cbNoon, cbEvening, cbBedtime;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.detail_fragment, container, false);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), R.layout.view_row, R.id.header_text, array);
        final ExpandableLayoutListView expandableLayoutListView = (ExpandableLayoutListView) view.findViewById(R.id.listview);

        expandableLayoutListView.setAdapter(arrayAdapter);




       /* tvType = (TextView) view.findViewById(R.id.tvType);
        tvTradeName = (TextView) view.findViewById(R.id.tvTradeName);
        tvProperty = (TextView) view.findViewById(R.id.tvProperty);
        cbBefore = (CheckBox) view.findViewById(R.id.cb_beforeMeal_pf);
        cbAfter = (CheckBox) view.findViewById(R.id.cb_afterMeal_pf);
        cbMorning = (CheckBox) view.findViewById(R.id.cb_Morning_pf);
        cbNoon = (CheckBox) view.findViewById(R.id.cb_Noon_pf);
        cbEvening = (CheckBox) view.findViewById(R.id.cb_Evening_pf);
        cbBedtime = (CheckBox) view.findViewById(R.id.cb_Bedtime_pf);
        ImageButton btnTTS = (ImageButton) view.findViewById(R.id.btnTTS);
        btnTTS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Main_Medicine_Details_Activity.speakOut(getString(R.string.type) + tvType.getText() + getString(R.string.properties) + tvProperty.getText().toString());
            }
        });
*/
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //setDataToUI();
    }


    private void checkLanguage() {
        Locale locale;
        String locale_lang = Locale.getDefault().getDisplayLanguage();
        Log.e(TAG, "Lang : " + locale_lang);
        if (locale_lang.equals("th") || locale_lang.equals(getString(R.string.thai))) {
            locale = new Locale("th", "TH");
        } else {
            locale = Locale.US;
        }
        Locale.setDefault(locale);
    }

    private void setDataToUI() {
        Main_Medicine_Details_Activity.progressBar_Medicine_Detail.setVisibility(View.VISIBLE);

        // set data in property
        int type_number = Main_Medicine_Details_Activity.medicineList.get(0).getType();
        String locale_lang = Locale.getDefault().getDisplayLanguage();
        String[] type_items;
        if (locale_lang.equals("th") || locale_lang.equals(getString(R.string.thai))) {
            type_items = getResources().getStringArray(R.array.medicine_type_th);
        } else {
            type_items = getResources().getStringArray(R.array.medicine_type);
        }
        String Type = type_items[type_number];
        tvType.setText(Type);

        String property = Main_Medicine_Details_Activity.medicineList.get(0).getUseFor();
        tvProperty.setText(property);
        Main_Medicine_Details_Activity.progressBar_Medicine_Detail.setVisibility(View.INVISIBLE);

        cbBefore.setChecked(Main_Medicine_Details_Activity.medicineList.get(0).isBeforeMeal());
        cbAfter.setChecked(Main_Medicine_Details_Activity.medicineList.get(0).isAfterMeal());
        cbMorning.setChecked(Main_Medicine_Details_Activity.medicineList.get(0).isMorning());
        cbNoon.setChecked(Main_Medicine_Details_Activity.medicineList.get(0).isAfterNoon());
        cbEvening.setChecked(Main_Medicine_Details_Activity.medicineList.get(0).isEvening());
        cbBedtime.setChecked(Main_Medicine_Details_Activity.medicineList.get(0).isBedtime());
    }
}
