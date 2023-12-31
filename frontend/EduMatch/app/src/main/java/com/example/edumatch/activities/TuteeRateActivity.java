package com.example.edumatch.activities;

import static com.example.edumatch.util.RateHelper.postReview;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.edumatch.R;
import com.example.edumatch.views.LabelAndRatingView;

import org.json.JSONException;
import org.json.JSONObject;

public class TuteeRateActivity extends AppCompatActivity {
    private double ratingValue;
    private boolean noShowValue;
    private boolean lateValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutee_rate);

        initName();
        initComponents();
        initSubmitButton();
    }

    // ChatGPT usage: Yes
    private void initComponents() {
        CheckBox noShowCheckBox = findViewById(R.id.no_show);
        CheckBox lateCheckBox = findViewById(R.id.late);


        noShowCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> noShowValue = isChecked);

// Set an OnCheckedChangeListener for the Late CheckBox
        lateCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> lateValue = isChecked);
    }

    @SuppressLint("SetTextI18n")
    private void initName(){
        //TODO: insert name from previous view
        TextView name = findViewById(R.id.tutee_name);
        name.setText("NEED THIS INFO FROM PREVIOUS VIEW");
    }

    // ChatGPT usage: Yes
    private void initSubmitButton() {
        Button submitButton = findViewById(R.id.submit_button);

        submitButton.setOnClickListener(v -> {
            LabelAndRatingView attitudeTextView = findViewById(R.id.rating);
            RatingBar rating = attitudeTextView.getRatingView();
            ratingValue = rating.getRating();
            JSONObject requestBody = constructRatingRequest();
            boolean success = postReview(TuteeRateActivity.this,requestBody);
            if(success){
                Toast.makeText(getApplicationContext(), "Successfully Rated Tutee!", Toast.LENGTH_SHORT).show();
                goToNewActivity();
            } else {
                Toast.makeText(getApplicationContext(), "Something went wrong. Rating not sent. ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ChatGPT usage: Yes
    private JSONObject constructRatingRequest() {
        try {
            JSONObject requestBody = new JSONObject();
            // TODO: don't use static receiverId
            // TODO: add appointmentId
            requestBody.put("receiverId", "6539d8bc84ac67a095b338e1");
            requestBody.put("rating", ratingValue);
            requestBody.put("noShow", noShowValue);
            requestBody.put("late", lateValue);
            // requestBody.put("appointmentId",appointmentId);
            // Add any other fields you need in the request.

            logRequestToConsole(requestBody);
            return requestBody;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void goToNewActivity() {
        // TODO: go back to scheduled list of appointments view
//        printSharedPreferences(sharedPreferences);
//        if(sharedPreferences.getBoolean("isEditing",false)){
//            newIntent = new Intent(CourseRatesActivity.this, EditProfileListActivity.class);
//        } else {
//            newIntent = new Intent(CourseRatesActivity.this, LocationInformationActivity.class);
//        }
//        startActivity(newIntent);
    }

    private void logRequestToConsole(JSONObject request) {
        Log.d("TuteeRatePost", "Request JSON: " + request.toString());
    }

}