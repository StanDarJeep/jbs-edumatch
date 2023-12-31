package com.example.edumatch.activities;

import static com.example.edumatch.util.AppointmentHelper.getAppointments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.edumatch.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AppointmentListActivity extends AppCompatActivity {
    String tutorName;
    String tutorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        SharedPreferences sharedPreferences = getSharedPreferences("AccountPreferences", Context.MODE_PRIVATE);
        if (sharedPreferences.getString("userType","tutee").equals("tutor")){
            setContentView(R.layout.activity_appointment_list_tutor);
        } else {
            setContentView(R.layout.activity_appointment_list);
        }
       JSONObject list = getAppointments(this);
        try {
            makeComponents(list.getJSONArray("appointments"));
        } catch (JSONException e) {
            Toast.makeText(this, "You have no appointments!", Toast.LENGTH_SHORT).show();
        }

    }
    // ChatGPT usage: Yes
    private void makeComponents(JSONArray appointments) {
        LinearLayout appointmentListLayout = findViewById(R.id.appointmentList);
        try {
            for (int i = 0; i < appointments.length(); i++) {
                JSONObject appointmentObject = appointments.getJSONObject(i);
                LayoutInflater inflater = LayoutInflater.from(this);
                View appointmentView = inflater.inflate(R.layout.appointment_component, null);
                // Set the margins for the appointmentView
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                // Set your desired space size in dp, for example, 8dp as an example
                int spaceInPixels = (int) (8 * getResources().getDisplayMetrics().density);
                layoutParams.setMargins(0, 0, 0, spaceInPixels);
                appointmentView.setLayoutParams(layoutParams);

                String course = appointmentObject.getString("course");
                String apptId = appointmentObject.getString("_id");
                appointmentView.setTag(apptId);

                String pstStartTime = appointmentObject.getString("pstStartDatetime");
                String pstEndTime = appointmentObject.getString("pstEndDatetime");
                String status = appointmentObject.getString("status").toUpperCase();
                //status = status.substring(0, 1).toUpperCase() + status.substring(1).toLowerCase();

                JSONArray participantsInfo = appointmentObject.getJSONArray("participantsInfo");

                JSONObject participant = participantsInfo.getJSONObject(1);

                SharedPreferences sharedPreferences = getSharedPreferences("AccountPreferences", Context.MODE_PRIVATE);
                if (sharedPreferences.getString("userType","tutee").equals("tutor")){
                     participant = participantsInfo.getJSONObject(1);
                } else {
                    participant = participantsInfo.getJSONObject(0);
                }

                tutorName = participant.getString("displayedName");
                tutorId = participant.getString("userId");
                TextView courseText = appointmentView.findViewById(R.id.courseCode);
                courseText.setText(course);
                TextView nameText = appointmentView.findViewById(R.id.tutorName);
                nameText.setText(tutorName);
                TextView statusB = appointmentView.findViewById(R.id.statusButton);
                statusB.setText(status);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                Date startDate = null;
                Date endDate = null;
                try {
                    startDate = sdf.parse(pstStartTime);
                    endDate = sdf.parse(pstEndTime);
                } catch (ParseException e) {
                    Toast.makeText(this, "Cannot find day", Toast.LENGTH_SHORT).show();
                }

                SimpleDateFormat dateSdf = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat timeSdf = new SimpleDateFormat("HH:mm");

                String date = dateSdf.format(startDate);
                String startTime = timeSdf.format(startDate);
                String endTime = timeSdf.format(endDate);
                String interval = startTime + " - " + endTime;

                TextView dateText = appointmentView.findViewById(R.id.date);
                dateText.setText(date);
                TextView intervalText = appointmentView.findViewById(R.id.timeInterval);
                intervalText.setText(interval);

                appointmentView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String clickedAppointmentId = (String) v.getTag();
                        Intent intent = new Intent(AppointmentListActivity.this, ScheduledAppointmentActivity.class);
                        intent.putExtra("appointmentId", clickedAppointmentId);
                        intent.putExtra("tutorName", tutorName);
                        intent.putExtra("tutorId", tutorId);
                        startActivity(intent);
                    }
                });

                appointmentListLayout.addView(appointmentView);
            }
        } catch (JSONException e) {
            Toast.makeText(this, "You have no appointments!", Toast.LENGTH_SHORT).show();
        }
    }
}