package kz.adamant.studenttimetable.model;

import android.widget.TextView;

import androidx.annotation.NonNull;

import java.io.Serializable;
public class Sticker implements Serializable {
    @NonNull private TextView view;
    private Schedule schedule;

    public Sticker() {
    }

    public TextView getView() {
        return view;
    }

    public void setView(TextView view) {
        this.view = view;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }
}