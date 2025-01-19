package com.wat.edu.pokedexmobileapp.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wat.edu.pokedexmobileapp.API.ApiService;
import com.wat.edu.pokedexmobileapp.Adapters.PokemonAdapter;
import com.wat.edu.pokedexmobileapp.Model.Pokemon;
import com.wat.edu.pokedexmobileapp.Data.PokemonResponse;
import com.wat.edu.pokedexmobileapp.R;

import java.util.ArrayList;
import java.util.List;
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

public class PokedexFragment extends Fragment {
    private RecyclerView recyclerView;
    private PokemonAdapter adapter;
    private List<Pokemon> pokemonList = new ArrayList<>();
    private List<Pokemon> filteredPokemonList = new ArrayList<>();
    private static final String BASE_URL = "https://pokeapi.co/api/v2/";

    private EditText searchInput;
    private Spinner typeSpinner;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pokedex, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3)); // Siatka z 3 kolumnami
        adapter = new PokemonAdapter(filteredPokemonList);
        recyclerView.setAdapter(adapter);

        searchInput = view.findViewById(R.id.searchInput);
        typeSpinner = view.findViewById(R.id.typeSpinner);

        setupTypeSpinner();
        setupSearchListener();

        fetchPokemons();

        return view;
    }

    private void fetchPokemons() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        // Pobierz listę Pokémonów
        apiService.getPokemonList(151).enqueue(new Callback<PokemonResponse>() {
            @Override
            public void onResponse(Call<PokemonResponse> call, Response<PokemonResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<PokemonResponse.Result> results = response.body().getResults();
                    loadPokemonDetails(results);
                } else {
                    Log.e("PokedexFragment", "Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<PokemonResponse> call, Throwable t) {
                Log.e("PokedexFragment", "Failed to fetch Pokémon list", t);
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
                    filteredPokemonList.clear();
                    filteredPokemonList.addAll(pokemonList);
                    adapter.notifyDataSetChanged();
                });
            } catch (Exception e) {
                Log.e("PokedexFragment", "Error executing tasks", e);
            } finally {
                executorService.shutdown();
            }
        }).start();
    }



    private void setupTypeSpinner() {
        String[] types = {"All", "Normal", "Fighting", "Flying", "Poison", "Ground", "Rock", "Bug",
                "Ghost", "Steel", "Fire", "Water", "Grass", "Electric", "Psychic",
                "Ice", "Dragon", "Dark", "Fairy"};

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                types
        );
        typeSpinner.setAdapter(spinnerAdapter);

        typeSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                filterPokemonList();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
    }

    private void setupSearchListener() {
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterPokemonList();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filterPokemonList() {
        String query = searchInput.getText().toString().toLowerCase();
        String selectedType = typeSpinner.getSelectedItem().toString().toLowerCase();

        filteredPokemonList.clear();

        filteredPokemonList.addAll(pokemonList.stream()
                .filter(pokemon -> (query.isEmpty() || pokemon.getName().toLowerCase().contains(query)) &&
                        (selectedType.equals("all") || pokemon.getTypes().stream()
                                .anyMatch(type -> type.getType().getName().equalsIgnoreCase(selectedType))))
                .collect(Collectors.toList()));

        adapter.notifyDataSetChanged();
    }
}
