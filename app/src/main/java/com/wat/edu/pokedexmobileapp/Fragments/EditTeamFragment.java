package com.wat.edu.pokedexmobileapp.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wat.edu.pokedexmobileapp.API.ApiClient;
import com.wat.edu.pokedexmobileapp.API.ApiService;
import com.wat.edu.pokedexmobileapp.Adapters.PokemonSlotAdapter;
import com.wat.edu.pokedexmobileapp.Model.Team;
import com.wat.edu.pokedexmobileapp.R;
import com.wat.edu.pokedexmobileapp.Data.PokemonResponse;
import com.wat.edu.pokedexmobileapp.Model.Pokemon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EditTeamFragment extends Fragment {
    private static final String BASE_URL = "https://pokeapi.co/api/v2/";
    private RecyclerView pokemonSlotsRecyclerView;
    private Button saveTeamButton;
    private List<String> pokemonNames = new ArrayList<>(Collections.nCopies(6, null)); // 6 slotów na Pokémony
    private List<String> availablePokemons = new ArrayList<>(); // Lista dostępnych Pokémonów
    private PokemonSlotAdapter adapter;
    private ApiService apiService;
    private Team team;
    private List<Pokemon> pokemonList = new ArrayList<>(); // Lista wszystkich Pokémonów z PokeAPI

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_team, container, false);

        // Initialize API service
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        // Odbierz dane drużyny z argumentów
        if (getArguments() != null) {
            team = getArguments().getParcelable("team"); // Pobierz drużynę z argumentów
            if (team != null) {
                Log.d("EditTeamFragment", "Editing team: " + team.getTeamName());
                pokemonNames = team.getPokemonNames(); // Zaktualizuj pokemonNames z danymi drużyny
            }
        }

        // Setup RecyclerView
        pokemonSlotsRecyclerView = view.findViewById(R.id.pokemonSlotsRecyclerView);
        saveTeamButton = view.findViewById(R.id.saveTeamButton);
        fetchPokemons(); // Załaduj pokemony

        // Zapis drużyny
        saveTeamButton.setOnClickListener(v -> saveTeam());

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
                    Log.e("EditTeamFragment", "Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<PokemonResponse> call, Throwable t) {
                Log.e("EditTeamFragment", "Failed to fetch Pokémon list", t);
            }
        });
    }

    private void loadPokemonDetails(List<PokemonResponse.Result> results) {
        ExecutorService executorService = Executors.newFixedThreadPool(10); // Tworzymy pulę wątków
        List<Callable<Pokemon>> tasks = new ArrayList<>();

        for (PokemonResponse.Result result : results) {
            tasks.add(() -> {
                try {
                    Response<Pokemon> response = apiService.getPokemonDetails(result.getUrl()).execute();
                    if (response.isSuccessful() && response.body() != null) {
                        return response.body();
                    }
                } catch (Exception e) {
                    Log.e("EditTeamFragment", "Error fetching Pokémon details", e);
                }
                return null;
            });
        }

        // Wykonaj równolegle zadania
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

                // Zaktualizuj listę Pokémonów
                getActivity().runOnUiThread(() -> {
                    pokemonList.clear();
                    pokemonList.addAll(loadedPokemonList);

                    // Zaktualizuj dostępne Pokémony
                    availablePokemons.clear();
                    for (Pokemon pokemon : pokemonList) {
                        availablePokemons.add(pokemon.getName());
                    }

                    // Zainicjalizuj adapter po załadowaniu danych
                    pokemonSlotsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    adapter = new PokemonSlotAdapter(pokemonNames, availablePokemons, new PokemonSlotAdapter.SlotActionListener() {
                        @Override
                        public void onPokemonSelected(int slotIndex, String pokemonName) {
                            pokemonNames.set(slotIndex, pokemonName); // Ustaw wybranego Pokémona w odpowiednim slocie
                        }

                        @Override
                        public void onPokemonRemoved(int slotIndex) {
                            pokemonNames.set(slotIndex, null); // Usuń Pokémona z danego slotu
                        }
                    });
                    pokemonSlotsRecyclerView.setAdapter(adapter);

                    // Aktualizuj sloty drużyny
                    updatePokemonSlots();
                });
            } catch (Exception e) {
                Log.e("EditTeamFragment", "Error executing tasks", e);
            } finally {
                executorService.shutdown();
            }
        }).start();
    }

    private void updatePokemonSlots() {
        for (int i = 0; i < pokemonNames.size(); i++) {
            if (pokemonNames.get(i) != null) {
                // Ustaw już wybranego Pokémona w odpowiednim slocie
                String selectedPokemon = pokemonNames.get(i);
                availablePokemons.remove(selectedPokemon);
                availablePokemons.add(0, selectedPokemon); // Wstaw wybranego Pokémona na początek listy
            }
        }
        adapter.notifyDataSetChanged(); // Zaktualizuj adapter po ustawieniu slotów
    }

    private void saveTeam() {
        // Step 1: Collect the Pokémon names from the selected slots
        List<String> selectedPokemonNames = new ArrayList<>(pokemonNames);

        // Step 2: Fetch the Pokémon sprites in parallel
        fetchPokemonSprites(selectedPokemonNames);
    }

    private void fetchPokemonSprites(List<String> selectedPokemonNames) {
        // Filtruj z listy pokemonList tylko te, które są w drużynie
        List<Pokemon> filteredPokemonList = new ArrayList<>();
        for (String selectedPokemon : selectedPokemonNames) {
            for (Pokemon pokemon : pokemonList) {
                if (pokemon.getName().equalsIgnoreCase(selectedPokemon)) {
                    filteredPokemonList.add(pokemon);
                    break;
                }
            }
        }

        // Pobierz URL-e sprite'ów dla wybranych Pokémonów
        List<String> sprites = new ArrayList<>();
        for (Pokemon pokemon : filteredPokemonList) {
            if (pokemon.getSprites() != null && pokemon.getSprites().getFrontDefault() != null) {
                sprites.add(pokemon.getSprites().getFrontDefault());
            } else {
                sprites.add(null); // Jeśli sprite nie istnieje, dodaj null
            }
        }

        // Przekaż dane do metody saveTeamWithSprites
        saveTeamWithSprites(sprites, selectedPokemonNames);
    }


    private void saveTeamWithSprites(List<String> sprites, List<String> pokemonNames) {
        apiService = ApiClient.getClient().create(ApiService.class);
        // Usuń wartości null z list pokemonNames i sprites
        List<String> filteredPokemonNames = new ArrayList<>();
        List<String> filteredSprites = new ArrayList<>();

        // Filtrowanie pokemonNames
        for (String name : pokemonNames) {
            if (name != null) {
                filteredPokemonNames.add(name);
            }
        }

        // Filtrowanie sprites
        for (String sprite : sprites) {
            if (sprite != null) {
                filteredSprites.add(sprite);
            }
        }

        // Stworzenie obiektu Team i ustawienie jego właściwości
        Team teamTemp = new Team();
        teamTemp.setTeamName(team.getTeamName()); // Ustaw nazwę drużyny
        teamTemp.setPokemonNames(filteredPokemonNames); // Przefiltrowane nazwy Pokémonów
        teamTemp.setPokemonSprites(filteredSprites); // Przefiltrowane sprite'y
        teamTemp.setUserName(getUserNameFromSharedPreferences()); // Ustaw nazwę użytkownika

        // Wyślij dane drużyny do API
        apiService.updateTeam(team.getId(), teamTemp, "Bearer " + getTokenFromSharedPreferences()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Zapis drużyny zakończony sukcesem
                    Toast.makeText(getContext(), "Team saved successfully!", Toast.LENGTH_SHORT).show();
                    pokemonNames.clear(); // Wyczyść listę nazw Pokémonów
                    // Zresetuj pola wejściowe lub zaktualizuj UI
                } else {
                    Toast.makeText(getContext(), "Failed to save team. Error: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("EditTeamFragment", "Error saving team", t);
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
