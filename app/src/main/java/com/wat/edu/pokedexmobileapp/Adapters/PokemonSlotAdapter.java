package com.wat.edu.pokedexmobileapp.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wat.edu.pokedexmobileapp.R;

import java.util.ArrayList;
import java.util.List;

public class PokemonSlotAdapter extends RecyclerView.Adapter<PokemonSlotAdapter.PokemonSlotViewHolder> {
    private List<String> pokemonNames;
    private List<String> availablePokemons;
    private SlotActionListener slotActionListener;

    public interface SlotActionListener {
        void onPokemonSelected(int slotIndex, String pokemonName);

        void onPokemonRemoved(int slotIndex);
    }

    public PokemonSlotAdapter(List<String> pokemonNames, List<String> availablePokemons, SlotActionListener slotActionListener) {
        this.pokemonNames = pokemonNames != null ? pokemonNames : new ArrayList<>(6);

        while (this.pokemonNames.size() < 6) {
            this.pokemonNames.add(null);
        }

        this.availablePokemons = availablePokemons;
        this.slotActionListener = slotActionListener;
    }

    @NonNull
    @Override
    public PokemonSlotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pokemon_slot, parent, false);
        return new PokemonSlotViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PokemonSlotViewHolder holder, int position) {
        List<String> spinnerData = new ArrayList<>();
        spinnerData.add("Select Pokémon Name");
        spinnerData.addAll(availablePokemons);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(holder.itemView.getContext(), android.R.layout.simple_spinner_item, spinnerData);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.spinner.setAdapter(adapter);

        String selectedPokemon = pokemonNames.get(position);
        if (selectedPokemon == null) {
            holder.spinner.setSelection(0);
        } else {
            Log.e("Test", selectedPokemon + " " + spinnerData.indexOf(selectedPokemon));
            holder.spinner.setSelection(spinnerData.indexOf(selectedPokemon));
        }

        holder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedPokemon = spinnerData.get(position);

                if (!selectedPokemon.equals("Select Pokémon Name")) {
                    slotActionListener.onPokemonSelected(holder.getAdapterPosition(), selectedPokemon);
                } else {
                    pokemonNames.set(holder.getAdapterPosition(), null);
                    slotActionListener.onPokemonSelected(holder.getAdapterPosition(), null);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
    }

    @Override
    public int getItemCount() {
        return 6;
    }

    public static class PokemonSlotViewHolder extends RecyclerView.ViewHolder {
        Spinner spinner;

        public PokemonSlotViewHolder(View itemView) {
            super(itemView);
            spinner = itemView.findViewById(R.id.pokemonSlotSpinner);
        }
    }
}
