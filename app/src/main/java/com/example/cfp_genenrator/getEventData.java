package com.example.cfp_genenrator;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class getEventData {

    private Context context;  // 添加成员变量保存上下文

    public getEventData(Context context) {
        this.context = context;
    }

    public void saveCFPEventList(List<CFPEvent> cfpEventList) {
        SharedPreferences sp = context.getSharedPreferences("eventData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        Gson gson = new Gson();
        String json = gson.toJson(cfpEventList);

        editor.putString("cfpEventList", json);
        editor.apply();
    }

    public List<CFPEvent> loadCFPEventList() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("eventData", Context.MODE_PRIVATE);
        Gson gson = new Gson();

        String json = sharedPreferences.getString("cfpEventList", "");

        if (!json.isEmpty()) {
            TypeToken<List<CFPEvent>> typeToken = new TypeToken<List<CFPEvent>>() {};
            List<CFPEvent> cfpEventList = gson.fromJson(json, typeToken.getType());
            return cfpEventList;
        } else {
            return new ArrayList<>();
        }
    }

    private static final String PREF_NAME = "Categories";
    private static final String KEY_CATEGORIES = "categories";

    public void saveCategories(Map<String, ArrayList<CFPEvent>> categories) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(categories);

        editor.putString(KEY_CATEGORIES, json);
        editor.apply();
    }

    public Map<String, ArrayList<CFPEvent>> LoadCategories() {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        String json = preferences.getString(KEY_CATEGORIES, "");

        Gson gson = new Gson();
        Type type = new TypeToken<HashMap<String, ArrayList<CFPEvent>>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public void saveALLCategory(ArrayList<String> category) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(category);

        editor.putString("allcategories", json);
        editor.apply();
    }

    public ArrayList<String> getCategory() {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        String json = preferences.getString("allcategories", "");

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        return gson.fromJson(json, type);
    }
}

