// NotificationsAdapter.java
package com.example.omarassignment3;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {

    private List<MatchRequest> matchRequests;
    private Context context;

    public NotificationsAdapter(Context context, List<MatchRequest> matchRequests) {
        this.context = context;
        this.matchRequests = matchRequests;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notification_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MatchRequest matchRequest = matchRequests.get(position);

        holder.firstName.setText(matchRequest.getFirstName());
        holder.lastName.setText(matchRequest.getLastName());
        String imageUrl = matchRequest.getProfileImageUrl();
        Log.d(TAG, "Profile image URL: " + imageUrl);
        Glide.with(context)
                .load(matchRequest.getProfileImageUrl())
                .into(holder.profileImage);

        holder.acceptButton.setOnClickListener(v -> {
            String url = "http://10.0.2.2:3001/addfriend";
            String deleteUrl = "http://10.0.2.2:3001/delete-match-request";

            new Thread(() -> {
                try {
                    SharedPreferences sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
                    String userEmail = sharedPreferences.getString("email", "User");
                    int userId = sharedPreferences.getInt("id", -1);
                    String targetUserEmail = matchRequest.getOtherEmail();
                    int targetUserId = matchRequest.getRequesterId();
                    URL addUrl = new URL(url);
                    HttpURLConnection addConnection = (HttpURLConnection) addUrl.openConnection();
                    addConnection.setRequestMethod("POST");
                    addConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    addConnection.setDoOutput(true);

                    String jsonInputString = "{\"email\": \"" + userEmail + "\", \"email2\": \"" + targetUserEmail + "\"}";
                    try(OutputStream os = addConnection.getOutputStream()) {
                        byte[] input = jsonInputString.getBytes("utf-8");
                        os.write(input, 0, input.length);
                    }

                    int addResponseCode = addConnection.getResponseCode();
                    if (addResponseCode == HttpURLConnection.HTTP_OK) {
                        // Delete Match Request
                        URL deleteMatchUrl = new URL(deleteUrl);
                        HttpURLConnection deleteConnection = (HttpURLConnection) deleteMatchUrl.openConnection();
                        deleteConnection.setRequestMethod("DELETE");
                        deleteConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                        deleteConnection.setDoOutput(true);

                        String deleteInputString = "{\"requester_id\": \"" + userId + "\", \"requested_id\": \"" + targetUserId + "\"}";
                        try(OutputStream os = deleteConnection.getOutputStream()) {
                            byte[] input = deleteInputString.getBytes("utf-8");
                            os.write(input, 0, input.length);
                        }

                        int deleteResponseCode = deleteConnection.getResponseCode();
                        if (deleteResponseCode == HttpURLConnection.HTTP_OK) {
                            Toast.makeText(context, "Friend Successfully added!", Toast.LENGTH_SHORT).show();
                        } else {
                            // Handle delete request error
                        }
                    } else {
                        // Handle add friend error
                    }

                    addConnection.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        });


        holder.declineButton.setOnClickListener(v -> {
            String deleteUrl = "http://10.0.2.2:3001/delete-match-request";

            new Thread(() -> {
                try {
                    SharedPreferences sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
                    int userId = sharedPreferences.getInt("id", -1);
                    int targetUserId = matchRequest.getRequesterId();
                    URL deleteMatchUrl = new URL(deleteUrl);
                    HttpURLConnection deleteConnection = (HttpURLConnection) deleteMatchUrl.openConnection();
                    deleteConnection.setRequestMethod("DELETE");
                    deleteConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    deleteConnection.setDoOutput(true);

                    String deleteInputString = "{\"requester_id\": \"" + userId + "\", \"requested_id\": \"" + targetUserId + "\"}";
                    try(OutputStream os = deleteConnection.getOutputStream()) {
                        byte[] input = deleteInputString.getBytes("utf-8");
                        os.write(input, 0, input.length);
                    }

                    int deleteResponseCode = deleteConnection.getResponseCode();
                    if (deleteResponseCode == HttpURLConnection.HTTP_OK) {
                        // Successfully deleted match request
                    } else {
                        // Handle delete request error
                    }

                    deleteConnection.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        });

    }

    @Override
    public int getItemCount() {
        return matchRequests.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImage;
        TextView firstName;
        TextView lastName;
        Button acceptButton;
        Button declineButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profile_image);
            firstName = itemView.findViewById(R.id.first_name);
            lastName = itemView.findViewById(R.id.last_name);
            acceptButton = itemView.findViewById(R.id.accept_button);
            declineButton = itemView.findViewById(R.id.decline_button);
        }
    }
}

