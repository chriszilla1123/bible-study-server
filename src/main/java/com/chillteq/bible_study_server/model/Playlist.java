package com.chillteq.bible_study_server.model;

import java.util.List;

public class Playlist implements Comparable<Playlist> {
    private String name;
    private List<Media> media;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Media> getMedia() {
        return media;
    }

    public void setMedia(List<Media> media) {
        this.media = media;
    }

    @Override
    public int compareTo(Playlist other) {
        return this.name.compareTo(other.name);
    }
}