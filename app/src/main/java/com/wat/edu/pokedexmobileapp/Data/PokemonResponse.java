package com.wat.edu.pokedexmobileapp.Data;

import java.util.List;

public class PokemonResponse {
    private List<Result> results;

    public PokemonResponse(List<Result> results) {
        this.results = results;
    }

    public List<Result> getResults() {
        return results;
    }

    public static class Result {
        private String name;
        private String url;

        public Result(String name, String url) {
            this.name = name;
            this.url = url;
        }

        public String getName() {
            return name;
        }

        public String getUrl() {
            return url;
        }
    }
}
