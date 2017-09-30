package com.szugyi.circlemenusample;

import com.szugyi.circlemenu.view.WheelLayout;

/**
 * Created by KhaledLela on 9/30/17.
 */

public class MenuItem implements WheelLayout.WheelItem {
    private long id;
    private String name;

    @Override
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
