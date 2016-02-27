package com.app.labelswhispering;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.app.labelswhispering.Model.Barcode;
import com.app.labelswhispering.Model.Medicine;
import com.app.labelswhispering.Model.Pictures;
import com.app.labelswhispering.Model.Report;
import com.app.labelswhispering.Model.Schedule;
import com.facebook.FacebookSdk;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseTwitterUtils;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        //register class use in project such as this project use table Car in parse also register subclass Car (in Project)
        ParseObject.registerSubclass(Schedule.class);
        ParseObject.registerSubclass(Medicine.class);
        ParseObject.registerSubclass(Barcode.class);
        ParseObject.registerSubclass(Pictures.class);
        ParseObject.registerSubclass(Report.class);

        // start initial parse with key
        Parse.initialize(this, "3YHOygynehOQS9Bgo0epWIL9wqEYgz4ov9rkD9XE", "gckBS2Ef2Jp7XZBCX86XkKTEn8AqvOLgjUW2pbO8");

        //initial facebook
        FacebookSdk.sdkInitialize(getApplicationContext());
        ParseFacebookUtils.initialize(this);

        // initial twitter key
        ParseTwitterUtils.initialize("vwIn54lXsNWD7mYqP0jPZ3uGD", "PvwOPuVKDTYkMgLJxfooinXwvi9BfEo2OZ5e1zPvrXpP8NXpRR");


    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}