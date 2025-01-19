package com.wat.edu.pokedexmobileapp.Model;

import java.util.List;

public class PokemonSpecies {
    private List<FlavorTextEntry> flavor_text_entries;

    public PokemonSpecies(List<FlavorTextEntry> flavor_text_entries) {
        this.flavor_text_entries = flavor_text_entries;
    }

    public List<FlavorTextEntry> getFlavorTextEntries() {
        return flavor_text_entries;
    }

    public static class FlavorTextEntry {
        private String flavor_text;
        private Language language;

        public FlavorTextEntry(String flavor_text, Language language) {
            this.flavor_text = flavor_text;
            this.language = language;
        }

        public String getFlavorText() {
            return flavor_text;
        }
        public Language getLanguage() {
            return language;
        }

        public static class Language {
            private String name;
            public String getName() {
                return name;
            }
            public void setName(String name) {
                this.name = name;
            }
        }
    }
}

