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

    public String getUrl() {return url;
    }

    public static class Sprites {
        @SerializedName("front_default")
        private String front_default;

        public String getFrontDefault() {
            return front_default;
        }
    }

    public static class TypeWrapper {
        private Type type;

        public Type getType() {
            return type;
        }
    }

    public static class Type {
        private String name;

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

        public void setStat(Stat stat) {
            this.stat = stat;
        }

        public int getBaseStat() {
            return baseStat;
        }

        public void setBaseStat(int baseStat) {
            this.baseStat = baseStat;
        }

        // Klasa Stat, która zawiera nazwę statystyki
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
