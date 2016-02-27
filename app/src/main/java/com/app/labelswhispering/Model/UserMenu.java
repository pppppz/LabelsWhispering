package com.app.labelswhispering.Model;

import android.content.Context;

public class UserMenu {

    private int string_id;
    private int icon;
    private String count = "0";
    // boolean to set visiblity of the counter
    private boolean isCounterVisible = false;
    private Context context;

    public UserMenu() {
    }

    public UserMenu(Context context, int id, int icon) {
        this.string_id = id;
        this.icon = icon;
        this.context = context;
    }

  /*  public UserMenu(int title, int icon, boolean isCounterVisible, String count) {
        this.title = title;
        this.icon = icon;
        this.isCounterVisible = isCounterVisible;
        this.count = count;
    }*/

    public String getTitle() {
        return context.getResources().getString(string_id);
    }
/*
    public void setTitle(String title) {
        this.title = title;
    }*/

    public int getIcon() {
        return this.icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getCount() {
        return this.count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public boolean getCounterVisibility() {
        return this.isCounterVisible;
    }

    public void setCounterVisibility(boolean isCounterVisible) {
        this.isCounterVisible = isCounterVisible;
    }
}
