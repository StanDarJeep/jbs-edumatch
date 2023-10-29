package com.example.edumatch.util;

import static com.example.edumatch.util.NetworkUtils.handlePutPostResponse;
import static com.example.edumatch.util.NetworkUtils.sendHttpRequest;
import static com.example.edumatch.util.NetworkUtils.showToastOnUiThread;
import static com.example.edumatch.util.ProfileHelper.constructSignUpRequest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.Toast;

import com.example.edumatch.activities.AvailabilityActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.mortbay.util.ajax.JSON;

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



    public static Boolean postSignUpInfo(Context context, JSONObject requestBody) {
        String apiUrl = "https://edumatch.canadacentral.cloudapp.azure.com/api/auth/signup";

        SharedPreferences sharedPreferences = context.getSharedPreferences("AccountPreferences", Context.MODE_PRIVATE);

        JSONObject jsonResponse = sendHttpRequest(apiUrl, sharedPreferences.getString("jwtToken", ""), "POST", requestBody);

        String successMessage = "Successfully Signed Up";
        String logTag = "SignUpPost";

        return handlePutPostResponse(context,jsonResponse,successMessage,logTag);
    }

}

