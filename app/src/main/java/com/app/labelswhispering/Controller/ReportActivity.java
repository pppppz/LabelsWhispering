package com.app.labelswhispering.Controller;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.labelswhispering.Model.Report;
import com.app.labelswhispering.R;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class ReportActivity extends AppCompatActivity {
    private String TAG = ReportActivity.class.getSimpleName();
    private EditText edReportProblem;
    private TextView tvCountChar;
    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            int finalCal = 500 - (edReportProblem.getText().toString().length());
            tvCountChar.setText(finalCal + getString(R.string.characters_left));
            tvCountChar.setTextColor(getResources().getColor(R.color.warning_color));

        }
    };
    private LinearLayout rootReport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_report);

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
        rootReport = (LinearLayout) findViewById(R.id.ll_report);

        tvCountChar = (TextView) findViewById(R.id.tvCountChar);
        edReportProblem = (EditText) findViewById(R.id.etReportProblem);
        edReportProblem.addTextChangedListener(textWatcher);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu_48; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.report, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.menu_report:
                reportToServer();
                break;
            case android.R.id.home:
                this.onBackPressed();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void reportToServer() {

        int length = edReportProblem.getText().length();
        if (length > 0 || length <= 500) {

            /** get data from task_input and set data by method in Current.class **/
            Report report = new Report();
            report.setUser(ParseUser.getCurrentUser());
            report.setMessage(edReportProblem.getText().toString());
            report.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Snackbar.make(MainActivity.rootLayout, R.string.thank_for_feedback, Snackbar.LENGTH_LONG).show();
                        finish();
                    } else {
                        Log.e(TAG, "error save report : " + e.getLocalizedMessage());
                    }
                }
            });
        } else {
            Snackbar.make(rootReport, R.string.please_check_length_message, Snackbar.LENGTH_LONG).show();
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}