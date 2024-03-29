package com.portfolio.andaya_jae;

import android.app.Application;
import android.graphics.Color;
import android.view.View;
import android.widget.Toast;

import java.util.Date;

import static java.security.AccessController.getContext;

public class Reading {

    private String id;
    private String userID;
    private Date date;
    private Long systolic;
    private Long diastolic;
    private String condition;
    private int color;

    public Reading() {};

    public Reading (String id, String userID, Date date, Long systolic, Long diastolic) {
        this.id = id;
        this.userID = userID;
        this.date = date;
        this.systolic = systolic;
        this.diastolic = diastolic;
        this.condition = setCondition(systolic, diastolic);
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

    public String getId() {
        return id;
    }

    public String getUserID() {
        return userID;
    }

    public Date getDate() {
        return date;
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

    public void setId(String id) {
        this.id = id;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setDate(Date date) {
        this.date = date;
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
