package kz.adamant.studenttimetable;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;

public class SettingsActivity extends AppCompatActivity {
    public static final int RESULT_OK = 1;
    public static final int RESULT_NO = 2;

    private SwitchCompat switchTheme, switchTeacher;
    private Spinner spinnerWeeks;
    SharedPreferences mPref;
    private final Integer[] weeks = {5, 6, 7};

    private boolean shouldRecreateActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        switchTheme = findViewById(R.id.switchForTheme);
        mPref = this.getSharedPreferences(MainActivity.main_pref, Context.MODE_PRIVATE);
        switchTheme.setChecked(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES);
        switchTheme.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked){
                saveSwitchTheme(true);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
            else {
                saveSwitchTheme(false);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

        spinnerWeeks = findViewById(R.id.num_weeks_spinner);
        int weekNum = mPref.getInt("weekNums", 6);
        ArrayAdapter<Integer> spinnerAdapter = new ArrayAdapter<Integer>(this, R.layout.spinner_text, R.id.spinner_text, weeks);
        spinnerWeeks.setAdapter(spinnerAdapter);
        spinnerWeeks.setSelection(weekNum-5);
        spinnerWeeks.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                saveWeekNums(weeks[i]);
                shouldRecreateActivity = weeks[i] != weekNum;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        switchTeacher = findViewById(R.id.switchForTeacher);
        boolean isCheckedShowTeacher = mPref.getBoolean("isCheckedShowTeacher", false);
        switchTeacher.setChecked(isCheckedShowTeacher);
        switchTeacher.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            saveSwitchTeacher(isChecked);
            shouldRecreateActivity = isCheckedShowTeacher != isChecked;
        });
    }

    private void saveSwitchTheme(boolean isChecked) {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putBoolean("isCheckedTheme", isChecked);
        editor.apply();
    }
    private void saveSwitchTeacher(boolean isChecked) {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putBoolean("isCheckedShowTeacher", isChecked);
        editor.apply();
    }

    private void saveWeekNums(int weekNums) {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putInt("weekNums", weekNums);
        editor.apply();
    }

    @Override
    public void onBackPressed() {
        setResult(shouldRecreateActivity ? RESULT_OK : RESULT_NO);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}