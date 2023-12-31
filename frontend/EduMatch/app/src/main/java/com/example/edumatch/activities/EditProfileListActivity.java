package com.example.edumatch.activities;

import static com.example.edumatch.util.ProfileHelper.getProfile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.edumatch.R;

public class EditProfileListActivity extends AppCompatActivity {

    // ChatGPT usage: Yes
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_list);
        Button accountInfoButton = findViewById(R.id.account_info);
        Button uniInfoButton = findViewById(R.id.uni_info);
        Button locationInfoButton = findViewById(R.id.location_info);
        Button availabilityInfoButton = findViewById(R.id.availability_info);

        Button signOutButton = findViewById(R.id.sign_out);


        updatePreferences();

        getProfile(EditProfileListActivity.this);
        accountInfoButton.setOnClickListener(v -> {
            Intent newIntent = new Intent(EditProfileListActivity.this,
                    AccountInformationActivity.class);
            startActivity(newIntent);

        });

        uniInfoButton.setOnClickListener(v -> {
            Intent newIntent = new Intent(EditProfileListActivity.this,
                    UniversityInformationActivity.class);
            startActivity(newIntent);
        });

        locationInfoButton.setOnClickListener(v -> {
            Intent newIntent = new Intent(EditProfileListActivity.this,
                    LocationInformationActivity.class);
            startActivity(newIntent);
        });

        availabilityInfoButton.setOnClickListener(v -> {
            Intent newIntent = new Intent(EditProfileListActivity.this,
                    AvailabilityActivity.class);
            startActivity(newIntent);
        });


        signOutButton.setOnClickListener(v -> {
            clearPreferences();

            Intent newIntent = new Intent(EditProfileListActivity.this,
                    MainActivity.class);
            startActivity(newIntent);
        });
    }

    @Override
    public void onBackPressed() {
        // Check the current view and decide what action to take
        super.onBackPressed();
        SharedPreferences sharedPreferences = getSharedPreferences("AccountPreferences", Context.MODE_PRIVATE);
        String userType = sharedPreferences.getString("userType", ""); // Assuming the key for user type is "type"
        Intent newIntent;
        switch (userType) {
            case "tutee":
                newIntent = new Intent(EditProfileListActivity.this, TuteeHomeActivity.class);
                break;
            case "tutor":
                newIntent = new Intent(EditProfileListActivity.this, TutorHomeActivity.class);
                break;
            default:
                // Handle unexpected cases or errors here
                Toast.makeText(this, "Invalid user type", Toast.LENGTH_SHORT).show();
                return;
        }
        startActivity(newIntent);
    }


    private void updatePreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("AccountPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isEditing",true);
        editor.apply();
    }

    // ChatGPT usage: Yes
    private void clearPreferences() {
        Context context = getApplicationContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences("AccountPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.clear(); // Clears all the data in the SharedPreferences file
        editor.apply(); // Apply the changes
    }

}