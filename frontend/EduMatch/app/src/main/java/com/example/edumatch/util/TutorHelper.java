package com.example.edumatch.util;

import static com.example.edumatch.util.NetworkUtils.handleGetResponse;
import static com.example.edumatch.util.NetworkUtils.sendHttpRequest;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONObject;

public class TutorHelper {
    static public String apiUrl = "https://edumatch.canadacentral.cloudapp.azure.com";
    // ChatGPT usage: Yes
    public static JSONObject fetchTutorAppointments(String id, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("AccountPreferences", Context.MODE_PRIVATE);

        String url = apiUrl + "/appointments";

        JSONObject jsonResponse = sendHttpRequest(url,sharedPreferences.getString("jwtToken", ""), "GET", null);

        String logTag = "TutorGET";

        return handleGetResponse(context,jsonResponse,logTag);
    }
    // ChatGPT usage: Yes
    public static JSONObject acceptAppointment(String id, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("AccountPreferences", Context.MODE_PRIVATE);

        String url = apiUrl + "/appointment/accept?appointmentId=" + id;

        JSONObject jsonResponse = sendHttpRequest(url,sharedPreferences.getString("jwtToken", ""), "PUT", null);

        String logTag = "TutorConfirm";

        return handleGetResponse(context,jsonResponse,logTag);
    }
    // ChatGPT usage: Yes
    public static JSONObject declineAppointment(String id, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("AccountPreferences", Context.MODE_PRIVATE);

        String url = apiUrl + "/appointment/cancel?appointmentId=" + id;

        JSONObject jsonResponse = sendHttpRequest(url,sharedPreferences.getString("jwtToken", ""), "PUT", null);

        String logTag = "TutorDecline";

        return handleGetResponse(context,jsonResponse,logTag);
    }

}
