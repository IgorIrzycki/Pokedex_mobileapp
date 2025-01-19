package com.wat.edu.pokedexmobileapp.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wat.edu.pokedexmobileapp.API.ApiService;
import com.wat.edu.pokedexmobileapp.Model.PokemonSpecies;
import com.wat.edu.pokedexmobileapp.R;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.wat.edu.pokedexmobileapp.Model.Pokemon;

import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PokemonAdapter extends RecyclerView.Adapter<PokemonAdapter.PokemonViewHolder> {
    private List<Pokemon> pokemons;

    public PokemonAdapter(List<Pokemon> pokemons) {
        this.pokemons = pokemons;
    }

    @NonNull
    @Override
    public PokemonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pokemon, parent, false);
        return new PokemonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PokemonViewHolder holder, int position) {
        Pokemon pokemon = pokemons.get(position);
        holder.pokemonName.setText(pokemon.getName());

        Glide.with(holder.itemView.getContext())
                .load(pokemon.getSprites().getFrontDefault()) // URL obrazka
                .into(holder.pokemonImage);

        holder.itemView.setOnLongClickListener(v -> {
            fetchPokemonDetails(pokemon, holder.itemView.getContext());
            return true; // Zatrzymuje dalsze propagowanie zdarzenia
        });
    }

    private void fetchPokemonDetails(Pokemon pokemon, Context context) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://pokeapi.co/api/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        // Pobranie szczegółów Pokémona
        apiService.getPokemonSpecies(pokemon.getId()).enqueue(new Callback<PokemonSpecies>() {
            @Override
            public void onResponse(Call<PokemonSpecies> call, Response<PokemonSpecies> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PokemonSpecies speciesData = response.body();

                    // Uzyskanie opisu Pokémona w języku angielskim
                    String description = speciesData.getFlavorTextEntries().stream()
                            .filter(entry -> entry.getLanguage().getName().equals("en")) // Access the language name here
                            .findFirst()
                            .map(PokemonSpecies.FlavorTextEntry::getFlavorText)
                            .orElse(speciesData.getFlavorTextEntries().get(0).getFlavorText());

                    // Przygotowanie statystyk Pokémona
                    StringBuilder statsBuilder = new StringBuilder();
                    statsBuilder.append("Base Stats:\n");
                    for (Pokemon.StatWrapper stat : pokemon.getStats()) {
                        statsBuilder.append(stat.getStat().getName())
                                .append(": ")
                                .append(stat.getBaseStat())
                                .append("\n");
                    }

                    // Tworzenie pełnych szczegółów
                    String details = "Name: " + pokemon.getName() + "\n"
                            + "Types: " + pokemon.getTypes().stream()
                            .map(type -> type.getType().getName())
                            .collect(Collectors.joining(", ")) + "\n\n"
                            + description + "\n\n"
                            + statsBuilder;

                    // Wyświetlenie szczegółów w AlertDialog
                    showDetailsDialog(context, pokemon.getName(), details);
                } else {
                    showErrorDialog(context, "Failed to fetch details.");
                }
            }

            @Override
            public void onFailure(Call<PokemonSpecies> call, Throwable t) {
                Log.e("PokemonAdapter", "Error fetching Pokemon species details", t);
                showErrorDialog(context, "Network error. Please try again later.");
            }
        });
    }

    private void showDetailsDialog(Context context, String title, String message) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Close", null)
                .show();
    }

    private void showErrorDialog(Context context, String message) {
        new AlertDialog.Builder(context)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    @Override
    public int getItemCount() {
        return pokemons.size();
    }

    public static class PokemonViewHolder extends RecyclerView.ViewHolder {
        TextView pokemonName;
        ImageView pokemonImage;

        public PokemonViewHolder(@NonNull View itemView) {
            super(itemView);
            pokemonName = itemView.findViewById(R.id.pokemonName);
            pokemonImage = itemView.findViewById(R.id.pokemonImage);
        }
    }
}
