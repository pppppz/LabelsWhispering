package com.app.labelswhispering;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.app.labelswhispering.Model.Medicine;
import com.app.labelswhispering.Model.Schedule;
import com.parse.FindCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;


public class AddSchedule_Activity extends AppCompatActivity {

    private static final String TAG = AddSchedule_Activity.class.getSimpleName();
    private Spinner amount, lunch;
    private EditText AmountED;
    private CheckBox Morning, Noon, Evening, Bedtime;
    private List<Medicine> medicineList = new ArrayList<>();
    private AppCompatAutoCompleteTextView actv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_schedule_fm);

        //Finally, let's add the Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.add_schedule_toolbar);
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
        makeUI();

    }


    private void makeUI() {

        //lunch spinner
        lunch = (Spinner) findViewById(R.id.spinner_lunch);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.lunch, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lunch.setAdapter(adapter);

        //type amount spinner
        amount = (Spinner) findViewById(R.id.spinner_amounttype);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.amount, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        amount.setAdapter(adapter2);


        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radio_group_type);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
            }
        });
        AmountED = (EditText) findViewById(R.id.ed_amount);
        Morning = (CheckBox) findViewById(R.id.cb_morning);
        Noon = (CheckBox) findViewById(R.id.cb_noon);
        Evening = (CheckBox) findViewById(R.id.cb_evening);
        Bedtime = (CheckBox) findViewById(R.id.cb_bedtime);


        String[] test = {"read", "red", "road"};
        //Creating the instance of ArrayAdapter containing list of language names
        ArrayAdapter<String> adapter_autocomplete = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, test);
        //Getting the instance of AutoCompleteTextView
        actv = (AppCompatAutoCompleteTextView) findViewById(R.id.ac_nameMedicine);
        actv.setThreshold(1);//will start working from first character
        actv.setAdapter(adapter_autocomplete);//setting the adapter data into the AutoCompleteTextView
        actv.setTextColor(Color.RED);


    }


    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.radio_tablet:
                if (checked)

                    Log.e(TAG, "radio tablet click");
                //liquid.toggle();
                break;
            case R.id.radio_liquid:
                if (checked)
                    //tablet.toggle();
                    Log.e(TAG, "radio liquid click");
                break;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.save_menu, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

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
        queryAutoComplete();
    }


    private void SaveSchedule() {

        if (actv.getText().length() > 0) {

            int amount_to_save = 0;

            /**  Set up a progress dialog **/
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage("updating please wait");
            dialog.show();

            /** get data from task_input and set data by method in Current.class **/
            Schedule s = new Schedule();
            s.setACL(new ParseACL(ParseUser.getCurrentUser()));
            s.setUser(ParseUser.getCurrentUser());
            s.setName(actv.getText().toString());

            if (AmountED.getText() == null) {
                amount_to_save = Integer.parseInt(AmountED.getText().toString());
            }

            s.setAmount(amount_to_save);
            s.setMorning(Morning.isChecked());
            s.setNoon(Noon.isChecked());
            s.setEvening(Evening.isChecked());
            s.setBedtime(Bedtime.isChecked());

            if (lunch.getSelectedItemId() == 0) {
                s.setLunch(true);
            } else {
                s.setLunch(false);
            }
            s.setAmount(amount.getSelectedItemPosition() + 1);
            s.pinInBackground();

            s.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {

                    /** hidden soft keyboard before swap fragment **/
                    InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    dialog.dismiss();


                    /** finish this class and swap to Main class **/
                    finish();
                }
            });
        }

    }

    private void queryAutoComplete() {

        //pattern query
        ParseQuery<Medicine> query = ParseQuery.getQuery(Medicine.class);
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        query.addDescendingOrder("createdAt");
        query.findInBackground(new FindCallback<Medicine>() {
            @Override
            public void done(List<Medicine> items, ParseException error) {
                //check if list task have some data = clear
                medicineList.clear();
                if (error == null) {
                    for (int i = 0; i < items.size(); i++) {
                        medicineList.add(items.get(i));
                    }
                } else {
                    Log.e("ParseException : ", String.valueOf(error));
                }


            }
        });

    }

}