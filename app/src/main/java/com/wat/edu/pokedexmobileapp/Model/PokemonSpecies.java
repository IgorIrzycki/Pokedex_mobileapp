package com.wat.edu.pokedexmobileapp.Model;

import java.util.List;

public class PokemonSpecies {
    private List<FlavorTextEntry> flavor_text_entries;

    public List<FlavorTextEntry> getFlavorTextEntries() {
        return flavor_text_entries;
    }

    public static class FlavorTextEntry {
        private String flavor_text;
        private Language language;

        // Getter for flavor_text
        public String getFlavorText() {
            return flavor_text;
        }

        // Getter for language
        public Language getLanguage() {
            return language;
        }

        public static class Language {
            private String name; // Assuming the language has a name field

            // Getter for name
            public String getName() {
                return name;
            }

            // Setter for name
            public void setName(String name) {
                this.name = name;
            }
        }
    }
}

