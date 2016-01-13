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


public class HowToTake_Fragment extends Fragment {
    public static TextView tvHowToTake, tvIfForget;
    private static List<Medicine> list = new ArrayList<>();
    private String TAG = HowToTake_Fragment.class.getSimpleName();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.how_to_take_fragment, container, false);
        tvHowToTake = (TextView) view.findViewById(R.id.tvHowTake);
        tvIfForget = (TextView) view.findViewById(R.id.tvIfForget);

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

    private void setHowToTakeData() {
        JSONArray dataHowTake = list.get(0).getHowTake();

        if (dataHowTake != null) {
            int howTake_Length = dataHowTake.length();
            if (howTake_Length != 0) {
                for (int i = 0; i < howTake_Length; i++) {
                    try {
                        tvHowToTake.append("-" + dataHowTake.get(i).toString() + "\n");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        JSONArray dataIfForget = list.get(0).getIfForget();
        if (dataIfForget != null) {
            int IfForget_Lenght = dataIfForget.length();
            if (IfForget_Lenght != 0) {
                for (int i = 0; i < IfForget_Lenght; i++) {
                    try {
                        tvIfForget.append("-" + dataIfForget.get(i).toString() + "\n");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
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
                } else {
                    Log.e("HowToTakeData", "ParseException : " + String.valueOf(error));
                }
                setHowToTakeData();
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
                    Log.e("HowToTakeData", "ParseException : " + String.valueOf(error));
                }
                setHowToTakeData();
            }
        });
    }

}



