package com.twyst.app.android.model;

import java.io.Serializable;

/**
 * Created by anshul on 1/14/2016.
 */
public class Cashback extends Data implements Serializable {

    private String min;
    private String max;

    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }

    public String getMax() {
        return max;
    }

    public void setMax(String max) {
        this.max = max;
    }
}
