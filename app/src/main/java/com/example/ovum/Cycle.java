package com.example.ovum;

public class Cycle {
    private String cycleTitle;
    private String log1;
    private String log2;
    private String log3;
    private float rating;

    public Cycle(String cycleTitle, String log1, String log2, String log3, float rating) {
        this.cycleTitle = cycleTitle;
        this.log1 = log1;
        this.log2 = log2;
        this.log3 = log3;
        this.rating = rating;
    }

    public String getCycleTitle() {
        return cycleTitle;
    }

    public String getLog1() {
        return log1;
    }

    public String getLog2() {
        return log2;
    }

    public String getLog3() {
        return log3;
    }

    public float getRating() {
        return rating;
    }

    public int getbackgroundColor() {
        if (rating > 4) {
            return 0xFFFFB6C1;
        } else if (rating > 3) {
            return 0xFFe3dbfd;
        } else {
            return 0xFF87CEEB;
        }
    }
}
