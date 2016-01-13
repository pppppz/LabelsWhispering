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

    public String getType() {
        return getString("Type");
    }

    public void setType(String type) {
        put("Type", type);
    }

    public String getBadSymptom() {
        return getString("badSymptom");
    }

    public void setBadSymptom(String badSymptom) {
        put("badSymptom", badSymptom);
    }

    public String getGeneralSymptom() {
        return getString("generalSymptom");
    }

    public void setGeneralSymptom(String generalSymptom) {
        put("generalSymptom", generalSymptom);
    }

    public JSONArray getUseFor() {
        return getJSONArray("UseFor");
    }

    public void setUseFor(JSONArray useFor) {
        put("UseFor", useFor);
    }

    public JSONArray getTellDoctor() {
        return getJSONArray("tellDoctor");
    }

    public void setTellDoctor(JSONArray tellDoctor) {
        put("tellDoctor", tellDoctor);
    }

    public JSONArray getIfForget() {
        return getJSONArray("ifForget");
    }

    public void setIfForget(JSONArray ifForget) {
        put("ifForget", ifForget);
    }

    public JSONArray getHowKeep() {
        return getJSONArray("howKeep");
    }

    public void setHowKeep(JSONArray howKeep) {
        put("howKeep", howKeep);
    }

    public JSONArray getHowTake() {
        return getJSONArray("HowTake");
    }

    public void setHowTake(JSONArray howTake) {
        put("HowTake", howTake);
    }

    public int getAmount() {
        return getInt("Amount");
    }

    public void setAmount(int amount) {
        put("Amount", amount);
    }


}
