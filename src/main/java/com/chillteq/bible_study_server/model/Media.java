package com.chillteq.bible_study_server.model;

import java.time.Duration;
import java.util.Objects;

public class Media implements Comparable<Media> {
    private String name;
    private String playlistName;
    private Duration duration;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

    public Duration getDuration() {
        return this.duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    @Override
    public int compareTo(Media other) {
        return this.name.compareTo(other.name);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Media media = (Media) o;
        return Objects.equals(name, media.name) && Objects.equals(playlistName, media.playlistName) && Objects.equals(duration, media.duration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, playlistName, duration);
    }

    @Override
    public String toString() {
        return "Media{" +
                "name='" + name + '\'' +
                ", playlistName='" + playlistName + '\'' +
                ", duration=" + duration +
                '}';
    }
}