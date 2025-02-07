package com.chillteq.bible_study_server.model;

public class Media implements Comparable<Media> {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int compareTo(Media other) {
        return this.name.compareTo(other.name);
    }
}