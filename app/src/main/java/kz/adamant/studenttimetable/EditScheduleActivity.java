package kz.adamant.studenttimetable;

import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Arrays;

import kz.adamant.studenttimetable.model.Schedule;
import kz.adamant.studenttimetable.model.Time;

public class EditScheduleActivity extends AppCompatActivity implements View.OnClickListener{
    public static final int RESULT_OK_ADD = 1;
    public static final int RESULT_OK_EDIT = 2;
    public static final int RESULT_OK_DELETE = 3;

    private Context context;

    private Button deleteBtn;
    private Button submitBtn;
    private EditText subjectEdit;
    private EditText classroomEdit;
    private EditText professorEdit;
    private Spinner daySpinner;
    private TextView startTv;
    private TextView endTv;

    //request mode
    private int mode;

    private Schedule schedule;
    private int editIdx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_schedule);
        init();
    }

    private void init(){
        this.context = this;
        deleteBtn = findViewById(R.id.delete_btn);
        submitBtn = findViewById(R.id.submit_btn);
        subjectEdit = findViewById(R.id.subject_edit);
        classroomEdit = findViewById(R.id.classroom_edit);
        professorEdit = findViewById(R.id.professor_edit);
        daySpinner = findViewById(R.id.day_spinner);
        startTv = findViewById(R.id.start_time);
        endTv = findViewById(R.id.end_time);

        checkMode();
        initView();
    }

    /** check whether the mode is ADD or EDIT */
    private void checkMode(){
        Intent i = getIntent();
        mode = i.getIntExtra("mode", MainActivity.REQUEST_ADD);

        if(mode == MainActivity.REQUEST_EDIT){
            loadScheduleData();
            deleteBtn.setVisibility(View.VISIBLE);
        } else {
            schedule = new Schedule();
            schedule.setStartTime(new Time(10,0));
            schedule.setEndTime(new Time(13,30));
        }
    }
    private void initView(){
        submitBtn.setOnClickListener(this);
        deleteBtn.setOnClickListener(this);
        SharedPreferences mPref = this.getSharedPreferences(MainActivity.main_pref, Context.MODE_PRIVATE);
        int weekNum = mPref.getInt("weekNums", 6);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, Arrays.copyOf(MainActivity.headerTitle, weekNum));
        daySpinner.setAdapter(adapter);
        daySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                schedule.setDay(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        startTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog dialog = new TimePickerDialog(context,listener,schedule.getStartTime().getHour(), schedule.getStartTime().getMinute(), false);
                dialog.show();
            }

            private TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    startTv.setText(putZeroIfNeeded(hourOfDay) + ":" + putZeroIfNeeded(minute));
                    schedule.getStartTime().setHour(hourOfDay);
                    schedule.getStartTime().setMinute(minute);
                }
            };
        });
        endTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog dialog = new TimePickerDialog(context,listener,schedule.getEndTime().getHour(), schedule.getEndTime().getMinute(), false);
                dialog.show();
            }

            private TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    endTv.setText(putZeroIfNeeded(hourOfDay) + ":" + putZeroIfNeeded(minute));
                    schedule.getEndTime().setHour(hourOfDay);
                    schedule.getEndTime().setMinute(minute);
                }
            };
        });
    }


    String putZeroIfNeeded(int n) {
        if (n < 10) {
            return "0" + n;
        }
        return String.valueOf(n);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.submit_btn:
                if(mode == MainActivity.REQUEST_ADD){
                    inputDataProcessing();
                    Intent i = new Intent();
                    i.putExtra("schedule",schedule);
                    setResult(RESULT_OK_ADD,i);
                    finish();
                }
                else if(mode == MainActivity.REQUEST_EDIT){
                    inputDataProcessing();
                    Intent i = new Intent();
                    i.putExtra("idx",editIdx);
                    i.putExtra("schedule",schedule);
                    setResult(RESULT_OK_EDIT,i);
                    finish();
                }
                break;
            case R.id.delete_btn:
                Intent i = new Intent();
                i.putExtra("idx",editIdx);
                setResult(RESULT_OK_DELETE, i);
                finish();
                break;
        }
    }

    private void loadScheduleData(){
        Intent i = getIntent();
        editIdx = i.getIntExtra("idx",-1);
        schedule = (Schedule) i.getSerializableExtra("schedule");
        subjectEdit.setText(schedule.getClassName());
        classroomEdit.setText(schedule.getClassPlace());
        professorEdit.setText(schedule.getClassTeacher());
        daySpinner.setSelection(schedule.getDay());
        startTv.setText(putZeroIfNeeded(schedule.getStartTime().getHour()) + ":" + putZeroIfNeeded(schedule.getStartTime().getMinute()));
        endTv.setText(putZeroIfNeeded(schedule.getEndTime().getHour()) + ":" + putZeroIfNeeded(schedule.getEndTime().getMinute()));
    }

    private void inputDataProcessing(){
        schedule.setClassName(subjectEdit.getText().toString());
        schedule.setClassPlace(classroomEdit.getText().toString());
        schedule.setClassTeacher(professorEdit.getText().toString());
    }
}