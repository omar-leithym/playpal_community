package com.example.omarassignment3;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Retrieve the firstName from SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String firstName = sharedPreferences.getString("firstName", "User");

        // Set the welcome text with the retrieved name
        TextView welcomeText = view.findViewById(R.id.welcomeText);
        welcomeText.setText("Hello " + firstName + ",");

        ImageView learnSportButton = view.findViewById(R.id.learnSportButton);
        ImageView playOneOnOneButton = view.findViewById(R.id.playOneOnOneButton);
        ImageView playTournamentButton = view.findViewById(R.id.playTournamentButton);

        learnSportButton.setOnClickListener(v -> navigateToSportSelection2());
        playOneOnOneButton.setOnClickListener(v -> navigateToSportSelection());
        playTournamentButton.setOnClickListener(v -> navigateToTournament());

        return view;
    }

    private void navigateToSportSelection() {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, new SportSelectionFragment())
                .addToBackStack(null)
                .commit();
    }

    private void navigateToSportSelection2() {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, new SportSelectionFragment2())
                .addToBackStack(null)
                .commit();
    }

    private void navigateToTournament() {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.frame_layout, new TournamentFragment())
                .addToBackStack(null)
                .commit();
    }
}

