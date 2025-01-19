package com.wat.edu.pokedexmobileapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.wat.edu.pokedexmobileapp.R;

public class HubFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hub, container, false);

        // Przyciski
        Button pokedexButton = view.findViewById(R.id.pokedexButton);
        Button createTeamButton = view.findViewById(R.id.createTeamButton);
        Button myTeamsButton = view.findViewById(R.id.myTeamsButton);

        // Obsługa kliknięć
        pokedexButton.setOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.action_hubFragment_to_pokedexFragment));

        createTeamButton.setOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.action_hubFragment_to_createTeamFragment));

        myTeamsButton.setOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.action_hubFragment_to_myTeamsFragment));

        return view;
    }
}
