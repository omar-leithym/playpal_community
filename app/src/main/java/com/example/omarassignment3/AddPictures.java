package com.example.omarassignment3;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddPictures extends AppCompatActivity {

    private EditText editTextPictureUrl;
    private Button buttonConfirm;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_pictures);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editTextPictureUrl = findViewById(R.id.editTextPictureUrl);
        buttonConfirm = findViewById(R.id.buttonConfirm);

        // Initialize ApiService using ApiClient
        apiService = ApiClient.getClient().create(ApiService.class);

        buttonConfirm.setOnClickListener(v -> {
            String pictureUrl = editTextPictureUrl.getText().toString().trim();
            if (!pictureUrl.isEmpty()) {
                uploadPicture(pictureUrl);
            } else {
                Toast.makeText(AddPictures.this, "Please enter a picture URL", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadPicture(String pictureUrl) {
        // Assuming email is retrieved from SharedPreferences or any other source
        String email = "omar"; // Replace with actual email retrieval logic
        UploadPictureRequest request = new UploadPictureRequest(email, pictureUrl, "default");

        Call<UploadPictureResponse> call = apiService.uploadPicture(request);
        call.enqueue(new Callback<UploadPictureResponse>() {
            @Override
            public void onResponse(Call<UploadPictureResponse> call, Response<UploadPictureResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        Toast.makeText(AddPictures.this, "Picture uploaded successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AddPictures.this, "Failed to upload picture: " + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AddPictures.this, "Failed to upload picture", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UploadPictureResponse> call, Throwable t) {
                Toast.makeText(AddPictures.this, "Network error, please try again", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
