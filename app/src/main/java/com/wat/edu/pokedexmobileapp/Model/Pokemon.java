package com.wat.edu.pokedexmobileapp.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Pokemon {
    private int id;
    private String url;
    private String name;
    private Sprites sprites;
    private List<TypeWrapper> types;
    private List<StatWrapper> stats;

    public Pokemon(int id, String url, String name, Sprites sprites, List<TypeWrapper> types, List<StatWrapper> stats) {
        this.id = id;
        this.url = url;
        this.name = name;
        this.sprites = sprites;
        this.types = types;
        this.stats = stats;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Sprites getSprites() {
        return sprites;
    }

    public List<TypeWrapper> getTypes() {
        return types;
    }

    public List<StatWrapper> getStats() {
        return stats;
    }

    public static class Sprites {
        @SerializedName("front_default")
        private String front_default;

        public Sprites(String front_default) {
            this.front_default = front_default;
        }

        public String getFrontDefault() {
            return front_default;
        }
    }

    public static class TypeWrapper {
        private Type type;

        public TypeWrapper(Type type) {
            this.type = type;
        }

        public Type getType() {
            return type;
        }
    }

    public static class Type {
        private String name;

        public Type(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public static class StatWrapper {
        private Stat stat;
        @SerializedName("base_stat")
        private int baseStat;

        public Stat getStat() {
            return stat;
        }

        public int getBaseStat() {
            return baseStat;
        }

        public static class Stat {
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
