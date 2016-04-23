package com.app.labelswhispering.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Report")
public class Report extends ParseObject {
    public Report() {

    }

    public String getMesssage() {
        return getString("Message");
    }

    public void setMessage(String message) {
        put("Message", message);
    }


    public void setUser(ParseUser user) {
        put("User", user);
    }
}
