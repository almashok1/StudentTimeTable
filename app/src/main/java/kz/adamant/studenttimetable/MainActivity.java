package kz.adamant.studenttimetable;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Arrays;
import java.util.HashMap;

import kz.adamant.studenttimetable.model.Schedule;
import kz.adamant.studenttimetable.model.Sticker;
import kz.adamant.studenttimetable.model.Time;
import kz.adamant.studenttimetable.services.SaveManager;

public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_ADD = 1;
    public static final int REQUEST_EDIT = 2;

    public static final int REQUEST_EVENT_SINGLE = 1;
    public static final int REQUEST_EVENT_ALL = 2;

    public static final int REQUEST_SETTINGS = 7;

    public static final String main_pref = "MAIN_PREF";

    private HashMap<Integer, String> allSubjects = new HashMap<>();
    private int rowCount = 14;
    private int columnCount = 7;
    private int cellHeight = 180;
    private int sideCellWidth = 60;
    public static final String[] headerTitle = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
    private String[] stickerColors;
    private int startTime = 9;
    private Typeface tf;
    private FloatingActionButton fab;
    private RelativeLayout stickerBox;
    GridLayout tableHeader;
    GridLayout tableBox;
    Toolbar toolbar;
    LayoutInflater inflater;

    HashMap<Integer, Sticker> stickers = new HashMap<Integer, Sticker>();
    private int stickerCount = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences mPref = getSharedPreferences(main_pref, Context.MODE_PRIVATE);
        boolean isCheckedTheme = mPref.getBoolean("isCheckedTheme", false);
        if (isCheckedTheme){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        loadSavedData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d("TAG", "onActivityResult: Called" + " || requestCode " + requestCode);
        switch (requestCode) {
            case REQUEST_ADD:
                if (resultCode == EditScheduleActivity.RESULT_OK_ADD) {
                    Schedule item = (Schedule) data.getSerializableExtra("schedule");
                    add(item);
                }
                break;
            case REQUEST_EDIT:
                if (resultCode == EditScheduleActivity.RESULT_OK_EDIT) {
                    int idx = data.getIntExtra("idx", -1);
                    Schedule item = (Schedule) data.getSerializableExtra("schedule");
                    edit(idx, item);
                }
                else if (resultCode == EditScheduleActivity.RESULT_OK_DELETE) {
                    int idx = data.getIntExtra("idx", -1);
                    remove(idx);
                }
                break;
            case REQUEST_SETTINGS:
                if (resultCode == SettingsActivity.RESULT_OK) recreate();
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        saveByPreference(SaveManager.saveSticker(stickers));
        super.onPause();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_event:
                Intent intent = new Intent(MainActivity.this, EventsActivity.class);
                intent.putExtra("mode", REQUEST_EVENT_ALL);
                intent.putExtra("allSubjects", allSubjects);
                startActivity(intent);
                return true;
            case R.id.settings:
                Intent intentSet = new Intent(MainActivity.this, SettingsActivity.class);
                startActivityForResult(intentSet, REQUEST_SETTINGS);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    private void init() {
        SharedPreferences mPref = getSharedPreferences(main_pref, Context.MODE_PRIVATE);
        columnCount = 1+mPref.getInt("weekNums", 6);
        Log.d("TAG@@@", columnCount+"");
        tf = ResourcesCompat.getFont(this, R.font.product_sans);
        toolbar = findViewById(R.id.main_toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        setSupportActionBar(toolbar);
        fab = findViewById(R.id.addFABButton);
        stickerBox = findViewById(R.id.sticker_box);
        tableHeader = findViewById(R.id.table_header);
        tableBox = findViewById(R.id.table_box);
        tableHeader.setColumnCount(columnCount);
        tableHeader.setRowCount(rowCount);
        tableBox.setColumnCount(columnCount);
        tableBox.setRowCount(rowCount);

        inflater = getLayoutInflater();
        stickerColors = getResources().getStringArray(R.array.default_sticker_color);

        fab.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, EditScheduleActivity.class);
            intent.putExtra("mode", REQUEST_ADD);
            startActivityForResult(intent, REQUEST_ADD);
        });
        createTable();
    }

    private void saveByPreference(String data){
        SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString("timetable",data);
        editor.apply();
    }

    private void loadSavedData(){
        removeAll();
        SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(this);
        String savedData = mPref.getString("timetable","");
        if(savedData == null || savedData.equals("")) return;
        load(savedData);
    }

    public void edit(int idx, Schedule schedule) {
        remove(idx);
        add(schedule, idx);
    }

    public void remove(int idx) {
        Sticker sticker = stickers.get(idx);
        stickerBox.removeView(sticker.getView());
        stickers.remove(idx);
        allSubjects.remove(idx);
        setStickerColor();
    }

    public void removeAll() {
        for (int key : stickers.keySet()) {
            Sticker sticker = stickers.get(key);
            stickerBox.removeView(sticker.getView());
        }
        stickers.clear();
    }

    private void add(final Schedule schedule) {
        add(schedule, -1);
        saveByPreference(SaveManager.saveSticker(stickers));
    }

    private void add(final Schedule schedule, final int specIdx) {
        final int count = specIdx < 0 ? ++stickerCount : specIdx;
        allSubjects.put(count, schedule.getClassName());
        Sticker sticker = new Sticker();
        TextView tv = new TextView(this);

        RelativeLayout.LayoutParams param = createStickerParam(schedule);
        tv.setLayoutParams(param);
        tv.setPadding(10, 4, 10, 0);
        tv.setTypeface(ResourcesCompat.getFont(this, R.font.product_sans));
        SharedPreferences mPref = getSharedPreferences(main_pref, Context.MODE_PRIVATE);
        boolean isShowTeacher = mPref.getBoolean("isCheckedShowTeacher", false);
        if (isShowTeacher) {
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
            tv.setText(schedule.getClassName() + "\n" + schedule.getClassPlace() + "\n" + schedule.getClassTeacher());
        }
        else {
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            tv.setText(schedule.getClassName() + "\n" + schedule.getClassPlace());
        }
        tv.setTextColor(Color.parseColor("#FFFFFF"));
        tv.setTypeface(tf, Typeface.NORMAL);

        tv.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, EventsActivity.class);
            intent.putExtra("mode", REQUEST_EVENT_SINGLE);
            intent.putExtra("subject", count);
            intent.putExtra("allSubjects", allSubjects);
            startActivity(intent);
        });

        tv.setOnLongClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, EditScheduleActivity.class);
            intent.putExtra("mode", REQUEST_EDIT);
            intent.putExtra("idx", count);
            intent.putExtra("schedule", schedule);
            startActivityForResult(intent, REQUEST_EDIT);
            return true;
        });

        sticker.setView(tv);
        sticker.setSchedule(schedule);
        stickers.put(count, sticker);
        stickerBox.addView(tv);
        setStickerColor();
    }

    public void load(String data) {
        stickers = SaveManager.loadSticker(data);
        int maxKey = 0;
        for (int key : stickers.keySet()) {
            Schedule schedule = stickers.get(key).getSchedule();
            add(schedule, key);
            if (maxKey < key) maxKey = key;
        }
        stickerCount = maxKey + 1;
        setStickerColor();
    }

    private void setStickerColor() {
        int size = stickers.size();
        int[] orders = new int[size];
        int i = 0;
        for (int key : stickers.keySet()) {
            orders[i++] = key;
        }
        Arrays.sort(orders);

        int colorSize = stickerColors.length;

        for (i = 0; i < size; i++) {
            TextView tv = stickers.get(orders[i]).getView();
            if (tv != null) {
                GradientDrawable gd = new GradientDrawable();
                gd.setCornerRadius(15.0f);
                gd.setColor(Color.parseColor(stickerColors[i % (colorSize)]));
                tv.setBackground(gd);
            }
        }

    }

    private void createTable() {
        createTableHeader();
        for (int i = 0; i < rowCount; i++) {
            for (int k = 0; k < columnCount; k++) {
                TextView tv = new TextView(this);
                if (k == 0) {
                    tv.setText(getHeaderTime(i));
                    tv.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
                    tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11);
                    tv.setTypeface(tf ,Typeface.BOLD);
                    tv.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
                    tv.setLayoutParams(createGridRowParam(sideCellWidth, cellHeight));
                } else {
                    tv.setBackground(ContextCompat.getDrawable(this, R.drawable.item_border));
                    tv.setGravity(Gravity.FILL_HORIZONTAL);
                    tv.setLayoutParams(createGridRowParam(cellHeight));
                }
                tableBox.addView(tv);
            }
        }
    }

    private void createTableHeader() {
        for (int i = 0; i < columnCount; i++) {
            TextView tv = new TextView(this);
            if (i == 0) {
                tv.setLayoutParams(createGridRowParam(sideCellWidth, cellHeight));
                tv.setText("");
            } else {
                tv.setLayoutParams(createGridRowParam(cellHeight));
                tv.setText(headerTitle[i-1]);
            }

            tv.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
            tv.setTypeface(tf, Typeface.BOLD);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);

            tv.setGravity(Gravity.CENTER);

            tableHeader.addView(tv);
        }
    }

    private RelativeLayout.LayoutParams createStickerParam(Schedule schedule) {
        int cell_w = calCellWidth();

        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(cell_w-10, calStickerHeightPx(schedule)-12);
        param.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        param.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        param.setMargins(sideCellWidth + cell_w * schedule.getDay()+5, calStickerTopPxByTime(schedule.getStartTime())+6, 0, 0);

        return param;
    }

    private int calCellWidth(){
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int cell_w = (size.x - 20 - sideCellWidth) / (columnCount - 1);
        return cell_w;
    }

    private int calStickerHeightPx(Schedule schedule) {
        int startTopPx = calStickerTopPxByTime(schedule.getStartTime());
        int endTopPx = calStickerTopPxByTime(schedule.getEndTime());
        int d = endTopPx - startTopPx;

        return d;
    }

    private int calStickerTopPxByTime(Time time) {
        int topPx = (time.getHour() - startTime) * cellHeight + (int) ((time.getMinute() / 60.0f) * cellHeight);
        return topPx;
    }

    private ViewGroup.LayoutParams createGridRowParam(int h) {
        return createGridRowParam(calCellWidth(), h);
    }

    private ViewGroup.LayoutParams createGridRowParam(int w, int h) {
        GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams(
                GridLayout.spec(GridLayout.UNDEFINED, GridLayout.FILL, 0),
                GridLayout.spec(GridLayout.UNDEFINED, GridLayout.FILL, 0));
        layoutParams.width = w;
        layoutParams.height = h;
        return layoutParams;
    }

    private String getHeaderTime(int i) {
        int p = (startTime + i) % 24;
        int res = p <= 12 ? p : p - 12;
        return res + "\n" + (p<12 ? "am" : "pm");
    }
}