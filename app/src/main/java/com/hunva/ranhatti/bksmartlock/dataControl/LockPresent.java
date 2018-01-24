package com.hunva.ranhatti.bksmartlock.dataControl;

/**
 * Created by Ranhatti Hunva on 11/27/2017.
 * Using to define class data for grid view in Manage Lock
 */

public class LockPresent {
    private String name;
    private int image;

    public LockPresent(String name, int image) {
        this.name = name;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
