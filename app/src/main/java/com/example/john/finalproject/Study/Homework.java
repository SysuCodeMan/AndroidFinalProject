package com.example.john.finalproject.Study;

/**
 * Created by Chen on 2016/12/6.
 */

public class Homework {
    private boolean isFinished;
    private String homework_description;

    public Homework(boolean isFinished, String homework_description) {
        this.isFinished = isFinished;
        this.homework_description = homework_description;
    }

    public boolean getFinished() {
        return isFinished;
    }

    public String getHomework_description() {
        return homework_description;
    }

    public void setFinished(boolean isFinished) {
        this.isFinished = isFinished;
    }

    public void setHomework_description(String homework_description) {
        this.homework_description = homework_description;
    }
}
