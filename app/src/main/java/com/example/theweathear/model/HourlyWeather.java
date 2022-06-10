package com.example.theweathear.model;

public class HourlyWeather {
    private String imgIcon;
    private int time;
    private double temlpcurrent;

    public HourlyWeather(String imgIcon, int time, double temlpcurrent) {
        this.imgIcon = imgIcon;
        this.time = time;
        this.temlpcurrent = temlpcurrent;
    }

    public String getImgIcon() {
        return imgIcon;
    }

    public void setImgIcon(String imgIcon) {
        this.imgIcon = imgIcon;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public double getTemlpcurrent() {
        return temlpcurrent;
    }

    public void setTemlpcurrent(double temlpcurrent) {
        this.temlpcurrent = temlpcurrent;
    }
}
