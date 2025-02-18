package com.chillteq.bible_study_server.Service;

import com.chillteq.bible_study_server.model.Media;
import com.chillteq.bible_study_server.model.Playlist;
import com.chillteq.bible_study_server.model.Schedule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class ScheduleService {
    private static final Logger logger = LoggerFactory.getLogger(ScheduleService.class);

    @Autowired
    private FileService fileService;

    //The list of songs to be added to the queue. Should be randomized before setting.
    private List<Media> mediaList = new ArrayList<>();

    private BlockingQueue<Schedule> schedule = new LinkedBlockingQueue<>();

    @Scheduled(initialDelay = 10000, fixedRate = 300000)
    private void init() {
        try {
            setMediaList();
        } catch (Exception e) {
            logger.error("Error updating the media list: {} ", e.getMessage());
        }
    }

    private void setMediaList() throws IOException {
        List<Playlist> playlists = fileService.getMediaMetadata();
        List<Media> incomingMediaList = new ArrayList<>();
        playlists.forEach(playlist -> {
            incomingMediaList.addAll(playlist.getMedia());
        });
        List<Media> existingMediaList = new ArrayList<>(this.mediaList);

        Collections.sort(incomingMediaList);
        Collections.sort(existingMediaList);

        if(incomingMediaList.equals(existingMediaList)) {
            //No change needed.
            logger.info("setMediaList - No change needed to mediaList");
        } else {
            logger.info("setMediaList - file update detected");
            logger.info("setMediaList - existing files: {}", existingMediaList);
            logger.info("setMediaList - incoming files: {}", incomingMediaList);
            Collections.shuffle(incomingMediaList);
            this.mediaList = incomingMediaList;
            this.schedule = new LinkedBlockingQueue<>();
            logger.info("setMediaList - mediaList updated and shuffled {}", this.mediaList);
        }
    }

    @Scheduled(fixedDelay = 5000)
    private void scheduleBuilder() {
        logger.info("scheduleBuilder - Started");
        if(mediaList.isEmpty()) {
            logger.info("scheduleBuilder - not running because mediaList is empty");
            return;
        }
        Schedule nowPlaying = schedule.peek();
        if (nowPlaying != null && nowPlaying.getEndTime().isBefore(ZonedDateTime.now())) {
            Schedule removed = schedule.poll();
            logger.info("scheduleBuilder - {} has been removed from the schedule", removed);
        }

        logger.info("scheduleBuilder - scheduleSize: {}, mediaListSize: {}", schedule.size(), this.mediaList.size());

        while (schedule.size() < this.mediaList.size()) {
            //The mediaList is already in the order that the radio will play. We need to find the index
            //of the last media in the list and use that to add the next media.
            Schedule newScheduledMedia = new Schedule();
            int onIndex = 0;
            if(schedule.isEmpty()) {
                logger.info("scheduleBuilder - Building new schedule");
                newScheduledMedia.setStartTime(ZonedDateTime.now());
            } else {
                Schedule lastSchedule = schedule.stream().toList().get(schedule.size() - 1);
                onIndex = mediaList.indexOf(lastSchedule.getMedia());
                onIndex++;
                onIndex = onIndex % (mediaList.size());

                newScheduledMedia.setStartTime(lastSchedule.getEndTime());
            }
            newScheduledMedia.setMedia(mediaList.get(onIndex));
            newScheduledMedia.setEndTime(newScheduledMedia.getStartTime().plusNanos(newScheduledMedia.getMedia().getDuration().toNanos()));
            logger.info("scheduleBuilder - Adding {} to the schedule from mediaList index {}", newScheduledMedia, onIndex);
            schedule.add(newScheduledMedia);
        }
    }

    public List<Schedule> getSchedule() {
        List<Schedule> schedule = new ArrayList<>(this.schedule.stream().toList());
        Collections.sort(schedule);
        return schedule;
    }
}
