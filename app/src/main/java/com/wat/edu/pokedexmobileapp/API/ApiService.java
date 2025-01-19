package com.wat.edu.pokedexmobileapp.API;

import com.wat.edu.pokedexmobileapp.Data.LoginRequest;
import com.wat.edu.pokedexmobileapp.Data.LoginResponse;
import com.wat.edu.pokedexmobileapp.Model.Pokemon;
import com.wat.edu.pokedexmobileapp.Data.PokemonResponse;
import com.wat.edu.pokedexmobileapp.Data.RegisterRequest;
import com.wat.edu.pokedexmobileapp.Data.UserDTO;
import com.wat.edu.pokedexmobileapp.Model.PokemonSpecies;
import com.wat.edu.pokedexmobileapp.Model.Team;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Url;

public interface ApiService {

    @POST("/api/v1/users/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("/api/v1/users/register")
    Call<String> register(@Body RegisterRequest request);

    @GET("api/v1/users/{userName}")
    Call<UserDTO> getUser(@Path("userName") String userName, @Header("Authorization") String token);

    @GET("pokemon?limit=151")
    Call<PokemonResponse> getPokemonList(@retrofit2.http.Query("limit") int limit);

    @GET
    Call<Pokemon> getPokemonDetails(@Url String url);

    @GET("pokemon-species/{id}")
    Call<PokemonSpecies> getPokemonSpecies(@Path("id") int id);

    @POST("api/v1/teams/createTeam")
    Call<Void> saveTeam(@Body Team team, @Header("Authorization") String token);

    @DELETE("api/v1/teams/{id}")
    Call<Void> deleteTeam(@Path("id") String teamId, @Header("Authorization") String token);

    @PUT("api/v1/teams/{id}")
    Call<Void> updateTeam(@Path("id") String teamId, @Body Team team, @Header("Authorization") String token);
}
