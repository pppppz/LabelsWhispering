package com.app.labelswhispering.Model;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import org.json.JSONArray;

@ParseClassName("Medicine")
public class Medicine extends ParseObject {

    private boolean beforeMeal, Morning, AfterNoon, Evening, Bedtime;
    private String Type, badSymptom, generalSymptom;

    private JSONArray UseFor, tellDoctor, ifForget, howKeep, HowTake;


    public Medicine() {

    }

    public String getName() {
        return getString("name");
    }

    public void setName(String name) {
        put("name", name);
    }


    public boolean isBeforeMeal() {
        return getBoolean("BeforeMeal");
    }

    public void setBeforeMeal(boolean beforeMeal) {
        put("BeforeMeal", beforeMeal);
    }

    public boolean isAfterMeal() {
        return getBoolean("AfterMeal");
    }

    public boolean isMorning() {
        return getBoolean("Morning");
    }

    public void setMorning(boolean morning) {
        put("Morning", morning);
    }

    public boolean isAfterNoon() {
        return getBoolean("AfterNoon");
    }

    public void setAfterNoon(boolean afterNoon) {
        put("AfterNoon", afterNoon);
    }

    public boolean isEvening() {
        return getBoolean("Evening");
    }

    public void setEvening(boolean evening) {
        put("Evening", evening);
    }

    public boolean isBedtime() {
        return getBoolean("Bedtime");
    }

    public void setBedtime(boolean bedtime) {
        put("Bedtime", bedtime);
    }

    public int getType() {
        return getInt("Type");
    }

    public void setType(String type) {
        put("Type", type);
    }

    public String getBadSymptom() {
        return getString("badSymptom");
    }

    public String getGeneralSymptom() {
        return getString("generalSymptom");
    }

    public String getUseFor() {
        return getString("UseFor");
    }

    public String getTellDoctor() {
        return getString("tellDoctor");
    }

    public String getIfForget() {
        return getString("ifForget");
    }

    public String getHowKeep() {
        return getString("howKeep");
    }


    public String getHowTake() {
        return getString("howTake");
    }

    public void setHowTake(String howTake) {
        put("howTake", howTake);
    }

    public int getAmount() {
        return getInt("Amount");
    }

    public void setAmount(int amount) {
        put("Amount", amount);
    }


}
