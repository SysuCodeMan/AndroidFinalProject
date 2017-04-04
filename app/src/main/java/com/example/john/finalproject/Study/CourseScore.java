package com.example.john.finalproject.Study;

/**
 * Created by Chen on 2016/12/4.
 */

public class CourseScore {
    private String name, teacher, rank, id, creadit, score, gpa;

    public CourseScore(String name, String teacher, String rank, String id, String creadit, String score, String gpa) {
        this.name = name;
        this.teacher = teacher;
        this.rank = rank;
        this.id = id;
        this.creadit = creadit;
        this.score = score;
        this.gpa = gpa;
    }

    public String getName() {
        return name;
    }

    public String getTeacher() {
        return teacher;
    }

    public String getRank() {
        return rank;
    }

    public String getId() {
        return id;
    }

    public String getCreadit() {
        return creadit;
    }

    public String getScore() {
        return score;
    }

    public String getGpa() {
        return gpa;
    }
}
