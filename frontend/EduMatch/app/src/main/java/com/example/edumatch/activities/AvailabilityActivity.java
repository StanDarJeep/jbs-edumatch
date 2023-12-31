package com.example.edumatch.activities;

import static com.example.edumatch.util.LoginSignupHelper.isStartTimeBeforeEndTime;
import static com.example.edumatch.util.LoginSignupHelper.postSignUpInfo;
import static com.example.edumatch.util.LoginSignupHelper.printSharedPreferences;
import static com.example.edumatch.util.ProfileHelper.constructSignUpRequest;
import static com.example.edumatch.util.ProfileHelper.logRequestToConsole;
import static com.example.edumatch.util.ProfileHelper.putEditProfile;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.edumatch.views.AvailableTimesViews;
import com.example.edumatch.views.DayOfTheWeekView;
import com.example.edumatch.R;
import com.example.edumatch.views.GoogleIconButtonView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AvailabilityActivity extends AppCompatActivity implements DayOfTheWeekView.DayOfTheWeekClickListener {
    final static String TAG = "AvailabilityActivity";
    private Map<String, List<String>> availabilityMap;

    private AvailableTimesViews availableTimesViews;

    private String currentDay;

    private boolean useGoogleCalendar;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    private TextView dayText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_availability);

        availableTimesViews = findViewById(R.id.available_times);

        availabilityMap = new HashMap<>();

        initSharedPreferences();
        initManualButton();
        initGoogleButton();
        initializeDayButtons();
        initializeSetTimeButton();
        initInvisibleFields();
        initFields();
    }

    private void initInvisibleFields() {
        dayText = findViewById(R.id.selected_day);
        dayText.setVisibility(View.GONE);
        if (!sharedPreferences.getBoolean("useGoogle", false) && !sharedPreferences.getString("username", "").isEmpty()) {
            GoogleIconButtonView googleView = findViewById(R.id.google);
            googleView.setVisibility(View.GONE);
            TextView text = findViewById(R.id.automatically_set_title);
            text.setVisibility(View.GONE);
        }
    }

    // ChatGPT usage: Yes
    private void initializeSetTimeButton() {
        Button setTimeButton = availableTimesViews.getSetTimesButton();

        setTimeButton.setOnClickListener(v -> {
            // Toggle the clicked state
            if (availabilityMap.containsKey(currentDay)) {
                List<String> availability = availabilityMap.get(currentDay);
                String startTimeString = availableTimesViews.getStartTime();
                String endTimeString = availableTimesViews.getEndTime();

                boolean isValid = isStartTimeBeforeEndTime(startTimeString, endTimeString);
                if (!isValid) {
                    Toast.makeText(AvailabilityActivity.this, "Start Time Not Before End Time, Not Saved!", Toast.LENGTH_SHORT).show();
                } else {
                    assert availability != null;
                    availability.set(0, availableTimesViews.getStartTime());
                    availability.set(1, availableTimesViews.getEndTime());
                    Toast.makeText(AvailabilityActivity.this, "Saved for " + currentDay, Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    // ChatGPT usage: Yes
    private void initializeDayButtons() {
        int[] buttonIds = {
                R.id.sunday_button,
                R.id.monday_button,
                R.id.tuesday_button,
                R.id.wednesday_button,
                R.id.thursday_button,
                R.id.friday_button,
                R.id.saturday_button
        };

        // Set up click listeners for each DayOfTheWeekButton
        for (int buttonId : buttonIds) {

            DayOfTheWeekView currentDayButton = findViewById(buttonId);
            final String day = currentDayButton.getDay();

            currentDayButton.setDayOfTheWeekClickListener(this);

            availabilityMap.put(day, Arrays.asList("00:00", "23:59"));
        }
    }

    // ChatGPT usage: Yes
    @Override
    public void onDayButtonClick(String day) {
        Log.w(TAG, "Day " + day);
        updateAvailability(day);

        if (availabilityMap.containsKey(day)) {
            List<String> availability = availabilityMap.get(day);

            assert availability != null;
            Log.w(TAG, day + availability.get(0) + availability.get(1));
        } else {
            Toast.makeText(AvailabilityActivity.this, "No availability data for " + day, Toast.LENGTH_SHORT).show();
            Log.w(TAG, "No availability data");
        }
        availableTimesViews.setVisibility(View.VISIBLE);
    }

    // ChatGPT usage: Yes
    @SuppressLint("SetTextI18n")
    private void updateAvailability(String day) {

        currentDay = day;
        if (availabilityMap.containsKey(day)) {
            List<String> availability = availabilityMap.get(day);
            assert availability != null;
            String startTime = availability.get(0);
            String endTime = availability.get(1);
            availableTimesViews.setStartTime(startTime);
            availableTimesViews.setEndTime(endTime);
        }
        dayText.setVisibility(View.VISIBLE);
        dayText.setText("Selected Day: " + day);
    }

    // ChatGPT usage: Yes
    public void showTimePicker(View view) {
        boolean isStartTime = "start_time".equals(view.getTag());

        TimePickerDialog timePickerDialog = new TimePickerDialog(AvailabilityActivity.this, (pickerView, hourOfDay, minute) -> {
            String selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
            if (isStartTime) {
                availableTimesViews.setStartTime(selectedTime);
            } else {
                availableTimesViews.setEndTime(selectedTime);
            }
        }, isStartTime ? 8 : 18, 0, true); // Default time: 8:00 AM for start, 6:00 PM for end

        timePickerDialog.show();
    }

    private void initManualButton() {
        Button manualButton = findViewById(R.id.manually_set_button);

        manualButton.setOnClickListener(v -> {
            useGoogleCalendar = false;
            goToNewActivity();
        });
    }

    private void initGoogleButton() {
        GoogleIconButtonView google = findViewById(R.id.google);
        Button googleButton = google.getButton();
        googleButton.setOnClickListener(v -> {
            useGoogleCalendar = true;
            goToNewActivity();
        });
    }

    // ChatGPT usage: Yes
    private void updatePreferences() {

        editor.putBoolean("useGoogleCalendar", useGoogleCalendar);

        if (!useGoogleCalendar) {
            for (Map.Entry<String, List<String>> entry : availabilityMap.entrySet()) {
                String key = entry.getKey();
                List<String> values = entry.getValue();

                // Convert the List<String> into a String set
                editor.putString(key + "StartTime", values.get(0));
                editor.putString(key + "EndTime", values.get(1));
            }
        }

        // Commit the changes to SharedPreferences
        editor.commit();
    }

    // ChatGPT usage: Yes
    private void goToNewActivity() {
        Intent newIntent;
        updatePreferences();
        printSharedPreferences(sharedPreferences);
        if (sharedPreferences.getBoolean("isEditing", false)) {
            JSONObject request = constructEditAvailabilityRequest();
            putEditProfile(request, AvailabilityActivity.this);
            newIntent = new Intent(AvailabilityActivity.this, EditProfileListActivity.class);
            startActivity(newIntent);
        } else {
            //try posting user details
            JSONObject requestBody = constructSignUpRequest(AvailabilityActivity.this);// Create your JSON request body
            boolean success = postSignUpInfo(AvailabilityActivity.this,requestBody);
            if (success) {
                String userType = sharedPreferences.getString("userType", ""); // Assuming the key for user type is "type"

                switch (userType) {
                    case "tutee":
                        newIntent = new Intent(AvailabilityActivity.this, TuteeHomeActivity.class);
                        break;
                    case "tutor":
                        newIntent = new Intent(AvailabilityActivity.this, TutorHomeActivity.class);
                        break;
                    default:
                        // Handle unexpected cases or errors here
                        Toast.makeText(this, "Invalid user type", Toast.LENGTH_SHORT).show();
                        return;
                }
                startActivity(newIntent);
            }
        }

    }


    private void initSharedPreferences() {
        sharedPreferences = getSharedPreferences("AccountPreferences", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    // ChatGPT usage: Yes
    private void initFields() {
        // Initialize the availability for each day
        for (String day : availabilityMap.keySet()) {
            String startTimeKey = day + "StartTime";
            String endTimeKey = day + "EndTime";

            if (availabilityMap.containsKey(day) && sharedPreferences.contains(startTimeKey) && sharedPreferences.contains(endTimeKey)) {
                String startTime = sharedPreferences.getString(startTimeKey, "00:00");
                String endTime = sharedPreferences.getString(endTimeKey, "23:59");

                availabilityMap.put(day, Arrays.asList(startTime, endTime));
            }
        }
    }

    // ChatGPT usage: Yes
    public JSONObject constructEditAvailabilityRequest() {

        try {
            // Retrieve data from SharedPreferences

            JSONObject requestBody = new JSONObject();

            // For useGoogleCalendar
            boolean useGoogleCalendar = sharedPreferences.getBoolean("useGoogleCalendar", false);
            requestBody.put("useGoogleCalendar", useGoogleCalendar);

            JSONArray manualAvailabilityArray = new JSONArray();

            JSONObject sunday = new JSONObject();
            sunday.put("day", "Sunday");
            sunday.put("startTime", sharedPreferences.getString("SundayStartTime", ""));
            sunday.put("endTime", sharedPreferences.getString("SundayEndTime", ""));
            manualAvailabilityArray.put(sunday);

            JSONObject monday = new JSONObject();
            monday.put("day", "Monday");
            monday.put("startTime", sharedPreferences.getString("MondayStartTime", ""));
            monday.put("endTime", sharedPreferences.getString("MondayEndTime", ""));
            manualAvailabilityArray.put(monday);

            JSONObject tuesday = new JSONObject();
            tuesday.put("day", "Tuesday");
            tuesday.put("startTime", sharedPreferences.getString("TuesdayStartTime", ""));
            tuesday.put("endTime", sharedPreferences.getString("TuesdayEndTime", ""));
            manualAvailabilityArray.put(tuesday);

            JSONObject wednesday = new JSONObject();
            wednesday.put("day", "Wednesday");
            wednesday.put("startTime", sharedPreferences.getString("WednesdayStartTime", ""));
            wednesday.put("endTime", sharedPreferences.getString("WednesdayEndTime", ""));
            manualAvailabilityArray.put(wednesday);

            JSONObject thursday = new JSONObject();
            thursday.put("day", "Thursday");
            thursday.put("startTime", sharedPreferences.getString("ThursdayStartTime", ""));
            thursday.put("endTime", sharedPreferences.getString("ThursdayEndTime", ""));
            manualAvailabilityArray.put(thursday);

            JSONObject friday = new JSONObject();
            friday.put("day", "Friday");
            friday.put("startTime", sharedPreferences.getString("FridayStartTime", ""));
            friday.put("endTime", sharedPreferences.getString("FridayEndTime", ""));
            manualAvailabilityArray.put(friday);

            JSONObject saturday = new JSONObject();
            saturday.put("day", "Saturday");
            saturday.put("startTime", sharedPreferences.getString("SaturdayStartTime", ""));
            saturday.put("endTime", sharedPreferences.getString("SaturdayEndTime", ""));
            manualAvailabilityArray.put(saturday);

            requestBody.put("manualAvailability", manualAvailabilityArray);
            logRequestToConsole(requestBody);
            return requestBody;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }


}
