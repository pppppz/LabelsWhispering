package com.app.labelswhispering;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.app.labelswhispering.Adapter.TypeAdapter;
import com.app.labelswhispering.Function.DividerItemDecoration;
import com.app.labelswhispering.Listener.RecyclerItemClickListener;
import com.app.labelswhispering.Model.Schedule;
import com.parse.ParseException;
import com.parse.SaveCallback;

import java.util.Locale;

public class AddSchedule_Activity extends AppCompatActivity {

    private static final String TAG = AddSchedule_Activity.class.getSimpleName();
    private SwitchCompat switch_alert;
    private CheckBox Morning, Noon, Evening, Bedtime, beforeMeal, afterMeal;
    private EditText editText_NameMedicine, editTextAmount;
    private int popup_type_medicine;
    private TextView tvMedicineType;
    private ArrayAdapter<CharSequence> adapter;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private CoordinatorLayout rootLayout;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_schedule_fm);

        String locale_lang = Locale.getDefault().getDisplayLanguage();
        if (locale_lang.equals("th") || locale_lang.equals("???")) {
            adapter = ArrayAdapter.createFromResource(getBaseContext(), R.array.medicine_type_th, android.R.layout.simple_spinner_item);
        } else {
            adapter = ArrayAdapter.createFromResource(getBaseContext(), R.array.medicine_type, android.R.layout.simple_spinner_item);
        }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        makeUI();
        setDataToUI();


    }


    private void makeUI() {
        //Finally, let's add the Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.schedule_toolbar);
        setSupportActionBar(toolbar);


        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        rootLayout = (CoordinatorLayout) findViewById(R.id.rootLayout_schedule_detail);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar_schedule);

        editText_NameMedicine = (EditText) findViewById(R.id.et_nameMedicine);
        editTextAmount = (EditText) findViewById(R.id.ed_amount);

        tvMedicineType = (TextView) findViewById(R.id.txtMedicineType);


        Morning = (CheckBox) findViewById(R.id.cb_morning);
        Noon = (CheckBox) findViewById(R.id.cb_noon);
        Evening = (CheckBox) findViewById(R.id.cb_evening);
        Bedtime = (CheckBox) findViewById(R.id.cb_bedtime);

        switch_alert = (SwitchCompat) findViewById(R.id.switch_alert);

        beforeMeal = (CheckBox) findViewById(R.id.checkBox_BeforeMeal);
        afterMeal = (CheckBox) findViewById(R.id.checkBox_afterMeal);

        LinearLayoutCompat linearLayoutCompat_MedicineType = (LinearLayoutCompat) findViewById(R.id.ll_medicine_type);
        linearLayoutCompat_MedicineType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                final View popupView = layoutInflater.inflate(R.layout.popup_medicine_type, null);

                final PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                mRecyclerView = (RecyclerView) popupView.findViewById(R.id.recycler_view_type);
                mRecyclerView.addItemDecoration(new DividerItemDecoration(getBaseContext(), R.drawable.divider));

                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false);
                mRecyclerView.setLayoutManager(mLayoutManager);

                /** create adapter for put on array from class schedule **/
                mAdapter = new TypeAdapter(getBaseContext(), adapter);
                mRecyclerView.setAdapter(mAdapter);
                mRecyclerView.setHasFixedSize(true);
                mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getBaseContext(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        popup_type_medicine = position;
                        Log.e(TAG, "type medicine " + popup_type_medicine);
                        tvMedicineType.setText(adapter.getItem(popup_type_medicine));
                        popupWindow.dismiss();
                    }
                }));
                popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
            }

        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_schedule, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_save:
                SaveSchedule();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onResume() {
        super.onResume();
    }


    private void SaveSchedule() {

        Schedule s = new Schedule();
        if (editText_NameMedicine.getText() != null) {
            s.setName(String.valueOf(editText_NameMedicine.getText()));
            s.setAlert(switch_alert.isChecked());
            s.setAmount(Integer.parseInt(editTextAmount.getText().toString()));
            s.setMorning(Morning.isChecked());
            s.setNoon(Noon.isChecked());
            s.setEvening(Evening.isChecked());
            s.setBedtime(Bedtime.isChecked());
            s.setBeforeMeal(beforeMeal.isChecked());
            s.setAfterMeal(afterMeal.isChecked());
            s.setType(popup_type_medicine);
            s.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Snackbar.make(rootLayout, R.string.schedule_saved, Snackbar.LENGTH_LONG).show();
                        finish();
                    } else {
                        Log.e(TAG, "save error " + e.getLocalizedMessage());
                    }

                }
            });
        } else {
            Snackbar.make(rootLayout, R.string.please_check_again, Snackbar.LENGTH_LONG).show();
        }

    }

    private void setDataToUI() {
        tvMedicineType.setText(adapter.getItem(0));
        switch_alert.setChecked(true);
        progressBar.setVisibility(View.INVISIBLE);
    }


}


