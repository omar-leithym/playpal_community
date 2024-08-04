package com.example.omarassignment3;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatFragment extends Fragment {

    private RecyclerView recyclerViewChats;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessages;
    private EditText editTextMessage;
    private ImageButton buttonSend;
    private TextView textViewUserName;
    private ImageView imageViewProfilePicture;
    private String user1Email;
    private String user2Email;
    private String userName;
    private String userPicUrl;
    private Handler handler = new Handler();
    private Runnable refreshRunnable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        if (getArguments() != null) {
            user1Email = getArguments().getString("user1_email");
            user2Email = getArguments().getString("user2_email");
        }

        ((Nav) requireActivity()).hideNavigationBar();


        recyclerViewChats = view.findViewById(R.id.recyclerViewChats);
        editTextMessage = view.findViewById(R.id.editTextMessage);
        buttonSend = view.findViewById(R.id.buttonSend);
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatMessages, user1Email);
        recyclerViewChats.setAdapter(chatAdapter);
        recyclerViewChats.setLayoutManager(new LinearLayoutManager(getContext()));

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        // Set user details
        textViewUserName = view.findViewById(R.id.textViewUserName);
        imageViewProfilePicture = view.findViewById(R.id.imageViewProfilePicture);

        // Fetch user details
        fetchUserDetails(user2Email);

        loadChatMessages();  // Load initial chat messages

        // Runnable to periodically refresh chat messages
        refreshRunnable = new Runnable() {
            @Override
            public void run() {
                loadChatMessages();
                handler.postDelayed(this, 2000); // Refresh every 2 seconds
            }
        };
        handler.post(refreshRunnable);  // Start refreshing

        // Handle back button press to pop back stack
        ImageButton backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
            ((Nav) requireActivity()).showNavigationBar();
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(refreshRunnable);  // Stop refreshing on fragment destroy
    }

    // Method to fetch user details from server
    private void fetchUserDetails(String userEmail) {
        String url = "http://10.0.2.2:3001/api/user?userEmail=" + userEmail;

        RequestQueue queue = Volley.newRequestQueue(requireContext());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            userName = response.getString("name");
                            userPicUrl = response.getString("profilePicUrl");

                            // Update UI with fetched details
                            textViewUserName.setText(userName);
//

                            if (userPicUrl != null && !userPicUrl.isEmpty()) {
                                Picasso.get().load(userPicUrl)
                                        .placeholder(R.drawable.default_profile_pic)
                                        .error(R.drawable.default_profile_pic)
                                        .into(imageViewProfilePicture, new Callback() {
                                            @Override
                                            public void onSuccess() {
                                                Log.d("Picasso", "Image loaded successfully");
                                            }

                                            @Override
                                            public void onError(Exception e) {
                                                Log.e("Picasso", "Error loading image: " + e.getMessage());
                                            }
                                        });
                            } else {
                                imageViewProfilePicture.setImageResource(R.drawable.default_profile_pic);
                            }

                            Log.d("ChatFragment", "Fetched user details - Name: " + userName + ", Profile Pic URL: " + userPicUrl);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("ChatFragment", "JSON exception while parsing user details", e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("ChatFragment", "Error fetching user details", error);
                        Toast.makeText(requireContext(), "Error fetching user details", Toast.LENGTH_SHORT).show();
                    }
                });

        queue.add(request);
    }

    // Method to load chat messages from server
    private void loadChatMessages() {
        String url = "http://10.0.2.2:3001/api/chats?user1_email=" + user1Email + "&user2_email=" + user2Email;

        RequestQueue queue = Volley.newRequestQueue(requireContext());
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        boolean atBottom = !recyclerViewChats.canScrollVertically(1);
                        chatMessages.clear();
                        String lastDate = null;
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject chatJson = response.getJSONObject(i);
                                String timestamp = chatJson.getString("timestamp");
                                String currentDate = formatDate(timestamp);
                                if (lastDate == null || !lastDate.equals(currentDate)) {
                                    chatMessages.add(new ChatMessage(currentDate));
                                    lastDate = currentDate;
                                }
                                ChatMessage chatMessage = new ChatMessage(
                                        chatJson.getString("sender_email"),
                                        chatJson.getString("receiver_email"),
                                        chatJson.getString("message"),
                                        timestamp
                                );
                                chatMessages.add(chatMessage);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        chatAdapter.notifyDataSetChanged();
                        if (atBottom) {
                            recyclerViewChats.scrollToPosition(chatMessages.size() - 1);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("ChatFragment", "Error loading chat messages", error);
                        Toast.makeText(requireContext(), "Error loading chat messages", Toast.LENGTH_SHORT).show();
                    }
                });

        queue.add(request);
    }

    // Method to send a message
    private void sendMessage() {
        String message = editTextMessage.getText().toString().trim();
        if (message.isEmpty()) {
            return;
        }

        String url = "http://10.0.2.2:3001/api/chats";
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("sender_email", user1Email);
            jsonBody.put("receiver_email", user2Email);
            jsonBody.put("message", message);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        editTextMessage.setText("");  // Clear message input
                        loadChatMessages();  // Refresh chat messages after sending
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("ChatFragment", "Error sending message", error);
                        Toast.makeText(requireContext(), "Error sending message", Toast.LENGTH_SHORT).show();
                    }
                });

        RequestQueue queue = Volley.newRequestQueue(requireContext());
        queue.add(request);
    }

    // Method to format timestamp to a readable date format
    private String formatDate(String timestamp) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            Date date = sdf.parse(timestamp);
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);
            return dateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

}
