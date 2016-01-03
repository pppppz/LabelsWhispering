package com.app.labelswhispering.DetailFragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.labelswhispering.MedicineDetail_Activity;
import com.app.labelswhispering.Model.Medicine;
import com.app.labelswhispering.R;
import com.app.labelswhispering.Service.isNetworkConnected;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;


public class SideEffect_Fragment extends Fragment {

    public static TextView tvGeneralSymptom, tvBadSymptom;
    private static List<Medicine> list = new ArrayList<>();
    private String TAG = SideEffect_Fragment.class.getSimpleName();

    public static void setSideEffectData() {

        String general = list.get(0).getGeneralSymptom();
        String bad = list.get(0).getBadSymptom();
        if (general != null) {
            tvGeneralSymptom.setText(general);
            tvBadSymptom.setText(bad);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sideeffect_fragment, container, false);
        tvGeneralSymptom = (TextView) view.findViewById(R.id.tvGeneralSymptom);
        tvBadSymptom = (TextView) view.findViewById(R.id.tvBadSymptom);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (new isNetworkConnected(getActivity()).CheckNow()) {
            indicateMedicineData(MedicineDetail_Activity.medicineID);
        } else {
            indicateMedicineDataOffline(MedicineDetail_Activity.medicineID);
        }

    }

    private void indicateMedicineData(String medicineID) {

        //pattern query
        ParseQuery<Medicine> query = ParseQuery.getQuery(Medicine.class);
        query.whereEqualTo("objectId", medicineID);
        query.addDescendingOrder("createdAt");
        query.findInBackground(new FindCallback<Medicine>() {
            @Override
            public void done(List<Medicine> items, ParseException error) {
                //check if list task have some data = clear
                list.clear();
                if (error == null) {

                    for (Medicine medicine : items) {
                        list.add(medicine);
                    }
                    // medicineList.add(items.get(0));
                    //Log.e(TAG , medicineList.get(0).getName());
                } else {
                    Log.e("SideEffect", "ParseException : " + String.valueOf(error));
                }
                setSideEffectData();

            }
        });
    }

    private void indicateMedicineDataOffline(String medicineID) {

        //pattern query
        ParseQuery<Medicine> query = ParseQuery.getQuery(Medicine.class);
        query.whereEqualTo("objectId", medicineID);
        query.addDescendingOrder("createdAt");
        query.fromLocalDatastore();
        query.findInBackground(new FindCallback<Medicine>() {
            @Override
            public void done(List<Medicine> items, ParseException error) {
                //check if list task have some data = clear
                list.clear();
                if (error == null) {

                    for (Medicine medicine : items) {
                        list.add(medicine);
                    }
                } else {
                    Log.e("SideEffect", "ParseException : " + String.valueOf(error));
                }
                setSideEffectData();

            }
        });
    }
}