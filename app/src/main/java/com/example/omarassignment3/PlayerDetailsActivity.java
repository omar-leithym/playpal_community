package com.example.omarassignment3;

import static android.app.PendingIntent.getActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlayerDetailsActivity extends AppCompatActivity {
    ImageView imageViewPlayer = findViewById(R.id.imageViewPlayer);
    private ApiService apiService;
    private String StringUrl;
    private String email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        setContentView(R.layout.activity_player_details);
        apiService = ApiClient.getClient().create(ApiService.class);
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String userEmail = sharedPreferences.getString("email", "User");
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> onBackPressed());

        // Retrieve player's information from intent extras
        int playerImageResId = getIntent().getIntExtra("playerImageResId", R.drawable.john);
        String playerName = getIntent().getStringExtra("playerName");

        // Set player's photo and name in the layout
        //ImageView imageViewPlayer = findViewById(R.id.imageViewPlayer);
        TextView textViewPlayerName = findViewById(R.id.textViewPlayerName);

       // imageViewPlayer.setImageResource(playerImageResId);
       retrieveProfilePic(email);
        textViewPlayerName.setText(playerName);
    }
    private void retrieveProfilePic(String email) {
       // Log.e(TAG, email.strip().trim());
        imageViewPlayer.setImageBitmap(null);
        Call<PicResponse> call = apiService.getPic(email.strip());
        call.enqueue(new Callback<PicResponse>() {
            @Override
            public void onResponse(Call<PicResponse> call, Response<PicResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PicResponse picResponse = response.body();
                  //  Log.d(TAG, "Full Response: " + picResponse.toString());
                    StringUrl = picResponse.getProfilePicURL();
                   // Log.e(TAG, "StringURL INSIDE RETRIEVE: " + StringUrl);
                    // Glide.with(this).load(url).dontAnimate().into(imageView);
                    if (StringUrl!=null){
                        byte[] bytes= Base64.decode(StringUrl,Base64.DEFAULT);
                        // Initialize bitmap
                        Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                        // set bitmap on imageView
                        imageViewPlayer.setImageBitmap(bitmap);
                        // Load the image using Glide
                        //Glide.with(profilepage.this).load(StringUrl).into(profilePicture);
                    }
                    else{
                        imageViewPlayer.setImageBitmap(null);
                    }

                } else {
                   // Log.e(TAG, "Failed to retrieve profile picture. Response code: " + response.code());
                    Toast.makeText(PlayerDetailsActivity.this, "Failed to retrieve profile picture", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PicResponse> call, Throwable t) {
             //   Log.e(TAG, "Error retrieving profile picture: " + t.getMessage());
                Toast.makeText(PlayerDetailsActivity.this, "Error retrieving profile picture", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
