package com.app.labelswhispering;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.app.labelswhispering.Function.isNetworkConnected;

public class ScannerFragmentActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        if (!new isNetworkConnected(this).CheckNow()) {
            Toast.makeText(this, R.string.please_check_your_connection, Toast.LENGTH_LONG).show();
            finish();
        }
        setContentView(R.layout.barcode_scanner);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_scan_barcode);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.barcode_scanner, menu);
        return true;
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


}