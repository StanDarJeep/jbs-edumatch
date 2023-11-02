package com.example.edumatch.activities;

import static com.example.edumatch.util.RateHelper.postRatingWeight;
import static com.example.edumatch.util.RateHelper.postReview;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.edumatch.R;
import com.example.edumatch.views.LabelAndCommentEditTextView;
import com.example.edumatch.views.LabelAndRatingView;

import org.json.JSONException;
import org.json.JSONObject;

public class TutorRateActivity extends AppCompatActivity {
    private double ratingValue;
    private boolean noShowValue;
    private boolean lateValue;

    private String commentValue;

    private String receiverId;
    private String receiverName;

    private String appointmentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);
        Intent intent = getIntent();
        appointmentId = intent.getStringExtra("appointmentId");
        receiverName = intent.getStringExtra("tutorName");
        receiverId = intent.getStringExtra("tutorId");

        initName();
        initComponents();
        initSubmitButton();
    }

    // ChatGPT usage: Yes
    private void initComponents() {
        CheckBox noShowCheckBox = findViewById(R.id.no_show);
        CheckBox lateCheckBox = findViewById(R.id.late);

        noShowCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> noShowValue = isChecked);

        lateCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> lateValue = isChecked);


        LabelAndRatingView organizationRatingView = findViewById(R.id.rating);
        organizationRatingView.getRatingView().setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> ratingValue = rating);
    }

    private void initName() {
        TextView nameTextView = findViewById(R.id.tutor_name);
        nameTextView.setText(receiverName);
    }

    // ChatGPT usage: Yes
    private void initSubmitButton() {
        Button submitButton = findViewById(R.id.submit_button);

        submitButton.setOnClickListener(v -> {
            LabelAndCommentEditTextView comment = findViewById(R.id.comments);
            commentValue = comment.getEnterUserEditText().getText().toString().trim();
            JSONObject requestBody = constructRatingRequest();
            JSONObject weightRequestBody = constructRatingWeightRequest();
            Boolean weight_success = postRatingWeight(TutorRateActivity.this,weightRequestBody);
            Boolean success = postReview(TutorRateActivity.this,requestBody);
            if(success && weight_success){
                Toast.makeText(getApplicationContext(), "Successfully Rated Tutor!", Toast.LENGTH_SHORT).show();
                goToNewActivity();
            } else {
                Toast.makeText(getApplicationContext(), "Something went wrong. Rating not sent. ", Toast.LENGTH_SHORT).show();
            }

        });
    }

    // ChatGPT usage: Yes
    private JSONObject constructRatingWeightRequest() {
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("tutorId", receiverId);
            requestBody.put("review", ratingValue);
            logRequestToConsole(requestBody, "RateWeightPost");
            return requestBody;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }


    // ChatGPT usage: Yes
    private JSONObject constructRatingRequest() {
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("receiverId", receiverId);
            requestBody.put("rating", ratingValue);
            requestBody.put("noShow", noShowValue);
            requestBody.put("late", lateValue);
            requestBody.put("comment",commentValue);
            requestBody.put("appointmentId",appointmentId);
            // Add any other fields you need in the request.

            logRequestToConsole(requestBody, "TutorRatePost");
            return requestBody;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void goToNewActivity() {
        Intent intent = new Intent(TutorRateActivity.this, AppointmentListActivity.class);
        startActivity(intent);
    }

    private void logRequestToConsole(JSONObject request, String tag) {
        Log.d(tag, "Request JSON: " + request.toString());
    }
}
