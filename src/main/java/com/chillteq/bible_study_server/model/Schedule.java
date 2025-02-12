package com.chillteq.bible_study_server.model;

import java.time.ZonedDateTime;
import java.util.Objects;

public class Schedule implements Comparable<Schedule> {
    private Media media;
    private ZonedDateTime startTime;
    private ZonedDateTime endTime;

    public Media getMedia() {
        return media;
    }

    public void setMedia(Media media) {
        this.media = media;
    }

    public ZonedDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(ZonedDateTime startTime) {
        this.startTime = startTime;
    }

    public ZonedDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(ZonedDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public int compareTo(Schedule other) {
        return startTime.toInstant().compareTo(other.startTime.toInstant());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Schedule schedule = (Schedule) o;
        return Objects.equals(media, schedule.media) && Objects.equals(startTime, schedule.startTime) && Objects.equals(endTime, schedule.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(media, startTime, endTime);
    }

    @Override
    public String toString() {
        return "Schedule{" +
                "media=" + media +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
