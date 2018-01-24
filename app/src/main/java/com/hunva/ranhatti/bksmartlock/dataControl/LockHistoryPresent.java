package com.hunva.ranhatti.bksmartlock.dataControl;

/**
 * Created by Admin on 12/25/2017.
 * Using to define class data for list view in tab History in Admin activity.
 */

public class LockHistoryPresent {
    private String time, action;
    private int image;

    public LockHistoryPresent(String time, String action, int image) {
        this.time = time;
        this.action = action;
        this.image = image;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
