package kz.adamant.studenttimetable;

import kz.adamant.studenttimetable.adapters.EventsAdapter;
import kz.adamant.studenttimetable.model.Event;
import kz.adamant.studenttimetable.model.Schedule;
import kz.adamant.studenttimetable.model.Sticker;
import kz.adamant.studenttimetable.services.SaveManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;


public class EventsActivity extends AppCompatActivity {
    public static final int REQUEST_ADD = 1;
    public static final int REQUEST_EDIT = 2;

    private ListView eventsList;
    private ArrayList<Event> events = new ArrayList<>();
    private ArrayList<Event> filteredEvents = new ArrayList<>();
    private FloatingActionButton fab;
    private EventsAdapter adapter;

    private int mode;

    private static HashMap<Integer, String> allSubjects;

    private static int compare(Event a, Event b) {
        return a.getDate().compareTo(b.getDate());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        init();
        loadSavedData();
    }

    private void init() {
        Intent intent = getIntent();
        mode = intent.getExtras().getInt("mode");
        fab = findViewById(R.id.addEventFABButton);
        eventsList = findViewById(R.id.events_list);

        allSubjects = (HashMap<Integer, String>) getIntent().getSerializableExtra("allSubjects");

        fab.setOnClickListener(view -> {
            Intent intent2 = new Intent(EventsActivity.this, EditEventActivity.class);
            intent2.putExtra("mode", REQUEST_ADD);
            intent2.putExtra("mode_for_subject", mode);
            if (mode == MainActivity.REQUEST_EVENT_ALL) intent2.putExtra("allSubjects", allSubjects);
            else intent2.putExtra("subject", getIntent().getIntExtra("subject", -1));
            startActivityForResult(intent2, REQUEST_ADD);
        });
        eventsList.setOnItemClickListener((AdapterView<?> adapterView, View view, int i, long l) -> {
            Intent intent1 = new Intent(EventsActivity.this, EditEventActivity.class);
            intent1.putExtra("mode", REQUEST_EDIT);
            intent1.putExtra("event", mode == MainActivity.REQUEST_EVENT_SINGLE ? filteredEvents.get(i) : events.get(i));
            intent1.putExtra("idx", i);
            intent1.putExtra("mode_for_subject", mode);
            if (mode == MainActivity.REQUEST_EVENT_ALL) intent1.putExtra("allSubjects", allSubjects);
            startActivityForResult(intent1, REQUEST_EDIT);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case REQUEST_ADD:
                if (resultCode == EditEventActivity.RESULT_OK_ADD) {
                    Event item = (Event) data.getSerializableExtra("event");
                    add(item);
                }
                break;
            case REQUEST_EDIT:
                if (resultCode == EditEventActivity.RESULT_OK_EDIT) {
                    int idx = data.getIntExtra("idx", -1);
                    Event item = (Event) data.getSerializableExtra("event");
                    edit(idx, item);
                }
                else if (resultCode == EditEventActivity.RESULT_OK_DELETE) {
                    int idx = data.getIntExtra("idx", -1);
                    Event item = (Event) data.getSerializableExtra("event");
                    remove(idx, item);
                }
                break;
        }
        saveByPreference(SaveManager.saveEvents(events));
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void setAdapter() {
        if (adapter == null) {
            adapter = new EventsAdapter(this, allSubjects);
            adapter.setEvents(
                    mode == MainActivity.REQUEST_EVENT_SINGLE ?
                            filteredEvents : events
            );
            eventsList.setAdapter(adapter);
        }
    }

    private void loadSavedData(){
        SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(this);
        String savedData = mPref.getString("events","");
        Log.d("TAG", "loadSavedData: " + savedData);
        if(savedData == null || savedData.equals("")) return;
        load(savedData);
    }

    private void load(String json) {
        events.addAll(SaveManager.loadEvents(json));
        sortEventsByDate();
        setAdapter();
        eventsList.setAdapter(adapter);
    }

    private void sortEventsByDate() {
        if (mode == MainActivity.REQUEST_EVENT_SINGLE) {
            setSingleSubjectEvents();
            filteredEvents.sort(EventsActivity::compare);
        } else
            events.sort(EventsActivity::compare);
    }

    private void setSingleSubjectEvents() {
        if(events != null) {
            int subject = getIntent().getIntExtra("subject", -1);
            filteredEvents.clear();
            events.forEach(e -> {
                if (e.getClassId() == (subject)) {
                    filteredEvents.add(e);
                }
            });
        }
    }

    private void add(Event event) {
        setAdapter();
        events.add(event);
        sortEventsByDate();
        adapter.notifyDataSetChanged();
    }

    private void edit(int idx, Event event) {
        Log.d("TAG", "edit: " + idx);
        if (idx >= 0 && idx < events.size()) {
            if (mode == MainActivity.REQUEST_EVENT_SINGLE) {
                for (int i = 0; i < events.size(); i++) {
                    if (events.get(i).equals(filteredEvents.get(idx))) {
                        events.set(i, event);
                    }
                }
                Log.d("TAG", "edit: " + events.size());
            }else {
                events.set(idx, event);
            }
            sortEventsByDate();
            adapter.notifyDataSetChanged();
        }
    }

    private void remove(int idx, Event event) {
        if (idx >= 0 && idx < events.size()) {
            if (mode == MainActivity.REQUEST_EVENT_SINGLE) {
                events.removeIf(e -> e.getName().equals(event.getName()));
            }
            else {
                events.remove(idx);
            }
        }
        sortEventsByDate();
        adapter.notifyDataSetChanged();
    }

    private void saveByPreference(String data){
        SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = mPref.edit();
        Log.d("TAG", "saveByPreference: " + data);
        editor.putString("events",data);
        editor.apply();
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveByPreference(SaveManager.saveEvents(events));
    }

    public void removeAll() {
        adapter.clear();
        events.clear();
    }
}