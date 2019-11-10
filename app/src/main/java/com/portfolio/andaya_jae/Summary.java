package com.portfolio.andaya_jae;

import android.graphics.Color;

import java.util.ArrayList;

public class Summary {

    private ArrayList<Reading> readingList;
    private String userID;
    private Long systolic;
    private Long diastolic;
    private String condition;
    private int color;

    public Summary() {};

    public Summary(ArrayList<Reading> readingList) {
        this.readingList = readingList;
        systolic = Long.valueOf(0);
        diastolic = Long.valueOf(0);
        generateInfo();
    }

    public void generateInfo() {
        this.userID = readingList.get(0).getUserID();
        for (Reading reading : readingList) {
            systolic += reading.getSystolic();
            diastolic += reading.getDiastolic();
        }
        systolic /= readingList.size();
        diastolic /= readingList.size();
        condition = setCondition(systolic, diastolic);
    }

    private String setCondition(Long systolic, Long diastolic) {
        if (systolic > 180 || diastolic > 120) {
            setColor(Color.RED);
            return "Hypertensive Crisis";
        } else if (systolic >= 140 || diastolic >= 90) {
            setColor(Color.argb(255, 255, 120, 120));
            return "High Blood Pressure 2";
        } else if (systolic >= 130 || diastolic >= 80) {
            setColor(Color.argb(255, 255, 180, 180));
            return "High Blood Pressure 1";
        } else if (systolic >= 120) {
            setColor(Color.YELLOW);
            return "Elevated";
        } else {
            setColor(Color.GREEN);
            return "Normal";
        }
    }

    public String getUserID() {
        return userID;
    }

    public Long getDiastolic() {
        return diastolic;
    }

    public Long getSystolic() {
        return systolic;
    }

    public String getCondition() {
        return condition;
    }

    public int getColor() { return color; }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setDiastolic(Long diastolic) {
        this.diastolic = diastolic;
    }

    public void setSystolic(Long systolic) {
        this.systolic = systolic;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public void setColor(int color) { this.color = color; }
}
