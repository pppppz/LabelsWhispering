package com.app.labelswhispering.DetailFragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.app.labelswhispering.R;


public class Property_Fragment extends Fragment {

    private TextView tvTradeName, tvType, tvProperty;
    private String TAG = Property_Fragment.class.getSimpleName();

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.property_fragment, container, false);
        tvType = (TextView) view.findViewById(R.id.tvType);
        tvTradeName = (TextView) view.findViewById(R.id.tvTradeName);
        tvProperty = (TextView) view.findViewById(R.id.tvProperty);
        ImageButton btnTTS = (ImageButton) view.findViewById(R.id.btnTTS);
        btnTTS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Main_Medicine_Details_Activity.speakOut(getString(R.string.type) + tvType.getText() + getString(R.string.properties) + tvProperty.getText().toString());
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setDataToUI();
        Log.e(TAG, "set");
    }

    private void setDataToUI() {
        Main_Medicine_Details_Activity.progressBar_Medicine_Detail.setVisibility(View.VISIBLE);

        // set data in property
        String Type;
        switch (Main_Medicine_Details_Activity.medicineList.get(0).getType()) {
            case 0:
                Type = "Liquid/Syrup";
                break;
            case 1:
                Type = "Tablet";
                break;
            case 2:
                Type = "Capsule";
                break;
            case 3:
                Type = "Lozenges";
                break;
            case 4:
                Type = "Cream/Ointment";
                break;
            case 5:
                Type = "Drops";
                break;
            case 6:
                Type = "Spay";
                break;
            default:
                Type = "null";
                break;
        }

        tvType.setText(Type);

        String property = Main_Medicine_Details_Activity.medicineList.get(0).getUseFor();
        tvProperty.setText(property);
        Main_Medicine_Details_Activity.progressBar_Medicine_Detail.setVisibility(View.INVISIBLE);
           /* JSONArray property = MedicineDetail_Activity.medicineList.get(0).getUseFor();
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
            }*/
        }
    }
