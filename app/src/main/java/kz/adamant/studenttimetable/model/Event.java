package kz.adamant.studenttimetable.model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class Event implements Serializable {
    private String name;
    private String note;
    private Calendar date;
    private int classId;
    private boolean checked;

    public Event(String name, String note, Calendar date, int classId, boolean checked) {
        this.name = name;
        this.note = note;
        this.date = date;
        this.classId = classId;
        this.checked = checked;
    }

    public Event() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
