package com.szugyi.circlemenusample;

import com.szugyi.circlemenu.view.WheelTextItem;

/**
 * Created by KhaledLela on 9/30/17.
 */

public class MenuItem implements WheelTextItem {
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
