package com.wat.edu.pokedexmobileapp.Data;

import com.wat.edu.pokedexmobileapp.Model.Team;

import java.util.List;

public class UserDTO {
    private String id;
    private String userName;
    private String email;
    private List<Team> teams;

    public UserDTO(String userName, String email, List<Team> teams) {
        this.userName = userName;
        this.email = email;
        this.teams = teams;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public List<Team> getTeams() { return teams; }
}

