package kz.adamant.studenttimetable;

import kz.adamant.studenttimetable.model.Event;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.Calendar;
import java.util.HashMap;

public class EditEventActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int RESULT_OK_ADD = 1;
    public static final int RESULT_OK_EDIT = 2;
    public static final int RESULT_OK_DELETE = 3;

    private Context context;

    private Button deleteBtn;
    private Button submitBtn;

    private Button datePickerBtn;

    private EditText eventNameEdit;
    private EditText eventNoteEdit;
    private Spinner subjectEdit;
    ArrayAdapter<String> spinnerAdapter;

    private DatePickerDialog datePickerDialog;
    private Calendar selectedDate;

    private int mode;
    private int modeForSubject;

    private Event event;
    private int editIdx;

    private int[] idOfSubjects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);
        init();
    }

    private void init(){
        this.context = this;
        editIdx = getIntent().getIntExtra("idx", -1);
        modeForSubject = getIntent().getIntExtra("mode_for_subject", MainActivity.REQUEST_EVENT_ALL);
        deleteBtn = findViewById(R.id.delete_event_btn);
        submitBtn = findViewById(R.id.submit_event_btn);
        datePickerBtn = findViewById(R.id.date_picker_button);
        eventNameEdit = findViewById(R.id.eventName_edit);
        eventNoteEdit = findViewById(R.id.eventNote_edit);
        subjectEdit = findViewById(R.id.subject_edit);
        HashMap<Integer, String> allSubjects = null;

        if (modeForSubject == MainActivity.REQUEST_EVENT_ALL) {
            allSubjects = (HashMap<Integer, String>) getIntent().getSerializableExtra("allSubjects");
            idOfSubjects = new int[allSubjects.size()];
            String[] subjects = new String[allSubjects.size()];
            int q = 0;
            for (int index : allSubjects.keySet()) {
                idOfSubjects[q] = index;
                subjects[q] = allSubjects.get(index);
                q++;
            }
            spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, subjects);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            subjectEdit.setAdapter(spinnerAdapter);
        }
        Calendar newCalendar = Calendar.getInstance();
        Intent i = getIntent();
        mode = i.getIntExtra("mode", EventsActivity.REQUEST_ADD);
        if (mode == EventsActivity.REQUEST_ADD)
            Utils.getFormattedDate(newCalendar.getTime(), false);
        else {
            event = (Event) i.getSerializableExtra("event");
            assert event != null;
            int compareValue = event.getClassId();
            if (compareValue != -1 && spinnerAdapter != null) {
                assert allSubjects != null;
                int spinnerPosition = spinnerAdapter.getPosition(allSubjects.get(compareValue));
                subjectEdit.setSelection(spinnerPosition);
            }
            datePickerBtn.setText(Utils.getFormattedDate(event.getDate().getTime(), false));
        }
        datePickerDialog = new DatePickerDialog(
                EditEventActivity.this,
                (view, year, monthOfYear, dayOfMonth) -> {
                    selectedDate = Calendar.getInstance();
                    selectedDate.set(year, monthOfYear, dayOfMonth);
                    datePickerBtn.setText(Utils.getFormattedDate(selectedDate.getTime(), false));
                },
                mode == EventsActivity.REQUEST_ADD ? newCalendar.get(Calendar.YEAR) : event.getDate().get(Calendar.YEAR),
                mode == EventsActivity.REQUEST_ADD ? newCalendar.get(Calendar.MONTH) : event.getDate().get(Calendar.MONTH),
                mode == EventsActivity.REQUEST_ADD ? newCalendar.get(Calendar.DAY_OF_MONTH) : event.getDate().get(Calendar.DAY_OF_MONTH));
        checkMode();
        initView();
    }

    private void initView() {
        if (modeForSubject == MainActivity.REQUEST_EVENT_SINGLE) {
            subjectEdit.setVisibility(View.INVISIBLE);
        }
        datePickerBtn.setOnClickListener(view -> datePickerDialog.show());
    }

    private void checkMode(){
        if(mode == MainActivity.REQUEST_EDIT){
            loadScheduleData();
            deleteBtn.setVisibility(View.VISIBLE);
        } else {
            event = new Event();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.submit_event_btn:
                Log.d("TAG", "onClick: submit");
                if(mode == EventsActivity.REQUEST_ADD){
                    inputDataProcessing();
                    Intent i = new Intent();
                    i.putExtra("event", event);
                    setResult(RESULT_OK_ADD,i);
                    finish();
                }
                else if(mode == EventsActivity.REQUEST_EDIT){
                    inputDataProcessing();
                    Intent i = new Intent();
                    i.putExtra("idx",editIdx);
                    i.putExtra("event",event);
                    setResult(RESULT_OK_EDIT,i);
                    finish();
                }
                break;
            case R.id.delete_event_btn:
                Intent i = new Intent();
                i.putExtra("idx",editIdx);
                i.putExtra("event", event);
                setResult(RESULT_OK_DELETE, i);
                finish();
                break;
        }
    }

    private void loadScheduleData(){
        Intent i = getIntent();
        editIdx = i.getIntExtra("idx",-1);
        event = (Event) i.getSerializableExtra("event");
        eventNoteEdit.setText(event.getNote());
        eventNameEdit.setText(event.getName());
    }

    private void inputDataProcessing(){
        event.setClassId(
           modeForSubject == MainActivity.REQUEST_EVENT_SINGLE ?
               mode == EventsActivity.REQUEST_EDIT ?
                    event.getClassId() : getIntent().getIntExtra("subject", -1)
                : idOfSubjects[(int)subjectEdit.getSelectedItemId()]);
        event.setName(eventNameEdit.getText().toString());
        event.setNote(eventNoteEdit.getText().toString());
        if (selectedDate != null) event.setDate(selectedDate);
    }


}