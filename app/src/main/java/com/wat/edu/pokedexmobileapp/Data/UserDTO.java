package com.wat.edu.pokedexmobileapp.Data;

import com.wat.edu.pokedexmobileapp.Model.Team;

import java.util.List;

public class UserDTO {
    private String id;
    private String userName;
    private String email;
    private List<Team> teams;

    // Gettery i Settery
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public List<Team> getTeams() { return teams; }
    public void setTeams(List<Team> teams) { this.teams = teams; }
}

