package com.hunva.ranhatti.bksmartlock.dataControl;

import com.hunva.ranhatti.bksmartlock.R;

/**
 * Created by Admin on 12/25/2017.
 *
 */

public class GuestPresent {
    private String name;

    public GuestPresent(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getArrow() {
        return R.drawable.arrow_right;
    }
}