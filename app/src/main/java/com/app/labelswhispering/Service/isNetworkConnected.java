package com.app.labelswhispering.Service;


import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;

public class isNetworkConnected {
    Activity activity;

    public isNetworkConnected(Activity activity) {
        this.activity = activity;
    }

    public boolean CheckNow() {
        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }
}
