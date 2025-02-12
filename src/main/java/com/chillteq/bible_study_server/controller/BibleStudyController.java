package com.chillteq.bible_study_server.controller;

import com.chillteq.bible_study_server.Service.FileService;
import com.chillteq.bible_study_server.Service.ScheduleService;
import com.chillteq.bible_study_server.model.Playlist;
import com.chillteq.bible_study_server.model.Schedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

@RestController
public class BibleStudyController {

    @Autowired
    FileService fileService;

    @Autowired
    ScheduleService scheduleService;

    /*
     * Gets the metadata for all media currently configured
     */
    @GetMapping(value="media")
    public ResponseEntity<List<Playlist>> getMediaMetadata() throws IOException {
        return ResponseEntity.ok().body(fileService.getMediaMetadata());
    }

    @GetMapping(value="media/fetch")
    public ResponseEntity<FileSystemResource> getMediaByPlaylistNameAndMediaName(@RequestParam(name="playlistName") String playlistName,
                                                                                 @RequestParam(name="mediaName") String mediaName) throws IOException {

        File mediaFile = fileService.getMediaByPlaylistNameAndMediaName(playlistName, mediaName);
        if (!mediaFile.exists()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + mediaName);
        responseHeaders.add(HttpHeaders.CONTENT_TYPE, "audio/mpeg");

        long fileLength = mediaFile.length();
        String range = responseHeaders.getFirst(HttpHeaders.RANGE);
        if (StringUtils.hasText(range)) {
            HttpRange httpRange = HttpRange.parseRanges(range).get(0);
            long start = httpRange.getRangeStart(fileLength);
            long end = httpRange.getRangeEnd(fileLength);
            long contentLength = end - start + 1;
            responseHeaders.add(HttpHeaders.CONTENT_RANGE, "bytes " + start + "-" + end + "/" + fileLength);
            responseHeaders.add(HttpHeaders.ACCEPT_RANGES, "bytes");
            responseHeaders.setContentLength(contentLength);

            byte[] data = Files.readAllBytes(mediaFile.toPath()).length > 0 ? Files.readAllBytes(mediaFile.toPath()) : new byte[0];
            return new ResponseEntity<>(new FileSystemResource(Arrays.toString(new String(data, (int) start, (int) contentLength).getBytes())), responseHeaders, HttpStatus.PARTIAL_CONTENT);
        } else {
            responseHeaders.setContentLength(fileLength);
            return new ResponseEntity<>(new FileSystemResource(mediaFile), responseHeaders, HttpStatus.OK);

        }
    }

    @GetMapping(value="schedule")
    public ResponseEntity<List<Schedule>> getSchedule() {
        return ResponseEntity.ok().body(scheduleService.getSchedule());
    }
}
