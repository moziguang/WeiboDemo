package com.lwq.base.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: luoweiqiang
 * Date: 13-11-28
 */
public class JsonUtils {
    public final static String TAG = "JsonUtils";

    public static String getString(JSONObject json, String name) {
        return getString(json, name, "");
    }

    public static String getString(JSONObject json, String name, String defaultValue) {
        if (json == null || json.isNull(name)) {
            return defaultValue;
        }
        try {
            return json.getString(name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return defaultValue;
    }

    public static Integer getInt(JSONObject json, String name, Integer defaultValue) {
        if (json == null || json.isNull(name)) {
            return defaultValue;
        }
        try {
            return json.getInt(name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return defaultValue;
    }

    public static Long getLong(JSONObject json, String name, Long defaultValue) {
        if (json == null || json.isNull(name)) {
            return defaultValue;
        }
        try {
            return json.getLong(name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return defaultValue;
    }

    public static Double getDouble(JSONObject json, String name) {
        return getDouble(json, name, 0.0);
    }

    public static Double getDouble(JSONObject json, String name, Double defaultValue) {
        if (json == null || json.isNull(name)) {
            return defaultValue;
        }
        try {
            return json.getDouble(name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return defaultValue;
    }

    public static Boolean getBoolean(JSONObject json, String name, boolean defaultValue) {
        if (json == null || json.isNull(name)) {
            return defaultValue;
        }
        try {
            return json.getBoolean(name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return defaultValue;
    }

    public static JSONObject getJSONObject(JSONObject json, String name) {
        if (json == null || json.isNull(name)) {
            return null;
        }

        try {
            return json.getJSONObject(name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONArray getJSONArray(JSONObject json, String name) {
        if (json == null || json.isNull(name)) {
            return null;
        }
        JSONArray jsonArray = null;
        try {
            jsonArray = json.getJSONArray(name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    public static List<Integer> JSONArray2IntegerList(JSONArray jsonArray) {
        if (jsonArray == null) {
            return null;
        }
        List<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                list.add((Integer) jsonArray.get(i));
            } catch (JSONException e) {
                Log.e(TAG, "jsonArray转换为Integer数组时失败.",e);
            }
        }
        return list;
    }

    public static void putJsonObject(JSONObject json, String name, Object value) {
        try {
            json.remove(name);
            json.put(name, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static JSONObject load(String str) {
        try {
            JSONObject json = new JSONObject(str);
            return json;
        } catch (Exception e) {
//            e.printStackTrace();
            return null;
        }
    }

    public static JSONObject load(Map map) {
        if (map==null) return null;
        try {
            JSONObject json = new JSONObject(map);
            return json;
        } catch (Exception e) {
//            e.printStackTrace();
            return null;
        }
    }

    public static JSONArray loadJsonArray(String str) {
        try {
            JSONArray json = new JSONArray(str);
            return json;
        } catch (Exception e) {
//            e.printStackTrace();
            return null;
        }
    }

    public static List parseToList(JSONArray json){
        List<Object> list = null;
        if(json!=null) {
            list = new ArrayList<>();
            for (int i=0;i<json.length();i++) {
                try {
                    Object value = json.get(i);
                    if (value instanceof JSONArray) {
                        list.add(parseToList((JSONArray) value));
                    } else if (value instanceof JSONObject) {
                        list.add(parseToMap((JSONObject) value));
                    }else{
                        list.add(value);
                    }
                }catch(Exception e){
                    Log.e(TAG,"parseToList Exception json = " + json);
                    Log.e(TAG,"parseToList Exception ",e);
                }
            }
        }
        return list;
    }

    public static Map parseToMap(JSONObject json){
        Map map = null;
        if(json!=null) {
            map = new HashMap();
            Iterator keys = json.keys();
            while (keys.hasNext()) {
                try {
                    String key = (String) keys.next();
                    Object value = json.get(key);
                    if (value instanceof JSONArray) {
                        map.put(key, parseToList((JSONArray) value));
                    } else if (value instanceof JSONObject) {
                        map.put(key, parseToMap((JSONObject) value));
                    }else{
                        map.put(key,value);
                    }
                }catch(Exception e){
                    Log.e(TAG,"parseToList Exception json = " + json);
                    Log.e(TAG,"parseToList Exception ",e);
                }
            }
        }
        return map;
    }
}
