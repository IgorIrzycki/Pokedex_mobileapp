package com.wat.edu.pokedexmobileapp.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wat.edu.pokedexmobileapp.Model.Pokemon;
import com.wat.edu.pokedexmobileapp.R;

import java.util.List;
import java.util.Map;

public class PokemonSimpleAdapter extends RecyclerView.Adapter<PokemonSimpleAdapter.PokemonSimpleViewHolder> {

    private List<Pokemon> pokemons;
    private Map<String, Integer> typeColors;
    private OnPokemonSelectListener listener;

    public interface OnPokemonSelectListener {
        void onPokemonSelect(Pokemon pokemon);
    }

    public PokemonSimpleAdapter(List<Pokemon> pokemons, Map<String, Integer> typeColors, OnPokemonSelectListener listener) {
        this.pokemons = pokemons;
        this.typeColors = typeColors;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PokemonSimpleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_simple_pokemon, parent, false);
        return new PokemonSimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PokemonSimpleViewHolder holder, int position) {
        Pokemon pokemon = pokemons.get(position);
        holder.pokemonName.setText(pokemon.getName());

        // Set background color based on Pokémon type
        String type = pokemon.getTypes().get(0).getType().getName(); // Załóż, że typ 0 istnieje
        if (typeColors.containsKey(type)) {
            holder.itemView.setBackgroundColor(typeColors.get(type));
        }

        holder.itemView.setOnClickListener(v -> listener.onPokemonSelect(pokemon));
    }

    @Override
    public int getItemCount() {
        return pokemons.size();
    }

    public static class PokemonSimpleViewHolder extends RecyclerView.ViewHolder {
        TextView pokemonName;

        public PokemonSimpleViewHolder(@NonNull View itemView) {
            super(itemView);
            pokemonName = itemView.findViewById(R.id.pokemonName);
        }
    }
}

