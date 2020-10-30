package kz.adamant.studenttimetable.services;

import kz.adamant.studenttimetable.model.Event;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import kz.adamant.studenttimetable.model.Schedule;
import kz.adamant.studenttimetable.model.Sticker;
import kz.adamant.studenttimetable.model.Time;

public class SaveManager {
    public static String saveSticker(HashMap<Integer, Sticker> stickers){
        JsonObject obj1 = new JsonObject();
        JsonArray arr1 = new JsonArray();
        int[] orders = getSortedKeySet(stickers);
        for (int order : orders) {
            JsonObject obj2 = new JsonObject();
            obj2.addProperty("idx", order);
            Schedule schedule = stickers.get(order).getSchedule();
//            for (Schedule schedule : schedules) {
                JsonObject obj3 = new JsonObject();
                obj3.addProperty("className", schedule.getClassName());
                obj3.addProperty("classPlace", schedule.getClassPlace());
                obj3.addProperty("classTeacher", schedule.getClassTeacher());
                obj3.addProperty("day", schedule.getDay());
                JsonObject obj4 = new JsonObject();//startTime
                obj4.addProperty("hour", schedule.getStartTime().getHour());
                obj4.addProperty("minute", schedule.getStartTime().getMinute());
                obj3.add("startTime", obj4);
                JsonObject obj5 = new JsonObject();//endTime
                obj5.addProperty("hour", schedule.getEndTime().getHour());
                obj5.addProperty("minute", schedule.getEndTime().getMinute());
                obj3.add("endTime", obj5);
//            }
            obj2.add("schedule", obj3);
            arr1.add(obj2);
        }
        obj1.add("stickers",arr1);
        return obj1.toString();
    }

    public static HashMap<Integer,Sticker> loadSticker(String json){
        HashMap<Integer, Sticker> stickers = new HashMap<Integer,Sticker>();
        JsonObject obj1 = (JsonObject)JsonParser.parseString(json);
        JsonArray arr1 = obj1.getAsJsonArray("stickers");
        for(int i = 0 ; i < arr1.size(); i++){
            Sticker sticker = new Sticker();
            JsonObject obj2 = (JsonObject)arr1.get(i);
            int idx = obj2.get("idx").getAsInt();
//            for(int k = 0 ; k < arr2.size(); k++){
                Schedule schedule = new Schedule();
                JsonObject obj3 = (JsonObject)(JsonObject)obj2.get("schedule");
                schedule.setClassName(obj3.get("className").getAsString());
                schedule.setClassPlace(obj3.get("classPlace").getAsString());
                schedule.setClassTeacher(obj3.get("classTeacher").getAsString());
                schedule.setDay(obj3.get("day").getAsInt());
                Time startTime = new Time();
                JsonObject obj4 = (JsonObject)obj3.get("startTime");
                startTime.setHour(obj4.get("hour").getAsInt());
                startTime.setMinute(obj4.get("minute").getAsInt());
                Time endTime = new Time();
                JsonObject obj5 = (JsonObject)obj3.get("endTime");
                endTime.setHour(obj5.get("hour").getAsInt());
                endTime.setMinute(obj5.get("minute").getAsInt());
                schedule.setStartTime(startTime);
                schedule.setEndTime(endTime);
                sticker.setSchedule(schedule);
//            }
            stickers.put(idx,sticker);
        }
        return stickers;
    }


    static private int[] getSortedKeySet(HashMap<Integer, Sticker> stickers){
        int[] orders = new int[stickers.size()];
        int i = 0;
        for(int key : stickers.keySet()){
            orders[i++] = key;
        }
        Arrays.sort(orders);
        return orders;
    }


    public static String saveEvents(ArrayList<Event> events) {
        Gson gson = new Gson();
        String json = gson.toJson(events);
        return json;
    }

    public static ArrayList<Event> loadEvents(String json) {
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Event>>(){}.getType();
        return gson.fromJson(json, type);
    }
}
