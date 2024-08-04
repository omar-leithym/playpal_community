package com.example.omarassignment3;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class TournamentSubmissionFragment extends Fragment {

    private static final String ARG_TOURNAMENT_ID = "tournament_id";

    public static TournamentSubmissionFragment newInstance(int tournamentId) {
        TournamentSubmissionFragment fragment = new TournamentSubmissionFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TOURNAMENT_ID, tournamentId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tournament_submission, container, false);

        TextView rulesText = view.findViewById(R.id.rules_text);
        EditText teamNameInput = view.findViewById(R.id.team_name_input);
        Button submitButton = view.findViewById(R.id.submit_button);

        submitButton.setOnClickListener(v -> {
            String teamName = teamNameInput.getText().toString().trim();
            if (!teamName.isEmpty()) {
                int tournamentId = getArguments() != null ? getArguments().getInt(ARG_TOURNAMENT_ID) : -1;
                if (tournamentId != -1) {
                    new TournamentSignupTask().execute(tournamentId, teamName);
                } else {
                    Toast.makeText(getContext(), "Invalid Tournament ID", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Please enter a team name", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
    private class TournamentSignupTask extends AsyncTask<Object, Void, String> {

        @Override
        protected String doInBackground(Object... params) {
            int tournamentId = (int) params[0];
            String teamName = (String) params[1];

            HttpURLConnection connection = null;
            try {
                URL url = new URL("http://10.0.2.2:3001/tournament/signup"); // Replace with your actual API URL
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                connection.setDoOutput(true);
                connection.setDoInput(true);

                // Create JSON object for the POST request
                JSONObject jsonParam = new JSONObject();
                jsonParam.put("tournament_id", tournamentId);
                jsonParam.put("team_name", teamName);
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
                int userId = sharedPreferences.getInt("id", -1);
                jsonParam.put("user_id", userId);

                // Send the request
                try (PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8")))) {
                    out.print(jsonParam.toString());
                    out.flush();
                }

                // Read the response
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();

                return response.toString();

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                try {
                    JSONObject jsonResponse = new JSONObject(result);

                    String message = jsonResponse.getString("message");
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

                    getParentFragmentManager().popBackStack();

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error processing response", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Request failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
