package com.wat.edu.pokedexmobileapp.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.wat.edu.pokedexmobileapp.Model.Team;
import com.wat.edu.pokedexmobileapp.R;

import java.util.List;

public class TeamAdapter extends RecyclerView.Adapter<TeamAdapter.TeamViewHolder> {
    private List<Team> teams;
    private OnEditClickListener editClickListener;
    private OnDeleteClickListener deleteClickListener;

    public interface OnEditClickListener {
        void onEditClick(Team team);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(Team team);
    }

    public TeamAdapter(List<Team> teams, OnEditClickListener editClickListener, OnDeleteClickListener deleteClickListener) {
        this.teams = teams;
        this.editClickListener = editClickListener;
        this.deleteClickListener = deleteClickListener;
    }

    @NonNull
    @Override
    public TeamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_team, parent, false);
        return new TeamViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TeamViewHolder holder, int position) {
        Team team = teams.get(position);
        holder.teamName.setText(team.getTeamName());

        // Resetowanie i ładowanie obrazków Pokémonów
        for (ImageView imageView : holder.pokemonImages) {
            imageView.setImageDrawable(null);
        }

        List<String> sprites = team.getPokemonSprites();
        if (sprites != null) {
            for (int i = 0; i < Math.min(sprites.size(), 6); i++) {
                Glide.with(holder.itemView.getContext())
                        .load(sprites.get(i))
                        .into(holder.pokemonImages[i]);
            }
        }

        // Obsługa kliknięć przycisków edycji i usuwania
        holder.editButton.setOnClickListener(v -> {
            if (editClickListener != null) {
                editClickListener.onEditClick(team);
            }
        });

        holder.deleteButton.setOnClickListener(v -> {
            if (deleteClickListener != null) {
                deleteClickListener.onDeleteClick(team);
            }
        });
    }

    @Override
    public int getItemCount() {
        return teams.size();
    }

    public static class TeamViewHolder extends RecyclerView.ViewHolder {
        TextView teamName;
        ImageView[] pokemonImages = new ImageView[6];
        View editButton;
        View deleteButton;

        public TeamViewHolder(@NonNull View itemView) {
            super(itemView);
            teamName = itemView.findViewById(R.id.teamName);
            pokemonImages[0] = itemView.findViewById(R.id.pokemonImage1);
            pokemonImages[1] = itemView.findViewById(R.id.pokemonImage2);
            pokemonImages[2] = itemView.findViewById(R.id.pokemonImage3);
            pokemonImages[3] = itemView.findViewById(R.id.pokemonImage4);
            pokemonImages[4] = itemView.findViewById(R.id.pokemonImage5);
            pokemonImages[5] = itemView.findViewById(R.id.pokemonImage6);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
