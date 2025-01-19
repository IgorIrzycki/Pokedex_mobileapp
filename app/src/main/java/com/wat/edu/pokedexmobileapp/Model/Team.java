package com.wat.edu.pokedexmobileapp.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Team implements Parcelable {
    private String id;
    private String teamName;
    private List<String> pokemonNames;
    private List<String> pokemonSprites;
    private String userName;

    public Team() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public List<String> getPokemonNames() {
        return pokemonNames;
    }

    public void setPokemonNames(List<String> pokemonNames) {
        this.pokemonNames = pokemonNames;
    }

    public List<String> getPokemonSprites() {
        return pokemonSprites;
    }

    public void setPokemonSprites(List<String> pokemonSprites) {
        this.pokemonSprites = pokemonSprites;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    protected Team(Parcel in) {
        id = in.readString();
        teamName = in.readString();
        pokemonNames = in.createStringArrayList();
        pokemonSprites = in.createStringArrayList();
        userName = in.readString();
    }

    public static final Creator<Team> CREATOR = new Creator<Team>() {
        @Override
        public Team createFromParcel(Parcel in) {
            return new Team(in);
        }

        @Override
        public Team[] newArray(int size) {
            return new Team[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(teamName);
        dest.writeStringList(pokemonNames != null ? pokemonNames : new ArrayList<>());
        dest.writeStringList(pokemonSprites != null ? pokemonSprites : new ArrayList<>());
        dest.writeString(userName);
    }
}
