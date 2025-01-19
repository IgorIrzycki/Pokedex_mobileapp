package com.wat.edu.pokedexmobileapp.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wat.edu.pokedexmobileapp.API.ApiClient;
import com.wat.edu.pokedexmobileapp.API.ApiService;
import com.wat.edu.pokedexmobileapp.Adapters.TeamAdapter;
import com.wat.edu.pokedexmobileapp.Model.Team;
import com.wat.edu.pokedexmobileapp.Data.UserDTO;
import com.wat.edu.pokedexmobileapp.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyTeamsFragment extends Fragment {

    private RecyclerView teamsRecyclerView;
    private TeamAdapter adapter;
    private List<Team> teamList = new ArrayList<>();
    private ApiService apiService;
    private String userName;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_teams, container, false);
        teamsRecyclerView = view.findViewById(R.id.teamsRecyclerView);
        teamsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        apiService = ApiClient.getClient().create(ApiService.class);

        adapter = new TeamAdapter(teamList, this::editTeam, this::deleteTeam);
        teamsRecyclerView.setAdapter(adapter);

        userName = getUserNameFromSharedPreferences();

        fetchTeams();

        return view;
    }

    private void fetchTeams() {
        String token = "Bearer " + getTokenFromSharedPreferences();

        if (userName != null && !userName.isEmpty()) {
            apiService.getUser(userName, token).enqueue(new Callback<UserDTO>() {
                @Override
                public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        UserDTO user = response.body();
                        teamList.clear();
                        teamList.addAll(user.getTeams());
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.e("MyTeamsFragment", "Błąd pobierania danych użytkownika: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<UserDTO> call, Throwable t) {
                    Log.e("MyTeamsFragment", "Błąd sieci: " + t.getMessage());
                }
            });
        } else {
            Log.e("MyTeamsFragment", "Username is null or empty.");
        }
    }

    private String getTokenFromSharedPreferences() {
        return requireContext()
                .getSharedPreferences("PokedexApp", Context.MODE_PRIVATE)
                .getString("TOKEN", "");
    }

    private String getUserNameFromSharedPreferences() {
        return requireContext()
                .getSharedPreferences("PokedexApp", Context.MODE_PRIVATE)
                .getString("USERNAME", "");
    }

    private void editTeam(Team team) {
        Log.d("MyTeamsFragment", "Editing team: " + team.getTeamName());

        Bundle bundle = new Bundle();
        bundle.putParcelable("team", team);

        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(R.id.action_myTeamsFragment_to_editTeamFragment, bundle);
    }



    private void deleteTeam(Team team) {
        String token = "Bearer " + getTokenFromSharedPreferences();
        apiService.deleteTeam(team.getId(), token).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("MyTeamsFragment", "Team deleted: " + team.getTeamName());
                    teamList.remove(team);
                    adapter.notifyDataSetChanged();
                } else {
                    Log.e("MyTeamsFragment", "Failed to delete team: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("MyTeamsFragment", "Error deleting team: " + t.getMessage());
            }
        });
    }
}
