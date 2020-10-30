package kz.adamant.studenttimetable.model;

import java.io.Serializable;

public class Schedule implements Serializable {
//    static final int MON = 0;
//    static final int TUE = 1;
//    static final int WED = 2;
//    static final int THU = 3;
//    static final int FRI = 4;
//    static final int SAT = 5;
//    static final int SUN = 6;

    private String className = "";
    private String classPlace = "";
    private String classTeacher = "";
    private int day;
    private Time startTime;
    private Time endTime;

    public Schedule(String className, String classPlace, String classTeacher, int day, Time startTime, Time endTime) {
        this.className = className;
        this.classPlace = classPlace;
        this.classTeacher = classTeacher;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Schedule() {}

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassPlace() {
        return classPlace;
    }

    public void setClassPlace(String classPlace) {
        this.classPlace = classPlace;
    }

    public String getClassTeacher() {
        return classTeacher;
    }

    public void setClassTeacher(String classTeacher) {
        this.classTeacher = classTeacher;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }
}
