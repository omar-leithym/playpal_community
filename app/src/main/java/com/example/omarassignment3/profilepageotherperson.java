package com.example.omarassignment3;


import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import com.bumptech.glide.Glide;


import org.json.JSONObject;


import java.io.IOException;
import java.util.List;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class profilepageotherperson extends AppCompatActivity {


    private static final String TAG = profilepage.class.getSimpleName();
    private TextView bioTextView;
    private ApiService apiService;
    private boolean areFriends = false;
    private ImageView profilePicture;
    private String StringUrl;
    private String userEmail; // Assuming you set this somewhere in your app
    private TextView emailTextView;
    private String yourEmail;
    private Button friendshipButton;

    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile_page_other_person);
        profilePicture = (ImageView) findViewById(R.id.roundImageView);
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String emaill = sharedPreferences.getString("email", "User");
        yourEmail = emaill;
        apiService = ApiClient.getClient().create(ApiService.class);

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        emailTextView = findViewById(R.id.emailField);
        intent = getIntent();
        userEmail = intent.getStringExtra("email");


        bioTextView = findViewById(R.id.bioTextView);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        new CheckFriendshipTask().execute(yourEmail, userEmail);
        retrieveBio(userEmail);
        retrieveProfilePic(userEmail);
        retrieveProfile(userEmail);
        Glide.with(this).load(StringUrl).into(profilePicture);
    }


    private class CheckFriendshipTask extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... params) {
            String user1 = params[0];
            String user2 = params[1];


            Call<CheckFriendShipResponse> call = apiService.checkfriendship(user1, user2);
            try {
                Response<CheckFriendShipResponse> response = call.execute();
                if (response.isSuccessful() && response.body() != null) {
                    return response.body().getMessage();
                } else {
                    return "not_friends";
                }
            } catch (IOException e) {
                e.printStackTrace();
                return "error";
            }
        }


        @Override
        protected void onPostExecute(String result) {
            if (result.equals("friends")) {
                areFriends = true;
                setupRemoveFriendButton();
            } else {
                areFriends = false;
                setupAddFriendButton();
            }
        }
    }


    private void setupAddFriendButton() {
        LinearLayout profileInfoContainer = findViewById(R.id.profileInfoContainer);

        if(friendshipButton!=null){
            profileInfoContainer.removeView(friendshipButton);
        }
        friendshipButton = new Button(this);
        friendshipButton.setId(ViewCompat.generateViewId());
        friendshipButton.setText("Add Friend");
        friendshipButton.setBackground(ContextCompat.getDrawable(this, R.drawable.button_background));

        friendshipButton.setOnClickListener(v -> {
            if (!areFriends) {
                new AddFriendTask().execute(yourEmail, userEmail);
            }
        });


        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, getResources().getDimensionPixelSize(R.dimen.button_margin), 10, 0);
        friendshipButton.setLayoutParams(layoutParams);

        profileInfoContainer.addView(friendshipButton);
    }


    private void setupRemoveFriendButton() {
        LinearLayout profileInfoContainer = findViewById(R.id.profileInfoContainer);
        if(friendshipButton!=null){
            profileInfoContainer.removeView(friendshipButton);
        }

        friendshipButton = new Button(this);
        friendshipButton.setId(ViewCompat.generateViewId());
        friendshipButton.setText("Remove Friend");
        friendshipButton.setBackground(ContextCompat.getDrawable(this, R.drawable.button_background));

        //friendshipButton.setPadding(15,0,15,0);

        friendshipButton.setOnClickListener(v -> {
            if (areFriends) {
                new RemoveFriendTask().execute(yourEmail, userEmail);
            }
        });


        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, getResources().getDimensionPixelSize(R.dimen.button_margin), 0, 0);
        friendshipButton.setLayoutParams(layoutParams);


        profileInfoContainer.addView(friendshipButton);
    }


    private class AddFriendTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            String user1 = params[0];
            String user2 = params[1];
            SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
            int ourId = sharedPreferences.getInt("id", -1);
            int hisID=intent.getIntExtra("id",-1);

            SendMatchRequest request = new SendMatchRequest(ourId,hisID);

            Call<SendMatchResponse> call = apiService.sendMatchRequest(request);
            call.enqueue(new Callback<SendMatchResponse>() {
                @Override
                public void onResponse(Call<SendMatchResponse> call, Response<SendMatchResponse> response) {
                    if (response.isSuccessful()) {
                        SendMatchResponse sendMatchResponse = response.body();
                        // Handle the response
                        Log.d("SendMatchRequest", "Match request sent, ID: " + sendMatchResponse.getId());
                    } else {
                        Log.e("SendMatchRequest", "Error: " + response.errorBody().toString());
                    }
                }

                @Override
                public void onFailure(Call<SendMatchResponse> call, Throwable t) {
                    Log.e("SendMatchRequest", "Failure: " + t.getMessage());
                }
            });
            try {
                Response<SendMatchResponse> response = call.execute();
                return response.isSuccessful() && response.body() != null;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }


        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                areFriends = true;
                Toast.makeText(profilepageotherperson.this, "Friendship added successfully", Toast.LENGTH_SHORT).show();
                setupRemoveFriendButton();
            } else {
                Toast.makeText(profilepageotherperson.this, "Failed to add friendship", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private class RemoveFriendTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            String user1 = params[0];
            String user2 = params[1];


            RemoveFriendRequest removeFriendRequest = new RemoveFriendRequest(user1, user2);
            Call<RemoveFriendResponse> call = apiService.removeFriend(removeFriendRequest);
            try {
                Response<RemoveFriendResponse> response = call.execute();
                return response.isSuccessful() && response.body() != null;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }


        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                areFriends = false;
                Toast.makeText(profilepageotherperson.this, "Friendship removed successfully", Toast.LENGTH_SHORT).show();
                setupAddFriendButton();
            } else {
                Toast.makeText(profilepageotherperson.this, "Failed to remove friendship", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void loadImage(String url, ImageView imageView) {
        Glide.with(this).load(url).dontAnimate().into(imageView);
    }
    private void retrieveProfile(String email) {
        retrieveProfilePic(email);
        Call<ProfileResponse> call = apiService.getProfile(email);
        TextView profileEmail = findViewById(R.id.emailField);

        call.enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String profileUsername = response.body().getUsername();
                    profileEmail.setText(profileUsername);
                    String profileName = response.body().getName();
                    TextView usernameTextView = findViewById(R.id.username);
                    usernameTextView.setText(profileName);
                    String profileBio = response.body().getBio();
                    List<String> urlPics = response.body().getUrlPics();
                    List<String> sports = response.body().getSports();
                    List<String> skillLevelSport = response.body().getSkillLevelSport();

                    // Update Image Grid
                    GridLayout gridLayout = findViewById(R.id.gridLayout);
                    gridLayout.removeAllViews(); // Clear existing views if any

                    if (urlPics != null && !urlPics.isEmpty()) {
                        for (String url : urlPics) {
                            ImageView imageView = new ImageView(profilepageotherperson.this);
                            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                            params.width = 300;
                            params.height = 300;
                            params.setMargins(8, 8, 8, 8);
                            imageView.setLayoutParams(params);
                            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

                            // Load image from URL
                            if (url!=null){
                                byte[] bytes = Base64.decode(url, Base64.DEFAULT);
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                imageView.setImageBitmap(bitmap);
                                gridLayout.addView(imageView);
                            }

                        }
                    } else {
                        Log.e(TAG, "URL Pics not available or empty");
                    }

                    // Update Favorite Sports
                    LinearLayout favoriteSportsLinearLayout = findViewById(R.id.favoriteSportsLinearLayout);
                    favoriteSportsLinearLayout.removeAllViews(); // Clear existing views if any

                    int size = Math.min(sports.size(), skillLevelSport.size());
                    for (int i = 0; i < size; i++) {
                        String sport = sports.get(i);
                        String skill = skillLevelSport.get(i);

                        Button button = new Button(profilepageotherperson.this);
                        if (skill.equals("pro")) {
                            button.setBackground(getDrawable(R.drawable.button_pro));
                        } else if (skill.equals("medium")) {
                            button.setBackground(getDrawable(R.drawable.button_medium));
                        } else {
                            button.setBackground(getDrawable(R.drawable.button_beginner));
                        }

                        button.setText(sport);
                        button.setTextColor(Color.WHITE);
                        button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);

                        int paddingPx = getResources().getDimensionPixelSize(R.dimen.button_padding);
                        button.setMinHeight(0);
                        button.setMinWidth(0);
                        button.setPadding(0, 0, 0, 0);
                        button.setWidth(WRAP_CONTENT);
                        button.setHeight(WRAP_CONTENT);
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        layoutParams.setMargins(0, 0, getResources().getDimensionPixelSize(R.dimen.button_margin), 0);
                        button.setLayoutParams(layoutParams);

                        favoriteSportsLinearLayout.addView(button);
                        // displayPFP();
                    }
                } else {
                    Log.e(TAG, "Failed to retrieve Profile. Response code: " + response.code());
                    Toast.makeText(profilepageotherperson.this, "Failed to retrieve profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                Log.e(TAG, "Error retrieving profile: " + t.getMessage());
                Toast.makeText(profilepageotherperson.this, "Error retrieving profile", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void retrieveBio(String email) {
        // Call API to get bio based on email
        Call<BioResponse> call = apiService.getBio(email);


        call.enqueue(new Callback<BioResponse>() {
            @Override
            public void onResponse(Call<BioResponse> call, Response<BioResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String bio = response.body().getBio();
                    bioTextView.setText(bio);
                } else {
                    Log.e(TAG, "Failed to retrieve bio. Response code: " + response.code());
                    Toast.makeText(profilepageotherperson.this, "Failed to retrieve bio", Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onFailure(Call<BioResponse> call, Throwable t) {
                Log.e(TAG, "Error retrieving bio: " + t.getMessage());


                Toast.makeText(profilepageotherperson.this, "Error retrieving bio", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void retrieveProfilePic(String email) {
        Log.e(TAG, email.strip().trim());
        profilePicture.setImageBitmap(null);
        Call<PicResponse> call = apiService.getPic(email.strip());
        call.enqueue(new Callback<PicResponse>() {
            @Override
            public void onResponse(Call<PicResponse> call, Response<PicResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PicResponse picResponse = response.body();
                    Log.d(TAG, "Full Response: " + picResponse.toString());
                    StringUrl = picResponse.getProfilePicURL();
                    Log.e(TAG, "StringURL INSIDE RETRIEVE: " + StringUrl);
                    // Glide.with(this).load(url).dontAnimate().into(imageView);
                    if (StringUrl!=null){
                        byte[] bytes= Base64.decode(StringUrl,Base64.DEFAULT);
                        // Initialize bitmap
                        Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                        // set bitmap on imageView
                        profilePicture.setImageBitmap(bitmap);
                        // Load the image using Glide
                        //Glide.with(profilepage.this).load(StringUrl).into(profilePicture);
                    }
                    else{
                        profilePicture.setImageBitmap(null);
                    }

                } else {
                    Log.e(TAG, "Failed to retrieve profile picture. Response code: " + response.code());
                    Toast.makeText(profilepageotherperson.this, "Failed to retrieve profile picture", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PicResponse> call, Throwable t) {
                Log.e(TAG, "Error retrieving profile picture: " + t.getMessage());
                Toast.makeText(profilepageotherperson.this, "Error retrieving profile picture", Toast.LENGTH_SHORT).show();
            }
        });
    }




}

