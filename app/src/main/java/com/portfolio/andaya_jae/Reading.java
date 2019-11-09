package com.portfolio.andaya_jae;

import java.util.Date;

public class Reading {

    private String id;
    private String userID;
    private Date date;
    private Long systolic;
    private Long diastolic;
    private String condition;

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
            return "Hypertensive Crisis";
        } else if (systolic >= 140 || diastolic >= 90) {
            return "High Blood Pressure (Stage2)";
        } else if (systolic >= 130 || diastolic >= 80) {
            return "High Blood Pressure (Stage1)";
        } else if (systolic >= 120) {
            return "Elevated";
        } else {
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
}
