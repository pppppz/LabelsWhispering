package com.app.labelswhispering.Class;


import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;

public class isNetworkConnected {

 /*   public boolean isNetworkConnected(CheckScheduleService checkScheduleService) {
        ConnectivityManager cm = (ConnectivityManager) checkScheduleService.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }*/


   /* public isNetworkConnected(Activity activity) {
        this.activity = activity;
    }*/

    public boolean Check(Activity activity) {
        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }



    /*public boolean CheckNow() {
        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }*/
}
