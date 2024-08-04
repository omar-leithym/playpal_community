package com.example.omarassignment3;

import static android.content.ContentValues.TAG;

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

public class TournamentFragment extends Fragment {

    private List<Tournament> tournamentList = new ArrayList<>();
    private TournamentAdapter adapter;
    private Button upcomingTournamentsButton;
    private Button myTournamentsButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tournament, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        upcomingTournamentsButton = view.findViewById(R.id.upcoming_tournaments);
        myTournamentsButton = view.findViewById(R.id.my_tournaments);

        upcomingTournamentsButton.setOnClickListener(v -> {
            // Update adapter to handle upcoming tournaments
            adapter = new TournamentAdapter(getContext(), tournamentList, getParentFragmentManager(), false);
            recyclerView.setAdapter(adapter);
            new FetchTournamentsTask(getContext(), "http://10.0.2.2:3001/upcoming-tournaments", false).execute();
        });

        myTournamentsButton.setOnClickListener(v -> {
            // Update adapter to handle my tournaments
            adapter = new TournamentAdapter(getContext(), tournamentList, getParentFragmentManager(), true);
            recyclerView.setAdapter(adapter);
            new FetchTournamentsTask(getContext(), "http://10.0.2.2:3001/tournaments/my", true).execute();
        });

        // Default to fetching upcoming tournaments
        adapter = new TournamentAdapter(getContext(), tournamentList, getParentFragmentManager(), false);
        recyclerView.setAdapter(adapter);
        new FetchTournamentsTask(getContext(), "http://10.0.2.2:3001/upcoming-tournaments", false).execute();

        ImageButton backButton = view.findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        return view;
    }


    private class FetchTournamentsTask extends AsyncTask<Void, Void, List<Tournament>> {
        private Context context;
        private String endpointUrl;
        private boolean isMyTournaments;

        FetchTournamentsTask(Context context, String endpointUrl, boolean isMyTournaments) {
            this.context = context;
            this.endpointUrl = endpointUrl;
            this.isMyTournaments = isMyTournaments;
        }

        @Override
        protected List<Tournament> doInBackground(Void... voids) {
            List<Tournament> tournaments = new ArrayList<>();
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            try {
                SharedPreferences sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
                String token = sharedPreferences.getString("token", null);
                int userId = sharedPreferences.getInt("id", -1); // Assuming user_id is stored in shared preferences

                URL url;
                if (isMyTournaments) {
                    url = new URL(endpointUrl + "?userID=" + userId);
                    Log.d(TAG,"url: " + url);
                } else {
                    url = new URL(endpointUrl);
                }

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");

                if (token != null) {
                    urlConnection.setRequestProperty("Authorization", "Bearer " + token);
                }

                urlConnection.connect();

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
                JSONArray tournamentArray = new JSONArray(jsonResponse);
                for (int i = 0; i < tournamentArray.length(); i++) {
                    JSONObject tournamentJson = tournamentArray.getJSONObject(i);
                    String tournamentName = tournamentJson.getString("tournament_name");
                    String sportName = tournamentJson.getString("sport_name");
                    String startDate = tournamentJson.getString("start_date");
                    String endDate = tournamentJson.getString("end_date");
                    int teamSize = tournamentJson.getInt("teamSize");
                    String location = tournamentJson.getString("location");
                    int remainingTeams = tournamentJson.getInt("remainingTeams");
                    int totalTeams = tournamentJson.getInt("totalTeams");
                    int prizePool = tournamentJson.getInt("prizePool");
                    int tournamentID = tournamentJson.getInt("tournament_id");
                    Log.d(TAG, "Fetched tournament: " + tournamentName + " - " + sportName);

                    Tournament tournament = new Tournament(tournamentName, sportName, startDate, endDate,
                            teamSize, location, totalTeams, remainingTeams, prizePool, tournamentID);
                    tournaments.add(tournament);
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

            return tournaments;
        }

        @Override
        protected void onPostExecute(List<Tournament> tournaments) {
            tournamentList.clear();
            tournamentList.addAll(tournaments);
            adapter.notifyDataSetChanged();
        }
    }
}
