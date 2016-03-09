
package com.app.labelswhispering;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

public class main_search_fragment extends Fragment {
    private FragmentActivity fragmentActivity;
    LinearLayout.OnClickListener type_listener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(fragmentActivity, SearchActivity.class);
            startActivity(intent);
        }
    };
    LinearLayout.OnClickListener ocr_listener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(fragmentActivity, ScanOCR_Activity.class);
            startActivity(intent);
        }
    };
    LinearLayout.OnClickListener barcode_listener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(fragmentActivity, ScannerFragmentActivity.class);
            startActivity(intent);
        }
    };
    private LinearLayout ll_ocr, ll_search, ll_barcode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentActivity = getActivity();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_search_activity, container, false);
        ll_ocr = (LinearLayout) view.findViewById(R.id.btn_ll_ocr);
        ll_barcode = (LinearLayout) view.findViewById(R.id.btn_ll_barcode);
        ll_search = (LinearLayout) view.findViewById(R.id.btn_ll_type);

        ll_ocr.setOnClickListener(ocr_listener);
        ll_barcode.setOnClickListener(barcode_listener);
        ll_search.setOnClickListener(type_listener);


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}


