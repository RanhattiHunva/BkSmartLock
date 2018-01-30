package com.hunva.ranhatti.bksmartlock.dataControl;

import com.hunva.ranhatti.bksmartlock.R;

/**
 * Created by Admin on 12/25/2017.
 *
 */

public class GuestPresent {
    private String name;
    private int accessPermission;

    public GuestPresent(String name, int accessPermission) {
        this.name = name;
        this.accessPermission = accessPermission;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAccessPermission() {
        return accessPermission;
    }

    public void setAccessPermission(int accessPermission) {
        this.accessPermission = accessPermission;
    }
}
