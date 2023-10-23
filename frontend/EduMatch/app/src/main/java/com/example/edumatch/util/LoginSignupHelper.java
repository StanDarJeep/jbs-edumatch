package com.example.edumatch.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LoginSignupHelper {
    private final static String TAG = "LoginSignupHelper";

    public static int getInputTypeFromString(String inputType) {
        int inputTypeValue = InputType.TYPE_CLASS_TEXT; // Default value

        if (inputType != null) {
            switch (inputType) {
                case "text":
                    inputTypeValue = InputType.TYPE_CLASS_TEXT;
                    break;
                case "textPassword":
                    inputTypeValue = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD;
                    break;
                // Add more cases for other input types as needed
            }
        }

        return inputTypeValue;
    }

    public static boolean isStartTimeBeforeEndTime(String startTimeString, String endTimeString) {

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Date startTime = null;
        Date endTime = null;

        try {
            startTime = sdf.parse(startTimeString);
            endTime = sdf.parse(endTimeString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (startTime != null && endTime != null) {
            if (endTime.before(startTime)) {
                return false;
            } else {
                return true;
            }
        } else {
            Log.e(TAG, "Parsing Error");
            return false;
        }
    }

    public static void printSharedPreferences(SharedPreferences sharedPreferences) {
        Map<String, ?> allEntries = sharedPreferences.getAll();

        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof String) {
                Log.d("SharedPreferencesData", "Key: " + key + ", Value: " + value);
            } else if (value instanceof Integer) {
                Log.d("SharedPreferencesData", "Key: " + key + ", Value: " + value);
            } else if (value instanceof Boolean) {
                Log.d("SharedPreferencesData", "Key: " + key + ", Value: " + value);
            } else if (value instanceof Float) {
                Log.d("SharedPreferencesData", "Key: " + key + ", Value: " + value);
            } else if (value instanceof Long) {
                Log.d("SharedPreferencesData", "Key: " + key + ", Value: " + value);
            } else if (value instanceof Set) {
                Set<String> stringSet = (Set<String>) value;
                for (String item : stringSet) {
                    Log.d("SharedPreferencesData", "Key: " + key + ", Value (Set): " + item);
                }
            } else {
                Log.d("SharedPreferencesData", "Key: " + key + ", Value: " + value.toString());
            }
        }
    }

}

