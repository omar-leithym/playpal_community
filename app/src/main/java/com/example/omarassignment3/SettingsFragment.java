package com.example.omarassignment3;


import static android.content.ContentValues.TAG;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import java.net.HttpURLConnection;
import java.net.URL;


public class SettingsFragment extends Fragment {
    private TextView languageText;
    private Switch darkModeSwitch;
    private SharedPreferences sharedPreferences, sharedPreferences2;
    private boolean isRecreating;
    private EditText addSportEditText;
    private EditText bioEditText, profilePicURLEditText;
    private Button updateBioButton, updateProfilePicButton, backButton; // Include backButton
    private ApiService apiService;
    private Spinner locationSpinner;
    private Button updateLocationButton;
    private Button addSportButton;
    private EditText skillLevelField;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Initialize EditTexts and Buttons for bio and profile pic URL
        bioEditText = view.findViewById(R.id.bioEditText);
        //profilePicURLEditText = view.findViewById(R.id.profilePicURLEditText);
        updateBioButton = view.findViewById(R.id.updateBioButton);
        updateLocationButton = view.findViewById(R.id.updateLocationButton);
        //updatePro filePicButton = view.findViewById(R.id.updateProfilePicButton);// Initialize backButton
        addSportButton=view.findViewById(R.id.addSportButton);
        // Set up click listeners for update bio and profile pic URL buttons
        updateBioButton.setOnClickListener(v -> updateBio());
        addSportButton.setOnClickListener(v->updateSport());
        apiService = ApiClient.getClient().create(ApiService.class);
        addSportEditText=view.findViewById(R.id.sportNameEditText);
        // Set the title
        TextView title = view.findViewById(R.id.profileSettingsTitle);
        title.setText("Profile Settings");

       // languageText = view.findViewById(R.id.languageText);
        darkModeSwitch = view.findViewById(R.id.darkModeSwitch);
        skillLevelField=view.findViewById(R.id.skillLevelEditText);

        // Set up shared preferences
        sharedPreferences = getActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE);
        sharedPreferences2 = getActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String appLanguage = sharedPreferences.getString("AppLanguage", "English");
        //languageText.setText(appLanguage);

        // Set up dark mode switch
        boolean isDarkMode = sharedPreferences.getBoolean("DarkMode", false);
        darkModeSwitch.setChecked(isDarkMode);
        isRecreating = false;

        locationSpinner = view.findViewById(R.id.locationSpinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.location_options,
                android.R.layout.simple_spinner_item
        );

// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

// Apply the adapter to the spinner
        locationSpinner.setAdapter(adapter);

        updateLocationButton.setOnClickListener(v -> updateLocation());


        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isRecreating) {
                sharedPreferences.edit().putBoolean("DarkMode", isChecked).apply();
                updateDarkMode(isChecked);
            }
        });

        // Add the logout button
        Button logoutButton = view.findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> {
            // Clear SharedPreferences
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
            getActivity().finish();
        });


        // Set up Terms of Service navigation
        LinearLayout termsLayout = view.findViewById(R.id.termsLayout);
        termsLayout.setOnClickListener(v -> {
            TermsFragment termsFragment = new TermsFragment();
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, termsFragment)
                    .addToBackStack(null)
                    .commit();
        });

        // Set up Change Password navigation
        LinearLayout changePasswordLayout = view.findViewById(R.id.changePasswordLayout);
        changePasswordLayout.setOnClickListener(v -> {
            ChangePasswordFragment changePasswordFragment = new ChangePasswordFragment();
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, changePasswordFragment)
                    .addToBackStack(null)
                    .commit();
        });

        // Set up Language selection navigation
//        view.findViewById(R.id.languageLayout).setOnClickListener(v -> {
//            LanguagesFragment languagesFragment = new LanguagesFragment();
//            getParentFragmentManager().beginTransaction()
//                    .replace(R.id.frame_layout, languagesFragment)
//                    .addToBackStack(null)
//                    .commit();
//        });

        return view;
    }
    private void updateSport(){
        String sportName = addSportEditText.getText().toString().trim();
        String email = sharedPreferences2.getString("email", ""); // Get user email from SharedPreferences
        String sportSkill=skillLevelField.getText().toString().trim();
        AddSportRequest request = new AddSportRequest(sportName, email, sportSkill);


        // Call the changePassword API endpoint
        Call<AddSportResponse> call = apiService.addSport(request);
        call.enqueue(new Callback<AddSportResponse>() {
            @Override
            public void onResponse(Call<AddSportResponse> call, Response<AddSportResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getActivity(), "Sport updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Failed to update Sport", Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onFailure(Call<AddSportResponse> call, Throwable t) {
                Toast.makeText(getActivity(), "Network error, please try again", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void updateBio() {
        String bio = bioEditText.getText().toString().trim();
        String email = sharedPreferences2.getString("email", ""); // Get user email from SharedPreferences

        // Ensure profilePicURLEditText is initialized if used
        // String profilePicURL = profilePicURLEditText != null ? profilePicURLEditText.getText().toString().trim() : "";

        UpdateRequest request = new UpdateRequest(email, bio, "");

        Call<UpdateResponse> call = apiService.updateBio(request);
        call.enqueue(new Callback<UpdateResponse>() {
            @Override
            public void onResponse(Call<UpdateResponse> call, Response<UpdateResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getActivity(), "Bio updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Failed to update bio", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UpdateResponse> call, Throwable t) {
                Toast.makeText(getActivity(), "Network error, please try again", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void updateLocation() {
        // Get selected location from Spinner
        String selectedLocation = locationSpinner.getSelectedItem().toString().trim();

        // Get user email from SharedPreferences
        SharedPreferences sharedPreferences2 = getActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String email = sharedPreferences2.getString("email", "");

        // Perform network operation in a background thread
        new Thread(() -> {
            HttpURLConnection urlConnection = null;
            try {
                // Construct the URL
                String baseUrl = "http://10.0.2.2:3001/updatelocation";
                URL url = new URL(baseUrl + "?email=" + email + "&location=" + selectedLocation);

                // Open connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                // Check response code
                int responseCode = urlConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Success
                    getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Location updated successfully", Toast.LENGTH_SHORT).show());
                } else {
                    // Failure
                    getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Failed to update location", Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                e.printStackTrace();
                // Handle error
                getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "Network error, please try again", Toast.LENGTH_SHORT).show());
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
        }).start();
    }




    private void updateProfilePicURL() {
        String profilePicURL = profilePicURLEditText.getText().toString().trim();
        String email = sharedPreferences2.getString("email", ""); // Get user email from SharedPreferences
        Log.e(TAG,"EMAIL: "+email);
        UpdateRequest request = new UpdateRequest(email, "", profilePicURL);
        Call<UpdateResponse> call = apiService.updateProfilePicURL(request);
        // Call the changePassword API endpoint
        // Perform API call to update profile picture URL
        call.enqueue(new Callback<UpdateResponse>() {
            @Override
            public void onResponse(Call<UpdateResponse> call, Response<UpdateResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getActivity(), "Profile picture URL updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Failed to update profile picture URL", Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onFailure(Call<UpdateResponse> call, Throwable t) {
                Toast.makeText(getActivity(), "Network error, please try again", Toast.LENGTH_SHORT).show();
            }
        });
    }


//    public void onLanguageSelected(String language) {
//        // Update the displayed language in settings
//        if (getActivity() != null) {
//            getActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE)
//                    .edit()
//                    .putString("AppLanguage", language)
//                    .apply();
//            languageText.setText(language);
//        }
//    }


    private void updateDarkMode(boolean isDarkMode) {
        if (getActivity() != null) {
            isRecreating = true;
            AppCompatDelegate.setDefaultNightMode(isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
            getActivity().recreate();
        }
    }
    // Methods for updating bio and profile pic URL, and other utility methods...
}
