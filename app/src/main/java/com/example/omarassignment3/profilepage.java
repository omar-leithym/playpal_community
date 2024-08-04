package com.example.omarassignment3;

import static android.app.PendingIntent.getActivity;
import static android.content.ContentValues.TAG;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.GridLayout;
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

import java.util.List;
import java.util.ResourceBundle;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class profilepage extends AppCompatActivity {
    private static final int REQUEST_CODE_PROFILE_PIC = 100;
    private static final int REQUEST_CODE_OTHER_PICS = 101;

    private static final String TAG = profilepage.class.getSimpleName();
    private TextView bioTextView;
    private ApiService apiService;
    String sImage;
    private ImageView profilePicture;
    private Button uploadPfpButton;
    private String StringUrl;
    private Button uploadPictures;
    private String userEmail; // Assuming you set this somewhere in your app
    private TextView emailTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_page);
        profilePicture = (ImageView) findViewById(R.id.roundImageView);
        // Initialize ApiService

        uploadPfpButton=findViewById(R.id.uploadPicture);
        uploadPictures=findViewById(R.id.uploadPictures);

        uploadPfpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                selectImage();

            }});
        uploadPictures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                selectImage2(); //code is 101

            }});
        apiService = ApiClient.getClient().create(ApiService.class);

        // Example assuming userEmail is set elsewhere (e.g., from login)
        emailTextView = findViewById(R.id.emailField);
        Intent intent = getIntent();
        userEmail = intent.getStringExtra("email");
        // userEmail=emailTextView.getText().toString();


        // Find views
        bioTextView = findViewById(R.id.bioTextView);

        // Apply system bars insets to the main layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Call method to retrieve bio
        retrieveBio(userEmail);
        //retrieveProfilePic(userEmail);
        retrieveProfile(userEmail);

//        TextView profileEmail=findViewById(R.id.emailField);
//        profileEmail.setText(userEmail);
        Log.e(TAG,"StringURL: "+StringUrl);
        Glide.with(this).load(StringUrl).into(profilePicture);
    }
    private void selectImage(){
            //clear current image
        profilePicture.setImageBitmap(null);
        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent,"Select Image"),100);
    }
    private void selectImage2(){
        //clear current image
        profilePicture.setImageBitmap(null);
        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent,"Select Image"),101);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // check condition
        if ( resultCode==RESULT_OK && data!=null)
        {
            // when result is ok
            // initialize uri
            Uri uri=data.getData();
            // Initialize bitmap
            try {
                Bitmap bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
                // initialize byte stream
                ByteArrayOutputStream stream=new ByteArrayOutputStream();
                // compress Bitmap
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);
                // Initialize byte array
                byte[] bytes=stream.toByteArray();
                // get base64 encoded string
                sImage= Base64.encodeToString(bytes,Base64.DEFAULT);
                // set encoded text on textview
                //Now we have the encoded string in sImage.
                //now we must send the sImage to the database. Let's test this out first
                if(requestCode==100){
                    updateProfilePicURL(sImage);
                    if(sImage !=null){
                        Log.e(TAG,"sImage "+sImage);
                        displayPFP();
                    }

                }
                if(requestCode==101){
                    //call function to add images.
                    if(sImage!=null)
                        uploadPics(sImage);


                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void updateProfilePicURL(String profilePicURL) {
        //Log.d(TAG,"your big ah string: "+profilePicURL);
        //String profilePicURL = .getText().toString().trim();
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String email = sharedPreferences.getString("email", ""); // Get user email from SharedPreferences
        Log.e(TAG,"EMAIL: "+email);
        UpdateRequest request = new UpdateRequest(email, "", profilePicURL);
        Call<UpdateResponse> call = apiService.updateProfilePicURL(request);
        // Call the changePassword API endpoint
        // Perform API call to update profile picture URL
        call.enqueue(new Callback<UpdateResponse>() {
            @Override
            public void onResponse(Call<UpdateResponse> call, Response<UpdateResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(profilepage.this, "Profile picture URL updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(profilepage.this, "Failed to update profile picture URL", Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onFailure(Call<UpdateResponse> call, Throwable t) {
                Toast.makeText(profilepage.this, "Network error, please try again", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void uploadPics(String profilePicURL) {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "");
        Log.e(TAG, "EMAIL: " + email);
        UploadPictureRequest request = new UploadPictureRequest(email, profilePicURL, "def");
        Call<UploadPictureResponse> call = apiService.uploadPicture(request);

        call.enqueue(new Callback<UploadPictureResponse>() {
            @Override
            public void onResponse(Call<UploadPictureResponse> call, Response<UploadPictureResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(profilepage.this, "Pics uploaded successfully", Toast.LENGTH_SHORT).show();
                    // Refresh the profile page to reflect the newly uploaded images
                    retrieveProfile(email);
                } else {
                    Toast.makeText(profilepage.this, "Failed to upload pics", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UploadPictureResponse> call, Throwable t) {
                Toast.makeText(profilepage.this, "Network error, please try again", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void displayPFP(){
        // decode base64 string
        if(sImage!=null){
            byte[] bytes=Base64.decode(sImage,Base64.DEFAULT);
            // Initialize bitmap
            Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
            // set bitmap on imageView
            profilePicture.setImageBitmap(bitmap);
        }

    }
    private void loadImage(String url, ImageView imageView) {
        Glide.with(this).load(url).dontAnimate().into(imageView);
//        byte[] bytes=Base64.decode(url,Base64.DEFAULT);
//        // Initialize bitmap
//        Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
//        // set bitmap on imageView
//        profilePicture.setImageBitmap(bitmap);
        //would need to change this to just de-code data
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
                            ImageView imageView = new ImageView(profilepage.this);
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

                        Button button = new Button(profilepage.this);
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
                    Toast.makeText(profilepage.this, "Failed to retrieve profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                Log.e(TAG, "Error retrieving profile: " + t.getMessage());
                Toast.makeText(profilepage.this, "Error retrieving profile", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(profilepage.this, "Failed to retrieve bio", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BioResponse> call, Throwable t) {
                Log.e(TAG, "Error retrieving bio: " + t.getMessage());

                Toast.makeText(profilepage.this, "Error retrieving bio", Toast.LENGTH_SHORT).show();
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
                        byte[] bytes=Base64.decode(StringUrl,Base64.DEFAULT);
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
                    Toast.makeText(profilepage.this, "Failed to retrieve profile picture", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PicResponse> call, Throwable t) {
                Log.e(TAG, "Error retrieving profile picture: " + t.getMessage());
                Toast.makeText(profilepage.this, "Error retrieving profile picture", Toast.LENGTH_SHORT).show();
            }
        });
    }

}




