package com.wat.edu.pokedexmobileapp.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wat.edu.pokedexmobileapp.API.ApiClient;
import com.wat.edu.pokedexmobileapp.API.ApiService;
import com.wat.edu.pokedexmobileapp.Adapters.PokemonSimpleAdapter;
import com.wat.edu.pokedexmobileapp.Model.Pokemon;
import com.wat.edu.pokedexmobileapp.Data.PokemonResponse;
import com.wat.edu.pokedexmobileapp.Model.Team;
import com.wat.edu.pokedexmobileapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CreateTeamFragment extends Fragment {

    private RecyclerView recyclerView;
    private PokemonSimpleAdapter adapter;
    private List<Pokemon> pokemonList = new ArrayList<>();
    private List<Pokemon> selectedPokemon = new ArrayList<>();
    private EditText teamNameInput;
    private Button saveButton;
    private ApiService apiService;

    private static final String BASE_URL = "https://pokeapi.co/api/v2/";
    private static final Map<String, Integer> TYPE_COLORS = new HashMap<>();

    static {
        TYPE_COLORS.put("normal", 0xFFA8A77A);
        TYPE_COLORS.put("fire", 0xFFEE8130);
        TYPE_COLORS.put("water", 0xFF6390F0);
        TYPE_COLORS.put("electric", 0xFFF7D02C);
        TYPE_COLORS.put("grass", 0xFF7AC74C);
        TYPE_COLORS.put("ice", 0xFF96D9D6);
        TYPE_COLORS.put("fighting", 0xFFC22E28);
        TYPE_COLORS.put("poison", 0xFFA33EA1);
        TYPE_COLORS.put("ground", 0xFFE2BF65);
        TYPE_COLORS.put("flying", 0xFFA98FF3);
        TYPE_COLORS.put("psychic", 0xFFF95587);
        TYPE_COLORS.put("bug", 0xFFA6B91A);
        TYPE_COLORS.put("rock", 0xFFB6A136);
        TYPE_COLORS.put("ghost", 0xFF735797);
        TYPE_COLORS.put("dragon", 0xFF6F35FC);
        TYPE_COLORS.put("dark", 0xFF705746);
        TYPE_COLORS.put("steel", 0xFFB7B7CE);
        TYPE_COLORS.put("fairy", 0xFFD685AD);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_team, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3)); // 3 kolumny
        teamNameInput = view.findViewById(R.id.teamNameInput);
        saveButton = view.findViewById(R.id.saveTeamButton);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);

        adapter = new PokemonSimpleAdapter(pokemonList, TYPE_COLORS, this::handlePokemonSelect);
        recyclerView.setAdapter(adapter);

        fetchPokemons();

        saveButton.setOnClickListener(v -> fetchPokemonSprites());

        return view;
    }

    private void fetchPokemons() {

        apiService.getPokemonList(151).enqueue(new Callback<PokemonResponse>() {
            @Override
            public void onResponse(Call<PokemonResponse> call, Response<PokemonResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<PokemonResponse.Result> results = response.body().getResults();
                    loadPokemonDetails(results);
                } else {
                    Log.e("CreateTeamFragment", "Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<PokemonResponse> call, Throwable t) {
                Log.e("CreateTeamFragment", "Failed to fetch Pokémon list", t);
            }
        });
    }

    private void loadPokemonDetails(List<PokemonResponse.Result> results) {
        ExecutorService executorService = Executors.newFixedThreadPool(10); // Tworzy pulę wątków (10 wątków równocześnie)
        List<Callable<Pokemon>> tasks = new ArrayList<>();

        // Tworzenie zadań dla każdego Pokémona
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        for (PokemonResponse.Result result : results) {
            tasks.add(() -> {
                try {
                    Response<Pokemon> response = apiService.getPokemonDetails(result.getUrl()).execute();
                    if (response.isSuccessful() && response.body() != null) {
                        return response.body();
                    }
                } catch (Exception e) {
                    Log.e("PokedexFragment", "Error fetching Pokémon details", e);
                }
                return null;
            });
        }

        // Wykonanie zadań równocześnie
        new Thread(() -> {
            try {
                List<Future<Pokemon>> futures = executorService.invokeAll(tasks);
                List<Pokemon> loadedPokemonList = new ArrayList<>();
                for (Future<Pokemon> future : futures) {
                    Pokemon pokemon = future.get();
                    if (pokemon != null) {
                        loadedPokemonList.add(pokemon);
                    }
                }

                // Aktualizacja listy i interfejsu użytkownika
                getActivity().runOnUiThread(() -> {
                    pokemonList.clear();
                    pokemonList.addAll(loadedPokemonList);
                    adapter.notifyDataSetChanged();
                });
            } catch (Exception e) {
                Log.e("PokedexFragment", "Error executing tasks", e);
            } finally {
                executorService.shutdown();
            }
        }).start();
    }

    private void handlePokemonSelect(Pokemon pokemon) {
        if (selectedPokemon.size() >= 6) {
            Toast.makeText(getContext(), "Your team can have at most 6 Pokémon.", Toast.LENGTH_SHORT).show();
            return;
        }

        selectedPokemon.add(pokemon);
        Toast.makeText(getContext(), pokemon.getName() + " added to the team!", Toast.LENGTH_SHORT).show();

        // Aktualizacja widoku wybranych Pokémonów
        updateSelectedPokemonView();
    }

    private void updateSelectedPokemonView() {
        GridLayout selectedPokemonContainer = getView().findViewById(R.id.selectedPokemonContainer);
        selectedPokemonContainer.removeAllViews(); // Usuń poprzednie widoki

        for (Pokemon pokemon : selectedPokemon) {
            View pokemonView = LayoutInflater.from(getContext()).inflate(R.layout.item_selected_pokemon, selectedPokemonContainer, false);

            // Ustaw nazwę Pokémona
            TextView pokemonName = pokemonView.findViewById(R.id.selectedPokemonName);
            pokemonName.setText(pokemon.getName());

            Button removeButton = pokemonView.findViewById(R.id.removeButton);
            removeButton.setOnClickListener(v -> {
                selectedPokemon.remove(pokemon);
                Toast.makeText(getContext(), pokemon.getName() + " removed from the team!", Toast.LENGTH_SHORT).show();
                updateSelectedPokemonView(); // Odśwież widok
            });

            // Dodaj widok do kontenera
            selectedPokemonContainer.addView(pokemonView);
        }
    }


    private void fetchPokemonSprites() {
        apiService = ApiClient.getClient().create(ApiService.class);
        ExecutorService executorService = Executors.newFixedThreadPool(6);
        List<Callable<String>> spriteTasks = new ArrayList<>();

        // Now, instead of fetching the full details, use the sprite URL directly
        for (Pokemon pokemon : selectedPokemon) {
            spriteTasks.add(() -> {
                try {
                    // Construct the URL to fetch only the sprite info
                    String spriteUrl = pokemon.getSprites().getFrontDefault();  // Assuming 'getFrontDefault()' gives you the sprite URL
                    if (spriteUrl != null) {
                        return spriteUrl;
                    }
                } catch (Exception e) {
                    Log.e("CreateTeamFragment", "Error fetching Pokémon sprite", e);
                }
                return null;
            });
        }

        new Thread(() -> {
            try {
                List<Future<String>> futures = executorService.invokeAll(spriteTasks);
                List<String> sprites = new ArrayList<>();
                for (Future<String> future : futures) {
                    String sprite = future.get();
                    if (sprite != null) {
                        sprites.add(sprite);
                    }
                }

                // Once the sprites are gathered, update the UI and save the team
                requireActivity().runOnUiThread(() -> {
                    saveTeam(sprites);
                });

            } catch (Exception e) {
                Log.e("CreateTeamFragment", "Error fetching Pokémon sprites", e);
            } finally {
                executorService.shutdown();
            }
        }).start();
    }


    private void saveTeam(List<String> sprites) {
        // Tworzenie i zapis drużyny z dodatkowymi sprite'ami
        Team team = new Team();
        team.setTeamName(teamNameInput.getText().toString().trim());
        team.setPokemonNames(selectedPokemon.stream().map(Pokemon::getName).collect(Collectors.toList()));
        team.setPokemonSprites(sprites);
        team.setUserName(getUserNameFromSharedPreferences());

        /*String teamData = "Team Name: " + team.getTeamName() + "\n"
                + "User: " + team.getUserName() + "\n"
                + "Pokémon Names: " + team.getPokemonNames().toString() + "\n"
                + "Sprites: " + team.getPokemonSprites().toString() + "\n"
                + "Token: "+ getTokenFromSharedPreferences();*/

        //Toast.makeText(getContext(), teamData, Toast.LENGTH_LONG).show();

        apiService.saveTeam(team, "Bearer " + getTokenFromSharedPreferences()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Team saved successfully!", Toast.LENGTH_SHORT).show();
                    selectedPokemon.clear();
                    teamNameInput.setText("");
                    updateSelectedPokemonView();
                } else {
                    Toast.makeText(getContext(), "Failed to save team. Error: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("CreateTeamFragment", "Error saving team", t);
                Toast.makeText(getContext(), "Failed to save team. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
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

}
