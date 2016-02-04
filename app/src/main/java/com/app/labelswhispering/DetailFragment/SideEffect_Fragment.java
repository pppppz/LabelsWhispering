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


public class SideEffect_Fragment extends Fragment {

    private TextView tvGeneralSymptom, tvBadSymptom;
    private String TAG = SideEffect_Fragment.class.getSimpleName();

    private void setDataToUI() {
        Main_Medicine_Details_Activity.progressBar_Medicine_Detail.setVisibility(View.VISIBLE);
        String general = Main_Medicine_Details_Activity.medicineList.get(0).getGeneralSymptom();
        String bad = Main_Medicine_Details_Activity.medicineList.get(0).getBadSymptom();
        if (general != null) {
            tvGeneralSymptom.setText(general);
            tvBadSymptom.setText(bad);
        }
        Main_Medicine_Details_Activity.progressBar_Medicine_Detail.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        setDataToUI();
        Log.e(TAG, "set");
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sideeffect_fragment, container, false);
        tvGeneralSymptom = (TextView) view.findViewById(R.id.tvGeneralSymptom);
        tvBadSymptom = (TextView) view.findViewById(R.id.tvBadSymptom);
        ImageButton button = (ImageButton) view.findViewById(R.id.btnTTS_SideEffect);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Main_Medicine_Details_Activity.speakOut(getString(R.string.general_symptom) + tvGeneralSymptom.getText().toString() + getString(R.string.bad_symptom) + tvBadSymptom.getText().toString());
            }
        });
        return view;
    }
}