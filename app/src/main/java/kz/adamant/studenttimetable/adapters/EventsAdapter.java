package kz.adamant.studenttimetable.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;

import kz.adamant.studenttimetable.R;
import kz.adamant.studenttimetable.Utils;
import kz.adamant.studenttimetable.model.Event;

public class EventsAdapter extends ArrayAdapter<Event> {
    private ArrayList<Event> events;
    private Context mContext;
    public String TAG = getClass().getSimpleName();
    private HashMap<Integer, String> allSubjects;

    public EventsAdapter(@NonNull Context context, HashMap<Integer, String> allSubjects) {
        super(context, R.layout.event_item);
        this.allSubjects = allSubjects;
        mContext = context;
        events = new ArrayList<>();
    }
    private static class ViewHolder {
        TextView eventName;
        TextView eventDate;
        TextView className;
        CheckBox checkbox;
        View divider;
    }

    public void setEvents(ArrayList<Event> events) {
        this.events = events;
    }

    @Override
    public int getCount() {
        return events.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Event event = events.get(position);
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.event_item, parent, false);
            holder.eventName = convertView.findViewById(R.id.eventName);
            holder.eventDate = convertView.findViewById(R.id.eventDate);
            holder.className = convertView.findViewById(R.id.className);
            holder.checkbox = convertView.findViewById(R.id.checkbox);
            holder.divider = convertView.findViewById(R.id.divider);
            holder.checkbox.setOnCheckedChangeListener((CompoundButton compoundButton, boolean b) -> {
                event.setChecked(b);
                if (event.isChecked())
                    holder.divider.setVisibility(View.VISIBLE);
                else holder.divider.setVisibility(View.INVISIBLE);
            });
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.className.setText(allSubjects.get(event.getClassId()));
        holder.eventDate.setText(Utils.getFormattedDate(event.getDate().getTime(), true));
        holder.eventName.setText(event.getName());
        holder.checkbox.setChecked(event.isChecked());
        if (event.isChecked()) {
            holder.divider.setVisibility(View.VISIBLE);
        }
        return convertView;
    }
}
