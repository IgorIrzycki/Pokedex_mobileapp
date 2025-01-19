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
    private List<String> pokemonNames; // Lista nazw Pokémonów (6 pozycji)
    private List<String> availablePokemons; // Lista dostępnych Pokémonów
    private SlotActionListener slotActionListener;

    public interface SlotActionListener {
        void onPokemonSelected(int slotIndex, String pokemonName);

        void onPokemonRemoved(int slotIndex);
    }

    // Konstruktor
    public PokemonSlotAdapter(List<String> pokemonNames, List<String> availablePokemons, SlotActionListener slotActionListener) {
        // Zapewniamy zawsze 6 slotów
        this.pokemonNames = pokemonNames != null ? pokemonNames : new ArrayList<>(6);

        // Uzupełniamy brakujące sloty nullami
        while (this.pokemonNames.size() < 6) {
            this.pokemonNames.add(null); // Dodajemy null do brakujących slotów
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
        // Lista danych do spinnera, zaczynamy od "Select Pokémon Name"
        List<String> spinnerData = new ArrayList<>();
        spinnerData.add("Select Pokémon Name");
        spinnerData.addAll(availablePokemons);

        // Ustawiamy adapter spinnera
        ArrayAdapter<String> adapter = new ArrayAdapter<>(holder.itemView.getContext(), android.R.layout.simple_spinner_item, spinnerData);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.spinner.setAdapter(adapter);

        // Jeśli slot ma już wybranego Pokémona, ustaw go w spinnerze
        String selectedPokemon = pokemonNames.get(position);
        if (selectedPokemon == null) {
            holder.spinner.setSelection(0);
            // Jeśli jest wybrany Pokémon, ustaw go w spinnerze
        } else {
            Log.e("Test", selectedPokemon + " " + spinnerData.indexOf(selectedPokemon));
            // Jeśli brak wybranego Pokémona, ustaw domyślną wartość
            holder.spinner.setSelection(spinnerData.indexOf(selectedPokemon));
        }

        holder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedPokemon = spinnerData.get(position);

                if (!selectedPokemon.equals("Select Pokémon Name")) {
                    // Jeśli wybrano Pokémona, przekazujemy go do listenera
                    slotActionListener.onPokemonSelected(holder.getAdapterPosition(), selectedPokemon);
                } else {
                    // Jeśli nie wybrano Pokémona, ustawiamy null
                    pokemonNames.set(holder.getAdapterPosition(), null);
                    slotActionListener.onPokemonSelected(holder.getAdapterPosition(), null);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Tu nic nie robimy, ale można dodać dodatkową logikę
            }
        });
    }

    @Override
    public int getItemCount() {
        return 6; // Zawsze 6 slotów
    }

    public static class PokemonSlotViewHolder extends RecyclerView.ViewHolder {
        Spinner spinner;

        public PokemonSlotViewHolder(View itemView) {
            super(itemView);
            spinner = itemView.findViewById(R.id.pokemonSlotSpinner);
        }
    }
}
