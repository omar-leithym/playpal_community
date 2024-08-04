// NotificationsFragment.java
package com.example.omarassignment3;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class NotificationsFragment extends Fragment {

    private static final String TAG = "NotificationsFragment";
    private List<MatchRequest> pendingRequests;
    private NotificationsAdapter notificationsAdapter;
    private RecyclerView recyclerView;
    private TextView noNotificationsTextView; // Add this

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_notifications);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        noNotificationsTextView = view.findViewById(R.id.text_no_notifications); // Initialize this

        fetchPendingRequests();

        return view;
    }

    private void fetchPendingRequests() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        int userId = sharedPreferences.getInt("id", -1);

        if (userId == -1) {
            Toast.makeText(getContext(), "User ID not found in shared preferences", Toast.LENGTH_SHORT).show();
            return;
        }

        new FetchPendingRequestsTask().execute(userId);
    }

    private class FetchPendingRequestsTask extends AsyncTask<Integer, Void, String> {

        @Override
        protected String doInBackground(Integer... params) {
            int userId = params[0];
            String urlString = "http://10.0.2.2:3001/pending-requests/" + userId;

            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(urlString);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                int responseCode = urlConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    reader.close();
                    return stringBuilder.toString();
                } else {
                    Log.e(TAG, "Server returned HTTP " + responseCode + " " + urlConnection.getResponseMessage());
                    return null;
                }
            } catch (Exception e) {
                Log.e(TAG, "Error: ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                try {
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<MatchRequest>>() {}.getType();
                    pendingRequests = gson.fromJson(result, listType);
                    // Update the UI with the fetched data
                    Log.d(TAG, "Fetched pending requests: " + pendingRequests);

                    // Initialize and set the adapter
                    notificationsAdapter = new NotificationsAdapter(getContext(), pendingRequests);
                    recyclerView.setAdapter(notificationsAdapter);

                    // Show or hide the "No notifications" message
                    if (pendingRequests != null && !pendingRequests.isEmpty()) {
                        noNotificationsTextView.setVisibility(View.GONE);
                    } else {
                        noNotificationsTextView.setVisibility(View.VISIBLE);
                    }

                } catch (Exception e) {
                    Log.e(TAG, "JSON Parsing error: ", e);
                    Toast.makeText(getContext(), "Failed to parse pending requests", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Failed to fetch pending requests", Toast.LENGTH_SHORT).show();
            }
        }
    }
}




