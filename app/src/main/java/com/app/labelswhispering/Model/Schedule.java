package com.app.labelswhispering.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Schedule")
public class Schedule extends ParseObject {


    public Schedule() {
    }


    public String getName() {
        return getString("name");
    }

    public void setName(String name) {
        put("name", name);
    }


    public String getUser() {
        return getString("user");
    }

    public void setUser(ParseUser userId) {
        put("user", userId);
    }


    public int getAmount() {
        return getInt("amount");
    }

    public void setAmount(int amount) {
        put("amount", amount);
    }

    public boolean isAfterMeal() {
        return getBoolean("afterMeal");
    }

    public void setAfterMeal(boolean afterMeal) {
        put("afterMeal", afterMeal);
    }

    public boolean isBeforeMeal() {
        return getBoolean("beforeMeal");
    }

    public void setBeforeMeal(boolean beforeMeal) {
        put("beforeMeal", beforeMeal);
    }

    public boolean isMorning() {
        return getBoolean("morning");
    }

    public void setMorning(boolean morning) {
        put("morning", morning);
    }

    public boolean isNoon() {
        return getBoolean("noon");
    }

    public void setNoon(boolean noon) {
        put("noon", noon);
    }

    public boolean isEvening() {
        return getBoolean("evening");
    }

    public void setEvening(boolean evening) {
        put("evening", evening);
    }

    public boolean isBedtime() {
        return getBoolean("bedtime");
    }

    public void setBedtime(boolean bedtime) {
        put("bedtime", bedtime);
    }

    public int getType() {
        return getInt("Type");
    }

    public void setType(int Type) {
        put("Type", Type);
    }

    public boolean isAlert() {
        return getBoolean("Alert");
    }

    public void setAlert(boolean alert) {
        put("Alert", alert);
    }


    public String getMedicineId() {
        return getString("medicineId");
    }

    public void setMedicineId(String medicineId) {
        put("medicineId", medicineId);
    }
}
