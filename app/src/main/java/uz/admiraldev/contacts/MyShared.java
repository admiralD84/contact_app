package uz.admiraldev.contacts;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;


import java.lang.reflect.Type;
import java.util.List;

public class MyShared<T> {
    private SharedPreferences sharedPreferences;
    private Gson gson;
    private final String prefsName;

    public MyShared(Context context, Gson gson, String prefsName) {
        this.sharedPreferences = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE);
        this.gson = gson;
        this.prefsName = prefsName;
    }

    public MyShared(SharedPreferences sharedPreferences, String prefsName) {
        this.prefsName = prefsName;
    }

    // Ma'lumotlarni SharePreference ga list ko'rinishida yozish
    public void putList(String key, List<T> list) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, gson.toJson(list));
        editor.apply();
    }

    // SharePreference dan ma'lumotlarni listko'rinishida o'qish
    public List<T> getList(String key, Type typeOfT) {
        String data = sharedPreferences.getString(key, null);
        return gson.fromJson(data, typeOfT);
    }
}