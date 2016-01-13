package com.app.labelswhispering;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

public class Detail_Schedule extends AppCompatActivity {

    private String scheduleId;
    private String TAG = Detail_Schedule.class.getSimpleName();
    private TextView tvTestId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail__schedule);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        tvTestId = (TextView) findViewById(R.id.testId);
        GetMedicineID();
    }

    private void GetMedicineID() {
        Intent intent = getIntent();
        scheduleId = intent.getStringExtra("scheduleId");
        Log.e(TAG, "ObjectID : " + scheduleId);
        tvTestId.setText(scheduleId);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
