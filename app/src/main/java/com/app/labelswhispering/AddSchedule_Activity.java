package com.app.labelswhispering;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.labelswhispering.Model.Schedule;
import com.parse.ParseException;
import com.parse.SaveCallback;

import java.util.Locale;

public class AddSchedule_Activity extends AppCompatActivity {

    private final String TAG = AddSchedule_Activity.class.getSimpleName();
    private SwitchCompat switch_alert;
    private CheckBox Morning, Noon, Evening, Bedtime, beforeMeal, afterMeal;
    private EditText editText_NameMedicine, editTextAmount;
    private TextView tvMedicineType;
    private ArrayAdapter<CharSequence> adapter;
    private CoordinatorLayout rootLayout;
    private ProgressBar progressBar;
    private int type_medicine_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_schedule_fm);

        String locale_lang = Locale.getDefault().getDisplayLanguage();
        if (locale_lang.equals("th") || locale_lang.equals(getString(R.string.thai))) {
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

        RelativeLayout linearLayoutCompat_MedicineType = (RelativeLayout) findViewById(R.id.ll_medicine_type);
        linearLayoutCompat_MedicineType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AddSchedule_Activity.this);
                builder.setTitle(getString(R.string.choose_your_medicine_type));
                builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {

                        // Intent intent = null;
                        // intent = new Intent(MainActivity.this, SearchActivity.class);
                        tvMedicineType.setText(adapter.getItem(item));
                        type_medicine_id = item;
                        // startActivity(intent);
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
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
            s.setType(type_medicine_id);
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


