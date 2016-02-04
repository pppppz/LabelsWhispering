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


public class If_Forget_Fragment extends Fragment {
    private TextView tvContent;
    private TextView tvHeader;
    private String TAG = If_Forget_Fragment.class.getSimpleName();


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.how_to_take_fragment, container, false);
        tvHeader = (TextView) view.findViewById(R.id.tvTitle);
        tvHeader.setText(R.string.if_forget);
        tvContent = (TextView) view.findViewById(R.id.tvContent);
        ImageButton button = (ImageButton) view.findViewById(R.id.btnTTS);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Main_Medicine_Details_Activity.speakOut(tvHeader.getText().toString() + tvContent.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }

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

        String dataIfForget = Main_Medicine_Details_Activity.medicineList.get(0).getIfForget();

        tvContent.setText(dataIfForget);
        Main_Medicine_Details_Activity.progressBar_Medicine_Detail.setVisibility(View.INVISIBLE);

        /*JSONArray dataIfForget = MedicineDetail_Activity.medicineList.get(0).getIfForget();
        if (dataIfForget != null) {
            int IfForget_Lenght = dataIfForget.length();
            if (IfForget_Lenght != 0) {
                for (int i = 0; i < IfForget_Lenght; i++) {
                    try {
                        tvContent.append("-" + dataIfForget.get(i).toString() + "\n");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }*/
    }
}



