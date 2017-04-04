package com.example.john.finalproject.Study;

/**
 * Created by Chen on 2016/12/4.
 */

public class Course {
    private String name, classroom, time, period;
    private int length, index;
    private int place;

    public Course(String name, String classroom, String time, String period, int length, int index, int place) {
        this.name = name;
        this.classroom = classroom;
        this.time = time;
        this.period = period;
        this.length = length;
        this.index = index;
        this.place = place;
    }
    public String getName() {
        return name;
    }

    public String getClassroom() {
        return classroom;
    }

    public String getTime() {
        return time;
    }

    public String getPeriod() {
        return period;
    }

    public int getLength() {
        return length;
    }

    public int getIndex() {
        return index;
    }

    public int getPlace() {return place;};

    public void setName(String name) {
        this.name = name;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public void setLength(int length) {
        this.length = length;
    }
    public void setIndex(int index) {
        this.index = index;
    }
    public void setPlace(int place) {this.place = place;}
}
