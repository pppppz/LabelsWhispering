package com.app.labelswhispering.detail_fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.app.labelswhispering.R;


public class HowToTake_Fragment extends Fragment {
    private TextView tvContent, tvTitle;
    private String TAG = HowToTake_Fragment.class.getSimpleName();

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.how_to_take_fragment, container, false);
        tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        tvTitle.setText(R.string.how_to_take);
        tvContent = (TextView) view.findViewById(R.id.tvContent);
        ImageButton button = (ImageButton) view.findViewById(R.id.btnTTS);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Main_Medicine_Details_Activity.speakOut(tvTitle.getText().toString() + tvContent.getText().toString());
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
        String dataHowTake = Main_Medicine_Details_Activity.medicineList.get(0).getHowTake();
        tvContent.setText(dataHowTake);
        Main_Medicine_Details_Activity.progressBar_Medicine_Detail.setVisibility(View.INVISIBLE);

        /*if (dataHowTake != null) {
            int howTake_Length = dataHowTake.length();
            if (howTake_Length != 0) {
                for (int i = 0; i < howTake_Length; i++) {
                    try {
                        tvContent.append("-" + dataHowTake.get(i).toString() + "\n");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }*/
    }

}



