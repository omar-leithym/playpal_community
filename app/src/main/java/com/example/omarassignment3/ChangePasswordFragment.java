package com.example.omarassignment3;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
 //NEEDS EMAIL AS INTENT. MOST FRAGMENTS/ACTIVITIES DO
public class ChangePasswordFragment extends Fragment {

    private ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);

        apiService = ApiClient.getClient().create(ApiService.class);

        EditText oldPassword = view.findViewById(R.id.oldPassword);
        EditText newPassword = view.findViewById(R.id.newPassword);
        EditText confirmPassword = view.findViewById(R.id.confirmPassword);
        Button changePasswordButton = view.findViewById(R.id.changePasswordButton);

        changePasswordButton.setOnClickListener(v -> {
            String oldPass = oldPassword.getText().toString();
            String newPass = newPassword.getText().toString();
            String confirmPass = confirmPassword.getText().toString();

            if (!newPass.equals(confirmPass)) {
                Toast.makeText(getActivity(), "New passwords do not match", Toast.LENGTH_SHORT).show();
            } else {
                // Call API to change password
                changePassword(oldPass, newPass);
            }
        });

        return view;
    }

    private void changePassword(String oldPassword, String newPassword) {
        // Replace with actual email retrieval logic if needed
        String email = "omar"; // Replace with actual user's email or retrieve dynamically
        //must be passed as intent ^^^

        // Create ChangePasswordRequest object
        ChangePasswordRequest request = new ChangePasswordRequest(email, oldPassword, newPassword);

        // Call the changePassword API endpoint
        Call<ChangePasswordResponse> call = apiService.changePassword(request);

        // Execute the call asynchronously
        call.enqueue(new Callback<ChangePasswordResponse>() {
            @Override
            public void onResponse(Call<ChangePasswordResponse> call, Response<ChangePasswordResponse> response) {
                if (response.isSuccessful()) {
                    ChangePasswordResponse changePasswordResponse = response.body();
                    if (changePasswordResponse != null) {
                        Toast.makeText(getActivity(), changePasswordResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        // Handle success, maybe navigate back or show a success message
                    } else {
                        Toast.makeText(getActivity(), "Failed to change password", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Failed to change password", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ChangePasswordResponse> call, Throwable t) {
                Toast.makeText(getActivity(), "Failed to change password: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
