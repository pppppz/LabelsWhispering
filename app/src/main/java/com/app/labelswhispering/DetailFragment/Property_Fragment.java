package com.app.labelswhispering.DetailFragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.labelswhispering.Function.isNetworkConnected;
import com.app.labelswhispering.MedicineDetail_Activity;
import com.app.labelswhispering.Model.Medicine;
import com.app.labelswhispering.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;


public class Property_Fragment extends Fragment {

    private static TextView tvName, tvTradeName, tvType, tvProperty;
    private static List<Medicine> medicineList = new ArrayList<>();
    private String TAG = Property_Fragment.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.property_fragment, container, false);
        tvName = (TextView) view.findViewById(R.id.tvMedicineName);
        tvType = (TextView) view.findViewById(R.id.tvType);
        tvTradeName = (TextView) view.findViewById(R.id.tvTradeName);
        tvProperty = (TextView) view.findViewById(R.id.tvProperty);

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
                medicineList.clear();
                if (error == null) {
                    for (Medicine medicine : items) {
                        medicineList.add(medicine);
                    }
                } else {
                    Log.e(TAG, "ParseException : " + String.valueOf(error));
                }
                setPropertyDataToUI();
            }
        });
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
                medicineList.clear();
                if (error == null) {
                    for (Medicine medicine : items) {
                        medicineList.add(medicine);
                    }
                    //Log.e(TAG , medicineList.get(0).getName());
                } else {
                    Log.e(TAG, "ParseException : " + String.valueOf(error));
                }
                setPropertyDataToUI();
            }
        });
    }

    private void setPropertyDataToUI() {

        // set data in property
        if (medicineList.get(0).getName() != null) {
            tvName.setText(medicineList.get(0).getName());
        }
        if (medicineList.get(0).getType() != null) {
            tvType.setText(medicineList.get(0).getType());
        }
        JSONArray property = medicineList.get(0).getUseFor();
        if (property != null) {
            int length = property.length();
            if (length != 0) {
                for (int i = 0; i < length; i++) {
                    try {
                        if (tvProperty != null) {
                            tvProperty.append("-" + property.get(i).toString() + "\n");
                        } else {
                            Log.e(TAG, property.get(i).toString());
                        }
                    } catch (JSONException e) {
                        Log.e("Property_Fragment", e.toString());
                    }
                }
            }
        } else {
            Log.e(TAG, "null");
        }

    }

}
