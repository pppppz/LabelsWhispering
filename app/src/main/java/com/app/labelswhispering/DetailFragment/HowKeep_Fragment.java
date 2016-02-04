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


public class HowKeep_Fragment extends Fragment {

    private TextView title, content;
    private String TAG = HowKeep_Fragment.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.how_to_take_fragment, container, false);
        title = (TextView) view.findViewById(R.id.tvTitle);
        content = (TextView) view.findViewById(R.id.tvContent);
        ImageButton button = (ImageButton) view.findViewById(R.id.btnTTS);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Main_Medicine_Details_Activity.speakOut(title.getText().toString() + content.getText().toString());
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

        String data = Main_Medicine_Details_Activity.medicineList.get(0).getHowKeep();
        title.setText(R.string.how_to_keep);
        content.setText(data);
        Main_Medicine_Details_Activity.progressBar_Medicine_Detail.setVisibility(View.INVISIBLE);

       /* for (int i = 0 ; i < data.length() ; i++){
            try {
                content.append(data.get(i).toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }*/
    }
}