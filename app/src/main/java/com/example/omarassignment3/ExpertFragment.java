package com.example.omarassignment3;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ExpertFragment extends Fragment {

    private static final String ARG_SPORT = "selectedSport";

    private List<Player> playerList = new ArrayList<>();
    private PlayerAdapter adapter;
    private String selectedSport;

    public static ExpertFragment newInstance(String sport) {
        ExpertFragment fragment = new ExpertFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SPORT, sport);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            selectedSport = getArguments().getString(ARG_SPORT);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_matchmaking, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initial data loading
        adapter = new PlayerAdapter(getContext(), playerList, getParentFragmentManager());
        recyclerView.setAdapter(adapter);
        new FetchPlayersTask(getContext(), null, null, selectedSport).execute(); // Load all players initially

        ImageButton backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        ImageButton filterButton = view.findViewById(R.id.filterButton);
        filterButton.setOnClickListener(v -> showFilterDialog());

        return view;
    }

    private void showFilterDialog() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View filterView = inflater.inflate(R.layout.dialog_filter2, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(filterView);
        AlertDialog dialog = builder.create();
        dialog.show();

        Spinner locationSpinner = filterView.findViewById(R.id.locationSpinner);
        Spinner genderSpinner = filterView.findViewById(R.id.genderSpinner);
        Button applyButton = filterView.findViewById(R.id.applyButton);

        applyButton.setOnClickListener(v -> {
            String location = locationSpinner.getSelectedItem().toString();
            String gender = genderSpinner.getSelectedItem().toString();

            // Fetch and update the RecyclerView with filtered data
            new FetchPlayersTask(getContext(), location, gender, selectedSport).execute();

            dialog.dismiss();
        });
    }

    private class FetchPlayersTask extends AsyncTask<Void, Void, List<Player>> {
        private Context context;
        private String location;
        private String gender;
        private String sport;

        FetchPlayersTask(Context context, String location, String gender, String sport) {
            this.context = context;
            this.location = location;
            this.gender = gender;
            this.sport = sport;
        }

        @Override
        protected List<Player> doInBackground(Void... voids) {
            List<Player> players = new ArrayList<>();
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            try {
                // Get token from SharedPreferences
                SharedPreferences sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
                String token = sharedPreferences.getString("token", null);
                int userId = sharedPreferences.getInt("id", -1);

                // Construct the URL
                String baseUrl = "http://10.0.2.2:3001/matchmake-experts";
                String query = String.format("?location=%s&gender=%s&sport=%s", location, gender, sport);
                URL url = new URL(baseUrl + query);
                Log.d(TAG, "URL: " + url);

                // Open connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");

                // Add the token as a header
                if (token != null) {
                    urlConnection.setRequestProperty("Authorization", "Bearer " + token);
                }

                urlConnection.connect();

                // Check for HTTP error codes
                int responseCode = urlConnection.getResponseCode();
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    InputStream errorStream = urlConnection.getErrorStream();
                    StringBuilder errorBuffer = new StringBuilder();
                    if (errorStream != null) {
                        reader = new BufferedReader(new InputStreamReader(errorStream));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            errorBuffer.append(line).append("\n");
                        }
                        reader.close();
                    }
                    throw new IOException("HTTP error code: " + responseCode + "\n" + errorBuffer.toString());
                }

                // Read the input stream
                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }

                String jsonResponse = buffer.toString();

                // Print the response for debugging
                System.out.println("Response: " + jsonResponse);

                // Parse the JSON response
                JSONArray playerArray = new JSONArray(jsonResponse);
                for (int i = 0; i < playerArray.length(); i++) {
                    JSONObject playerJson = playerArray.getJSONObject(i);
                    String firstName = playerJson.getString("firstName");
                    String lastName = playerJson.getString("lastName");
                    String level = playerJson.getString("level");
                    String email = playerJson.getString("email");
                    String imageUrl = playerJson.optString("profilePicURL");
                    int id = playerJson.optInt("id");
                    if(userId != id) {
                        Player player = new Player(imageUrl, firstName, lastName, level, email, id);
                        players.add(player);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return players;
        }

        @Override
        protected void onPostExecute(List<Player> players) {
            playerList.clear();
            playerList.addAll(players);
            adapter.notifyDataSetChanged();
        }
    }
}
